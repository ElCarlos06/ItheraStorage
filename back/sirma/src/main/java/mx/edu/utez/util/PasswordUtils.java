package mx.edu.utez.util;

import java.security.SecureRandom;

public class PasswordUtils {

    // ======================================================================
    // REGEX — Formato oficial CURP mexicano (18 caracteres)
    // Estructura: AAAA######SSEEAAAA##
    //   4 letras (apellidos + nombre) + 6 dígitos (fecha yyMMdd) +
    //   1 letra (sexo H/M) + 2 letras (entidad federativa) +
    //   3 consonantes internas + 1 alfanumérico (homoclave) + 1 dígito (verificador)
    // ======================================================================


    // Caracteres para generación de contraseña segura
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera una contraseña temporal segura de {@value PASSWORD_LENGTH} caracteres
     * que incluye mayúsculas, minúsculas, dígitos y caracteres especiales.
     */
    public static String generarPasswordTemporal() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        // Garantizar al menos 1 mayúscula, 1 minúscula, 1 dígito y 1 especial
        sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(26)));        // A-Z
        sb.append(PASSWORD_CHARS.charAt(26 + RANDOM.nextInt(26)));   // a-z
        sb.append(PASSWORD_CHARS.charAt(52 + RANDOM.nextInt(10)));   // 0-9
        sb.append(PASSWORD_CHARS.charAt(62 + RANDOM.nextInt(PASSWORD_CHARS.length() - 62))); // especial

        // Rellenar el resto
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }

        // Mezclar para que los primeros 4 no siempre sean en el mismo orden
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }

        return new String(chars);
    }
}
