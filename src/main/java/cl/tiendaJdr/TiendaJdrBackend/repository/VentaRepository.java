package cl.tiendaJdr.TiendaJdrBackend.repository;

import cl.tiendaJdr.TiendaJdrBackend.model.Venta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = "detalles")
    List<Venta> findAllByOrderByIdAsc();

    @Override
    @EntityGraph(attributePaths = "detalles")
    Optional<Venta> findById(Long id);
}
