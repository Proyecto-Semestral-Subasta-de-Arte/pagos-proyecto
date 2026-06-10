package cl.sda1085.pagos.service;

import cl.sda1085.pagos.dto.PagoRequestDTO;
import cl.sda1085.pagos.dto.PagoResponseDTO;
import cl.sda1085.pagos.model.Pago;
import cl.sda1085.pagos.repository.PagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    @Test
    @DisplayName("Debería retornar un PagoResponseDTO válido cuando se busca por ID existente")
    void shouldObtenerPorIdExitosamente() {
        // Given (Arrange) con datos fijos estáticos
        Long idExistente = 1L;
        Pago pagoSimulado = new Pago(
                idExistente,
                10L, // idSubasta
                6L,  // idUsuario
                new BigDecimal("175000.00"),
                "COMPLETADO",
                "TARJETA_CREDITO"
        );

        when(pagoRepository.findById(idExistente)).thenReturn(Optional.of(pagoSimulado));

        // When (Act)
        PagoResponseDTO response = pagoService.obtenerPorId(idExistente);

        // Then (Assert)
        assertNotNull(response);
        assertEquals(idExistente, response.getId());
        assertEquals("COMPLETADO", response.getEstado());
        verify(pagoRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("Debería registrar un nuevo pago en estado PENDIENTE de forma exitosa")
    void shouldGuardarPagoExitosamente() {
        // Given (Arrange) con datos fijos estáticos
        Long idSubasta = 2L;
        PagoRequestDTO requestDto = new PagoRequestDTO(
                idSubasta,
                12L, // idUsuario
                new BigDecimal("360000.00"),
                "TRANSFERENCIA"
        );

        Pago pagoGuardado = new Pago(
                999L,
                requestDto.getIdSubasta(),
                requestDto.getIdUsuario(),
                requestDto.getMonto(),
                "PENDIENTE",
                requestDto.getMetodo()
        );

        when(pagoRepository.existsByIdSubasta(idSubasta)).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);

        // When (Act)
        PagoResponseDTO response = pagoService.guardar(requestDto);

        // Then (Assert)
        assertNotNull(response);
        assertEquals(999L, response.getId());
        assertEquals("PENDIENTE", response.getEstado());
        verify(pagoRepository, times(1)).existsByIdSubasta(idSubasta);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debería lanzar PagoNoEncontradoException cuando el ID no existe")
    void shouldLanzarExceptionCuandoPagoNoExiste() {
        // Given (Arrange)
        Long idInexistente = 888L;
        when(pagoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // When & Then (Act & Assert)
        assertThrows(cl.sda1085.pagos.exception.PagoNoEncontradoException.class, () -> {
            pagoService.obtenerPorId(idInexistente);
        });

        verify(pagoRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Debería lanzar PagoDuplicadoException cuando la subasta ya registra un pago")
    void shouldLanzarExceptionCuandoPagoEstaDuplicado() {
        // Given (Arrange)
        Long idSubastaDuplicada = 450L;
        PagoRequestDTO requestDto = new PagoRequestDTO(
                idSubastaDuplicada,
                5L,
                new BigDecimal("550000.00"),
                "PAYPAL"
        );

        when(pagoRepository.existsByIdSubasta(idSubastaDuplicada)).thenReturn(true);

        // When & Then (Act & Assert)
        assertThrows(cl.sda1085.pagos.exception.PagoDuplicadoException.class, () -> {
            pagoService.guardar(requestDto);
        });

        verify(pagoRepository, times(1)).existsByIdSubasta(idSubastaDuplicada);
        verify(pagoRepository, never()).save(any(Pago.class));
    }
}