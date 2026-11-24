package cl.tiendaJdr.TiendaJdrBackend.repository;

import cl.tiendaJdr.TiendaJdrBackend.model.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByUsuarioIdOrderByFechaDescIdDesc(Long usuarioId);
}
