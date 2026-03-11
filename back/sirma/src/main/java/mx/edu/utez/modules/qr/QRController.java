package mx.edu.utez.modules.qr;

import com.google.zxing.WriterException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
public class QRController {

    private final QRService qrService;

    @GetMapping(value = "/generar-qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] obtenerQr(
            @RequestParam(name = "texto") String texto,
            @RequestParam(name = "ancho", defaultValue = "100") int ancho,
            @RequestParam(name = "alto", defaultValue = "100") int alto
    ) throws WriterException, IOException {

        return qrService.generateQrByteArray(texto, ancho, alto);
    }

    @GetMapping(value = "/descargar-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam(name = "texto") String texto,
            @RequestParam(name = "nombreArchivo", defaultValue = "mi_codigo_qr") String nombreArchivo
    ) {
        try {
            byte[] pdfBytes = qrService.generateQrPdf(texto, 400, 400);

            // Aquí es donde sucede la magia:
            // Agregamos -".pdf" al final para que el sistema operativo lo reconozca
            String nombreFinal = nombreArchivo + ".pdf";

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + nombreFinal + "\"")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
