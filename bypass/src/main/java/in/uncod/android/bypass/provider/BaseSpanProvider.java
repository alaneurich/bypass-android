package in.uncod.android.bypass.provider;

public abstract class BaseSpanProvider {
    public abstract Object onUrlSpanNeeded(String url);
    public abstract Object onQuoteSpanNeed(int quoteColor);
    public abstract Object onHorizontalLineSpanNeeded(int color, int size, int topBottomPadding);
    public abstract Object onStrikethroughSpanNeeded();
    public abstract String onCreateAuthorityString(String url);
}
