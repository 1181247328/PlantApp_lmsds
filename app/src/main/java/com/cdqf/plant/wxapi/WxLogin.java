package com.cdqf.plant.wxapi;

import android.content.Context;

import com.cdqf.plant_state.PlantState;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by liu on 2017/7/19.
 */

public class WxLogin {

    private final static String TOAST = "用户未安装微信";

    //微信登录
    private static IWXAPI WXapi = null;

    private static PlantState plantState = new PlantState();

    /**
     * 微信登录
     */
    public static void loginWx(Context context) {
        WXapi = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, true);
        WXapi.registerApp(Constants.WX_APP_ID);
        if (WXapi != null && WXapi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo";
            WXapi.sendReq(req);
        } else {
            plantState.initToast(context,TOAST,true,0);
        }
    }
}
