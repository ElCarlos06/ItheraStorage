package mx.edu.utez.modules.core.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.core.assets.AssetsRepository;
import mx.edu.utez.modules.media.imagen_activo.ImagenActivo;
import mx.edu.utez.modules.media.imagen_activo.ImagenActivoRepository;
import mx.edu.utez.util.CloudinaryPaths;
import mx.edu.utez.util.CloudinaryService;
import mx.edu.utez.util.QrPayloadCodec;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Optional;

/**
 * Servicio encargado de la generación y manipulación de Códigos QR.
 * Utiliza ZXing para generar QR y Apache PDFBox para exportar a PDF.
 */
@Service
@Transactional
@AllArgsConstructor
@Log4j2
public class QRService {

    private static final String QR_FILENAME = "QR_CODE";

    private final AssetsRepository assetsRepository;
    private final ImagenActivoRepository imagenActivoRepository;
    private final CloudinaryService cloudinaryService;
    private final QrPayloadCodec qrPayloadCodec;

    // -------------------------------------------------------------------------
    // Generación de imagen QR
    // -------------------------------------------------------------------------

    /**
     * Genera un QR básico como BufferedImage.
     */
    private BufferedImage generateQrImage(String text, int width, int height) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    /**
     * Genera un QR y lo retorna como byte[] en formato PNG.
     */
    private byte[] generateQrByteArray(String text, int width, int height)
            throws WriterException, IOException {
        BufferedImage image = generateQrImage(text, width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }

    /**
     * Sobrecarga que acepta DTO.
     */
    public byte[] generateQrByteArray(QRDTO qrdto) throws WriterException, IOException {
        return generateQrByteArray(qrdto.getTexto(), qrdto.getAncho(), qrdto.getAlto());
    }

    /**
     * Genera un QR con etiqueta de texto al pie de la imagen.
     */
    public byte[] generateQrByteArrayWithLabel(String text, int width, int height, String label)
            throws WriterException, IOException {

        BufferedImage qrImage = generateQrImage(text, width, height);

        if (label == null || label.isBlank()) {
            return generateQrByteArray(text, width, height);
        }

        int fontSize   = 20;
        int extraHeight = 50;

        BufferedImage combined = new BufferedImage(width, height + extraHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combined.createGraphics();

        // Fondo blanco + QR
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height + extraHeight);
        g.drawImage(qrImage, 0, 0, null);

        // Texto centrado al pie
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics metrics = g.getFontMetrics();
        int x = Math.max(5, (width - metrics.stringWidth(label)) / 2);
        int y = height + metrics.getAscent() + (extraHeight - metrics.getHeight()) / 2 - 5;

        g.drawString(label, x, y);
        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        }
    }

    // -------------------------------------------------------------------------
    // Generación de PDF con PDFBox (sin dependencia itext/lowagie)
    // -------------------------------------------------------------------------

    /**
     * Genera un PDF con el QR centrado. Versión simple (sin etiqueta).
     */
    public byte[] generateQrPdf(String text, int width, int height) throws Exception {
        return generateQrPdf(text, width, height, null);
    }

    /**
     * Genera un PDF A4 con el QR centrado y etiqueta opcional debajo.
     */
    public byte[] generateQrPdf(String text, int width, int height, String labelText) throws Exception {
        byte[] qrBytes = generateQrByteArray(text, width, height);

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float pageWidth  = page.getMediaBox().getWidth();   // 595 pt
            float pageHeight = page.getMediaBox().getHeight();  // 842 pt

            // Imagen QR: máximo 400x400 pt, centrada verticalmente con margen
            float imgSize = 400f;
            float imgX = (pageWidth - imgSize) / 2f;
            float imgY = (pageHeight - imgSize) / 2f + 40f; // ligeramente arriba del centro

            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, qrBytes, QR_FILENAME);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                // Dibujar QR
                cs.drawImage(pdImage, imgX, imgY, imgSize, imgSize);

