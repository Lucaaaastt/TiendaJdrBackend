package cl.tiendaJdr.TiendaJdrBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductoSolicitud(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    String nombre,

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    String descripcion,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    Long precio,

    @NotBlank(message = "La imagen es obligatoria")
    @Size(max = 500, message = "La ruta de la imagen no puede superar los 500 caracteres")
    String imagenUrl,

    Boolean activo,
    Boolean novedad
) {}
