package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.model.Activo;
import mx.edu.utez.model.Prioridad;
import mx.edu.utez.model.Usuario;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateReporteDTO {

    private Activo activo;
    private Prioridad prioridad;
    private Usuario usuario;
    private LocalDateTime fechaReporte;
    private String video;
}
