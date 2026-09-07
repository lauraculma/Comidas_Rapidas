package com.comidarapida.controller;

import com.comidarapida.model.Usuario;
import com.comidarapida.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final UsuarioRepository usuarioRepository;
    private final Logger log = LoggerFactory.getLogger(PageController.class);

    public PageController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private void ensureSessionAttributes(Model model, HttpSession session) {
        try {
            Object idObj = session.getAttribute("usuarioId");
            Long id = null;
            if (idObj instanceof Number) id = ((Number) idObj).longValue();
            else if (idObj instanceof String) {
                try { id = Long.parseLong((String) idObj); } catch (NumberFormatException ignored) {}
            }
            if (id != null) {
                var opt = usuarioRepository.findById(id);
                if (opt.isPresent()) {
                    Usuario u = opt.get();
                    model.addAttribute("usuario", u);
                    String role;
                    if (u instanceof com.comidarapida.model.Administrador) role = "ADMIN";
                    else if (u instanceof com.comidarapida.model.Vendedor) role = "VENDEDOR";
                    else if (u instanceof com.comidarapida.model.EncargadoInventario) role = "INVENTARIO";
                    else role = "UNKNOWN";
                    model.addAttribute("sessionRole", role);
                    log.debug("PageController: added usuario id={} and sessionRole={}", id, role);
                    return;
                }
            }
            Object roleObj = session.getAttribute("sessionRole");
            if (roleObj != null) {
                model.addAttribute("sessionRole", roleObj.toString());
                log.debug("PageController: added sessionRole from sessionAttr={}", roleObj);
            }
        } catch (Exception ex) {
            log.warn("PageController: error ensuring session attributes", ex);
        }
    }

    @GetMapping({"/","/dashboard","/dashboard-admin"})
    public String dashboardAdmin(Model model, HttpSession session) {
        ensureSessionAttributes(model, session);
        return "dashboard-admin";
    }

    @GetMapping("/dashboard-vendedor")
    public String dashboardVendedor(Model model, HttpSession session) {
        ensureSessionAttributes(model, session);
        return "dashboard-vendedor";
    }

    @GetMapping("/dashboard-inventario")
    public String dashboardInventario(Model model, HttpSession session) {
        ensureSessionAttributes(model, session);
        return "dashboard-inventario";
    }



    @GetMapping("/vendedor/productos")
    public String vendedorProductos(Model model, HttpSession session) {
        ensureSessionAttributes(model, session);
        return "disponibilidad";
    }

    @GetMapping("/vendedor/venta/nueva")
    public String nuevaVenta(Model model, HttpSession session) {
        ensureSessionAttributes(model, session);
        return "nueva-venta";
    }

}
