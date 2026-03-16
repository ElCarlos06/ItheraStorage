package mx.edu.utez.modules.imagen_activo;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.imagen.BaseImagen;

/**
 * Entidad que representa la asociación entre una imagen almacenada en Cloudinary y un Activo.
 * Almacena la URL pública y el ID para gestionar el archivo en la nube.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "IMAGEN_ACTIVO")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_activo"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenActivo extends BaseImagen {

    /** Activo al que pertenece la imagen. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;
}
