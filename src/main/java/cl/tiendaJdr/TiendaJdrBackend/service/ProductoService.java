package cl.tiendaJdr.TiendaJdrBackend.service;

import cl.tiendaJdr.TiendaJdrBackend.dto.ProductoDTO;
import cl.tiendaJdr.TiendaJdrBackend.dto.ProductoSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import cl.tiendaJdr.TiendaJdrBackend.repository.ProductoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductoDTO> listarProductos() {
        return productoRepository.findByActivoTrue().stream()
            .map(this::aDto)
            .toList();
    }

    public ProductoDTO buscarProducto(Long id) {
        return productoRepository.findById(id)
            .filter(Producto::isActivo)
            .map(this::aDto)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
    }

    public ProductoDTO crearProducto(ProductoSolicitud datos) {
        Producto producto = new Producto();
        copiarDatos(producto, datos);
        return aDto(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoSolicitud datos) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
        copiarDatos(producto, datos);
        return aDto(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private void copiarDatos(Producto producto, ProductoSolicitud datos) {
        producto.setNombre(datos.nombre());
        producto.setDescripcion(datos.descripcion());
        producto.setPrecio(datos.precio());
        producto.setImagenUrl(datos.imagenUrl());
        if (datos.activo() != null) {
            producto.setActivo(datos.activo());
        }
        if (datos.novedad() != null) {
            producto.setNovedad(datos.novedad());
        }
    }

    private ProductoDTO aDto(Producto producto) {
        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getImagenUrl(),
            producto.isActivo(),
            producto.isNovedad()
        );
    }
}