                // Etiqueta opcional debajo del QR
                if (labelText != null && !labelText.isBlank()) {
                    PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                    float fontSize = 18f;

                    float textWidth  = font.getStringWidth(labelText) / 1000f * fontSize;
                    float textX = (pageWidth - textWidth) / 2f;
                    float textY = imgY - 30f; // 30 pt debajo del borde inferior del QR

                    cs.beginText();
                    cs.setFont(font, fontSize);
                    cs.newLineAtOffset(textX, textY);
                    cs.showText(labelText);
                    cs.endText();
                }
            }

            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Genera PDF para un texto arbitrario y devuelve ApiResponse.
     */
    public ApiResponse generateQrPdfResponse(String text) {
        try {
            byte[] pdfBytes = generateQrPdf(text, 400, 400);
            return new ApiResponse("PDF generado correctamente", pdfBytes, false, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al generar PDF: ", e);
            return new ApiResponse("Error al generar PDF", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------------------
    // Lógica de negocio: QR por Activo
    // -------------------------------------------------------------------------

    /**
     * Devuelve la imagen PNG del QR asociado al activo (con etiqueta).
     */
    @Transactional
    @Cacheable(value = "qr_images", key = "#assetId")
    public ApiResponse getQrImageForAsset(Long assetId) {
        String qrContent = resolveQrContent(assetId);
        if (qrContent == null)
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        String etiqueta = assetsRepository.findById(assetId)
                .map(Assets::getEtiqueta)
                .orElse("");

        try {
            byte[] qrBytes = generateQrByteArrayWithLabel(qrContent, 300, 300, etiqueta);
            return new ApiResponse("QR generado correctamente", qrBytes, false, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al generar imagen QR para activo {}", assetId, e);
            return new ApiResponse("Error interno al generar QR", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Devuelve el PDF con el QR asociado al activo.
     */
    @Transactional
    @Cacheable(value = "qr_pdfs", key = "#assetId")
    public ApiResponse getQrPdfForAsset(Long assetId) {
        String qrContent = resolveQrContent(assetId);
        if (qrContent == null)
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        String etiqueta = assetsRepository.findById(assetId)
                .map(Assets::getEtiqueta)
                .orElse("");

        try {
            byte[] pdfBytes = generateQrPdf(qrContent, 400, 400, etiqueta);
            return new ApiResponse("PDF generado correctamente", pdfBytes, false, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al generar PDF QR para activo {}", assetId, e);
            return new ApiResponse("Error interno al generar PDF", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Resuelve (o genera) el contenido del QR para un activo,
     * subiéndolo a Cloudinary si aún no existe.
     */
    @Transactional
    public String resolveQrContent(Long assetId) {
        Optional<Assets> assetOpt = assetsRepository.findById(assetId);
        if (assetOpt.isEmpty()) return null;

        Assets asset = assetOpt.get();
        String qrContent = ensureOpaqueQrPayload(asset);

        boolean qrImageExists = imagenActivoRepository.findByActivoId(assetId).stream()
                .anyMatch(img -> QR_FILENAME.equals(img.getNombreArchivo()));

        if (!qrImageExists) {
            try {
                byte[] qrBytes = generateQrByteArray(qrContent, 300, 300);

                // Carpeta por activo: sirma/activos/{ID}/qr
                // El usuario solicitó: "por cada activo, haya una carpeta de qro q el qr este peggado a la carpeta del activo"
                String dynamicFolder = CloudinaryPaths.ACTIVOS + "/" + asset.getId() + "/qr";
                
                var uploadResult = cloudinaryService.upload(qrBytes, dynamicFolder);
                String url      = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                ImagenActivo qrImg = new ImagenActivo();
                qrImg.setActivo(asset);
                qrImg.setNombreArchivo(QR_FILENAME);
                qrImg.setUrlCloudinary(url);
                qrImg.setPublicIdCloudinary(publicId);
                imagenActivoRepository.save(qrImg);

                log.info("Imagen QR generada y guardada para activo ID: {}", assetId);
            } catch (Exception e) {
                log.error("Error al subir imagen QR a Cloudinary", e);
            }
        }

        return qrContent;
    }

    /**
     * Garantiza que {@link Assets#getQrCodigo()} use el formato {@code {"v":2,"p":"..."}} cifrado.
     * Si existía el formato legado {@code {"id":N}}, borra la imagen QR previa y persiste el nuevo payload.
     */
    private String ensureOpaqueQrPayload(Assets asset) {
        String current = asset.getQrCodigo();
        boolean blank = current == null || current.isBlank();
        String trimmed = blank ? "" : current.trim();
        boolean legacy = !blank && isLegacyPlainIdJson(trimmed);

        if (!blank && !legacy) {
            return trimmed;
        }

        Long assetId = asset.getId();
        if (legacy) {
            deleteQrByAssetId(assetId);
        }

        String token = qrPayloadCodec.encode(assetId);
        String qrContent = String.format("{\"v\":2,\"p\":\"%s\"}", token);
        asset.setQrCodigo(qrContent);
        assetsRepository.save(asset);
        return qrContent;
    }

    private static boolean isLegacyPlainIdJson(String s) {
        return s.matches("\\{\\s*\"id\"\\s*:\\s*\\d+\\s*}");
    }

    /**
     * Elimina la imagen QR asociada al activo de Cloudinary y de la base de datos.
     * Útil cuando se baja/oculta un activo.
     * @param assetId ID del activo del cual se desea eliminar el QR.
     */
    @Transactional
    @CacheEvict(value = {"qr_images", "qr_pdfs"}, key = "#assetId")
    public void deleteQrByAssetId(Long assetId) {
        Optional<ImagenActivo> qrImgOpt = imagenActivoRepository.findByActivoId(assetId).stream()
                .filter(img -> QR_FILENAME.equals(img.getNombreArchivo()))
                .findFirst();

        if (qrImgOpt.isPresent()) {
            ImagenActivo qrImg = qrImgOpt.get();
            try {
                // Borrar de Cloudinary
                if (qrImg.getPublicIdCloudinary() != null) {
                    cloudinaryService.delete(qrImg.getPublicIdCloudinary());
                    log.info("Imagen QR borrada de Cloudinary para activo ID: {}", assetId);
                }
                // Borrar de BD
                imagenActivoRepository.delete(qrImg);
                log.info("Registro de Imagen QR borrado de BD para activo ID: {}", assetId);
            } catch (Exception e) {
                log.error("Error al eliminar imagen QR para activo ID: {}", assetId, e);
                // No lanzamos excepción para no abortar la transacción principal del activo
            }
        }
    }
}

