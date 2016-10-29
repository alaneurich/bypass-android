package in.uncod.android.bypass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import in.uncod.android.bypass.Element.Type;
import in.uncod.android.bypass.provider.BaseSpanProvider;
import in.uncod.android.bypass.provider.DefaultSpanProvider;
import in.uncod.android.bypass.provider.TypefaceFamilyDef;
import in.uncod.android.bypass.provider.TypefaceFormatDef;

public class Bypass {
	static {
		System.loadLibrary("bypass");
	}

	private final Options mOptions;

	private final BaseSpanProvider mSpanProvider;

	private final int mListItemIndent;
	private final int mBlockQuoteIndent;
	private final int mCodeBlockIndent;
	private final int mHruleSize;

	private final int mHruleTopBottomPadding;

	// Keeps track of the ordered list number for each LIST element.
	// We need to track multiple ordered lists at once because of nesting.
	private final Map<Element, Integer> mOrderedListNumber = new ConcurrentHashMap<Element, Integer>();

	/**
	 * @deprecated Use {@link #Bypass(Context)} instead.
	 */
	@Deprecated
	public Bypass() {
		// Default constructor for backwards-compatibility
		mOptions = new Options();
		mListItemIndent = 20;
		mBlockQuoteIndent = 10;
		mCodeBlockIndent = 10;
		mHruleSize = 2;
		mHruleTopBottomPadding = 20;
		mSpanProvider = new DefaultSpanProvider(mOptions);
	}

	public Bypass(Context context) {
		this(context, new Options());
	}

	public Bypass(Context context, Options options) {
		this(context, options, null);
	}

	public Bypass(Context context, Options options, BaseSpanProvider spanProvider) {
		mOptions = options != null ? options : new Options();

		DisplayMetrics dm = context.getResources().getDisplayMetrics();

		mListItemIndent = (int) TypedValue.applyDimension(mOptions.getListItemIndentUnit(),
				mOptions.getListItemIndentSize(), dm);

		mBlockQuoteIndent = (int) TypedValue.applyDimension(mOptions.getBlockQuoteIndentUnit(),
				mOptions.getBlockQuoteIndentSize(), dm);

		mCodeBlockIndent = (int) TypedValue.applyDimension(mOptions.getCodeBlockIndentUnit(),
				mOptions.getCodeBlockIndentSize(), dm);

		mHruleSize = (int) TypedValue.applyDimension(mOptions.getHruleUnit(),
				mOptions.getHruleSize(), dm);

		mHruleTopBottomPadding = (int) dm.density * 10;

		mSpanProvider = spanProvider != null ? spanProvider : new DefaultSpanProvider(mOptions);
	}

	public CharSequence markdownToSpannable(String markdown) {
		return markdownToSpannable(markdown, null);
	}

	public CharSequence markdownToSpannable(String markdown, ImageGetter imageGetter) {
		Document document = processMarkdown(markdown);

		int size = document.getElementCount();
		CharSequence[] spans = new CharSequence[size];

		for (int i = 0; i < size; i++) {
			spans[i] = recurseElement(document.getElement(i), i, size, imageGetter);
		}

		return TextUtils.concat(spans);
	}


	@SuppressWarnings("JniMissingFunction")
	private native Document processMarkdown(String markdown);

