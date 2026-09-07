package com.comidarapida.controller;

import com.comidarapida.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VendedorClienteController {

    private final ClienteService clienteService;

    public VendedorClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/vendedor/clientes/buscar")
    public String buscarCliente(@RequestParam("documento") String documento) {
        var opt = clienteService.buscarPorDocumento(documento);
        if (opt.isPresent()) {
            return "redirect:/vendedor/venta/nueva?clienteId=" + opt.get().getIdCliente();
        } else {
            // no encontrado, redirect to new cliente form
            return "redirect:/vendedor/clientes/nuevo?documento=" + documento;
        }
    }
}