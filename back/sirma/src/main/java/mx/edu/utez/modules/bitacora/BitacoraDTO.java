package mx.edu.utez.modules.bitacora;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BitacoraDTO {

    private Long id;

    @NotNull
    private Long idActivo;

    @NotNull
    private Long idUsuario;

    @NotBlank
    private String tipoEvento;

    @NotBlank
    private String descripcion;

    private String estadoCustodiaAnterior;
    private String estadoCustodiaNuevo;
    private String estadoOperativoAnterior;
    private String estadoOperativoNuevo;

}

