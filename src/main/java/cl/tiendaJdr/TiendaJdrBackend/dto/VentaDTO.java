package cl.tiendaJdr.TiendaJdrBackend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record VentaDTO(
    Long id,
    Long usuarioId,
    OffsetDateTime fecha,
    Long total,
    List<Detalle> detalles
) {
    public record Detalle(Long id, Long productoId, Integer cantidad, Long precioUnitario) {}
}
