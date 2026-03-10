package mx.edu.utez.modules.resguardos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResguardoDTO {

    private Long id;

    @NotNull
    private Long idActivo;

    @NotNull
    private Long idUsuarioEmpleado;

    @NotNull
    private Long idUsuarioAdmin;

    private String observacionesAsig;
    private String observacionesConf;
    private String observacionesDev;
    private String estadoResguardo;

}

