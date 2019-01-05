package com.cdqf.plant.wxapi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * 微信
 * Created by liu on 2017/6/29.
 */

public class HttpWxPayWrap {

    private static String TAG = HttpWxPayWrap.class.getSimpleName();

    private static IWXAPI api = null;

    private static HttpRequestWrap httpRequestWrap = null;

    /**
     * 判断是不是安装了微信
     * @param context 上下文
     * @param appId 申请的APP_ID
     * @return
     */
    public static boolean isWxApp(Context context,String appId){
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return false;
        }
        return true;
    }

    /**
     * 请求的方式
     * @param context 上下文
     * @param appId 微信开放平台申请的APP_ID
     * @param isPost 请求方式
     * @param orderKey 键
     * @param order 值
     */
    public static void wxPayParamss(final Context context,String appId,String isPost,String orderKey,String order,String address){
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return;
        }
        Log.e(TAG,"---"+isPost+"---"+orderKey+"---"+order+"---"+address);
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(isPost);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "微信支付中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG,"---微信支付---"+result);
                wxPostJSON(context,result,status);
            }
        }));
        Map<String, Object> paramss = new HashMap<String, Object>();
        paramss.put(orderKey, order);
        httpRequestWrap.send(address, paramss);
    }

    /**
     * 请求参数为两个
     * @param context
     * @param appId
     * @param isPost
     * @param orderKey
     * @param order
     * @param idKey
     * @param id
     */
    public static void wxPayParamss(final Context context,String appId,String isPost,String orderKey,String order,String idKey,String id,String address){
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return;
        }
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(isPost);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "微信支付中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG,"---微信支付---"+result);
                wxPostJSON(context,result,status);
            }
        }));
        Map<String, Object> paramss = new HashMap<String, Object>();
        paramss.put(orderKey, order);
        paramss.put(idKey, id);
        httpRequestWrap.send(address, paramss);
    }

    /**
     * 请求参数处理
     * @param context
     * @param result
     * @param status
     */
    private static void wxPostJSON(final Context context,String result, RequestStatus status){
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject pay = JSON.parseObject(result);
                String retData = null;
                try {
                    retData = JSON.parseObject(pay.getString("result")).getString("retData");
                    if (retData == null) {
                        initToast(context, "JSON有误!");
                        return;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    initToast(context, "数据返回有误!请检查网络");
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(retData);
                //APP_ID
                String appId = jsonObject.getString("appid");
                String partnerId = jsonObject.getString("partnerid");
                String prepayId = jsonObject.getString("prepayid");
                String packageValue = jsonObject.getString("package");
                String nonceStr = jsonObject.getString("noncestr");
                int timestamp = jsonObject.getInteger("timestamp");
                String sign = jsonObject.getString("sign");
                Log.e(TAG,"---appId---"+appId+"---partnerId---"+partnerId+"---prepayId---"+prepayId+"---nonceStr---"+nonceStr+"---sign---"+sign);
                PayReq wechatPay = new PayReq();
                wechatPay.appId = appId;
                wechatPay.partnerId = partnerId;
                wechatPay.prepayId = prepayId;
                wechatPay.packageValue = packageValue;
                wechatPay.nonceStr = nonceStr;
                wechatPay.timeStamp = timestamp+"";
                wechatPay.sign = sign;
                api.sendReq(wechatPay);
            } else {
                initToast(context, "支付失败,请检查网络");
            }
        } else {
            initToast(context, "支付失败,请检查网络");
        }
    }

    private static void initToast(Context context,String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
}
