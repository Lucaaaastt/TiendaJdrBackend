package cl.tiendaJdr.TiendaJdrBackend.repository;

import cl.tiendaJdr.TiendaJdrBackend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}
