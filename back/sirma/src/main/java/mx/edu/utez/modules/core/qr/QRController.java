package mx.edu.utez.modules.core.qr;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Controlador REST para la generación de códigos QR y PDFs.
 * Proporciona endpoints para generar código QR desde texto genérico o desde la información de un activo.
 * @author Ithera Team
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
public class QRController {

    private final QRService qrService;

    /**
     * Endpoint para generar un código QR a partir de valores en el cuerpo.
     * Se recomienda usar POST si se envía cuerpo.
     * @param dto Objeto con el texto, ancho y alto deseados para el QR.
     * @return ResponseEntity con la imagen del QR en bytes.
     */
    @PostMapping("/")
    public ResponseEntity<ApiResponse> obtenerQr(@RequestBody QRDTO dto) {
        try {
            byte[] qrBytes = qrService.generateQrByteArray(dto);
            return new ResponseEntity<>(new ApiResponse("QR Generado", qrBytes, HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Error al generar QR", true, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para descargar un PDF con el código QR de un texto arbitrario.
     * @param texto Texto que contendrá el QR.
     * @return ResponseEntity con el archivo PDF en bytes.
     */
    @GetMapping("/")
    public ResponseEntity<ApiResponse> descargarPdf(@RequestParam(name = "texto") String texto) {
        ApiResponse response = qrService.generateQrPdfResponse(texto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Genera o recupera el código QR asociado a un Activo específico.
     * Si no existe, lo genera y lo guarda. Además, incrusta el nombre del activo en la imagen.
     * @param id Identificador único del activo.
     * @return ResponseEntity con la imagen del QR en bytes.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> generarQrPorActivo(@PathVariable Long id) {
        ApiResponse response = qrService.getQrImageForAsset(id);
        if (response.isError()) {
            return new ResponseEntity<>(response, response.getStatus());
        }
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(response);
    }

    /**
     * Descarga un PDF con el código QR del activo.
     * El PDF incluye la imagen del QR y el nombre del activo al pie.
     * @param id Identificador único del activo.
     * @return ResponseEntity con el archivo PDF en bytes.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<ApiResponse> descargarPdfActivo(@PathVariable Long id) {
        ApiResponse response = qrService.getQrPdfForAsset(id);
        if (response.isError()) {
            return new ResponseEntity<>(response, response.getStatus());
        }
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(response);
    }
}
