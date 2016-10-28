package in.uncod.android.bypass.provider;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import in.uncod.android.bypass.Bypass;
import in.uncod.android.bypass.style.ClickableUrlSpan;
import in.uncod.android.bypass.style.HorizontalLineSpan;

public class DefaultSpanProvider extends BaseSpanProvider {

    public DefaultSpanProvider(Bypass.Options bypassOptions) {
        super(bypassOptions);
    }

    @Override
    public Object[] onCreateHeaderSpans(int level) {
        return new Object[] {
                new RelativeSizeSpan(mOptions.mHeaderSizes[level - 1]),
                new StyleSpan(Typeface.BOLD)
        };
    }

    @Override
    public Object[] onCreateListSpans(int listItemIndent) {
        return new Object[] {
                new LeadingMarginSpan.Standard(listItemIndent)
        };
    }

    @Override
    public Object[] onCreateEmphasisSpans() {
        return new Object[] {
                new StyleSpan(Typeface.ITALIC)
        };
    }

    @Override
    public Object[] onCreateDoubleEmphasisSpans() {
        return new Object[] {
                new StyleSpan(Typeface.BOLD)
        };
    }

    @Override
    public Object[] onCreateTripleEmphasisSpans() {
        return new Object[] {
                new StyleSpan(Typeface.BOLD_ITALIC)
        };
    }

    @Override
    public Object[] onCreateCodeBlockSpans(int codeBlockIndent) {
        LeadingMarginSpan.Standard marginSpan = new LeadingMarginSpan.Standard(codeBlockIndent);
        StyleSpan styleSpan = new StyleSpan(mOptions.mCodeBlockTypefaceFormat);
        if(mOptions.mOverrideCodeBlockTypefaceFamily) {
            return new Object[] {
                    marginSpan,
                    new TypefaceSpan(mOptions.mCodeBlockTypefaceFamily),
                    styleSpan
            };
        } else {
            return new Object[] {
                    marginSpan,
                    styleSpan
            };
        }
    }

    @Override
    public Object[] onCreateCodeLineSpans() {
        StyleSpan styleSpan = new StyleSpan(mOptions.mCodeBlockTypefaceFormat);
        if(mOptions.mOverrideCodeBlockTypefaceFamily) {
            return new Object[] {
                    new TypefaceSpan(mOptions.mCodeBlockTypefaceFamily),
                    styleSpan
            };
        } else {
            return new Object[] {
                    styleSpan
            };
        }
    }

    @Override
    public Object[] onCreateLinkSpans(String url) {
        return new Object[] {
                new ClickableUrlSpan(url)
        };
    }

    @Override
    public Object[] onCreateBlockquoteSpans(int blockquoteIndent) {
        LeadingMarginSpan marginSpan = new LeadingMarginSpan.Standard(blockquoteIndent);
        QuoteSpan quoteSpan = new QuoteSpan(mOptions.mBlockQuoteColor);
        StyleSpan styleSpan = new StyleSpan(mOptions.mBlockquoteTypefaceFormat);
        if(mOptions.mOverrideCodeBlockTypefaceFamily) {
            return new Object[] {
                    marginSpan,
                    quoteSpan,
                    marginSpan,
                    new TypefaceSpan(mOptions.mBlockquoteTypefaceFamily),
                    styleSpan
            };
        } else {
            return new Object[] {
                    marginSpan,
                    quoteSpan,
                    marginSpan,
                    styleSpan
            };
        }
    }

    @Override
    public Object[] onCreateStrikethroughSpans() {
        return new Object[] {
                new StrikethroughSpan()
        };
    }

    @Override
    public Object[] onCreateHorizontalLineSpans(int lineHeight, int lineTopBottomPadding) {
        return new Object[] {
                new HorizontalLineSpan(mOptions.mHruleColor, lineHeight, lineTopBottomPadding)
        };
    }

    @Override
    public Object[] onCreateImageSpans(Drawable drawable) {
        return new Object[] {
                new ImageSpan(drawable)
        };
    }

    @Override
    public String onCreateAuthorityString(String url) {
        return " - (" + Uri.parse(url).getAuthority() + ")";
    }
}
