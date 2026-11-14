package cl.tiendaJdr.TiendaJdrBackend.controller;

import cl.tiendaJdr.TiendaJdrBackend.dto.UsuarioDTO;
import cl.tiendaJdr.TiendaJdrBackend.dto.UsuarioSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.service.UsuarioService;
import cl.tiendaJdr.TiendaJdrBackend.service.UsuarioService.UsuarioConVentasException;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios();
        log.info("Se ha realizado el listado de usuarios correctamente");
        return usuarios;
    }

    @GetMapping("/{id}")
    public UsuarioDTO buscarUsuario(@PathVariable("id") Long id) {
        validarId(id);
        UsuarioDTO usuario = usuarioService.buscarUsuario(id);
        log.info("Se ha realizado la búsqueda del usuario {} correctamente", id);
        return usuario;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crearUsuario(@Valid @RequestBody UsuarioSolicitud datos) {
        UsuarioDTO usuario = usuarioService.crearUsuario(datos);
        log.info("Se ha realizado la creación del usuario {} correctamente", usuario.id());
        return usuario;
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizarUsuario(
        @PathVariable("id") Long id,
        @Valid @RequestBody UsuarioSolicitud datos
    ) {
        validarId(id);
        UsuarioDTO usuario = usuarioService.actualizarUsuario(id, datos);
        log.info("Se ha realizado la actualización del usuario {} correctamente", id);
        return usuario;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarUsuario(@PathVariable("id") Long id) {
        validarId(id);
        usuarioService.eliminarUsuario(id);
        log.info("Se ha realizado la eliminación del usuario {} correctamente", id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail usuarioNoEncontrado(NoSuchElementException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail correoDuplicado() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "El correo ya está registrado");
    }

    @ExceptionHandler(UsuarioConVentasException.class)
    public ProblemDetail usuarioConVentas(UsuarioConVentasException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Map<String, String>> datosInvalidos(MethodArgumentNotValidException error) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (var campo : error.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(campo.getField(), Objects.toString(campo.getDefaultMessage(), "Valor inválido"));
        }
        return Map.of("errores", errores);
    }

    private void validarId(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser mayor que cero");
        }
    }
}
