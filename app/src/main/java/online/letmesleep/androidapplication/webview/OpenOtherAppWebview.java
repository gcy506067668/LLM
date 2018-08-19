package online.letmesleep.androidapplication.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;

import online.letmesleep.androidapplication.MApplication;

public class OpenOtherAppWebview extends WebView {
    Context context;

    public OpenOtherAppWebview(Context context) {
        super(context);
        this.context = context;
    }

    public OpenOtherAppWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public OpenOtherAppWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public OpenOtherAppWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }


    @Override
    public void loadUrl(String url){
        if(isSchemeUrl(url)){
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
            return;
        }
        super.loadUrl(url);
    }

    private boolean isSchemeUrl(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        String[] strs = url.split("://");
        if (strs.length > 1) {
            String host = strs[0];
            if (host.equalsIgnoreCase("http") || host.equalsIgnoreCase("https"))
                return false;
            else
                return true;
        } else return false;
    }
}
