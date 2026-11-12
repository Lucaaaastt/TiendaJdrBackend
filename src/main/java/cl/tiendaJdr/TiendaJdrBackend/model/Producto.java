package cl.tiendaJdr.TiendaJdrBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false)
    private Long precio;

    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private boolean novedad = false;

}
