package com.comidarapida.service;

import com.comidarapida.model.Usuario;
import com.comidarapida.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null) return Optional.empty();
        String correoNorm = correo.trim().toLowerCase();
        String passNorm = contrasena.trim();
        Optional<Usuario> u = usuarioRepository.findByCorreoElectronico(correoNorm);
        if (u.isPresent() && u.get().validarCredenciales(correoNorm, passNorm)) {
            return u;
        }
        return Optional.empty();
    }

    public Usuario actualizarDatosContacto(Long id, String correo, String telefono) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        u.actualizarDatosContacto(correo, telefono);
        return usuarioRepository.save(u);
    }

    public Usuario registrar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> consultar(Long id) {
        return usuarioRepository.findById(id);
    }
}
