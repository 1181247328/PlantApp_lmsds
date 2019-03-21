package com.cdqf.wechtlocalpay;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdqf.plant_3des.Constants;

import java.util.Map;
import java.util.Set;

/**
 * 提交订单
 * Created by liu on 2017/6/23.
 */

public class WXPayIdAsyncTask extends AsyncTask<String, Void, Map<String, String>> {

    private String TAG = WXPayIdAsyncTask.class.getSimpleName();

    private Context context = null;

    private XProgressDialog dialog = null;

    private String boby = null;

    private String notify_url = null;

    private String total_fee = null;

    private String trade_type = null;

    public WXPayIdAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new XProgressDialog(context, "提交订单中...");
        dialog.show();
    }

    @Override
    protected Map<String, String> doInBackground(String... params) {
        String url = String.format(params[0]);
        String entity = WXPay.getProductArgs(context);
        byte[] buf = Util.httpPost(url, entity);
        String content = new String(buf);
        Map<String, String> xml = WXPay.decodeXml(content);
        return xml;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        super.onPostExecute(result);
        if (dialog != null) {
            dialog.dismiss();
        }
        Set<String> r = result.keySet();
        for (String s : r) {
            Log.e(TAG, "---onPostExecute---键---" + s + "---值---" + result.get(s));
        }
        //加签
        WXPay.genPayReq(context, Constants.APP_ID, Constants.MCH_ID, result);
        //调起微信
        WXPay.sendPayReq(Constants.APP_ID);
    }
}


