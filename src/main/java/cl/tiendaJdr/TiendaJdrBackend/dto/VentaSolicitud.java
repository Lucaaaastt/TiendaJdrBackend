package cl.tiendaJdr.TiendaJdrBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record VentaSolicitud(
    @NotNull(message = "El usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser mayor que cero")
    Long usuarioId,

    @NotEmpty(message = "La venta debe incluir al menos un producto")
    List<@NotNull(message = "El detalle es obligatorio") @Valid Detalle> detalles
) {
    public record Detalle(
        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El ID del producto debe ser mayor que cero")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        Integer cantidad
    ) {}
}
