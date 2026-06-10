package cl.sda1085.pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de petición para registrar un nuevo pago en el sistema")

public class PagoRequestDTO {

    //ID se genera automaticamente

    @NotNull(message = "El ID de la subasta es obligatorio.")
    @Schema(description = "ID único de la subasta que se está pagando", example = "1")
    private Long idSubasta;

    @NotNull(message = "El ID del usuario es obligatorio.")
    @Schema(description = "ID del usuario comprador que realiza la transacción", example = "6")
    private Long idUsuario;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
    @Schema(description = "Monto final de la transacción", example = "175000.00")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago no debe estar vacío.")
    @Schema(description = "Método financiero utilizado", example = "TARJETA_CREDITO", allowableValues = {"TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA", "PAYPAL"})
    private String metodo;
}


