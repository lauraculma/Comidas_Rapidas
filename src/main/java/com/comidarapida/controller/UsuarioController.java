package com.comidarapida.controller;

import com.comidarapida.dto.UsuarioDto;
import com.comidarapida.dto.UsuarioRegistroDto;
import com.comidarapida.model.Administrador;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.model.Usuario;
import com.comidarapida.model.Vendedor;
import com.comidarapida.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestParam String correo, @RequestParam String contraseña) {
        return usuarioService.autenticar(correo, contraseña)
                .map(u -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.status(401).build());
    }

    @PutMapping("/{id}/contacto")
    public ResponseEntity<UsuarioDto> actualizarContacto(@PathVariable Long id, @RequestBody UsuarioDto dto) {
        Usuario actualizado = usuarioService.actualizarDatosContacto(id, dto.correoElectronico, dto.telefono);
        UsuarioDto r = new UsuarioDto();
        r.id = actualizado.getIdUsuario();
        r.nombre = actualizado.getNombre();
        r.apellido = actualizado.getApellido();
        r.correoElectronico = actualizado.getCorreoElectronico();
        r.telefono = actualizado.getTelefono();
        r.documento = actualizado.getDocumento();
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody UsuarioRegistroDto dto) {
        if (dto == null || dto.tipo == null || dto.tipo.isBlank()) {
            return ResponseEntity.badRequest().body("Tipo de usuario es requerido");
        }
        String tipo = dto.tipo.trim().toLowerCase();
        Usuario u;
        switch (tipo) {
            case "vendedor":
                u = new Vendedor();
                break;
            case "administrador":
                u = new Administrador();
                break;
            case "encargadoinventario":
            case "encargado_inventario":
            case "encargado-inventario":
                u = new EncargadoInventario();
                break;
            default:
                return ResponseEntity.badRequest().body("Tipo de usuario inválido: " + dto.tipo);
        }

        // Campos base
        u.setNombre(dto.nombre);
        u.setApellido(dto.apellido);
        u.setCorreoElectronico(dto.correoElectronico);
        u.setContraseña(dto.contrasena);
        u.setDocumento(dto.documento);
        u.setTelefono(dto.telefono);

        Usuario creado = usuarioService.registrar(u);
        return ResponseEntity.status(201).body(Map.of("id", creado.getIdUsuario()));
    }
}
