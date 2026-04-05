package mx.edu.utez.kernel;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * Clase de respuesta API unificada para SIRMA.
 * Estandariza todas las respuestas HTTP con mensaje, datos, error y código de estado.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    /**
     * Mensaje descriptivo de la respuesta.
     */
    private String message;

    /**
     * Datos o cuerpo de la respuesta, puede ser cualquier objeto.
     */
    private Object data;

    /**
     * Indicador booleano de si ocurrió un error en la petición.
     */
    private boolean error;

    /**
     * Código de estado HTTP devuelto en la respuesta.
     */
    private HttpStatus status;

    //Mensajes de éxito sin cuerpo (sin data)
    /**
     * Constructor para respuestas de éxito que no requieren cuerpo o datos adicionales.
     *
     * @param message Mensaje descriptivo de la respuesta.
     * @param status  Código de estado HTTP aplicable.
     */
    public ApiResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    //Mensajes de éxito con cuerpo
    /**
     * Constructor para respuestas de éxito que incluyen un cuerpo de datos y el estado a devolver.
     *
     * @param message Mensaje descriptivo de la respuesta.
     * @param data    Datos obtenidos tras la petición.
     * @param status  Código de estado HTTP de la respuesta.
     */
    public ApiResponse(String message, Object data, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    //Mensaje de error sin payload
    /**
     * Constructor para respuestas de error que no incluyen payload (data).
     *
     * @param message Mensaje de la causa del error.
     * @param error   Se debe asignar como verdadero (true) para indicar el error.
     * @param status  Estado HTTP asociado al error.
     */
    public ApiResponse(String message, boolean error, HttpStatus status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }

}