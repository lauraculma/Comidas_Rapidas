package com.comidarapida.controller;

import com.comidarapida.model.Administrador;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.model.Usuario;
import com.comidarapida.model.Vendedor;
import com.comidarapida.repository.UsuarioRepository;
import com.comidarapida.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        try {
            var opt = usuarioService.autenticar(username, password);
            if (opt.isEmpty()) {
                return "redirect:/login?error";
            }
            Usuario u = opt.get();
            Long uid = u.getIdUsuario();
            if (uid == null) {
                // ensure persisted
                return "redirect:/login?error";
            }
            session.setAttribute("usuarioId", uid);
            String role;
            if (u instanceof Administrador) role = "ADMIN";
            else if (u instanceof Vendedor) role = "VENDEDOR";
            else if (u instanceof EncargadoInventario) role = "INVENTARIO";
            else role = "UNKNOWN";
            session.setAttribute("sessionRole", role);
            // Redirect to the proper dashboard
            switch (role) {
                case "ADMIN":
                    return "redirect:/dashboard-admin";
                case "VENDEDOR":
                    return "redirect:/dashboard-vendedor";
                case "INVENTARIO":
                    return "redirect:/dashboard-inventario";
                default:
                    return "redirect:/login";
            }
        } catch (Exception ex) {
            // On any unexpected error, send back to login with error flag
            return "redirect:/login?error";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
