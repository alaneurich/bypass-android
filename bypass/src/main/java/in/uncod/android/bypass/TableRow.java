package in.uncod.android.bypass;

import java.util.ArrayList;

public class TableRow {

    private ArrayList<String> mCells;

    public TableRow() {
        mCells = new ArrayList<>();
    }

    public void addCell(String cell) {
        mCells.add(cell);
    }

    public ArrayList<String> getCells() {
        return mCells;
    }
}
