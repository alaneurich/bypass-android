package in.uncod.android.bypass.provider;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({BaseSpanProvider.TYPEFACE_FAMILY_DEFAULT, BaseSpanProvider.TYPEFACE_FAMILY_SERIF,
        BaseSpanProvider.TYPEFACE_FAMILY_SANS_SERIF, BaseSpanProvider.TYPEFACE_FAMILY_MONOSPACE})
public @interface TypefaceFamilyDef {}

