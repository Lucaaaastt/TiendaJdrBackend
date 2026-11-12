package cl.tiendaJdr.TiendaJdrBackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cl.tiendaJdr.TiendaJdrBackend.dto.ProductoSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import cl.tiendaJdr.TiendaJdrBackend.repository.ProductoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ProductoServiceTest {

    private final ProductoRepository repositorio = mock(ProductoRepository.class);
    private final ProductoService servicio = new ProductoService(repositorio);

    @Test
    void listaLosProductos() {
        when(repositorio.findByActivoTrue()).thenReturn(List.of(producto(7L, "Fallout 4")));

        var productos = servicio.listarProductos();
        assertEquals(1, productos.size());
        assertEquals("Fallout 4", productos.getFirst().nombre());
    }

    @Test
    void buscaUnProductoPorId() {
        when(repositorio.findById(7L)).thenReturn(Optional.of(producto(7L, "Fallout 4")));
        assertEquals(7L, servicio.buscarProducto(7L).id());
    }

    @Test
    void devuelveElIdGeneradoAlCrear() {
        when(repositorio.save(any(Producto.class))).thenReturn(producto(7L, "Fallout 4"));
        assertEquals(7L, servicio.crearProducto(solicitud("Fallout 4", null, null)).id());
    }

    @Test
    void creaConLosDatosRecibidosYGeneraElId() {
        ProductoSolicitud datos = solicitud("Fallout 4", null, true);
        when(repositorio.save(any(Producto.class))).thenReturn(producto(7L, "Fallout 4"));
        servicio.crearProducto(datos);
        var captor = ArgumentCaptor.forClass(Producto.class);
        verify(repositorio).save(captor.capture());
        Producto guardado = captor.getValue();
        assertNull(guardado.getId());
        assertTrue(guardado.isActivo());
        assertTrue(guardado.isNovedad());
        assertEquals(datos.nombre(), guardado.getNombre());
        assertEquals(datos.descripcion(), guardado.getDescripcion());
        assertEquals(datos.precio(), guardado.getPrecio());
        assertEquals(datos.imagenUrl(), guardado.getImagenUrl());
    }

    @Test
    void actualizaElProductoConservandoSuId() {
        Producto existente = producto(7L, "Fallout 4");
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);
        var actualizado = servicio.actualizarProducto(7L, solicitud("Fallout 4 GOTY", null, null));
        assertEquals(7L, actualizado.id());
        assertEquals("Fallout 4 GOTY", actualizado.nombre());
        verify(repositorio).save(existente);
    }

    @Test
    void rechazaLaActualizacionSiElProductoNoExiste() {
        when(repositorio.findById(7L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
            () -> servicio.actualizarProducto(7L, solicitud("Fallout 4", null, null)));
        verify(repositorio, never()).save(any(Producto.class));
    }

    @Test
    void desactivaElProductoEncontrado() {
        Producto existente = producto(7L, "Fallout 4");
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));
        servicio.eliminarProducto(7L);
        assertFalse(existente.isActivo());
        verify(repositorio).save(existente);
        verify(repositorio, never()).delete(any(Producto.class));
    }

    @Test
    void rechazaLaEliminacionSiElProductoNoExiste() {
        when(repositorio.findById(7L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> servicio.eliminarProducto(7L));
        verify(repositorio, never()).delete(any(Producto.class));
    }

    @Test
    void ocultaUnProductoInactivoEnLaConsultaPublica() {
        Producto inactivo = producto(7L, "Fallout 4");
        inactivo.setActivo(false);
        when(repositorio.findById(7L)).thenReturn(Optional.of(inactivo));
        assertThrows(NoSuchElementException.class, () -> servicio.buscarProducto(7L));
    }

    @Test
    void conservaLosIndicadoresCuandoLaEdicionLosOmite() {
        Producto existente = producto(7L, "Fallout 4");
        existente.setActivo(false);
        existente.setNovedad(true);
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);

        var actualizado = servicio.actualizarProducto(7L, solicitud("Fallout 4 GOTY", null, null));

        assertFalse(actualizado.activo());
        assertTrue(actualizado.novedad());
    }

    @Test
    void permiteCambiarLosIndicadoresExplicitamente() {
        Producto existente = producto(7L, "Fallout 4");
        existente.setNovedad(true);
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);

        var actualizado = servicio.actualizarProducto(7L, solicitud("Fallout 4", false, false));

        assertFalse(actualizado.activo());
        assertFalse(actualizado.novedad());
    }

    private ProductoSolicitud solicitud(String nombre, Boolean activo, Boolean novedad) {
        return new ProductoSolicitud(nombre, "Juego de rol", 3_000_000_000L,
            "/Image/fl4.webp", activo, novedad);
    }

    private Producto producto(Long id, String nombre) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion("Juego de rol");
        producto.setPrecio(13900L);
        producto.setImagenUrl("/Image/fl4.webp");
        return producto;
    }
}
