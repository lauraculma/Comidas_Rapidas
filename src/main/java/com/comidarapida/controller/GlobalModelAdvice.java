package com.comidarapida.controller;

import com.comidarapida.model.Usuario;
import com.comidarapida.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioRepository usuarioRepository;
    private final Logger log = LoggerFactory.getLogger(GlobalModelAdvice.class);

    public GlobalModelAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void addSessionAttributes(Model model, HttpSession session) {
        try {
            Object idObj = session.getAttribute("usuarioId");
            log.debug("GlobalModelAdvice: session usuarioId={} sessionRoleAttr={}", idObj, session.getAttribute("sessionRole"));

            Long id = null;
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                try {
                    id = Long.parseLong((String) idObj);
                } catch (NumberFormatException nfe) {
                    // ignore, will try sessionRole fallback
                    log.debug("GlobalModelAdvice: usuarioId in session is non-numeric string: {}", idObj);
                }
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
                    log.debug("GlobalModelAdvice: added usuario id={} and sessionRole={}", id, role);
                    return; // done
                } else {
                    log.debug("GlobalModelAdvice: no usuario found for id={}", id);
                }
            }

            // fallback: expose whatever sessionRole attribute exists
            Object roleObj = session.getAttribute("sessionRole");
            if (roleObj != null) {
                model.addAttribute("sessionRole", roleObj.toString());
                log.debug("GlobalModelAdvice: added sessionRole from sessionAttr={}", roleObj);
            }

        } catch (Exception ex) {
            log.warn("GlobalModelAdvice: error while adding session attributes", ex);
            // Swallow exceptions to avoid breaking view rendering; expose nothing
        }
    }
}
