package mx.edu.utez.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(mensaje, true, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiResponse> handleBadRequest(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(e.getMessage() != null ? e.getMessage() : "Parámetros inválidos", true, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception e) {
        log.error("Error inesperado: {}", e.getMessage());
        String mensaje = isDev() && e.getMessage() != null
                ? e.getMessage()
                : "Ocurrió un error interno. Intenta más tarde.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(mensaje, true, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
