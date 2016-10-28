package in.uncod.android.bypass.provider;

public abstract class BaseSpanProvider {
    public abstract Object onCreateUrlSpan(String url);
    public abstract Object onCreateQuoteSpan(int quoteColor);
    public abstract Object onCreateHorizontalLineSpan(int color, int size, int topBottomPadding);
    public abstract Object onCreateStrikethroughSpan();
    public abstract String onCreateAuthorityString(String url);
}
