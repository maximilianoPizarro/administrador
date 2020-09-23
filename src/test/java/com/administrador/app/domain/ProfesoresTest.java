package com.administrador.app.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.administrador.app.web.rest.TestUtil;

public class ProfesoresTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profesores.class);
        Profesores profesores1 = new Profesores();
        profesores1.setId(1L);
        Profesores profesores2 = new Profesores();
        profesores2.setId(profesores1.getId());
        assertThat(profesores1).isEqualTo(profesores2);
        profesores2.setId(2L);
        assertThat(profesores1).isNotEqualTo(profesores2);
        profesores1.setId(null);
        assertThat(profesores1).isNotEqualTo(profesores2);
    }
}
