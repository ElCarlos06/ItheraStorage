package mx.edu.utez.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.PersistenceException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Maneja excepciones globalmente y devuelve respuestas JSON consistentes.
 */
@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final Environment env;

    private boolean isDev() {
        return Arrays.asList(env.getActiveProfiles()).contains("dev");
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiResponse(ex.getMessage(), true, ex.getStatus()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse("No tienes permiso para realizar esta acción", true, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException e) {
        String mensaje;
        if (isDev()) {
            mensaje = e.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else {
            mensaje = "Los datos enviados no son válidos.";
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(mensaje, true, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleUnreadable(HttpMessageNotReadableException e) {
        log.debug("JSON inválido o cuerpo ilegible: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("Solicitud con formato incorrecto.", true, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiResponse> handleBadRequest(Exception e) {
        String mensaje = isDev() && e.getMessage() != null && !e.getMessage().isBlank()
                ? e.getMessage()
                : "Parámetros inválidos.";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(mensaje, true, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse> handleDataAccess(DataAccessException e) {
        log.error("Error de persistencia o SQL (detalle solo en servidor)", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("No se pudo completar la operación. Intenta más tarde.", true, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ApiResponse> handlePersistence(PersistenceException e) {
        log.error("Error de persistencia JPA (detalle solo en servidor)", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("No se pudo completar la operación. Intenta más tarde.", true, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception e) {
        log.error("Error inesperado", e);
        String mensaje = isDev() && e.getMessage() != null
                ? e.getMessage()
                : "Ocurrió un error interno. Intenta más tarde.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(mensaje, true, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
