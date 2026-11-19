package cl.tiendaJdr.TiendaJdrBackend.controller;

import cl.tiendaJdr.TiendaJdrBackend.dto.VentaDTO;
import cl.tiendaJdr.TiendaJdrBackend.dto.VentaSolicitud;
import cl.tiendaJdr.TiendaJdrBackend.service.VentaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<VentaDTO> listarVentas() {
        List<VentaDTO> ventas = ventaService.listarVentas();
        log.info("Se ha realizado el listado de ventas correctamente");
        return ventas;
    }

    @GetMapping("/{id}")
    public VentaDTO buscarVenta(@PathVariable("id") Long id) {
        validarId(id);
        VentaDTO venta = ventaService.buscarVenta(id);
        log.info("Se ha realizado la búsqueda de la venta {} correctamente", id);
        return venta;
    }

    @GetMapping("/{id}/detalles")
    public List<VentaDTO.Detalle> listarDetalles(@PathVariable("id") Long id) {
        validarId(id);
        return ventaService.buscarVenta(id).detalles();
    }

    @GetMapping("/{id}/detalles/{detalleId}")
    public VentaDTO.Detalle buscarDetalle(
        @PathVariable("id") Long id,
        @PathVariable("detalleId") Long detalleId
    ) {
        validarId(id);
        validarId(detalleId);
        return ventaService.buscarVenta(id).detalles().stream()
            .filter(detalle -> detalle.id().equals(detalleId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Detalle no encontrado en la venta: " + detalleId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaDTO crearVenta(@Valid @RequestBody VentaSolicitud datos) {
        VentaDTO venta = ventaService.crearVenta(datos);
        log.info("Se ha realizado el registro de la venta {} correctamente", venta.id());
        return venta;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail ventaNoEncontrada(NoSuchElementException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    public ProblemDetail importeFueraDeLimite(ArithmeticException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
            "El importe de la venta supera el límite permitido");
    }

    private void validarId(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser mayor que cero");
        }
    }
}
