package mx.edu.utez.util;

import org.springframework.http.HttpStatus;

/**
 * Clase de utilidad para manejar los códigos de estado HTTP.
 * Proporciona un método estático para convertir códigos de estado numéricos en objetos HttpStatus.
 * @author Ithera Team
 */
public class HandleStatus {

    /**
     * Convierte un código de estado numérico en un objeto HttpStatus correspondiente.
     * @param code Código de estado HTTP como entero
     * @return Objeto HttpStatus correspondiente al código proporcionado, o INTERNAL_SERVER_ERROR si el código no es reconocido
    */
    public static HttpStatus getStatus(int code){
        return (
                switch (code) {
                    case 200 -> HttpStatus.OK;
                    case 201 -> HttpStatus.CREATED;
                    case 204 -> HttpStatus.NO_CONTENT;
                    case 400 -> HttpStatus.BAD_REQUEST;
                    case 401 -> HttpStatus.UNAUTHORIZED;
                    case 403 -> HttpStatus.FORBIDDEN;
                    case 404 -> HttpStatus.NOT_FOUND;
                    case 409 -> HttpStatus.CONFLICT;
                    default -> HttpStatus.INTERNAL_SERVER_ERROR;
                }
        );
    }

}
