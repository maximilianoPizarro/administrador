package com.administrador.app.web.rest;

import com.administrador.app.AdministradorApp;
import com.administrador.app.domain.Estudiantes;
import com.administrador.app.domain.User;
import com.administrador.app.repository.EstudiantesRepository;
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
 * Integration tests for the {@link EstudiantesResource} REST controller.
 */
@SpringBootTest(classes = AdministradorApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class EstudiantesResourceIT {

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

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    @Autowired
    private EstudiantesRepository estudiantesRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEstudiantesMockMvc;

    private Estudiantes estudiantes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estudiantes createEntity(EntityManager em) {
        Estudiantes estudiantes = new Estudiantes()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .dni(DEFAULT_DNI)
            .domicilio(DEFAULT_DOMICILIO)
            .telefono(DEFAULT_TELEFONO)
            .email(DEFAULT_EMAIL);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        estudiantes.setUser(user);
        return estudiantes;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estudiantes createUpdatedEntity(EntityManager em) {
        Estudiantes estudiantes = new Estudiantes()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .dni(UPDATED_DNI)
            .domicilio(UPDATED_DOMICILIO)
            .telefono(UPDATED_TELEFONO)
            .email(UPDATED_EMAIL);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        estudiantes.setUser(user);
        return estudiantes;
    }

    @BeforeEach
    public void initTest() {
        estudiantes = createEntity(em);
    }

    @Test
    @Transactional
    public void createEstudiantes() throws Exception {
        int databaseSizeBeforeCreate = estudiantesRepository.findAll().size();
        // Create the Estudiantes
        restEstudiantesMockMvc.perform(post("/api/estudiantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(estudiantes)))
            .andExpect(status().isCreated());

        // Validate the Estudiantes in the database
        List<Estudiantes> estudiantesList = estudiantesRepository.findAll();
        assertThat(estudiantesList).hasSize(databaseSizeBeforeCreate + 1);
        Estudiantes testEstudiantes = estudiantesList.get(estudiantesList.size() - 1);
        assertThat(testEstudiantes.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testEstudiantes.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testEstudiantes.getDni()).isEqualTo(DEFAULT_DNI);
        assertThat(testEstudiantes.getDomicilio()).isEqualTo(DEFAULT_DOMICILIO);
        assertThat(testEstudiantes.getTelefono()).isEqualTo(DEFAULT_TELEFONO);
        assertThat(testEstudiantes.getEmail()).isEqualTo(DEFAULT_EMAIL);

        // Validate the id for MapsId, the ids must be same
        assertThat(testEstudiantes.getId()).isEqualTo(testEstudiantes.getUser().getId());
    }

    @Test
    @Transactional
    public void createEstudiantesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = estudiantesRepository.findAll().size();

        // Create the Estudiantes with an existing ID
        estudiantes.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEstudiantesMockMvc.perform(post("/api/estudiantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(estudiantes)))
            .andExpect(status().isBadRequest());

        // Validate the Estudiantes in the database
        List<Estudiantes> estudiantesList = estudiantesRepository.findAll();
        assertThat(estudiantesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateEstudiantesMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        estudiantesRepository.saveAndFlush(estudiantes);
        int databaseSizeBeforeCreate = estudiantesRepository.findAll().size();

        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the estudiantes
        Estudiantes updatedEstudiantes = estudiantesRepository.findById(estudiantes.getId()).get();
        // Disconnect from session so that the updates on updatedEstudiantes are not directly saved in db
        em.detach(updatedEstudiantes);

        // Update the User with new association value
        updatedEstudiantes.setUser(user);

        // Update the entity
        restEstudiantesMockMvc.perform(put("/api/estudiantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedEstudiantes)))
            .andExpect(status().isOk());

        // Validate the Estudiantes in the database
        List<Estudiantes> estudiantesList = estudiantesRepository.findAll();
        assertThat(estudiantesList).hasSize(databaseSizeBeforeCreate);
        Estudiantes testEstudiantes = estudiantesList.get(estudiantesList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testEstudiantes.getId()).isEqualTo(testEstudiantes.getUser().getId());
    }

    @Test
    @Transactional
    public void getAllEstudiantes() throws Exception {
        // Initialize the database
        estudiantesRepository.saveAndFlush(estudiantes);

        // Get all the estudiantesList
        restEstudiantesMockMvc.perform(get("/api/estudiantes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(estudiantes.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].dni").value(hasItem(DEFAULT_DNI)))
            .andExpect(jsonPath("$.[*].domicilio").value(hasItem(DEFAULT_DOMICILIO)))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }
    
    @Test
    @Transactional
    public void getEstudiantes() throws Exception {
        // Initialize the database
        estudiantesRepository.saveAndFlush(estudiantes);

        // Get the estudiantes
        restEstudiantesMockMvc.perform(get("/api/estudiantes/{id}", estudiantes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(estudiantes.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.dni").value(DEFAULT_DNI))
            .andExpect(jsonPath("$.domicilio").value(DEFAULT_DOMICILIO))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }
    @Test
    @Transactional
    public void getNonExistingEstudiantes() throws Exception {
        // Get the estudiantes
        restEstudiantesMockMvc.perform(get("/api/estudiantes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEstudiantes() throws Exception {
        // Initialize the database
        estudiantesRepository.saveAndFlush(estudiantes);

        int databaseSizeBeforeUpdate = estudiantesRepository.findAll().size();

        // Update the estudiantes
        Estudiantes updatedEstudiantes = estudiantesRepository.findById(estudiantes.getId()).get();
        // Disconnect from session so that the updates on updatedEstudiantes are not directly saved in db
        em.detach(updatedEstudiantes);
        updatedEstudiantes
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .dni(UPDATED_DNI)
            .domicilio(UPDATED_DOMICILIO)
            .telefono(UPDATED_TELEFONO)
            .email(UPDATED_EMAIL);

        restEstudiantesMockMvc.perform(put("/api/estudiantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedEstudiantes)))
            .andExpect(status().isOk());

        // Validate the Estudiantes in the database
        List<Estudiantes> estudiantesList = estudiantesRepository.findAll();
        assertThat(estudiantesList).hasSize(databaseSizeBeforeUpdate);
        Estudiantes testEstudiantes = estudiantesList.get(estudiantesList.size() - 1);
        assertThat(testEstudiantes.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testEstudiantes.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testEstudiantes.getDni()).isEqualTo(UPDATED_DNI);
        assertThat(testEstudiantes.getDomicilio()).isEqualTo(UPDATED_DOMICILIO);
        assertThat(testEstudiantes.getTelefono()).isEqualTo(UPDATED_TELEFONO);
        assertThat(testEstudiantes.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingEstudiantes() throws Exception {
        int databaseSizeBeforeUpdate = estudiantesRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstudiantesMockMvc.perform(put("/api/estudiantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(estudiantes)))
            .andExpect(status().isBadRequest());

        // Validate the Estudiantes in the database
        List<Estudiantes> estudiantesList = estudiantesRepository.findAll();
        assertThat(estudiantesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEstudiantes() throws Exception {
        // Initialize the database
        estudiantesRepository.saveAndFlush(estudiantes);

        int databaseSizeBeforeDelete = estudiantesRepository.findAll().size();

        // Delete the estudiantes
        restEstudiantesMockMvc.perform(delete("/api/estudiantes/{id}", estudiantes.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Estudiantes> estudiantesList = estudiantesRepository.findAll();
        assertThat(estudiantesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
