package cl.tiendaJdr.TiendaJdrBackend.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ventas")
@Getter
@Setter
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    @Column(nullable = false)
    private Long total;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.PERSIST)
    @OrderBy("id ASC")
    private List<DetalleVenta> detalles = new ArrayList<>();
}
