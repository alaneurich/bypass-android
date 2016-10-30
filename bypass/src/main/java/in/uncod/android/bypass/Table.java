package in.uncod.android.bypass;

import java.util.ArrayList;

public class Table {

    private ArrayList<TableRow> mRows;

    public Table() {
        mRows = new ArrayList<>();
    }

    public ArrayList<TableRow> getRows() {
        return mRows;
    }

    public void removeLastRow() {
        mRows.remove(mRows.size() - 1);
    }

    public void startNewRow() {
        mRows.add(new TableRow());
    }

    public void startNewCellInCurrentRow() { mRows.get(mRows.size() - 1).addCell(""); }

    public void appendToCurrentCell(CharSequence chars) {
        mRows.get(mRows.size() - 1).appendToCurrentCell(chars);
    }

    public void removeLastCell() {
        mRows.get(mRows.size() - 1).removeLastCell();
    }

    public void addToCurrentRow(CharSequence cell) {
        mRows.get(mRows.size() - 1).addCell(cell);
    }
}
