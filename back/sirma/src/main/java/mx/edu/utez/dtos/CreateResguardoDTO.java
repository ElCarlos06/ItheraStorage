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
public class CreateResguardoDTO {

    private Activo activo;
    private Usuario usuario;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDevolucion;
    private String estatusDevolucion;
    private String videoUrl;
}
