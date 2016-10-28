package in.uncod.android.bypass.provider;

import android.graphics.drawable.Drawable;

import in.uncod.android.bypass.Bypass;

public abstract class BaseSpanProvider {
    public static final String TYPEFACE_FAMILY_DEFAULT = "default";
    public static final String TYPEFACE_FAMILY_SERIF = "serif";
    public static final String TYPEFACE_FAMILY_SANS_SERIF = "sans-serif";
    public static final String TYPEFACE_FAMILY_MONOSPACE = "monospace";

    protected Bypass.Options mOptions;

    public BaseSpanProvider(Bypass.Options bypassOptions) {
        mOptions = bypassOptions;
    }

    public abstract Object[] onCreateHeaderSpans(int level);
    public abstract Object[] onCreateListSpans(int listItemIndent);
    public abstract Object[] onCreateEmphasisSpans();
    public abstract Object[] onCreateDoubleEmphasisSpans();
    public abstract Object[] onCreateTripleEmphasisSpans();
    public abstract Object[] onCreateCodeBlockSpans(int codeBlockIndent);
    public abstract Object[] onCreateCodeLineSpans();
    public abstract Object[] onCreateLinkSpans(String url);
    public abstract Object[] onCreateBlockquoteSpans(int blockquoteIndent);
    public abstract Object[] onCreateStrikethroughSpans();
    public abstract Object[] onCreateHorizontalLineSpans(int lineHeight, int lineTopBottomPadding);
    public abstract Object[] onCreateImageSpans(Drawable imageDrawable);

    public abstract String onCreateAuthorityString(String url);
}
