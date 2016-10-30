package in.uncod.android.bypass;

import android.text.TextUtils;

import java.util.ArrayList;

public class TableRow {

    private ArrayList<CharSequence> mCells;

    public TableRow() {
        mCells = new ArrayList<>();
    }

    public void appendToCurrentCell(CharSequence append) {
        mCells.set(mCells.size() - 1, TextUtils.concat(mCells.get(mCells.size() - 1), append));
    }

    public void removeLastCell() {
        mCells.remove(mCells.size() - 1);
    }

    public void addCell(CharSequence cell) {
        mCells.add(cell);
    }

    public ArrayList<CharSequence> getCells() {
        return mCells;
    }
}
