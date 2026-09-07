package com.comidarapida.repository;

import com.comidarapida.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    @Modifying
    @Transactional
    @Query(value = "UPDATE usuario SET tipo_usuario = :tipo WHERE id_usuario = :id", nativeQuery = true)
    void updateTipoUsuario(@Param("id") Long id, @Param("tipo") String tipo);
}
