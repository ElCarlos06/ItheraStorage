package mx.edu.utez.modules.media.imagen;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Clase base para las entidades que representan una imagen en el sistema.
 * Contiene los atributos comunes para Cloudinary y la fecha de subida.
 *
 * @author Ithera Team
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseImagen extends BaseEntity {

    /** URL pública de la imagen en Cloudinary. */
    @Column(name = "url_cloudinary", nullable = false, length = 500)
    private String urlCloudinary;

    /** Public ID asignado por Cloudinary (necesario para eliminar/transformar). */
    @Column(name = "public_id_cloudinary", nullable = false, length = 255)
    private String publicIdCloudinary;

    /** Nombre original del archivo subido por el usuario. */
    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    /** Timestamp de creación del registro. */
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }

    /**
     * Llena los atributos comunes de la imagen a partir del resultado de Cloudinary.
     *
     * @param resultado Mapa con la respuesta de Cloudinary.
     * @param nombreArchivo Nombre original del archivo.
     */
    public void llenarDesdeCloudinary(Map<?, ?> resultado, String nombreArchivo) {
        this.urlCloudinary = (String) resultado.get("secure_url");
        this.publicIdCloudinary = (String) resultado.get("public_id");
        this.nombreArchivo = nombreArchivo;
    }
}
