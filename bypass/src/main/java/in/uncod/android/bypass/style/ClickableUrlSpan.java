package in.uncod.android.bypass.style;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class ClickableUrlSpan extends ClickableSpan {

    private final static int CLICK_URL_SPAN = 10000;
    private String mUrl;

    public ClickableUrlSpan(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public void onClick(View view) {
        Uri uri = Uri.parse(getUrl());
        Context context = view.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }
}