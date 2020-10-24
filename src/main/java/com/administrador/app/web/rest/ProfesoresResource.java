package com.administrador.app.web.rest;

import com.administrador.app.domain.Profesores;
import com.administrador.app.repository.ProfesoresRepository;
import com.administrador.app.repository.UserRepository;
import com.administrador.app.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.administrador.app.domain.Profesores}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProfesoresResource {

    private final Logger log = LoggerFactory.getLogger(ProfesoresResource.class);

    private static final String ENTITY_NAME = "profesores";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfesoresRepository profesoresRepository;

    private final UserRepository userRepository;

    public ProfesoresResource(ProfesoresRepository profesoresRepository, UserRepository userRepository) {
        this.profesoresRepository = profesoresRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /profesores} : Create a new profesores.
     *
     * @param profesores the profesores to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profesores, or with status {@code 400 (Bad Request)} if the profesores has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/profesores")
    public ResponseEntity<Profesores> createProfesores(@RequestBody Profesores profesores) throws URISyntaxException {
        log.debug("REST request to save Profesores : {}", profesores);
        if (profesores.getId() != null) {
            throw new BadRequestAlertException("A new profesores cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(profesores.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long userId = profesores.getUser().getId();
        userRepository.findById(userId).ifPresent(profesores::user);
        Profesores result = profesoresRepository.save(profesores);
        return ResponseEntity.created(new URI("/api/profesores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /profesores} : Updates an existing profesores.
     *
     * @param profesores the profesores to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profesores,
     * or with status {@code 400 (Bad Request)} if the profesores is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profesores couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/profesores")
    public ResponseEntity<Profesores> updateProfesores(@RequestBody Profesores profesores) throws URISyntaxException {
        log.debug("REST request to update Profesores : {}", profesores);
        if (profesores.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Profesores result = profesoresRepository.save(profesores);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profesores.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /profesores} : get all the profesores.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of profesores in body.
     */
    @GetMapping("/profesores")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Profesores>> getAllProfesores(Pageable pageable) {
        log.debug("REST request to get a page of Profesores");
        Page<Profesores> page = profesoresRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /profesores/:id} : get the "id" profesores.
     *
     * @param id the id of the profesores to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profesores, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/profesores/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Profesores> getProfesores(@PathVariable Long id) {
        log.debug("REST request to get Profesores : {}", id);
        Optional<Profesores> profesores = profesoresRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(profesores);
    }

    /**
     * {@code DELETE  /profesores/:id} : delete the "id" profesores.
     *
     * @param id the id of the profesores to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/profesores/{id}")
    public ResponseEntity<Void> deleteProfesores(@PathVariable Long id) {
        log.debug("REST request to delete Profesores : {}", id);
        profesoresRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
