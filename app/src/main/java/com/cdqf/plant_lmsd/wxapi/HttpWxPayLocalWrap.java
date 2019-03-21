package com.cdqf.plant_lmsd.wxapi;

import android.content.Context;
import android.widget.Toast;

import com.cdqf.plant_utils.HttpRequestWrap;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class HttpWxPayLocalWrap {
    private static String TAG = HttpWxPayWrap.class.getSimpleName();

    private static IWXAPI api = null;

    private static HttpRequestWrap httpRequestWrap = null;

    /**
     * 判断是不是安装了微信
     *
     * @param context 上下文
     * @param appId   申请的APP_ID
     * @return
     */
    public static boolean isWxApp(Context context, String appId) {
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return false;
        }
        return true;
    }

    private static void initToast(Context context, String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
}
