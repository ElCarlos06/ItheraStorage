package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.model.TipoActivo;
import mx.edu.utez.model.Ubicacion;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateActivoDTO {

    private TipoActivo tipoActivo;
    private Ubicacion ubicacion;
    private String etiquetaProducto;
    private Character estatus;
    private String descripcion;
    private LocalDate fechaAlta;
    private BigDecimal costo;
    private String qrCodeUrl;
}
