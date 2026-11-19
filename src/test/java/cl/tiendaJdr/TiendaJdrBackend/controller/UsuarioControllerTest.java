package cl.tiendaJdr.TiendaJdrBackend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.tiendaJdr.TiendaJdrBackend.repository.UsuarioRepository;
import cl.tiendaJdr.TiendaJdrBackend.model.Usuario;
import cl.tiendaJdr.TiendaJdrBackend.service.UsuarioService;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UsuarioControllerTest {

    private final UsuarioRepository repositorio = mock(UsuarioRepository.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(
        new UsuarioController(new UsuarioService(repositorio))).build();

    @Test
    void respondeConflictoAlCrearUnCorreoDuplicadoConMayusculasYEspacios() throws Exception {
        Usuario existente = new Usuario();
        existente.setId(2L);
        when(repositorio.findByCorreoIgnoreCase("cliente@ejemplo.test"))
            .thenReturn(Optional.of(existente));

        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
            {"nombre":"Cliente","correo":" CLIENTE@Ejemplo.Test ","contrasena":"clave-prueba"}
            """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value("El correo ya está registrado"));

        verify(repositorio, never()).save(any());
    }

    @Test
    void respondeConflictoAlEditarConElCorreoDeOtroUsuario() throws Exception {
        Usuario existente = new Usuario();
        existente.setId(2L);
        Usuario otro = new Usuario();
        otro.setId(3L);
        when(repositorio.findById(2L)).thenReturn(Optional.of(existente));
        when(repositorio.findByCorreoIgnoreCase("otro@ejemplo.test"))
            .thenReturn(Optional.of(otro));

        mvc.perform(put("/api/usuarios/2").contentType(MediaType.APPLICATION_JSON).content("""
            {"nombre":"Cliente","correo":"otro@ejemplo.test","contrasena":"clave-prueba"}
            """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value("El correo ya está registrado"));

        verify(repositorio, never()).save(any());
    }

    @Test
    void respondeConflictoSiLaBaseDetectaElDuplicadoAlGuardar() throws Exception {
        when(repositorio.save(any())).thenThrow(new DataIntegrityViolationException(
            "correo duplicado", new SQLException("correo duplicado", "23505")));

        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
            {"nombre":"Cliente","correo":"cliente@ejemplo.test","contrasena":"clave-prueba"}
            """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value("El correo ya está registrado"));
    }

    @Test
    void sigueRechazandoUnCorreoConFormatoInvalido() throws Exception {
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
            {"nombre":"Cliente","correo":"sin-arroba","contrasena":"clave-prueba"}
            """))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(repositorio);
    }
}
