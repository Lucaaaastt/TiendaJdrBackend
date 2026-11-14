package cl.tiendaJdr.TiendaJdrBackend.service;

import cl.tiendaJdr.TiendaJdrBackend.dto.UsuarioDTO;
import cl.tiendaJdr.TiendaJdrBackend.dto.UsuarioSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.model.Usuario;
import cl.tiendaJdr.TiendaJdrBackend.repository.UsuarioRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::aDto).toList();
    }

    public UsuarioDTO buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
            .map(this::aDto)
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));
    }

    public UsuarioDTO crearUsuario(UsuarioSolicitud datos) {
        Usuario usuario = new Usuario();
        copiarDatos(usuario, datos);
        return aDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioSolicitud datos) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));
        copiarDatos(usuario, datos);
        return aDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.buscarParaEliminar(id)
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));
        if (usuarioRepository.tieneVentas(id)) {
            throw new UsuarioConVentasException();
        }
        usuarioRepository.delete(usuario);
    }

    private void copiarDatos(Usuario usuario, UsuarioSolicitud datos) {
        usuario.setNombre(datos.nombre());
        usuario.setCorreo(datos.correo());
        usuario.setContrasena(datos.contrasena());
    }

    private UsuarioDTO aDto(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
    }

    public static class UsuarioConVentasException extends RuntimeException {
        public UsuarioConVentasException() {
            super("El usuario tiene ventas asociadas");
        }
    }
}
