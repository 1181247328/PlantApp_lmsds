package com.cdqf.plant_okhttp;

import android.content.Context;

import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.util.Map;
import java.util.Set;

public class OKHttpRequestWrap {

    private static final String TAG = OKHttpRequestWrap.class.getSimpleName();

    private Map<String, String> okhttpParams;

    private Context context = null;

    private OnHttpRequest http = null;

    public OKHttpRequestWrap(Context context) {
        this.context = context;
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     */
    public void post(String url, boolean isDialog, String them, Map<String, Object> params, OnHttpRequest onHttpRequest) {
        http = onHttpRequest;
        PostFormBuilder postFormBuilder = new PostFormBuilder();
        postFormBuilder.url(url);
        Set<String> keys = params.keySet();
        for (String key : keys) {
            Object value = params.get(key);
            if (value.getClass() == String.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Integer.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Boolean.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else {
                //TODO
            }
        }
        postFormBuilder.build().execute(new OKHttpStringCallback(context, isDialog, them, new OnOkHttpResponseHandler() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                if (http != null) {
                    http.onOkHttpResponse(response, id);
                }
            }

            /**
             * 失败
             * @param error
             */
            @Override
            public void onOkHttpError(String error) {
                if (http != null) {
                    http.onOkHttpError(error);
                }
            }
        }));
    }

}
