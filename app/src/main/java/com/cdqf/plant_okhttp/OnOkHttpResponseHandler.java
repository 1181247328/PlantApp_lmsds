package com.cdqf.plant_okhttp;

public interface OnOkHttpResponseHandler {

    void onOkHttpResponse(String response, int id);

    void onOkHttpError(String error);
}
