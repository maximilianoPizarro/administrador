package com.administrador.app.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects if a call is to be forwarded to a microservice and adds it to zuul routes
 */
@Component
@Order(1)
public class RouteDetectorFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(RouteDetectorFilter.class);
    private final RestTemplate restTemplate;
    private final ZuulProperties zuulProperties;
    private final ZuulHandlerMapping zuulHandlerMapping;

    public RouteDetectorFilter(@Qualifier("restTemplate") RestTemplate restTemplate, ZuulProperties zuulProperties, ZuulHandlerMapping zuulHandlerMapping) {
        this.restTemplate = restTemplate;
        this.zuulProperties = zuulProperties;
        this.zuulHandlerMapping = zuulHandlerMapping;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String requestURL = ((HttpServletRequest) request).getRequestURI();

        Pattern pattern = Pattern.compile("/services/(.*?)/.*");
        Matcher matcher = pattern.matcher(requestURL);

        // match a service-call by a rule like /services/{serviceName}/**
        if (matcher.find()) {
            String serviceName = matcher.group(1);

            // test, if the service is missing in zuul routes
            if (!zuulProperties.getRoutes().containsKey(serviceName)) {
                try {
                    URI uri = new URI(
                        "http",
                        null,
                        serviceName,
                        80,
                        "/management/health",
                        null,
                        null
                    );

                    // try to reach the health endpoint
                    ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
                    if (!responseEntity.getStatusCode().isError()) {
                        ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute(
                            serviceName,
                            "/" + serviceName + "/**",
                            null,
                            "http://" + serviceName + "/",
                            true,
                            false,
                            new HashSet<>()
                        );

                        // update routes
                        zuulProperties.getRoutes().put(serviceName, route);
                        zuulHandlerMapping.setDirty(true);
                        log.info("added route {} dynamically", route);
                    } else {
                        log.warn("could not reach health endpoint of service {}", serviceName);
                    }
                } catch (URISyntaxException e) {
                    log.error("could not parse URI", e);
                }
            }

        }

        chain.doFilter(request, response);
    }
}
