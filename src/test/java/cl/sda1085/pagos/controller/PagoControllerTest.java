package cl.sda1085.pagos.controller;

import cl.sda1085.pagos.dto.PagoResponseDTO;
import cl.sda1085.pagos.service.PagoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    @Test
    @DisplayName("Obtener por ID debería retornar respuesta exitosa con datos simulados")
    void shouldReturnPagoByIdPureUnit() {
        // Given (Preparar el escenario simulado)
        Long pagoId = 1L;
        PagoResponseDTO responseDto = PagoResponseDTO.builder()
                .id(pagoId)
                .idSubasta(10L)
                .idUsuario(6L)
                .monto(BigDecimal.valueOf(175000.00))
                .estado("COMPLETADO")
                .metodo("TARJETA_CREDITO")
                .build();

        when(pagoService.obtenerPorId(pagoId)).thenReturn(responseDto);

        // When (Ejecutar la acción directa sobre el controlador)
        ResponseEntity<PagoResponseDTO> response = pagoController.obtenerPorId(pagoId);

        // Then (Validar que las respuestas sean correctas)
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(pagoId, response.getBody().getId());
    }
}