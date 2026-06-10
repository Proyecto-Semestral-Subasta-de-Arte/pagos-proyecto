package cl.sda1085.pagos.controller;


import cl.sda1085.pagos.dto.PagoRequestDTO;
import cl.sda1085.pagos.dto.PagoResponseDTO;
import cl.sda1085.pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
@Tag(name = "Gestión de Pagos", description = "Endpoints para el procesamiento, control y consultas del flujo de transacciones financieras de subastas")

public class PagoController {

    //Conexión con 'service'
    private final PagoService pagoService;


    //------------------------------
    //CRUD estándar
    //------------------------------

    //Obtener todos los pagos
    @GetMapping
    @Operation(summary = "Obtener todos los pagos", description = "Retorna una lista completa con todos los registros de pago almacenados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    public ResponseEntity<CollectionModel<PagoResponseDTO>> obtenerTodos() {
        List<PagoResponseDTO> pagos = pagoService.obtenerTodos();
        CollectionModel<PagoResponseDTO> result = CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).obtenerTodos()).withSelfRel());
        return ResponseEntity.ok(result);
    }

    //Obtener pago por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por ID", description = "Busca y retorna un registro de pago específico mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún pago con el ID proporcionado")
    })
    public ResponseEntity<PagoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    //Guardar (crear) nuevo pago
    @PostMapping
    @Operation(summary = "Registrar un nuevo pago", description = "Procesa un pago inicializando su estado en 'PENDIENTE'. Bloquea la operación si la subasta ya cuenta con un pago registrado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago creado y registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto: La subasta ya tiene un pago registrado")
    })
    public ResponseEntity<PagoResponseDTO> guardar(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.guardar(dto));
    }

    //Actualizar pago existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago existente", description = "Modifica el monto y el método de un pago activo según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pago para actualizar")
    })
    public ResponseEntity<PagoResponseDTO> actualizar(
            @Parameter(description = "ID del pago a modificar", example = "1")
            @PathVariable Long id, @Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.ok(pagoService.actualizar(id, dto));
    }

    //Eliminar pago
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un registro de pago", description = "Remueve físicamente el registro de pago de la base de datos.")
    @ApiResponse(responseCode = "200", description = "Pago eliminado de forma conforme")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    //------------------------------
    //CRUD personalizado
    //------------------------------

    //Buscar historial de pagos de un usuario
    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener pagos por usuario", description = "Retorna el historial completo de pagos realizados por un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Historial del usuario obtenido")
    public ResponseEntity<CollectionModel<PagoResponseDTO>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario comprador", example = "6")
            @PathVariable Long idUsuario) {
        List<PagoResponseDTO> pagos = pagoService.obtenerPorUsuario(idUsuario);
        CollectionModel<PagoResponseDTO> result = CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).obtenerPorUsuario(idUsuario)).withSelfRel());
        return ResponseEntity.ok(result);
    }

    //Buscar pagos asociados a una subasta específica
    @GetMapping("/subasta/{idSubasta}")
    @Operation(summary = "Obtener pagos de una subasta", description = "Filtra y devuelve todos los registros de pago asociados a una subasta.")
    @ApiResponse(responseCode = "200", description = "Lista de pagos de la subasta obtenida")
    public ResponseEntity<CollectionModel<PagoResponseDTO>> obtenerPorSubasta(
            @Parameter(description = "ID de la subasta consultada", example = "1")
            @PathVariable Long idSubasta) {
        List<PagoResponseDTO> pagos = pagoService.obtenerPorSubasta(idSubasta);
        CollectionModel<PagoResponseDTO> result = CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).obtenerPorSubasta(idSubasta)).withSelfRel());
        return ResponseEntity.ok(result);
    }

    //Filtrar pagos por estado
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Filtrar pagos por estado", description = "Obtiene los pagos que coincidan con un estado determinado.")
    @ApiResponse(responseCode = "200", description = "Filtro por estado aplicado exitosamente")
    public ResponseEntity<CollectionModel<PagoResponseDTO>> obtenerPorEstado(
            @Parameter(description = "Estado a filtrar", example = "PENDIENTE", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"PENDIENTE", "COMPLETADO", "FALLIDO"}))
            @PathVariable String estado){
        List<PagoResponseDTO> pagos = pagoService.obtenerPorEstado(estado);
        CollectionModel<PagoResponseDTO> result = CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).obtenerPorEstado(estado)).withSelfRel());
        return ResponseEntity.ok(result);
    }

    //Cambiar estado del pago
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del pago", description = "Permite transicionar de forma directa el estado del pago (ej. de PENDIENTE a COMPLETADO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado con éxito"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PagoResponseDTO> actualizarEstado (@Parameter(description = "ID del pago", example = "1") @PathVariable Long id,
                                                             @Parameter(description = "Nuevo estado de la transacción", example = "COMPLETADO") @RequestParam String nuevoEstado){

        return ResponseEntity.ok(pagoService.actualizarEstado(id, nuevoEstado));
    }

    //Verificar existencia del pago
    @GetMapping("/subasta/{idSubasta}/existe")
    @Operation(summary = "Verificar existencia de pago por subasta", description = "Indica con un booleano si una subasta específica ya fue pagada.")
    @ApiResponse(responseCode = "200", description = "Verificación completada (true/false)")
    public ResponseEntity<Boolean> existePagoParaSubasta(@Parameter(description = "ID de la subasta a verificar", example = "1") @PathVariable Long idSubasta) {
        return ResponseEntity.ok(pagoService.existePagoParaSubasta(idSubasta));
    }

    //Buscar pago específico de usuario en subasta
    @GetMapping("/buscar/especifico")
    @Operation(summary = "Buscar pago específico de un usuario en una subasta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago específico encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un pago que vincule a este usuario con esa subasta")
    })
    public ResponseEntity<PagoResponseDTO> buscarPagoEspecifico(@Parameter(description = "ID de la subasta", example = "1") @RequestParam Long idSubasta,
                                                                @Parameter(description = "ID del usuario comprador", example = "6") @RequestParam Long idUsuario) {

        return pagoService.buscarPagoEspecifico(idSubasta, idUsuario)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    //Filtrar por método de pago
    @GetMapping("/metodo/{metodo}")
    @Operation(summary = "Filtrar pagos por método de pago")
    @ApiResponse(responseCode = "200", description = "Listado por método obtenido con éxito")
    public ResponseEntity<CollectionModel<PagoResponseDTO>> buscarPorMetodo(@Parameter(description = "Nombre del método financiero", example = "TARJETA_CREDITO") @PathVariable String metodo) {
        List<PagoResponseDTO> pagos = pagoService.buscarPorMetodo(metodo);
        CollectionModel<PagoResponseDTO> result = CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).buscarPorMetodo(metodo)).withSelfRel());
        return ResponseEntity.ok(result);
    }

    //Buscar pagos mayores a un monto
    @GetMapping("/buscar/monto-mayor")
    @Operation(summary = "Buscar pagos que superen un monto mínimo")
    @ApiResponse(responseCode = "200", description = "Filtro de montos mínimos ejecutado correctamente")
    public ResponseEntity<CollectionModel<PagoResponseDTO>> buscarPagosMayoresA(@Parameter(description = "Monto límite inferior", example = "100000.00") @RequestParam BigDecimal monto) {
        List<PagoResponseDTO> pagos = pagoService.buscarPagosMayoresA(monto);
        CollectionModel<PagoResponseDTO> result = CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).buscarPagosMayoresA(monto)).withSelfRel());
        return ResponseEntity.ok(result);
    }
}
