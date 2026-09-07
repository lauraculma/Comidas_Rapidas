package com.comidarapida.dto;

import java.util.Date;
import java.util.List;

public class PedidoDto {
    public Long id;
    public Date fecha;
    public List<DetallePedidoDto> detalles;
    public double total;
    public Long clienteId;
    public Long vendedorId;
}
