package in.uncod.android.bypass.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.commit451.bypass.R;

import java.util.ArrayList;

import in.uncod.android.bypass.Table;
import in.uncod.android.bypass.TableRow;

import static android.graphics.Typeface.BOLD;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private Table mTable;

    public TableAdapter(Table table) {
        mTable = table;
        int defRowSize = mTable.getRows().get(0).getCells().size();
        for(int a = 1; a < mTable.getRows().size(); a++) {
            while (mTable.getRows().get(a).getCells().size() < defRowSize) {
                mTable.getRows().get(a).addCell("");
            }
        }
    }

    @Override
    public TableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.table_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(TableAdapter.ViewHolder holder, int position) {
        int row = position != 0 ? (int) Math.ceil(position / mTable.getRows().get(0).getCells().size()) : 0;
        if(row == 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(getTableCell(position));
            builder.setSpan(new StyleSpan(BOLD), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.content.setText(builder, TextView.BufferType.SPANNABLE);
        } else {
            holder.content.setText(getTableCell(position), TextView.BufferType.SPANNABLE);
        }
    }

    private CharSequence getTableCell(int pos) {
        int row = pos != 0 ? (int) Math.ceil(pos / mTable.getRows().get(0).getCells().size()) : 0;
        int cell = pos - (mTable.getRows().get(0).getCells().size() * row);
        Log.d("test", "Pos: " + pos + ", Determined Row: " + row + ", Determined Cell: " + cell);
        return mTable.getRows().get(row).getCells().get(cell);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        ArrayList<TableRow> rows = mTable.getRows();
        for(TableRow row : rows) {
            count += row.getCells().size();
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.cell_content);

        }
    }
}
