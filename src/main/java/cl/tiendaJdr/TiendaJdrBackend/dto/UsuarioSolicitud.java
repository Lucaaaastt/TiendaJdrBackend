package cl.tiendaJdr.TiendaJdrBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioSolicitud(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String nombre,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede superar los 255 caracteres")
    String contrasena
) {
    @Override
    public String toString() {
        return "UsuarioSolicitud[nombre=" + nombre + ", correo=" + correo + "]";
    }
}
