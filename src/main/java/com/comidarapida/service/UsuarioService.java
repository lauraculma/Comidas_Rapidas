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

    public Optional<Usuario> autenticar(String correo, String contraseña) {
        Optional<Usuario> u = usuarioRepository.findByCorreoElectronico(correo);
        if (u.isPresent() && u.get().validarCredenciales(correo, contraseña)) {
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
