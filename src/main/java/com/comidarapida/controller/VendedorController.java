package com.comidarapida.controller;

import com.comidarapida.dto.ClienteDto;
import com.comidarapida.dto.DetallePedidoDto;
import com.comidarapida.dto.PedidoDto;
import com.comidarapida.model.*;
import com.comidarapida.repository.ClienteRepository;
import com.comidarapida.repository.ProductoRepository;
import com.comidarapida.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendedor")
public class VendedorController {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PedidoService pedidoService;

    public VendedorController(ClienteRepository clienteRepository, ProductoRepository productoRepository, PedidoService pedidoService) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.pedidoService = pedidoService;
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

    @PostMapping("/pedidos/{pedidoId}/detalles")
    public ResponseEntity<?> agregarDetalle(@PathVariable Long pedidoId, @RequestBody DetallePedidoDto dto) {
        Pedido p = pedidoService.consultar(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        ProductoComidaRapida prod = productoRepository.findById(dto.productoId).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        DetallePedido d = new DetallePedido();
        d.setProducto(prod);
        d.setCantidad(dto.cantidad);
        d.setPrecioUnitario(prod.getPrecio());
        p.addDetalle(d);
        pedidoService.guardarPedido(p);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pedidos/{pedidoId}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long pedidoId) {
        Pedido p = pedidoService.consultar(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        Vendedor vendedor = new Vendedor();
        // No se autentica aquí; se asume ya autenticado en capa superior
        vendedor.confirmarPedido(p);
        pedidoService.guardarPedido(p);
        return ResponseEntity.ok().build();
    }
}
