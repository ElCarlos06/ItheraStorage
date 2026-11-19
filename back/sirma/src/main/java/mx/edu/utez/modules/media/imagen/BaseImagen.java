package mx.edu.utez.modules.media.imagen;

import com.fasterxml.jackson.annotation.JsonProperty;
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
 * Contiene los atributos comunes para almacenamiento en la nube (AWS S3) y la fecha de subida.
 *
 * @author Ithera Team
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseImagen extends BaseEntity {

    /** URL pública de la imagen en S3 / CDN. */
    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;

    /** Clave única del objeto dentro del bucket de S3. */
    @Column(name = "storage_key", nullable = false, length = 255)
    private String storageKey;

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
     * Llena los atributos comunes de la imagen a partir del resultado de S3 o Storage.
     *
     * @param resultado Mapa con la respuesta del servicio de almacenamiento.
     * @param nombreArchivo Nombre original del archivo.
     */
    public void llenarDesdeStorage(Map<?, ?> resultado, String nombreArchivo) {
        String key = (String) (resultado.containsKey("key") ? resultado.get("key") : resultado.get("public_id"));
        // Preferir URL canónica en BD; la firmada (presign) se usa solo al responder al cliente.
        String url = (String) resultado.get("canonical_url");
        if (url == null || url.isBlank()) {
            url = (String) (resultado.containsKey("url") ? resultado.get("url") : resultado.get("secure_url"));
        }
        this.urlImagen = url;
        this.storageKey = key;
        this.nombreArchivo = nombreArchivo;
    }

    /** Reemplaza urlImagen por una URL firmada temporal (no persistir después de esto). */
    public void aplicarUrlTemporal(String urlFirmada) {
        if (urlFirmada != null && !urlFirmada.isBlank()) {
            this.urlImagen = urlFirmada;
        }
    }

    /**
     * Alias de compatibilidad para código anterior.
     */
    @Deprecated
    public void llenarDesdeCloudinary(Map<?, ?> resultado, String nombreArchivo) {
        llenarDesdeStorage(resultado, nombreArchivo);
    }

    // =========================================================================
    // Getters y Setters de compatibilidad con Frontend React y Android
    // =========================================================================

    @JsonProperty("urlCloudinary")
    public String getUrlCloudinary() {
        return urlImagen;
    }

    public void setUrlCloudinary(String url) {
        this.urlImagen = url;
    }

    @JsonProperty("publicIdCloudinary")
    public String getPublicIdCloudinary() {
        return storageKey;
    }

    public void setPublicIdCloudinary(String key) {
        this.storageKey = key;
    }

    @JsonProperty("url")
    public String getUrl() {
        return urlImagen;
    }
}
