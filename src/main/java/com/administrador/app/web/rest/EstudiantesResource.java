package com.administrador.app.web.rest;

import com.administrador.app.domain.Estudiantes;
import com.administrador.app.repository.EstudiantesRepository;
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
import java.util.Optional;

/**
 * REST controller for managing {@link com.administrador.app.domain.Estudiantes}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EstudiantesResource {

    private final Logger log = LoggerFactory.getLogger(EstudiantesResource.class);

    private static final String ENTITY_NAME = "estudiantes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EstudiantesRepository estudiantesRepository;

    public EstudiantesResource(EstudiantesRepository estudiantesRepository) {
        this.estudiantesRepository = estudiantesRepository;
    }

    /**
     * {@code POST  /estudiantes} : Create a new estudiantes.
     *
     * @param estudiantes the estudiantes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new estudiantes, or with status {@code 400 (Bad Request)} if the estudiantes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/estudiantes")
    public ResponseEntity<Estudiantes> createEstudiantes(@RequestBody Estudiantes estudiantes) throws URISyntaxException {
        log.debug("REST request to save Estudiantes : {}", estudiantes);
        if (estudiantes.getId() != null) {
            throw new BadRequestAlertException("A new estudiantes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Estudiantes result = estudiantesRepository.save(estudiantes);
        return ResponseEntity.created(new URI("/api/estudiantes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /estudiantes} : Updates an existing estudiantes.
     *
     * @param estudiantes the estudiantes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estudiantes,
     * or with status {@code 400 (Bad Request)} if the estudiantes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the estudiantes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/estudiantes")
    public ResponseEntity<Estudiantes> updateEstudiantes(@RequestBody Estudiantes estudiantes) throws URISyntaxException {
        log.debug("REST request to update Estudiantes : {}", estudiantes);
        if (estudiantes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Estudiantes result = estudiantesRepository.save(estudiantes);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, estudiantes.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /estudiantes} : get all the estudiantes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of estudiantes in body.
     */
    @GetMapping("/estudiantes")
    public ResponseEntity<List<Estudiantes>> getAllEstudiantes(Pageable pageable) {
        log.debug("REST request to get a page of Estudiantes");
        Page<Estudiantes> page = estudiantesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /estudiantes/:id} : get the "id" estudiantes.
     *
     * @param id the id of the estudiantes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the estudiantes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/estudiantes/{id}")
    public ResponseEntity<Estudiantes> getEstudiantes(@PathVariable Long id) {
        log.debug("REST request to get Estudiantes : {}", id);
        Optional<Estudiantes> estudiantes = estudiantesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(estudiantes);
    }

    /**
     * {@code DELETE  /estudiantes/:id} : delete the "id" estudiantes.
     *
     * @param id the id of the estudiantes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/estudiantes/{id}")
    public ResponseEntity<Void> deleteEstudiantes(@PathVariable Long id) {
        log.debug("REST request to delete Estudiantes : {}", id);
        estudiantesRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
