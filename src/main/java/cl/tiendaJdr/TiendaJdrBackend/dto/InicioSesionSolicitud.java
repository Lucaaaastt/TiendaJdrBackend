package cl.tiendaJdr.TiendaJdrBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record InicioSesionSolicitud(
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede superar los 255 caracteres")
    String contrasena
) {
    public InicioSesionSolicitud {
        if (correo != null) {
            correo = correo.trim().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public String toString() {
        return "InicioSesionSolicitud[correo=" + correo + "]";
    }
}
