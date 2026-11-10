package cl.tiendaJdr.TiendaJdrBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 150, message = "La descripción no puede superar los 150 caracteres")
    private String descripcion;

    @Positive(message = "El precio debe ser mayor que cero")
    private int precio;

    @NotBlank(message = "La imagen es obligatoria")
    @Size(max = 255, message = "La ruta de la imagen no puede superar los 255 caracteres")
    private String imagenUrl;

}
