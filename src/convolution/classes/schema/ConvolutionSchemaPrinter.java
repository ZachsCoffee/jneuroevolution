package schema;

import dnl.utils.text.table.TextTable;

import java.util.LinkedList;
import java.util.Objects;

public class ConvolutionSchemaPrinter {
    private final String[] columns;
    private final LinkedList<Object[]> data = new LinkedList<>();

    public ConvolutionSchemaPrinter(String[] columns) {
        this.columns = Objects.requireNonNull(columns);
    }

    public void addRow(Object... row) {
        if (row.length != columns.length) {
            throw new IllegalArgumentException("The given row doesn't have the correct number of data!");
        }

        data.add(row);
    }

    public void print() {
        new TextTable(columns, data.toArray(new Object[0][])).printTable();
    }
}
