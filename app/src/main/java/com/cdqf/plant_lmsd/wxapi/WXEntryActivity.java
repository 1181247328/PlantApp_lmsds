package com.cdqf.plant_lmsd.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.greenrobot.event.EventBus;

/**
 * 微信登录
 * Created by XinAiXiaoWen on 2017/4/20.
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private String TAG = WXEntryActivity.class.getSimpleName();

    private IWXAPI api = null;

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "---微信---");

        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e(TAG, "---onReq---");
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "---" + resp.errCode);
        String result = "";
        if (resp != null) {
            resp = resp;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_SHORT).show();
                String code = ((SendAuth.Resp) resp).code;
                String get_access_token = getCodeRequest(code);
                Log.e(TAG, "---请求地址---" + get_access_token);
                HttpRequestWrap httpRequestWrap = new HttpRequestWrap(this);
                httpRequestWrap.setMethod(HttpRequestWrap.GET);
                httpRequestWrap.setCallBack(new RequestHandler(WXEntryActivity.this, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        Log.e(TAG, "---第一次---" + result);
                        if (status == RequestStatus.SUCCESS) {
                            if (result != null) {
                                JSONObject wxCodeJSON = JSON.parseObject(result);
                                int expires_in = 0;
                                expires_in = wxCodeJSON.getInteger("expires_in");
                                if (expires_in != 7200) {
                                    Toast.makeText(WXEntryActivity.this, "微信登录失败", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String access_token = wxCodeJSON.getString("access_token");
                                String openid = wxCodeJSON.getString("openid");
                                String get_user_info_url = getUserInfo(access_token, openid);
                                getUserInfo(get_user_info_url);
                            } else {
                                Toast.makeText(WXEntryActivity.this, "微信登录失败,请检查网络", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(WXEntryActivity.this, "微信登录失败,请检查网络", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }));
                httpRequestWrap.send(get_access_token);
                break;
            //发送取消
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_SHORT).show();
                finish();
                break;
            //发送被拒绝
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送拒绝";
                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_SHORT).show();
                finish();
                break;
            //发送返回
            default:
                result = "发送返回";
                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    private String getCodeRequest(String code) {
        String request = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

        request = request.replace("APPID", urlEnodeUTF8(Constants.WX_APP_ID));
        request = request.replace("SECRET", urlEnodeUTF8(Constants.WX_APP_SECRET));
        request = request.replace("CODE", urlEnodeUTF8(code));
        return request;
    }

    private String getUserInfo(String access_token, String openid) {
        String result = null;
        String info= "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        info = info.replace("ACCESS_TOKEN", urlEnodeUTF8(access_token));
        info = info.replace("OPENID", urlEnodeUTF8(openid));
        return info;
    }

    /**
     * 请求用户个人信息
     *
     * @param user_info_url
     */
    private void getUserInfo(String user_info_url) {
        Log.e(TAG, "---用户个人信息地址---" + user_info_url);
        HttpRequestWrap httpRequestWrap = new HttpRequestWrap(this);
        httpRequestWrap.setMethod(HttpRequestWrap.GET);
        httpRequestWrap.setCallBack(new RequestHandler(WXEntryActivity.this, new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG, "---用户个人信息---" + result);
                if (status == RequestStatus.SUCCESS) {
                    if (result != null) {
                        JSONObject wxUserJSON = JSON.parseObject(result);
                        String openid = wxUserJSON.getString("openid");
                        String nickname = wxUserJSON.getString("nickname");
                        String headimgurl = wxUserJSON.getString("headimgurl").replace("\"", "");
                        Log.e(TAG, "---" + headimgurl);
                        eventBus.post(new WXFind(openid, nickname, headimgurl));
                        finish();
                    } else {
                        Toast.makeText(WXEntryActivity.this, "微信登录失败,请检查网络", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(WXEntryActivity.this, "微信登录失败,请检查网络", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }));
        httpRequestWrap.send(user_info_url);
    }

    private String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "---恢复---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
    }
}
