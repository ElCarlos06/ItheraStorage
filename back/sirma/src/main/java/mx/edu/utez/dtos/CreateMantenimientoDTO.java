package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.model.Activo;
import mx.edu.utez.model.Usuario;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateMantenimientoDTO {

    private Activo activo;
    private Usuario usuario;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String diagnostico;
    private String accionesRealizadas;
    private String estatusFinal;
}
