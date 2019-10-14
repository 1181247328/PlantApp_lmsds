package com.cdqf.plant_view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WebViewScroll extends WebView {

    public String TAG = WebViewScroll.class.getSimpleName();

    private WebScrollListener webScrollListener = null;

    public WebViewScroll(Context context) {
        super(context);
    }

    public WebViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWebScrollListener(WebScrollListener webScrollListener) {
        this.webScrollListener = webScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.getScrollY() == 0) {
            if (webScrollListener != null) {
                webScrollListener.scroll(true);
            }
        } else {
            if (webScrollListener != null) {
                webScrollListener.scroll(false);
            }
        }
    }

    public interface WebScrollListener {
        void scroll(boolean isScroll);
    }
}