	// The 'numberOfSiblings' parameters refers to the number of siblings within the parent, including
	// the 'element' parameter, as in "How many siblings are you?" rather than "How many siblings do
	// you have?".
	private CharSequence recurseElement(Element element, int indexWithinParent, int numberOfSiblings,
			ImageGetter imageGetter) {

		Type type = element.getType();

		boolean isOrderedList = false;
		if (type == Type.LIST) {
			String flagsStr = element.getAttribute("flags");
			if (flagsStr != null) {
				int flags = Integer.parseInt(flagsStr);
				isOrderedList = (flags & Element.F_LIST_ORDERED) != 0;
				if (isOrderedList) {
					mOrderedListNumber.put(element, 1);
				}
			}
		}

		int size = element.size();
		CharSequence[] spans = new CharSequence[size];
		
		for (int i = 0; i < size; i++) {
			spans[i] = recurseElement(element.children[i], i, size, imageGetter);
		}

		// Clean up after we're done
		if (isOrderedList) {
			mOrderedListNumber.remove(this);
		}

		CharSequence concat = TextUtils.concat(spans);

		SpannableStringBuilder builder = new ReverseSpannableStringBuilder();

		String text = element.getText();
		if (element.size() == 0
			&& element.getParent() != null
			&& element.getParent().getType() != Type.BLOCK_CODE) {
			text = text.replace('\n', ' ');
		}

		// Retrieve the image now so we know whether we're going to have something to display later
		// If we don't, then show the alt text instead (if available).
		Drawable imageDrawable = null;
		if (type == Type.IMAGE && imageGetter != null && !TextUtils.isEmpty(element.getAttribute("link"))) {
			imageDrawable = imageGetter.getDrawable(element.getAttribute("link"));
		}

		switch (type) {
			case LIST:
				if (element.getParent() != null
					&& element.getParent().getType() == Type.LIST_ITEM) {
					builder.append("\n");
				}
				break;
			case LINEBREAK:
				builder.append("\n");
				break;
			case LIST_ITEM:
				builder.append(" ");
				if (mOrderedListNumber.containsKey(element.getParent())) {
					int number = mOrderedListNumber.get(element.getParent());
					builder.append(Integer.toString(number) + ".");
					mOrderedListNumber.put(element.getParent(), number + 1);
				}
				else {
					builder.append(mOptions.getUnorderedListItem());
				}
				builder.append("  ");
				break;
			case AUTOLINK:
				builder.append(element.getAttribute("link"));
				break;
			case HRULE:
				// This ultimately gets drawn over by the line span, but
				// we need something here or the span isn't even drawn.
				builder.append("-");
				break;
			case IMAGE:
				// Display alt text (or title text) if there is no image
				if (imageDrawable == null) {
					String show = element.getAttribute("alt");
					if (TextUtils.isEmpty(show)) {
						show = element.getAttribute("title");
					}
					if (!TextUtils.isEmpty(show)) {
						show = "[" + show + "]";
						builder.append(show);
					}
				}
				else {
					// Character to be replaced
					builder.append("\uFFFC");
				}
				break;
		}

		builder.append(text);
		builder.append(concat);
		
		// Don't auto-append whitespace after last item in document. The 'numberOfSiblings'
		// is the number of children the parent of the current element has (including the
		// element itself), hence subtracting a number from that count gives us the index
		// of the last child within the parent.
		if (element.getParent() != null || indexWithinParent < (numberOfSiblings - 1)) {
			if (type == Type.LIST_ITEM) {
				if (element.size() == 0 || !element.children[element.size() - 1].isBlockElement()) {
					builder.append("\n");
				}
			}
			else if (element.isBlockElement() && type != Type.BLOCK_QUOTE) {
				if (type == Type.LIST) {
					// If this is a nested list, don't include newlines
					if (element.getParent() == null || element.getParent().getType() != Type.LIST_ITEM) {
						builder.append("\n");
					}
				}
				else if (element.getParent() != null
					&& element.getParent().getType() == Type.LIST_ITEM) {
					// List items should never double-space their entries
					builder.append("\n");
				}
				else {
					builder.append("\n\n");
				}
			} else if(type == Type.LINK && mOptions.isAppendAuthorityToTextLinks()) {
				String link = element.getAttribute("link");
				builder.append(mSpanProvider.onCreateAuthorityString(link));
			}
		}

		switch (type) {
			case HEADER:
				String levelStr = element.getAttribute("level");
				int level = Integer.parseInt(levelStr);
				setSpans(builder, mSpanProvider.onCreateHeaderSpans(level));
				break;
			case LIST:
				setBlockSpans(builder, mSpanProvider.onCreateListSpans(mListItemIndent));
				break;
			case EMPHASIS:
				setSpans(builder, mSpanProvider.onCreateEmphasisSpans());
				break;
			case DOUBLE_EMPHASIS:
				setSpans(builder, mSpanProvider.onCreateDoubleEmphasisSpans());
				break;
			case TRIPLE_EMPHASIS:
				setSpans(builder, mSpanProvider.onCreateTripleEmphasisSpans());
				break;
			case BLOCK_CODE:
				setSpans(builder, mSpanProvider.onCreateCodeBlockSpans(mCodeBlockIndent));
				break;
			case CODE_SPAN:
				setSpans(builder, mSpanProvider.onCreateCodeLineSpans());
				break;
			case LINK:
			case AUTOLINK:
				String link = element.getAttribute("link");
				if (!TextUtils.isEmpty(link) && Patterns.EMAIL_ADDRESS.matcher(link).matches()) {
					link = "mailto:" + link;
				}
				setSpans(builder, mSpanProvider.onCreateLinkSpans(link));
				break;
			case BLOCK_QUOTE:
				// We add two leading margin spans so that when the order is reversed,
				// the QuoteSpan will always be in the same spot.
				setBlockSpans(builder, mSpanProvider.onCreateBlockquoteSpans(mBlockQuoteIndent));
				break;
			case STRIKETHROUGH:
				setSpans(builder, mSpanProvider.onCreateStrikethroughSpans());
				break;
			case HRULE:
				setSpans(builder, mSpanProvider.onCreateHorizontalLineSpans(mHruleSize, mHruleTopBottomPadding));
				break;
			case IMAGE:
				if (imageDrawable != null) {
					setSpans(builder, mSpanProvider.onCreateImageSpans(imageDrawable));
				}
				break;
		}

		return builder;
	}

