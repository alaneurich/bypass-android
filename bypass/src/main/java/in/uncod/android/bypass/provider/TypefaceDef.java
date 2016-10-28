package in.uncod.android.bypass.provider;

import android.graphics.Typeface;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({Typeface.BOLD, Typeface.BOLD_ITALIC, Typeface.ITALIC, Typeface.NORMAL})
public @interface TypefaceDef {}

