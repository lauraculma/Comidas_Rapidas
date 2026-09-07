package com.comidarapida.service;

import com.comidarapida.model.Cliente;
import com.comidarapida.model.Pedido;
import com.comidarapida.model.Vendedor;
import com.comidarapida.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido crearPedido(Cliente cliente, Vendedor vendedor) {
        Vendedor vend = vendedor; // se asume ya instanciado por el controlador/servicio superior
        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setVendedor(vend);
        p.setFecha(new java.util.Date());
        return pedidoRepository.save(p);
    }

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> consultar(Long id) {
        return pedidoRepository.findById(id);
    }
}
