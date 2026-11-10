package cl.tiendaJdr.TiendaJdrBackend.dto;


public record ProductoDTO(
    Long id,
    String nombre,
    String descripcion,
    int precio,
    String imagenUrl
) {}