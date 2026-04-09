package mx.edu.utez.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifra/descifra el identificador interno del activo para el contenido del QR (campo {@code p}),
 * sin exponer el id numérico en claro en el payload.
 */
@Component
public class QrPayloadCodec {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LEN = 12;
    private static final int GCM_TAG_BITS = 128;
    /** Versión interna del plaintext (1 byte) + long (8 bytes). */
    private static final int PLAIN_LEN = 9;

    private final SecretKeySpec keySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public QrPayloadCodec(@Value("${jwt.secret}") String jwtSecret) {
        byte[] key = sha256(jwtSecret + "|SIRMA_QR_PAYLOAD_V1");
        this.keySpec = new SecretKeySpec(key, "AES");
    }

    private static byte[] sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /**
     * Genera el token opaco (Base64 URL sin padding) para incluir en {@code {"v":2,"p":"..."}}.
     */
    public String encode(long assetId) {
        byte[] plain = ByteBuffer.allocate(PLAIN_LEN)
                .put((byte) 1)
                .putLong(assetId)
                .array();
        byte[] iv = new byte[GCM_IV_LEN];
        secureRandom.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
            byte[] cipherText = cipher.doFinal(plain);
            byte[] combined = ByteBuffer.allocate(iv.length + cipherText.length)
                    .put(iv)
                    .put(cipherText)
                    .array();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Error al cifrar payload QR", e);
        }
    }

    /**
     * Recupera el id del activo desde el token; lanza {@link IllegalArgumentException} si es inválido.
     */
    public long decode(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token vacío");
        }
        byte[] combined = Base64.getUrlDecoder().decode(token.trim());
        if (combined.length < GCM_IV_LEN + PLAIN_LEN) {
            throw new IllegalArgumentException("token corto");
        }
        ByteBuffer bb = ByteBuffer.wrap(combined);
        byte[] iv = new byte[GCM_IV_LEN];
        bb.get(iv);
        byte[] cipherBytes = new byte[bb.remaining()];
        bb.get(cipherBytes);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
            byte[] plain = cipher.doFinal(cipherBytes);
            if (plain.length != PLAIN_LEN || plain[0] != 1) {
                throw new IllegalArgumentException("formato plaintext");
            }
            return ByteBuffer.wrap(plain, 1, 8).getLong();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("token inválido", e);
        }
    }
}
