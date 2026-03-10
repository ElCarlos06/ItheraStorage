package mx.edu.utez.kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private String message;
    private Object data;
    private boolean error;
    private HttpStatus status;

    //Mensajes de éxito sin cuerpo (sin data)
    public ApiResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    //Mensajes de éxito con cuerpo
    public ApiResponse(String message, Object data, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    //Mensaje de error sin payload
    public ApiResponse(String message, boolean error, HttpStatus status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }



}