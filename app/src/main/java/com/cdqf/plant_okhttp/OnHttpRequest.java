package com.cdqf.plant_okhttp;

public interface OnHttpRequest {

    void onOkHttpResponse(String response, int id);

    void onOkHttpError(String error);
}
