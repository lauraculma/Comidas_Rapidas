package com.comidarapida.repository;

import com.comidarapida.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    java.util.List<com.comidarapida.model.Pedido> findByCliente_IdClienteOrderByFechaDesc(Long clienteId);
}
