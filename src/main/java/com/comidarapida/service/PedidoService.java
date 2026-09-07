package com.comidarapida.service;

import com.comidarapida.model.Cliente;
import com.comidarapida.model.Pedido;
import com.comidarapida.model.Vendedor;
import com.comidarapida.model.Cliente;
import com.comidarapida.model.DetallePedido;
import com.comidarapida.model.MateriaPrima;
import com.comidarapida.model.MovimientoInventario;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.model.Pedido;
import com.comidarapida.model.ProductoComidaRapida;
import com.comidarapida.model.ComposicionProducto;
import com.comidarapida.model.Vendedor;
import com.comidarapida.repository.PedidoRepository;
import com.comidarapida.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final InventarioService inventarioService;

    public PedidoService(PedidoRepository pedidoRepository, InventarioService inventarioService) {
        this.pedidoRepository = pedidoRepository;
        this.inventarioService = inventarioService;
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

    @Transactional
    public void confirmarYAplicarInventario(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        // validar disponibilidad total por materia prima
        for (DetallePedido detalle : pedido.getDetalles()) {
            ProductoComidaRapida producto = detalle.getProducto();
            int cantidadProducto = detalle.getCantidad();
            for (ComposicionProducto cp : producto.getComposiciones()) {
                MateriaPrima materia = cp.getMateriaPrima();
                int requerido = cp.getCantidadMateriaPrima() * cantidadProducto;
                if (!materia.hayStock(requerido)) {
                    throw new IllegalArgumentException("Stock insuficiente para producto '" + producto.getNombre() + "' (se requieren " + requerido + ")");
                }
            }
        }

        // aplicar salidas en inventario
        // Usar el vendedor del pedido como encargado id si existe
        EncargadoInventario encargado = new EncargadoInventario();
        if (pedido.getVendedor() != null && pedido.getVendedor().getIdUsuario() != null) {
            encargado.setIdUsuario(pedido.getVendedor().getIdUsuario());
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            ProductoComidaRapida producto = detalle.getProducto();
            int cantidadProducto = detalle.getCantidad();
            for (ComposicionProducto cp : producto.getComposiciones()) {
                MateriaPrima materia = cp.getMateriaPrima();
                int requerido = cp.getCantidadMateriaPrima() * cantidadProducto;
                // registrar salida por cada materia
                inventarioService.registrarSalida(materia, requerido, encargado);
            }
        }

        // calcular total y confirmar pedido
        pedido.calcularTotal();
        pedido.setConfirmado(true);
        pedidoRepository.save(pedido);
    }
}
