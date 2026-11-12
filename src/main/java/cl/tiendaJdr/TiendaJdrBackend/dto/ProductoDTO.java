package cl.tiendaJdr.TiendaJdrBackend.dto;


public record ProductoDTO(
    Long id,
    String nombre,
    String descripcion,
    Long precio,
    String imagenUrl,
    boolean activo,
    boolean novedad
) {}