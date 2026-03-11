package mx.edu.utez.modules.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AllArgsConstructor;
import org.openpdf.text.Document;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@AllArgsConstructor
public class QRService {

    /**
     * Genera un código QR básico en formato BufferedImage.
     *
     * @param text   Contenido que tendrá el código QR.
     * @param width  Ancho en píxeles.
     * @param height Alto en píxeles.
     * @return BufferedImage con el código QR generado.
     * @throws WriterException Si ocurre un error en el motor de ZXing.
     */
    public BufferedImage generateQrImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Genera un QR y lo convierte a un arreglo de bytes (byte[]).
     * Útil para retornar imágenes directamente en respuestas HTTP o guardar en BD.
     *
     * @param text Contenido del QR.
     * @return byte[] de la imagen en formato PNG.
     */
    public byte[] generateQrByteArray(String text, int width, int height) throws WriterException, IOException {
        BufferedImage image = generateQrImage(text, width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }

    /**
     * Genera un QR y lo devuelve como una cadena Base64.
     * Ideal para mostrar imágenes directamente en etiquetas <img> de HTML/Frontend.
     *
     * @param text Contenido del QR.
     * @return String en formato Base64 (data:image/png;base64,...).
     */
    public String generateQrBase64(String text, int width, int height) throws WriterException, IOException {
        byte[] imageBytes = generateQrByteArray(text, width, height);
        String base64String = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/png;base64," + base64String;
    }

    public byte[] generateQrPdf(String text, int width, int height) throws Exception {
        // 1. Obtenemos los bytes de la imagen PNG que ya sabemos generar
        byte[] qrImageBytes = generateQrByteArray(text, width, height);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 2. Creamos un documento PDF tamaño A4 (o el que prefieras)
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // 3. Convertimos los bytes de la imagen a un objeto Image de OpenPDF
            Image qrImage = Image.getInstance(qrImageBytes);
            qrImage.setAlignment(Image.ALIGN_CENTER);

            document.add(qrImage);
            document.close();

            return baos.toByteArray();
        }
    }

}
