package cl.tiendaJdr.TiendaJdrBackend.controller;

import cl.tiendaJdr.TiendaJdrBackend.dto.ProductoDTO;
import cl.tiendaJdr.TiendaJdrBackend.model.Producto;
import cl.tiendaJdr.TiendaJdrBackend.service.ProductoService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<ProductoDTO> listarProductos() {
        List<ProductoDTO> productos = productoService.listarProductos();
        log.info("Se a realizado el listado de productos correctamente");
        return productos;
    }

    @GetMapping("/{id}")
    public ProductoDTO buscarProducto(@PathVariable("id") Long id) {
        validarId(id);
        ProductoDTO producto = productoService.buscarProducto(id);
        log.info("Se a realizado la búsqueda del producto {} correctamente", id);
        return producto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crearProducto(@Valid @RequestBody Producto datos) {
        ProductoDTO producto = productoService.crearProducto(datos);
        log.info("Se a realizado la creación del producto {} correctamente", producto.id());
        return producto;
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizarProducto(
        @PathVariable("id") Long id,
        @Valid @RequestBody Producto datos
    ) {
        validarId(id);
        ProductoDTO producto = productoService.actualizarProducto(id, datos);
        log.info("Se a realizado la actualización del producto {} correctamente", id);
        return producto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable("id") Long id) {
        validarId(id);
        productoService.eliminarProducto(id);
        log.info("Se a realizado la eliminación del producto {} correctamente", id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail productoNoEncontrado(NoSuchElementException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
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
