package cl.tiendaJdr.TiendaJdrBackend.repository;

import cl.tiendaJdr.TiendaJdrBackend.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT * FROM usuarios WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Usuario> buscarParaEliminar(Long id);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM ventas WHERE usuario_id = :id)", nativeQuery = true)
    boolean tieneVentas(Long id);
}
