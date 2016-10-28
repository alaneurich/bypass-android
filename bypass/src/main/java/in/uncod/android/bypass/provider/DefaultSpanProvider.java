package in.uncod.android.bypass.provider;

import android.net.Uri;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;

import in.uncod.android.bypass.style.ClickableUrlSpan;
import in.uncod.android.bypass.style.HorizontalLineSpan;

public class DefaultSpanProvider extends BaseSpanProvider {
    @Override
    public Object onCreateUrlSpan(String url) {
        return new ClickableUrlSpan(url);
    }

    @Override
    public Object onCreateQuoteSpan(int quoteColor) {
        return new QuoteSpan(quoteColor);
    }

    @Override
    public Object onCreateHorizontalLineSpan(int color, int size, int topBottomPadding) {
        return new HorizontalLineSpan(color, size, topBottomPadding);
    }

    @Override
    public Object onCreateStrikethroughSpan() {
        return new StrikethroughSpan();
    }

    @Override
    public String onCreateAuthorityString(String url) {
        return " - (" + Uri.parse(url).getAuthority() + ")";
    }
}
