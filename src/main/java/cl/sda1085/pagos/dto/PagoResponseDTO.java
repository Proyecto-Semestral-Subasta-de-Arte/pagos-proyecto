package cl.sda1085.pagos.dto;

//No hay anotaciones de validación.
//DTO de salida.

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de respuesta con los datos detallados del pago procesado")

public class PagoResponseDTO extends RepresentationModel<PagoResponseDTO> {

    @Schema(description = "ID autogenerado del registro de pago", example = "1")
    private Long id;

    @Schema(description = "ID de la subasta asociada", example = "1")
    private Long idSubasta;

    @Schema(description = "ID del usuario que pagó", example = "6")
    private Long idUsuario;

    @Schema(description = "Monto total pagado", example = "175000.00")
    private BigDecimal monto;

    @Schema(description = "Estado actual de la transacción", example = "PENDIENTE", allowableValues = {"PENDIENTE", "COMPLETADO", "FALLIDO"})
    private String estado;

    @Schema(description = "Método con el que se efectuó", example = "TARJETA_CREDITO")
    private String metodo;
}
