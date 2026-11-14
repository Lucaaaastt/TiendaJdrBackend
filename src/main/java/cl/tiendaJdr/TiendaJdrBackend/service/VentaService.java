package cl.tiendaJdr.TiendaJdrBackend.service;

import cl.tiendaJdr.TiendaJdrBackend.dto.VentaDTO;
import cl.tiendaJdr.TiendaJdrBackend.dto.VentaSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.model.DetalleVenta;
import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import cl.tiendaJdr.TiendaJdrBackend.model.Venta;
import cl.tiendaJdr.TiendaJdrBackend.repository.ProductoRepository;
import cl.tiendaJdr.TiendaJdrBackend.repository.UsuarioRepository;
import cl.tiendaJdr.TiendaJdrBackend.repository.VentaRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<VentaDTO> listarVentas() {
        return ventaRepository.findAllByOrderByIdAsc().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public VentaDTO buscarVenta(Long id) {
        return ventaRepository.findById(id).map(this::aDto)
            .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + id));
    }

    @Transactional
    public VentaDTO crearVenta(VentaSolicitud datos) {
        var usuario = usuarioRepository.findById(datos.usuarioId())
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + datos.usuarioId()));
        var ids = datos.detalles().stream().map(VentaSolicitud.Detalle::productoId).distinct().toList();
        var productos = productoRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(Producto::getId, Function.identity()));

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS));
        long total = 0;

        for (var linea : datos.detalles()) {
            Producto producto = productos.get(linea.productoId());
            if (producto == null || !producto.isActivo()) {
                throw new NoSuchElementException("Producto no disponible: " + linea.productoId());
            }
            try {
                total = Math.addExact(total, Math.multiplyExact(producto.getPrecio(), (long) linea.cantidad()));
            } catch (ArithmeticException error) {
                throw new IllegalArgumentException("El total supera el importe máximo permitido", error);
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(linea.cantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            venta.getDetalles().add(detalle);
        }

        venta.setTotal(total);
        return aDto(ventaRepository.save(venta));
    }

    private VentaDTO aDto(Venta venta) {
        var detalles = venta.getDetalles().stream()
            .map(detalle -> new VentaDTO.Detalle(detalle.getId(), detalle.getProducto().getId(),
                detalle.getCantidad(), detalle.getPrecioUnitario()))
            .toList();
        return new VentaDTO(venta.getId(), venta.getUsuario().getId(), venta.getFecha(), venta.getTotal(), detalles);
    }
}