	private static void setSpans(SpannableStringBuilder builder, Object[] objects) {
		if(objects == null) return;
		for(Object object : objects) {
			setSpan(builder, object);
		}
	}

	private static void setBlockSpans(SpannableStringBuilder builder, Object[] objects) {
		if(objects == null) return;
		for(Object object : objects) {
			setBlockSpan(builder, object);
		}
	}

	private static void setSpan(SpannableStringBuilder builder, Object what) {
		builder.setSpan(what, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	// These have trailing newlines that we want to avoid spanning
	private static void setBlockSpan(SpannableStringBuilder builder, Object what) {
		int length = Math.max(0, builder.length() - 1);
		builder.setSpan(what, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * Configurable options for how Bypass renders certain elements.
	 */
	public static final class Options {
		private float[] mHeaderSizes;

		private String mUnorderedListItem;
		private int mListItemIndentUnit;
		private float mListItemIndentSize;

		private int mBlockQuoteColor;
		private int mBlockQuoteIndentUnit;
		@TypefaceFormatDef
		private int mBlockquoteTypefaceFormat;
		private boolean mOverrideBlockquoteTypefaceFamily;
		@TypefaceFamilyDef
		private String mBlockquoteTypefaceFamily;
		private float mBlockQuoteIndentSize;

		@TypefaceFormatDef
		private int mCodeBlockTypefaceFormat;
		private boolean mOverrideCodeBlockTypefaceFamily;
		@TypefaceFamilyDef
		private String mCodeBlockTypefaceFamily;
		private int mCodeBlockIndentUnit;
		private float mCodeBlockIndentSize;

		private int mHruleColor;
		private int mHruleUnit;
		private float mHruleSize;

		private boolean mAppendAuthorityToTextLinks;

		public Options() {
			mHeaderSizes = new float[] {
				1.5f, // h1
				1.4f, // h2
				1.3f, // h3
				1.2f, // h4
				1.1f, // h5
				1.0f, // h6
			};

			mUnorderedListItem = "\u2022";
			mListItemIndentUnit = TypedValue.COMPLEX_UNIT_DIP;
			mListItemIndentSize = 10;

			mBlockQuoteColor = 0xff0000ff;
			mBlockQuoteIndentUnit = TypedValue.COMPLEX_UNIT_DIP;
			mBlockQuoteIndentSize = 10;
			mBlockquoteTypefaceFormat = Typeface.ITALIC;
			mBlockquoteTypefaceFamily = BaseSpanProvider.TYPEFACE_FAMILY_DEFAULT;
			mOverrideBlockquoteTypefaceFamily = false;

			mCodeBlockTypefaceFamily = BaseSpanProvider.TYPEFACE_FAMILY_MONOSPACE;
			mOverrideCodeBlockTypefaceFamily = true;
			mCodeBlockTypefaceFormat = Typeface.NORMAL;
			mCodeBlockIndentUnit = TypedValue.COMPLEX_UNIT_DIP;
			mCodeBlockIndentSize = 10;

			mHruleColor = Color.GRAY;
			mHruleUnit = TypedValue.COMPLEX_UNIT_DIP;
			mHruleSize = 1;

			mAppendAuthorityToTextLinks = false;
		}

		public Options setHeaderSizes(float[] headerSizes) {
			if (headerSizes == null) {
				throw new IllegalArgumentException("headerSizes must not be null");
			}
			else if (headerSizes.length != 6) {
				throw new IllegalArgumentException("headerSizes must have 6 elements (h1 through h6)");
			}

			mHeaderSizes = headerSizes;

			return this;
		}

		public Options setH1Size(float size) {
			getHeaderSizes()[0] = size;
			return this;
		}

		public Options setH2Size(float size) {
			getHeaderSizes()[1] = size;
			return this;
		}

		public Options setH3Size(float size) {
			getHeaderSizes()[2] = size;
			return this;
		}

		public Options setH4Size(float size) {
			getHeaderSizes()[3] = size;
			return this;
		}

		public Options setH5Size(float size) {
			getHeaderSizes()[4] = size;
			return this;
		}

		public Options setH6Size(float size) {
			getHeaderSizes()[5] = size;
			return this;
		}

		public Options setUnorderedListItem(String unorderedListItem) {
			mUnorderedListItem = unorderedListItem;
			return this;
		}

		public Options setListItemIndentSize(int unit, float size) {
			mListItemIndentUnit = unit;
			mListItemIndentSize = size;
			return this;
		}

		public Options setBlockQuoteColor(int color) {
			mBlockQuoteColor = color;
			return this;
		}

		public Options setBlockQuoteIndentSize(int unit, float size) {
			mBlockQuoteIndentUnit = unit;
			mBlockQuoteIndentSize = size;
			return this;
		}

		public Options setCodeBlockIndentSize(int unit, float size) {
			mCodeBlockIndentUnit = unit;
			mCodeBlockIndentSize = size;
			return this;
		}

		public Options setCodeBlockTypefaceFormat(@TypefaceFormatDef int typeface) {
			mCodeBlockTypefaceFormat = typeface;
			return this;
		}

		public Options setCodeBlockTypefaceFamily(@TypefaceFamilyDef String family) {
			mOverrideCodeBlockTypefaceFamily = !family.equals(BaseSpanProvider.TYPEFACE_FAMILY_DEFAULT);
			mCodeBlockTypefaceFamily = family;
			return this;
		}

		public Options setHruleColor(int color) {
			mHruleColor = color;
			return this;
		}

		public Options setHruleSize(int unit, float size) {
			mHruleUnit = unit;
			mHruleSize = size;
			return this;
		}

		public Options setAppendAuthorityToTextLink(boolean append) {
			mAppendAuthorityToTextLinks = append;
			return this;
		}

		public Options setBlockquoteTypefaceFamily(@TypefaceFamilyDef String family) {
			mOverrideBlockquoteTypefaceFamily = !family.equals(BaseSpanProvider.TYPEFACE_FAMILY_DEFAULT);
			mBlockquoteTypefaceFamily = family;
			return this;
		}

		public Options setBlockquoteTypefaceFormat(@TypefaceFormatDef int typeface) {
			mBlockquoteTypefaceFormat = typeface;
			return this;
		}

		public float[] getHeaderSizes() {
			return mHeaderSizes;
		}

		public String getUnorderedListItem() {
			return mUnorderedListItem;
		}

		public int getListItemIndentUnit() {
			return mListItemIndentUnit;
		}

		public float getListItemIndentSize() {
			return mListItemIndentSize;
		}

		public int getBlockQuoteColor() {
			return mBlockQuoteColor;
		}

		public int getBlockQuoteIndentUnit() {
			return mBlockQuoteIndentUnit;
		}

		public int getBlockQuoteTypefaceFormat() {
			return mBlockquoteTypefaceFormat;
		}

		public boolean isOverrideBlockquoteTypefaceFamily() {
			return mOverrideBlockquoteTypefaceFamily;
		}

		public String getBlockquoteTypefaceFamily() {
			return mBlockquoteTypefaceFamily;
		}

		public float getBlockQuoteIndentSize() {
			return mBlockQuoteIndentSize;
		}

		public int getCodeBlockTypefaceFormat() {
			return mCodeBlockTypefaceFormat;
		}

		public boolean isOverrideCodeBlockTypefaceFamily() {
			return mOverrideCodeBlockTypefaceFamily;
		}

		public String getCodeBlockTypefaceFamily() {
			return mCodeBlockTypefaceFamily;
		}

		public int getCodeBlockIndentUnit() {
			return mCodeBlockIndentUnit;
		}

		public float getCodeBlockIndentSize() {
			return mCodeBlockIndentSize;
		}

		public int getHruleColor() {
			return mHruleColor;
		}

		public int getHruleUnit() {
			return mHruleUnit;
		}

		public float getHruleSize() {
			return mHruleSize;
		}

		public boolean isAppendAuthorityToTextLinks() {
			return mAppendAuthorityToTextLinks;
		}
	}

	/**
	 * Retrieves images for markdown images.
	 */
	public static interface ImageGetter {

		/**
		 * This method is called when the parser encounters an image tag.
		 */
		public Drawable getDrawable(String source);

	}
}
