package in.uncod.android.bypass.style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;
import android.util.Log;

public class TableReplacementSpan2 extends ReplacementSpan {
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
            int h = 96;
            if (- fm.top + fm.ascent < h) {
                // if there is not enough room, "raise" the top
                fm.top = fm.ascent - h;
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        paint.setColor(Color.parseColor("#8a000000"));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x, top, Integer.MAX_VALUE, bottom, paint);
        paint.setColor(Color.parseColor("#000000"));
        Rect bounds = new Rect();
        paint.getTextBounds("a", 0, 1, bounds);
        canvas.drawText(text.toString(), x + 16, top + ((bottom - top) / 2) + (bounds.height() / 2), paint);
    }
}
