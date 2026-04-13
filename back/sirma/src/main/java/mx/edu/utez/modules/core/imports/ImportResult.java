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
        StringBuilder sb = new StringBuilder();

        if (tieneErrores()) {
            sb.append("Se importaron ").append(inserciones)
              .append(" activo").append(inserciones == 1 ? "" : "s").append(" correctamente, ")
              .append("pero ").append(rechazos)
              .append(" fila").append(rechazos == 1 ? "" : "s")
              .append(" fueron rechazadas.\n\nDetalle de rechazos:\n")
              .append(String.join("\n", errores));
        } else {
            sb.append("Se importaron ").append(inserciones)
              .append(" activo").append(inserciones == 1 ? "" : "s")
              .append(" correctamente.");
        }

        return sb.toString();
    }

}