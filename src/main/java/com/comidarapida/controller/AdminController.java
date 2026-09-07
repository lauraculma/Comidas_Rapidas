package com.comidarapida.controller;

import com.comidarapida.dto.UsuarioRegistroDto;
import com.comidarapida.model.Administrador;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.model.ProductoComidaRapida;
import com.comidarapida.model.Usuario;
import com.comidarapida.model.Vendedor;
import com.comidarapida.repository.ProductoRepository;
import com.comidarapida.repository.UsuarioRepository;
import com.comidarapida.service.ProductoService;
import com.comidarapida.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final ProductoRepository productoRepository;
    private final Logger log = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UsuarioRepository usuarioRepository, UsuarioService usuarioService, ProductoService productoService, ProductoRepository productoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.productoRepository = productoRepository;
    }

    // Usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(@RequestParam(value = "tipo", required = false) String tipo, Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (tipo != null && !tipo.isBlank()) {
            String t = tipo.trim().toLowerCase();
            usuarios = usuarios.stream().filter(u -> {
                if (t.equals("administrador")) return u instanceof Administrador;
                if (t.equals("vendedor")) return u instanceof Vendedor;
                if (t.equals("encargadoinventario")) return u instanceof EncargadoInventario;
                return true;
            }).collect(Collectors.toList());
        }
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("currentTipo", tipo==null?"":tipo);
        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuarioForm", new UsuarioRegistroDto());
        return "usuario-form";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model, RedirectAttributes ra) {
        var opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
        Usuario u = opt.get();
        UsuarioRegistroDto dto = new UsuarioRegistroDto();
        dto.setId(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreoElectronico(u.getCorreoElectronico());
        dto.setDocumento(u.getDocumento());
        dto.setTelefono(u.getTelefono());
        if (u instanceof Administrador) dto.setTipo("administrador");
        else if (u instanceof Vendedor) dto.setTipo("vendedor");
        else if (u instanceof EncargadoInventario) dto.setTipo("encargadoinventario");
        else dto.setTipo("");
        model.addAttribute("usuarioForm", dto);
        return "usuario-form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuarioForm") UsuarioRegistroDto dto, RedirectAttributes ra) {
        try {
            // create or update
            Usuario u;
            if (dto.getId() != null) {
                u = usuarioRepository.findById(dto.getId()).orElse(null);
                if (u == null) {
                    ra.addFlashAttribute("error", "Usuario no encontrado");
                    return "redirect:/admin/usuarios";
                }
                // map dto tipo to discriminator value
                String newDisc = null;
                if (dto.getTipo() != null) {
                    String t = dto.getTipo().trim().toLowerCase();
                    if (t.equals("administrador")) newDisc = "Administrador";
                    else if (t.equals("vendedor")) newDisc = "Vendedor";
                    else if (t.equals("encargadoinventario") || t.equals("encargado_inventario") || t.equals("encargado-inventario")) newDisc = "EncargadoInventario";
                }
                String currentDisc = u.getClass().getSimpleName();
                if (newDisc != null && !newDisc.equals(currentDisc)) {
                    // change discriminator in DB, then reload entity as the new subtype
                    usuarioRepository.updateTipoUsuario(u.getIdUsuario(), newDisc);
                    u = usuarioRepository.findById(dto.getId()).orElse(u);
                }

                u.setNombre(dto.getNombre());
                u.setApellido(dto.getApellido());
                u.setDocumento(dto.getDocumento());
                u.setTelefono(dto.getTelefono());
                if (dto.getCorreoElectronico() != null) u.setCorreoElectronico(dto.getCorreoElectronico().trim().toLowerCase());
                if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) u.setContrasena(dto.getContrasena().trim());
                usuarioRepository.save(u);
                ra.addFlashAttribute("success", "Usuario actualizado");
            } else {
                String tipo = dto.getTipo()==null?"":dto.getTipo().trim().toLowerCase();
                switch (tipo) {
                    case "vendedor": u = new Vendedor(); break;
                    case "administrador": u = new Administrador(); break;
                    case "encargadoinventario":
                    case "encargado_inventario":
                    case "encargado-inventario": u = new EncargadoInventario(); break;
                    default:
                        ra.addFlashAttribute("error", "Tipo de usuario inválido");
                        return "redirect:/admin/usuarios/nuevo";
                }
                u.setNombre(dto.getNombre());
                u.setApellido(dto.getApellido());
                u.setDocumento(dto.getDocumento());
                u.setTelefono(dto.getTelefono());
                if (dto.getCorreoElectronico() != null) u.setCorreoElectronico(dto.getCorreoElectronico().trim().toLowerCase());
                if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
                    ra.addFlashAttribute("error", "La contraseña es requerida");
                    return "redirect:/admin/usuarios/nuevo";
                }
                u.setContrasena(dto.getContrasena().trim());
                usuarioService.registrar(u);
                ra.addFlashAttribute("success", "Usuario registrado");
            }
            return "redirect:/admin/usuarios";
        } catch (Exception ex) {
            log.warn("Error guardando usuario", ex);
            ra.addFlashAttribute("error", "Error al guardar usuario");
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/usuarios/{id}/desactivar")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes ra) {
        var opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
        Usuario u = opt.get();
        try {
            // mark inactive
            u.desactivar();
            usuarioRepository.save(u);
            ra.addFlashAttribute("success", "Usuario desactivado");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "No se pudo desactivar usuario");
        }
        return "redirect:/admin/usuarios";
    }

    // Productos
    @GetMapping("/productos")
    public String listarProductos(Model model) {
        List<ProductoComidaRapida> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        return "productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new ProductoComidaRapida());
        return "producto-form";
    }

    @GetMapping("/productos/{id}/editar")
    public String editarProducto(@PathVariable Long id, Model model, RedirectAttributes ra) {
        var opt = productoService.consultarProducto(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/admin/productos";
        }
        model.addAttribute("producto", opt.get());
        return "producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute ProductoComidaRapida producto, RedirectAttributes ra) {
        try {
            if (producto.getIdProducto() != null) {
                var opt = productoService.consultarProducto(producto.getIdProducto());
                if (opt.isPresent()) {
                    ProductoComidaRapida p = opt.get();
                    p.setCodigo(producto.getCodigo());
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    p.setActivo(producto.isActivo());
                    productoService.actualizarProducto(p);
                    ra.addFlashAttribute("success", "Producto actualizado");
                } else {
                    ra.addFlashAttribute("error", "Producto no encontrado");
                }
            } else {
                productoService.registrarProducto(producto);
                ra.addFlashAttribute("success", "Producto registrado");
            }
        } catch (Exception ex) {
            log.warn("Error guardando producto", ex);
            ra.addFlashAttribute("error", "Error al guardar producto");
        }
        return "redirect:/admin/productos";
    }

}
