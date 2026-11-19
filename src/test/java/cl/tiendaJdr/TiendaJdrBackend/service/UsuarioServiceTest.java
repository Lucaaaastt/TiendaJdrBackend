package cl.tiendaJdr.TiendaJdrBackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cl.tiendaJdr.TiendaJdrBackend.dto.UsuarioSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.model.Usuario;
import cl.tiendaJdr.TiendaJdrBackend.repository.UsuarioRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UsuarioServiceTest {

    private final UsuarioRepository repositorio = mock(UsuarioRepository.class);
    private final UsuarioService servicio = new UsuarioService(repositorio);

    @Test
    void listaLosUsuariosConSuRol() {
        when(repositorio.findAll()).thenReturn(List.of(usuario()));

        var usuarios = servicio.listarUsuarios();

        assertEquals(1, usuarios.size());
        assertEquals("cliente@ejemplo.test", usuarios.getFirst().correo());
        assertEquals(Usuario.Rol.CLIENTE, usuarios.getFirst().rol());
    }

    @Test
    void buscaUnUsuarioPorId() {
        when(repositorio.findById(2L)).thenReturn(Optional.of(usuario()));
        assertEquals(2L, servicio.buscarUsuario(2L).id());
    }

    @Test
    void rechazaLaBusquedaSiElUsuarioNoExiste() {
        assertThrows(NoSuchElementException.class, () -> servicio.buscarUsuario(99L));
    }

    @Test
    void creaElUsuarioConRolClienteYDevuelveElIdGenerado() {
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario guardado = invocacion.getArgument(0);
            assertNull(guardado.getId());
            guardado.setId(2L);
            return guardado;
        });

        var creado = servicio.crearUsuario(solicitud());

        var captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorio).save(captor.capture());
        assertEquals("clave-prueba", captor.getValue().getContrasena());
        assertEquals("Cliente", creado.nombre());
        assertEquals("cliente@ejemplo.test", creado.correo());
        assertEquals(2L, creado.id());
        assertEquals(Usuario.Rol.CLIENTE, creado.rol());
    }

    @Test
    void actualizaLosDatosConservandoElIdYElRol() {
        Usuario existente = usuario();
        existente.setRol(Usuario.Rol.ADMIN);
        when(repositorio.findById(2L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);

        var actualizado = servicio.actualizarUsuario(2L,
            new UsuarioSolicitud("Nombre nuevo", "nuevo@ejemplo.test", "otra-clave"));

        assertEquals(2L, actualizado.id());
        assertEquals(Usuario.Rol.ADMIN, actualizado.rol());
        assertEquals("Nombre nuevo", actualizado.nombre());
        assertEquals("nuevo@ejemplo.test", actualizado.correo());
        assertEquals("otra-clave", existente.getContrasena());
    }

    @Test
    void rechazaLaActualizacionSiElUsuarioNoExiste() {
        assertThrows(NoSuchElementException.class,
            () -> servicio.actualizarUsuario(99L, solicitud()));
        verify(repositorio, never()).save(any());
    }

    @Test
    void eliminaUnUsuarioSinVentas() {
        Usuario existente = usuario();
        when(repositorio.buscarParaEliminar(2L)).thenReturn(Optional.of(existente));

        assertTrue(servicio.eliminarUsuario(2L));
        verify(repositorio).delete(existente);
    }

    @Test
    void conservaElUsuarioCuandoTieneVentas() {
        when(repositorio.buscarParaEliminar(2L)).thenReturn(Optional.of(usuario()));
        when(repositorio.tieneVentas(2L)).thenReturn(true);

        assertFalse(servicio.eliminarUsuario(2L));
        verify(repositorio, never()).delete(any());
    }

    @Test
    void rechazaLaEliminacionSiElUsuarioNoExiste() {
        assertThrows(NoSuchElementException.class, () -> servicio.eliminarUsuario(99L));
        verify(repositorio, never()).delete(any());
    }

    @Test
    void normalizaElCorreoAntesDeConsultarYGuardar() {
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario guardado = invocacion.getArgument(0);
            guardado.setId(2L);
            return guardado;
        });

        var creado = servicio.crearUsuario(
            new UsuarioSolicitud("Cliente", "  CLIENTE@Ejemplo.Test  ", "clave-prueba"));

        assertEquals("cliente@ejemplo.test", creado.correo());
        verify(repositorio).findByCorreoIgnoreCase("cliente@ejemplo.test");
    }

    @Test
    void rechazaLaCreacionConCorreoDuplicado() {
        when(repositorio.findByCorreoIgnoreCase("cliente@ejemplo.test"))
            .thenReturn(Optional.of(usuario()));

        var error = assertThrows(IllegalArgumentException.class,
            () -> servicio.crearUsuario(solicitud()));

        assertEquals("El correo ya está registrado", error.getMessage());
        verify(repositorio, never()).save(any());
    }

    @Test
    void permiteEditarConservandoElCorreoPropio() {
        Usuario existente = usuario();
        when(repositorio.findById(2L)).thenReturn(Optional.of(existente));
        when(repositorio.findByCorreoIgnoreCase("cliente@ejemplo.test"))
            .thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);

        var actualizado = servicio.actualizarUsuario(2L,
            new UsuarioSolicitud("Nombre nuevo", " CLIENTE@EJEMPLO.TEST ", "otra-clave"));

        assertEquals("Nombre nuevo", actualizado.nombre());
        assertEquals("cliente@ejemplo.test", actualizado.correo());
        verify(repositorio).save(existente);
    }

    @Test
    void rechazaElCorreoDeOtroUsuarioSinModificarElExistente() {
        Usuario existente = usuario();
        Usuario otro = usuario();
        otro.setId(3L);
        otro.setCorreo("otro@ejemplo.test");
        when(repositorio.findById(2L)).thenReturn(Optional.of(existente));
        when(repositorio.findByCorreoIgnoreCase("otro@ejemplo.test"))
            .thenReturn(Optional.of(otro));

        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarUsuario(2L,
            new UsuarioSolicitud("Nombre nuevo", " OTRO@EJEMPLO.TEST ", "otra-clave")));

        assertEquals("Cliente", existente.getNombre());
        assertEquals("cliente@ejemplo.test", existente.getCorreo());
        assertEquals("clave-prueba", existente.getContrasena());
        verify(repositorio, never()).save(any());
    }

    private UsuarioSolicitud solicitud() {
        return new UsuarioSolicitud("Cliente", "cliente@ejemplo.test", "clave-prueba");
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNombre("Cliente");
        usuario.setCorreo("cliente@ejemplo.test");
        usuario.setContrasena("clave-prueba");
        return usuario;
    }
}
