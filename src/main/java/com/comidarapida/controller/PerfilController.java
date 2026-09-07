package com.comidarapida.controller;

import com.comidarapida.model.Usuario;
import com.comidarapida.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    public PerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/perfil")
    public String perfil(Model model, HttpSession session) {
        // Ensure usuario is in model (GlobalModelAdvice also tries)
        Object idObj = session.getAttribute("usuarioId");
        Long id = null;
        if (idObj instanceof Number) id = ((Number) idObj).longValue();
        else if (idObj instanceof String) {
            try { id = Long.parseLong((String) idObj); } catch (NumberFormatException ignored) {}
        }
        if (id == null) return "redirect:/login";
        var opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) return "redirect:/login";
        Usuario u = opt.get();
        model.addAttribute("usuario", u);
        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam("correoElectronico") String correo,
                                   @RequestParam("telefono") String telefono,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        try {
            Object idObj = session.getAttribute("usuarioId");
            Long id = null;
            if (idObj instanceof Number) id = ((Number) idObj).longValue();
            else if (idObj instanceof String) {
                try { id = Long.parseLong((String) idObj); } catch (NumberFormatException ignored) {}
            }
            if (id == null) {
                ra.addFlashAttribute("error","Sesión no válida. Por favor ingresa de nuevo.");
                return "redirect:/login";
            }
            var opt = usuarioRepository.findById(id);
            if (opt.isEmpty()) {
                ra.addFlashAttribute("error","Usuario no encontrado.");
                return "redirect:/login";
            }
            Usuario u = opt.get();
            if (correo != null && !correo.isBlank()) u.setCorreoElectronico(correo.trim());
            if (telefono != null && !telefono.isBlank()) u.setTelefono(telefono.trim());
            usuarioRepository.save(u);
            ra.addFlashAttribute("success","Perfil actualizado correctamente.");
            return "redirect:/perfil";
        } catch (Exception ex) {
            ra.addFlashAttribute("error","Error al actualizar perfil.");
            return "redirect:/perfil";
        }
    }
}

