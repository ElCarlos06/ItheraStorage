package mx.edu.utez.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Por defecto este va a retornar un Bad Request
     * @param message El mensaje de error que queremo mostrarle al cliente
     */
    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    /**
     * Este es el método mas libre que se tiene, ya que le podemos insertar el estatus que se nos hinche
     * @param message El mensaje de error que queremo mostrarle al cliente
     * @param status El estatus que nosotros le queremos dar a nuestra cosa
     */
    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

}

