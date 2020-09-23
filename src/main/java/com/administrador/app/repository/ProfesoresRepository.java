package com.administrador.app.repository;

import com.administrador.app.domain.Profesores;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Profesores entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfesoresRepository extends JpaRepository<Profesores, Long> {
}
