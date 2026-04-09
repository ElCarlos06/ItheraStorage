package mx.edu.utez.modules.core.imports;

import java.util.List;

/**
 * Encargado de mandar
 * @param inserciones Número de inserciones hechas en la BD
 * @param rechazos Número de rechazos hechos por el servicio
 * @param errores Los errores o mensajes que tenga el servicio parra el cliente
 * @author Ithera Team
 */
public record ImportResult(int inserciones, int rechazos, List<String> errores) {

    public boolean tieneErrores() { return rechazos > 0; }

    public String mensaje() {
        StringBuilder sb = new StringBuilder()
                .append("Importación realizada exitosamente.\n")
                .append("Total de inserciones: ").append(inserciones).append("\n");

        if (tieneErrores())
            sb.append("Total de rechazos: ").append(rechazos)
                    .append("\n\nDetalle de rechazos:\n")
                    .append(String.join("\n", errores));

        return sb.toString();
    }

}