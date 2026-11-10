package cl.tiendaJdr.TiendaJdrBackend.service;

import cl.tiendaJdr.TiendaJdrBackend.dto.ProductoDTO;
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
        return productoRepository.findAll().stream()
            .map(this::aDto)
            .toList();
    }

    public ProductoDTO buscarProducto(Long id) {
        return productoRepository.findById(id)
            .map(this::aDto)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
    }

    public ProductoDTO crearProducto(Producto datos) {
        Producto producto = new Producto();
        copiarDatos(producto, datos);
        return aDto(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizarProducto(Long id, Producto datos) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
        copiarDatos(producto, datos);
        return aDto(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
        productoRepository.delete(producto);
    }

    private void copiarDatos(Producto producto, Producto datos) {
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setImagenUrl(datos.getImagenUrl());
    }

    private ProductoDTO aDto(Producto producto) {
        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getImagenUrl()
        );
    }
}
