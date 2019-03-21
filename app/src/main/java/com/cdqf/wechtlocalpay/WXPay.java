package com.cdqf.wechtlocalpay;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.cdqf.plant_3des.Constants;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 微信本地加签
 * Created by liu on 2017/6/23.
 */

public class WXPay {

    private static String LAG = WXPay.class.getSimpleName();


    private static Map<String, String> resultunifiedorder = null;

    private static IWXAPI msgApi = null;

    private static PayReq req = null;

    /**
     * 初始化
     * @param context
     */
    public static void openIWXAPI(Context context){
        msgApi = WXAPIFactory.createWXAPI(context, null);
        req = new PayReq();
    }

    /**
     * 本地加签订单信息
     * @return
     */
    public static String getProductArgs(Context context) {
        StringBuffer xml = new StringBuffer();
        try {
            String nonceStr = getNonceStr();
            xml.append("<xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            //开放平台APP_ID
            packageParams.add(new BasicNameValuePair("appid", WXPayLocalConstants.APP_ID));
            //商品名称
            packageParams.add(new BasicNameValuePair("body", "hdsj ticket"));
            //商户平台MCH_ID
            packageParams.add(new BasicNameValuePair("mch_id", WXPayLocalConstants.MCH_ID));
            //随机数防重发
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            //回调地址
            packageParams.add(new BasicNameValuePair("notify_url", "https://www.baidu.com"));//写你们的回调地址
            //订单号
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            //金额
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            //交易类型
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            //生成签名
            String sign = getPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));
            //转化为xml
            String xmlString = toXml(packageParams);
            return xmlString;
        } catch (Exception e) {
            Toast.makeText(context, "订单加签失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * 生成随机号，防重发
     *
     * @return
     */
    private static String getNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成订单号,测试用，在客户端生成
     *
     * @return
     */
    private static String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成签名
     */
    private static String getPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    /**
     * 转换成xml
     */
    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("Simon", ">>>>" + sb.toString());
        return sb.toString();
    }

    /**
     * 保护订单信息
     * @param content
     * @return
     */
    public static Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
            return xml;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 调起微信支付参数
     * @param context
     * @param appid
     * @param mchid
     * @param result
     */
    public static void genPayReq(Context context,String appid,String mchid,Map<String, String> result) {
        req.appId = appid;
        req.partnerId = mchid;
        resultunifiedorder = result;
        if (resultunifiedorder != null) {
            req.prepayId = resultunifiedorder.get("prepay_id");
            req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
        } else {
            Toast.makeText(context, "prepayid为空", Toast.LENGTH_SHORT).show();
        }
        req.nonceStr = getNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genAppSign(signParams);
    }

    public static String getTime(){
        return String.valueOf(genTimeStamp());
    }

    private static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private static String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }

    /**
     * 调起微信支付
     */
    public static void sendPayReq(String appid) {
        msgApi.registerApp(appid);
        msgApi.sendReq(req);
    }
}
