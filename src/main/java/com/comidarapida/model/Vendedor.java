package com.comidarapida.model;

import jakarta.persistence.Entity;

@Entity
public class Vendedor extends Usuario {

    public Pedido crearPedido(Cliente cliente) {
        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setFecha(new java.util.Date());
        return p;
    }

    public void agregarProducto(Pedido pedido, ProductoComidaRapida producto, int cantidad) {
        DetallePedido d = new DetallePedido();
        d.setProducto(producto);
        d.setCantidad(cantidad);
        d.setPrecioUnitario(producto.getPrecio());
        pedido.addDetalle(d);
    }

    public void agregarAdicion(Pedido pedido, ProductoComidaRapida adicion, int cantidad) {
        // En este modelo una adición se trata como un DetallePedido con un flag implícito; por simplicidad se reutiliza agregarProducto
        agregarProducto(pedido, adicion, cantidad);
    }

    public void confirmarPedido(Pedido pedido) {
        pedido.calcularTotal();
        pedido.setConfirmado(true);
    }
}
