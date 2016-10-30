package in.uncod.android.bypass.style;

import android.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.commit451.bypass.R;

import java.util.ArrayList;

import in.uncod.android.bypass.Table;
import in.uncod.android.bypass.TableRow;
import in.uncod.android.bypass.adapter.TableAdapter;

public class TableClickSpan extends ClickableSpan {

    private Table mTable;

    public TableClickSpan(Table table) {
        mTable = table;
    }

    @Override
    public void onClick(View view) {
        ArrayList<TableRow> rows = mTable.getRows();
        int count = 0;
        String tableRow = "";
        for (TableRow row : rows) {
            ArrayList<String> cells = row.getCells();
            for (String cell : cells) {
                tableRow += cell + ", ";
            }
            tableRow += "\n";
            count++;
        }
        View containerView = LayoutInflater.from(view.getContext()).inflate(R.layout.table_dialog, null);
        RecyclerView recView = (RecyclerView) containerView.findViewById(R.id.rec_view);
        recView.setLayoutManager(new GridLayoutManager(view.getContext(), mTable.getRows().get(0).getCells().size(),
                GridLayoutManager.VERTICAL, false));
        recView.setAdapter(new TableAdapter(mTable));
        new AlertDialog.Builder(view.getContext())
                .setTitle("Table")
                .setView(containerView)
                .show();
    }
}
