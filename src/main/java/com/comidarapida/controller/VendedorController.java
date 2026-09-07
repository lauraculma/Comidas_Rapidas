package com.comidarapida.controller;

import com.comidarapida.dto.ClienteDto;
import com.comidarapida.dto.DetallePedidoDto;
import com.comidarapida.dto.PedidoDto;
import com.comidarapida.model.*;
import com.comidarapida.repository.ClienteRepository;
import com.comidarapida.repository.ProductoRepository;
import com.comidarapida.repository.DetallePedidoRepository;
import com.comidarapida.repository.PedidoRepository;
import com.comidarapida.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendedor")
public class VendedorController {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PedidoService pedidoService;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;

    public VendedorController(ClienteRepository clienteRepository, ProductoRepository productoRepository, PedidoService pedidoService, DetallePedidoRepository detallePedidoRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.pedidoService = pedidoService;
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/clientes")
    public ResponseEntity<ClienteDto> registrarCliente(@RequestBody ClienteDto dto) {
        Cliente c = new Cliente();
        c.setNombre(dto.nombre);
        c.setApellido(dto.apellido);
        c.setCorreoElectronico(dto.correoElectronico);
        c.setDocumento(dto.documento);
        Cliente saved = clienteRepository.save(c);
        dto.id = saved.getIdCliente();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/clientes/{documento}")
    public ResponseEntity<?> consultarCliente(@PathVariable String documento) {
        var opt = clienteRepository.findByDocumento(documento);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Cliente c = opt.get();
        ClienteDto dto = new ClienteDto();
        dto.id = c.getIdCliente();
        dto.nombre = c.getNombre();
        dto.apellido = c.getApellido();
        dto.correoElectronico = c.getCorreoElectronico();
        dto.documento = c.getDocumento();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/pedidos")
    public ResponseEntity<PedidoDto> crearPedido(@RequestParam Long clienteId, @RequestParam Long vendedorId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Vendedor vendedor = new Vendedor();
        vendedor.setIdUsuario(vendedorId);
        Pedido p = pedidoService.crearPedido(cliente, vendedor);
        PedidoDto dto = new PedidoDto();
        dto.id = p.getIdPedido();
        dto.fecha = p.getFecha();
        dto.total = p.getTotal();
        return ResponseEntity.ok(dto);
    }

    @Transactional
    @PostMapping("/pedidos/{pedidoId}/detalles")
    public ResponseEntity<?> agregarDetalle(@PathVariable Long pedidoId, @RequestBody DetallePedidoDto dto) {
        Pedido p = pedidoService.consultar(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        ProductoComidaRapida prod = productoRepository.findById(dto.productoId).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        DetallePedido d = new DetallePedido();
        d.setProducto(prod);
        d.setCantidad(dto.cantidad);
        // Use provided precioUnitario if present (> 0), otherwise default to product price
        if (dto.precioUnitario > 0) d.setPrecioUnitario(dto.precioUnitario);
        else d.setPrecioUnitario(prod.getPrecio());
        // associate and persist detail explicitly to avoid cascade re-insert issues
        d.setPedido(p);
        var savedDetalle = detallePedidoRepository.save(d);
        // add to pedido collection if not present
        boolean exists = p.getDetalles().stream().anyMatch(x -> x.getIdDetalle() != null && x.getIdDetalle().equals(savedDetalle.getIdDetalle()));
        if (!exists) p.getDetalles().add(savedDetalle);
        // recalculate total and save pedido
        p.calcularTotal();
        pedidoRepository.save(p);
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", savedDetalle.getIdDetalle());
        resp.put("productoId", savedDetalle.getProducto().getIdProducto());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/pedidos/{pedidoId}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long pedidoId) {
        // Delegar a servicio transaccional que valida disponibilidad, registra salidas y confirma pedido
        pedidoService.confirmarYAplicarInventario(pedidoId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/pedidos/{pedidoId}/detalles/{detalleId}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Long pedidoId, @PathVariable Long detalleId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        var opt = detallePedidoRepository.findById(detalleId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        DetallePedido detalle = opt.get();
        if (detalle.getPedido() == null || detalle.getPedido().getIdPedido() == null || !detalle.getPedido().getIdPedido().equals(pedidoId)) {
            return ResponseEntity.status(400).body("El detalle no pertenece al pedido especificado");
        }
        // remove from in-memory collection (orphanRemoval will delete on save)
        boolean removed = pedido.getDetalles().removeIf(d -> d.getIdDetalle() != null && d.getIdDetalle().equals(detalleId));
        if (!removed) {
            // fallback: ensure relation cleared
            detalle.setPedido(null);
        }
        // recalculate total and persist pedido — orphanRemoval will remove the child
        pedido.calcularTotal();
        pedidoRepository.save(pedido);
        return ResponseEntity.ok().build();
    }
}
