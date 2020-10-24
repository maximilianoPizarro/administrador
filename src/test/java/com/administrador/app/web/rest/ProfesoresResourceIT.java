package com.administrador.app.web.rest;

import com.administrador.app.AdministradorApp;
import com.administrador.app.domain.Profesores;
import com.administrador.app.domain.User;
import com.administrador.app.repository.ProfesoresRepository;
import com.administrador.app.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProfesoresResource} REST controller.
 */
@SpringBootTest(classes = AdministradorApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ProfesoresResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_DNI = "AAAAAAAAAA";
    private static final String UPDATED_DNI = "BBBBBBBBBB";

    private static final String DEFAULT_DOMICILIO = "AAAAAAAAAA";
    private static final String UPDATED_DOMICILIO = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    @Autowired
    private ProfesoresRepository profesoresRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfesoresMockMvc;

    private Profesores profesores;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profesores createEntity(EntityManager em) {
        Profesores profesores = new Profesores()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .dni(DEFAULT_DNI)
            .domicilio(DEFAULT_DOMICILIO)
            .telefono(DEFAULT_TELEFONO)
            .titulo(DEFAULT_TITULO);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        profesores.setUser(user);
        return profesores;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profesores createUpdatedEntity(EntityManager em) {
        Profesores profesores = new Profesores()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .dni(UPDATED_DNI)
            .domicilio(UPDATED_DOMICILIO)
            .telefono(UPDATED_TELEFONO)
            .titulo(UPDATED_TITULO);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        profesores.setUser(user);
        return profesores;
    }

    @BeforeEach
    public void initTest() {
        profesores = createEntity(em);
    }

    @Test
    @Transactional
    public void createProfesores() throws Exception {
        int databaseSizeBeforeCreate = profesoresRepository.findAll().size();
        // Create the Profesores
        restProfesoresMockMvc.perform(post("/api/profesores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(profesores)))
            .andExpect(status().isCreated());

        // Validate the Profesores in the database
        List<Profesores> profesoresList = profesoresRepository.findAll();
        assertThat(profesoresList).hasSize(databaseSizeBeforeCreate + 1);
        Profesores testProfesores = profesoresList.get(profesoresList.size() - 1);
        assertThat(testProfesores.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testProfesores.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testProfesores.getDni()).isEqualTo(DEFAULT_DNI);
        assertThat(testProfesores.getDomicilio()).isEqualTo(DEFAULT_DOMICILIO);
        assertThat(testProfesores.getTelefono()).isEqualTo(DEFAULT_TELEFONO);
        assertThat(testProfesores.getTitulo()).isEqualTo(DEFAULT_TITULO);

        // Validate the id for MapsId, the ids must be same
        assertThat(testProfesores.getId()).isEqualTo(testProfesores.getUser().getId());
    }

    @Test
    @Transactional
    public void createProfesoresWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = profesoresRepository.findAll().size();

        // Create the Profesores with an existing ID
        profesores.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfesoresMockMvc.perform(post("/api/profesores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(profesores)))
            .andExpect(status().isBadRequest());

        // Validate the Profesores in the database
        List<Profesores> profesoresList = profesoresRepository.findAll();
        assertThat(profesoresList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateProfesoresMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        profesoresRepository.saveAndFlush(profesores);
        int databaseSizeBeforeCreate = profesoresRepository.findAll().size();

        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the profesores
        Profesores updatedProfesores = profesoresRepository.findById(profesores.getId()).get();
        // Disconnect from session so that the updates on updatedProfesores are not directly saved in db
        em.detach(updatedProfesores);

        // Update the User with new association value
        updatedProfesores.setUser(user);

        // Update the entity
        restProfesoresMockMvc.perform(put("/api/profesores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProfesores)))
            .andExpect(status().isOk());

        // Validate the Profesores in the database
        List<Profesores> profesoresList = profesoresRepository.findAll();
        assertThat(profesoresList).hasSize(databaseSizeBeforeCreate);
        Profesores testProfesores = profesoresList.get(profesoresList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testProfesores.getId()).isEqualTo(testProfesores.getUser().getId());
    }

    @Test
    @Transactional
    public void getAllProfesores() throws Exception {
        // Initialize the database
        profesoresRepository.saveAndFlush(profesores);

        // Get all the profesoresList
        restProfesoresMockMvc.perform(get("/api/profesores?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profesores.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].dni").value(hasItem(DEFAULT_DNI)))
            .andExpect(jsonPath("$.[*].domicilio").value(hasItem(DEFAULT_DOMICILIO)))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO)))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)));
    }
    
    @Test
    @Transactional
    public void getProfesores() throws Exception {
        // Initialize the database
        profesoresRepository.saveAndFlush(profesores);

        // Get the profesores
        restProfesoresMockMvc.perform(get("/api/profesores/{id}", profesores.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profesores.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.dni").value(DEFAULT_DNI))
            .andExpect(jsonPath("$.domicilio").value(DEFAULT_DOMICILIO))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO));
    }
    @Test
    @Transactional
    public void getNonExistingProfesores() throws Exception {
        // Get the profesores
        restProfesoresMockMvc.perform(get("/api/profesores/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProfesores() throws Exception {
        // Initialize the database
        profesoresRepository.saveAndFlush(profesores);

        int databaseSizeBeforeUpdate = profesoresRepository.findAll().size();

        // Update the profesores
        Profesores updatedProfesores = profesoresRepository.findById(profesores.getId()).get();
        // Disconnect from session so that the updates on updatedProfesores are not directly saved in db
        em.detach(updatedProfesores);
        updatedProfesores
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .dni(UPDATED_DNI)
            .domicilio(UPDATED_DOMICILIO)
            .telefono(UPDATED_TELEFONO)
            .titulo(UPDATED_TITULO);

        restProfesoresMockMvc.perform(put("/api/profesores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProfesores)))
            .andExpect(status().isOk());

        // Validate the Profesores in the database
        List<Profesores> profesoresList = profesoresRepository.findAll();
        assertThat(profesoresList).hasSize(databaseSizeBeforeUpdate);
        Profesores testProfesores = profesoresList.get(profesoresList.size() - 1);
        assertThat(testProfesores.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testProfesores.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testProfesores.getDni()).isEqualTo(UPDATED_DNI);
        assertThat(testProfesores.getDomicilio()).isEqualTo(UPDATED_DOMICILIO);
        assertThat(testProfesores.getTelefono()).isEqualTo(UPDATED_TELEFONO);
        assertThat(testProfesores.getTitulo()).isEqualTo(UPDATED_TITULO);
    }

    @Test
    @Transactional
    public void updateNonExistingProfesores() throws Exception {
        int databaseSizeBeforeUpdate = profesoresRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfesoresMockMvc.perform(put("/api/profesores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(profesores)))
            .andExpect(status().isBadRequest());

        // Validate the Profesores in the database
        List<Profesores> profesoresList = profesoresRepository.findAll();
        assertThat(profesoresList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProfesores() throws Exception {
        // Initialize the database
        profesoresRepository.saveAndFlush(profesores);

        int databaseSizeBeforeDelete = profesoresRepository.findAll().size();

        // Delete the profesores
        restProfesoresMockMvc.perform(delete("/api/profesores/{id}", profesores.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Profesores> profesoresList = profesoresRepository.findAll();
        assertThat(profesoresList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
