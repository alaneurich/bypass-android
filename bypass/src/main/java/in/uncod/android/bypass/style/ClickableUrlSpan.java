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

public class ClickableUrlSpan extends ClickableSpan implements ParcelableSpan {

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

    @Override
    public int getSpanTypeId() {
        return CLICK_URL_SPAN;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUrl);
    }

    protected ClickableUrlSpan(Parcel in) {
        this.mUrl = in.readString();
    }

    public static final Creator<ClickableUrlSpan> CREATOR = new Creator<ClickableUrlSpan>() {
        @Override
        public ClickableUrlSpan createFromParcel(Parcel source) {
            return new ClickableUrlSpan(source);
        }

        @Override
        public ClickableUrlSpan[] newArray(int size) {
            return new ClickableUrlSpan[size];
        }
    };
}