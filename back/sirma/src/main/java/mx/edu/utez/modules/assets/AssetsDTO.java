package mx.edu.utez.modules.assets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.modules.espacios.Espacio;
import mx.edu.utez.modules.tipo_activos.TipoActivo;

import java.math.BigDecimal;
import java.util.List;

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
    private Long idEspacio;

    private String estadoCustodia;
    private String estadoOperativo;
    private String descripcion;
    private BigDecimal costo;
    private String qrCodigo;
    private String fechaAlta; // yyyy-MM-dd
    private Boolean esActivo;

    private String asignadoA;
    private Long idResguardo;

    private TipoActivo tipoActivo;
    private Espacio espacio;

    // Campos de visualización para detalle extendido
    /** URLs de imágenes del perfil del activo */
    private List<String> imagenesPerfil;

    /** URLs de imágenes de reportes asociados */
    private List<String> imagenesReportes;

    /** URLs de imágenes de mantenimientos asociados */
    private List<String> imagenesMantenimientos;

}
