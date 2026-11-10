package cl.tiendaJdr.TiendaJdrBackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(repositorio.findAll()).thenReturn(List.of(producto(7L, "Fallout 4")));

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
        assertEquals(7L, servicio.crearProducto(producto(null, "Fallout 4")).id());
    }

    @Test
    void creaConLosDatosRecibidosEIgnoraElIdEnviado() {
        Producto datos = producto(99L, "Fallout 4");
        when(repositorio.save(any(Producto.class))).thenReturn(producto(7L, "Fallout 4"));
        servicio.crearProducto(datos);
        var captor = ArgumentCaptor.forClass(Producto.class);
        verify(repositorio).save(captor.capture());
        Producto guardado = captor.getValue();
        assertNull(guardado.getId());
        assertEquals(datos.getNombre(), guardado.getNombre());
        assertEquals(datos.getDescripcion(), guardado.getDescripcion());
        assertEquals(datos.getPrecio(), guardado.getPrecio());
        assertEquals(datos.getImagenUrl(), guardado.getImagenUrl());
    }

    @Test
    void actualizaElProductoConservandoSuId() {
        Producto existente = producto(7L, "Fallout 4");
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);
        var actualizado = servicio.actualizarProducto(7L, producto(99L, "Fallout 4 GOTY"));
        assertEquals(7L, actualizado.id());
        assertEquals("Fallout 4 GOTY", actualizado.nombre());
        verify(repositorio).save(existente);
    }

    @Test
    void rechazaLaActualizacionSiElProductoNoExiste() {
        when(repositorio.findById(7L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
            () -> servicio.actualizarProducto(7L, producto(null, "Fallout 4")));
        verify(repositorio, never()).save(any(Producto.class));
    }

    @Test
    void eliminaElProductoEncontrado() {
        Producto existente = producto(7L, "Fallout 4");
        when(repositorio.findById(7L)).thenReturn(Optional.of(existente));
        servicio.eliminarProducto(7L);
        verify(repositorio).delete(existente);
    }

    @Test
    void rechazaLaEliminacionSiElProductoNoExiste() {
        when(repositorio.findById(7L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> servicio.eliminarProducto(7L));
        verify(repositorio, never()).delete(any(Producto.class));
    }

    private Producto producto(Long id, String nombre) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion("Juego de rol");
        producto.setPrecio(13900);
        producto.setImagenUrl("/Image/fl4.webp");
        return producto;
    }
}
