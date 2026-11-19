package cl.tiendaJdr.TiendaJdrBackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cl.tiendaJdr.TiendaJdrBackend.dto.VentaSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.model.DetalleVenta;
import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import cl.tiendaJdr.TiendaJdrBackend.model.Usuario;
import cl.tiendaJdr.TiendaJdrBackend.model.Venta;
import cl.tiendaJdr.TiendaJdrBackend.repository.ProductoRepository;
import cl.tiendaJdr.TiendaJdrBackend.repository.UsuarioRepository;
import cl.tiendaJdr.TiendaJdrBackend.repository.VentaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class VentaServiceTest {

    private final VentaRepository ventas = mock(VentaRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final ProductoRepository productos = mock(ProductoRepository.class);
    private final VentaService servicio = new VentaService(ventas, usuarios, productos);

    @Test
    void listaLasVentasConSusDetalles() {
        when(ventas.findAll()).thenReturn(List.of(venta()));

        var listado = servicio.listarVentas();

        assertEquals(1, listado.size());
        assertEquals(2L, listado.getFirst().usuarioId());
        assertEquals(10000L, listado.getFirst().total());
        assertEquals(3L, listado.getFirst().detalles().getFirst().productoId());
        assertEquals(10000L, listado.getFirst().detalles().getFirst().precioUnitario());
    }

    @Test
    void buscaUnaVentaPorId() {
        when(ventas.findById(1L)).thenReturn(Optional.of(venta()));
        assertEquals(1L, servicio.buscarVenta(1L).id());
    }

    @Test
    void rechazaLaBusquedaSiLaVentaNoExiste() {
        assertThrows(NoSuchElementException.class, () -> servicio.buscarVenta(99L));
    }

    @Test
    void calculaElTotalConLosPreciosActualesYGuardaLosDetalles() {
        Usuario cliente = usuario();
        Producto primero = producto(3L, 10000L);
        Producto segundo = producto(8L, 7500L);
        when(usuarios.findById(2L)).thenReturn(Optional.of(cliente));
        when(productos.findAllById(List.of(3L, 8L))).thenReturn(List.of(primero, segundo));
        when(ventas.save(any(Venta.class))).thenAnswer(invocacion -> {
            Venta guardada = invocacion.getArgument(0);
            guardada.setId(1L);
            long detalleId = 1;
            for (var detalle : guardada.getDetalles()) {
                detalle.setId(detalleId++);
                assertSame(guardada, detalle.getVenta());
            }
            assertSame(cliente, guardada.getUsuario());
            assertNotNull(guardada.getFecha());
            return guardada;
        });

        var creada = servicio.crearVenta(new VentaSolicitud(2L, List.of(
            new VentaSolicitud.Detalle(3L, 2), new VentaSolicitud.Detalle(8L, 1))));

        assertEquals(1L, creada.id());
        assertEquals(27500L, creada.total());
        assertEquals(2, creada.detalles().size());
        assertEquals(3L, creada.detalles().getFirst().productoId());
        assertEquals(2, creada.detalles().getFirst().cantidad());
        assertEquals(10000L, creada.detalles().getFirst().precioUnitario());
        assertEquals(7500L, creada.detalles().getLast().precioUnitario());
        primero.setPrecio(99999L);
        assertEquals(10000L, creada.detalles().getFirst().precioUnitario());
    }

    @Test
    void rechazaLaVentaSiElUsuarioNoExiste() {
        assertThrows(NoSuchElementException.class, () -> servicio.crearVenta(solicitud()));
        verifyNoInteractions(productos, ventas);
    }

    @Test
    void rechazaLaVentaSiElProductoNoExiste() {
        when(usuarios.findById(2L)).thenReturn(Optional.of(usuario()));
        when(productos.findAllById(List.of(3L))).thenReturn(List.of());

        assertThrows(NoSuchElementException.class, () -> servicio.crearVenta(solicitud()));
        verify(ventas, never()).save(any());
    }

    @Test
    void rechazaLaVentaSiElProductoEstaInactivo() {
        Producto inactivo = producto(3L, 10000L);
        inactivo.setActivo(false);
        when(usuarios.findById(2L)).thenReturn(Optional.of(usuario()));
        when(productos.findAllById(List.of(3L))).thenReturn(List.of(inactivo));

        assertThrows(NoSuchElementException.class, () -> servicio.crearVenta(solicitud()));
        verify(ventas, never()).save(any());
    }

    @Test
    void noGuardaUnaVentaParcialSiUnaLineaNoEstaDisponible() {
        when(usuarios.findById(2L)).thenReturn(Optional.of(usuario()));
        when(productos.findAllById(List.of(3L, 99L)))
            .thenReturn(List.of(producto(3L, 10000L)));

        var datos = new VentaSolicitud(2L, List.of(
            new VentaSolicitud.Detalle(3L, 1), new VentaSolicitud.Detalle(99L, 1)));

        assertThrows(NoSuchElementException.class, () -> servicio.crearVenta(datos));
        verify(ventas, never()).save(any());
    }

    private VentaSolicitud solicitud() {
        return new VentaSolicitud(2L, List.of(new VentaSolicitud.Detalle(3L, 1)));
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        return usuario;
    }

    private Producto producto(Long id, Long precio) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setPrecio(precio);
        return producto;
    }

    private Venta venta() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario());
        venta.setFecha(OffsetDateTime.parse("2026-11-19T12:00:00Z"));
        venta.setTotal(10000L);
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1L);
        detalle.setVenta(venta);
        detalle.setProducto(producto(3L, 10000L));
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(10000L);
        venta.getDetalles().add(detalle);
        return venta;
    }
}
