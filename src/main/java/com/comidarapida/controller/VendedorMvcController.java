package com.comidarapida.controller;

import com.comidarapida.model.Cliente;
import com.comidarapida.service.ClienteService;
import com.comidarapida.repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VendedorMvcController {

    private final ClienteService clienteService;
    private final PedidoRepository pedidoRepository;

    public VendedorMvcController(ClienteService clienteService, PedidoRepository pedidoRepository) {
        this.clienteService = clienteService;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/vendedor/clientes/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        var saved = clienteService.guardarCliente(cliente);
        redirect.addFlashAttribute("success", "Cliente registrado");
        // redirigir a nueva venta y pasar clienteId para que pueda usarse inmediatamente
        return "redirect:/vendedor/venta/nueva?clienteId=" + saved.getIdCliente();
    }

    @GetMapping("/vendedor/clientes/nuevo")
    public String nuevoCliente(Model model, @RequestParam(value = "documento", required = false) String documento) {
        Cliente c = new Cliente();
        if (documento != null) c.setDocumento(documento);
        model.addAttribute("cliente", c);
        return "cliente-form";
    }

    @GetMapping("/vendedor/clientes")
    public String listarClientes(Model model) {
        var clientes = clienteService.listarTodos();
        model.addAttribute("clientes", clientes);
        model.addAttribute("active", "clientes");
        return "clientes";
    }

    @GetMapping("/vendedor/clientes/{id}/historial")
    public String verHistorial(@PathVariable("id") Long id, Model model) {
        var pedidos = pedidoRepository.findByCliente_IdClienteOrderByFechaDesc(id);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("clienteId", id);
        model.addAttribute("active", "clientes");
        return "cliente-historial";
    }
}