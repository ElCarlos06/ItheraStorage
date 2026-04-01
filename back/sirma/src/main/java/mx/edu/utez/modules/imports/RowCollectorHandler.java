package mx.edu.utez.modules.imports;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


/**
 * Esta clase maneja lo que esta pensada para manetener el consumo de RAM en lo más bajo posible
 * El parser XML va disparando eventos: empieza fila, hay celda, termina fila.
 * @author Ithera Team
 */
public class RowCollectorHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    private final Consumer<String[]> onRowEnd;
    private String[] currentRow;

    public RowCollectorHandler(Consumer<String[]> onRowEnd) {
        this.onRowEnd = onRowEnd;
    }

    /**
     * Checa si es el inicio de la fila :D
     * @param rowNum Número de fila
     */
    @Override
    public void startRow(int rowNum) {
        currentRow = new String[10]; // índice 0 = rowNum, índices 1-9 = columnas A-I
        java.util.Arrays.fill(currentRow, "");
        currentRow[0] = String.valueOf(rowNum);
    }

    /**
     * Checa si la fila ha terminado y la agrega a la lista de filas, excepto la fila 0 que es la cabecera
     * @param rowNum Número de fila
     */
    @Override
    public void endRow(int rowNum) {
        // Fila 0 = cabecera, la ignoramos
        if (rowNum == 0 || currentRow == null) return;
        onRowEnd.accept(currentRow);
        currentRow = null;
    }

    /**
     * Es el formateador de las celdas para parsearlas al valor que son xd
     * @param cellRef Es la celda en sí
     * @param formattedValue Es el formato que espera
     * @param comment Si tiene un comentario lo ignora épicamente xD
     */
    @Override
    public void cell(String cellRef, String formattedValue, XSSFComment comment) {
        if (currentRow == null || formattedValue == null) return;
        int colIdx = colLetterToIndex(cellRef); // A→0, B→1, ..., I→8
        if (colIdx >= 0 && colIdx <= 8)
            currentRow[colIdx + 1] = formattedValue.trim(); // +1 porque índice 0 son los encabezados del excel xd

    }

    /** Convierte la letra(s) de columna ("A","B","AA"…) a índice base-0 */
    private int colLetterToIndex(String cellRef) {
        int col = 0;
        for (char c : cellRef.toCharArray()) {
            if (!Character.isLetter(c)) break;
            col = col * 26 + (Character.toUpperCase(c) - 'A' + 1);
        }
        return col - 1;
    }
}