package in.uncod.android.bypass.provider;

import android.net.Uri;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;

import in.uncod.android.bypass.style.ClickableUrlSpan;
import in.uncod.android.bypass.style.HorizontalLineSpan;

public class DefaultSpanProvider extends BaseSpanProvider {
    @Override
    public Object onUrlSpanNeeded(String url) {
        return new ClickableUrlSpan(url);
    }

    @Override
    public Object onQuoteSpanNeed(int quoteColor) {
        return new QuoteSpan(quoteColor);
    }

    @Override
    public Object onHorizontalLineSpanNeeded(int color, int size, int topBottomPadding) {
        return new HorizontalLineSpan(color, size, topBottomPadding);
    }

    @Override
    public Object onStrikethroughSpanNeeded() {
        return new StrikethroughSpan();
    }

    @Override
    public String onCreateAuthorityString(String url) {
        return " - (" + Uri.parse(url).getAuthority() + ")";
    }
}
