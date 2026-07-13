package com.ticket_system.Repository;

import com.ticket_system.Model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    List<Artista> findByEstado(String estado);

    List<Artista> findByGeneroIgnoreCase(String genero);

    List<Artista> findByNacionalidadIgnoreCase(String nacionalidad);

    List<Artista> findByNombreContainingIgnoreCase(String nombre);
}