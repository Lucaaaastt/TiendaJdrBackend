package cl.tiendaJdr.TiendaJdrBackend.repository;

import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
}
