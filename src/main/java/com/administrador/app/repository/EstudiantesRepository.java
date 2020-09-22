package com.administrador.app.repository;

import com.administrador.app.domain.Estudiantes;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Estudiantes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EstudiantesRepository extends JpaRepository<Estudiantes, Long> {
}
