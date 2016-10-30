package in.uncod.android.bypass.style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.commit451.bypass.R;

import java.lang.ref.WeakReference;

public class TableReplacementSpan2 extends ReplacementSpan {

    private Context mContext;

    public TableReplacementSpan2(Context context) {
        mContext = context;
    }

    private int dpToSp(int value) {
        return Math.round(value * mContext.getResources().getDisplayMetrics().density);
    }

    public int getLongestDisplayEdge() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels >= metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
    }

    @Override
    public int getSize(Paint paint, CharSequence charSequence, int i, int i1, Paint.FontMetricsInt fm) {
                /*
         * This method is where we make room for the drawing.
         * We are passed in a FontMetrics that we can check to see if there is enough space.
         * If we need to, we can alter these FontMetrics to suit our needs.
         */
        if (fm != null) {  // test for null because sometimes fm isn't passed in
            /*
             * Everything is measured from the baseline, so the ascent is a negative number,
             * and the top is an even more negative number.  We are going to make sure that
             * there is enough room between the top and the ascent line for the graphic.
             */
            int h = dpToSp(48);
            if (- fm.top + fm.ascent < h) {
                // if there is not enough room, "raise" the top
                fm.top = fm.ascent - h;
            }
        }
        return getLongestDisplayEdge();
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        paint.setColor(Color.parseColor("#8a000000"));
        canvas.drawLine(x, top, getLongestDisplayEdge(), top + 1, paint);
        canvas.drawLine(x, bottom, getLongestDisplayEdge(), bottom - 1, paint);
        paint.setColor(Color.parseColor("#000000"));
        Rect bounds = new Rect();
        paint.getTextBounds(text.toString(), 0, 1, bounds);
        canvas.drawText(text.toString(), x + dpToSp(56), ((bottom - top) / 2) - (bounds.height() / 2), paint);
        canvas.save();
        Drawable draw = getCachedDrawable();
        draw.setBounds(0, 0, dpToSp(24), dpToSp(24));
        canvas.translate(x + dpToSp(16), top + ((bottom - top) / 2) - (draw.getBounds().height() / 2));
        draw.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;
        if (wr != null)
            d = wr.get();
        if (d == null) {
            d = mContext.getResources().getDrawable(R.drawable.ic_table);
            mDrawableRef = new WeakReference<Drawable>(d);
        }
        return d;
    }
    private WeakReference<Drawable> mDrawableRef;
}
