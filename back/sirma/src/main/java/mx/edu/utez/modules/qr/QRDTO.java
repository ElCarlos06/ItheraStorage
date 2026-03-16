package mx.edu.utez.modules.qr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class QRDTO {

    private String texto;

    private Integer alto;
    private Integer ancho;
}
