package mx.edu.utez.modules.assets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AssetsDTO {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String etiqueta;

    @NotBlank
    @Size(max = 100)
    private String numeroSerie;

    @NotNull
    private Long idTipoActivo;

    @NotNull
    private Long idModelo;

    @NotNull
    private Long idEspacio;

    private String estadoCustodia;
    private String estadoOperativo;
    private String descripcion;
    private BigDecimal costo;
    private String qrCodigo;
    private String fechaAlta; // yyyy-MM-dd
    private Boolean esActivo;

}

