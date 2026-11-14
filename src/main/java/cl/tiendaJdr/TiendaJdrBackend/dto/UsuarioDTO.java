package cl.tiendaJdr.TiendaJdrBackend.dto;

import cl.tiendaJdr.TiendaJdrBackend.model.Usuario.Rol;

public record UsuarioDTO(Long id, String nombre, String correo, Rol rol) {}
