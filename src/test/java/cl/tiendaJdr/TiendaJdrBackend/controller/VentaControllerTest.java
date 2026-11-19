package cl.tiendaJdr.TiendaJdrBackend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import cl.tiendaJdr.TiendaJdrBackend.model.Usuario;
import cl.tiendaJdr.TiendaJdrBackend.model.Venta;
import cl.tiendaJdr.TiendaJdrBackend.repository.ProductoRepository;
import cl.tiendaJdr.TiendaJdrBackend.repository.UsuarioRepository;
import cl.tiendaJdr.TiendaJdrBackend.repository.VentaRepository;
import cl.tiendaJdr.TiendaJdrBackend.service.VentaService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class VentaControllerTest {

    private final VentaRepository ventas = mock(VentaRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final ProductoRepository productos = mock(ProductoRepository.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(
        new VentaController(new VentaService(ventas, usuarios, productos))).build();

    @BeforeEach
    void prepararUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        when(usuarios.findById(2L)).thenReturn(Optional.of(usuario));
    }

    @Test
    void respondeBadRequestSiElSubtotalSuperaElLimiteSinGuardarLaVenta() throws Exception {
        when(productos.findAllById(List.of(3L)))
            .thenReturn(List.of(producto(3L, Long.MAX_VALUE)));

        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content("""
            {"usuarioId":2,"detalles":[{"productoId":3,"cantidad":2}]}
            """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("El importe de la venta supera el límite permitido"));

        verify(ventas, never()).save(any());
    }

    @Test
    void respondeBadRequestSiLaSumaDeSubtotalesSuperaElLimiteSinGuardarLaVenta() throws Exception {
        when(productos.findAllById(List.of(3L, 8L)))
            .thenReturn(List.of(producto(3L, Long.MAX_VALUE), producto(8L, 1L)));

        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content("""
            {"usuarioId":2,"detalles":[{"productoId":3,"cantidad":1},{"productoId":8,"cantidad":1}]}
            """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("El importe de la venta supera el límite permitido"));

        verify(ventas, never()).save(any());
    }

    @Test
    void permiteUnaVentaCuyoImporteEstaJustoEnElLimite() throws Exception {
        when(productos.findAllById(List.of(3L)))
            .thenReturn(List.of(producto(3L, Long.MAX_VALUE)));
        when(ventas.save(any(Venta.class))).thenAnswer(invocacion -> {
            Venta guardada = invocacion.getArgument(0);
            guardada.setId(1L);
            return guardada;
        });

        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content("""
            {"usuarioId":2,"detalles":[{"productoId":3,"cantidad":1}]}
            """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.total").value(Long.MAX_VALUE));

        verify(ventas).save(any(Venta.class));
    }

    private Producto producto(Long id, Long precio) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setPrecio(precio);
        return producto;
    }
}
