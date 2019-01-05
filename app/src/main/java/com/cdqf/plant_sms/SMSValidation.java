package com.cdqf.plant_sms;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.cdqf.plant_state.PlantState;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import de.greenrobot.event.EventBus;

/**
 * Created by liu on 2017/7/20.
 */

public class SMSValidation {
    private static String TAG = SMSValidation.class.getSimpleName();

    private static boolean isValidation = false;

    private static PlantState plantState = PlantState.getPlantState();

    private static EventBus eventBus = EventBus.getDefault();

    private static EventHandler eventHandler = null;

    private static PassWordTimer passWordTimer = null;

    private static int timerNumber = 60;

    private static String smsPhone = null;

    private static String smsAreaCode = null;

    private static Timer timer = new Timer();

    private static Context smsContext = null;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    timerNumber--;
                    Log.e(TAG, "---" + timerNumber);
                    if (timerNumber == 0) {
                        timerNumber = 60;
                        passwordClose();
                        eventBus.post(new SmsTimerFind(0));
                        return;
                    }
                    eventBus.post(new SmsTimerFind(timerNumber));
                    break;
                case 0x002:
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    Log.e(TAG, "---event---" + event + "---result---" + result);
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) { //获取验证码
                            plantState.initToast(smsContext, "发送验证码成功", true, 0);
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码
                            plantState.initToast(smsContext, "提交成功", true, 0);
                            eventBus.post(new CommitFind(true));
                        } else {
                            Log.d(TAG, data.toString());
                            plantState.initToast(smsContext, "验证码不正确", true, 0);
                            passwordClose();
                            eventBus.post(new CommitFind(false));
                        }
                    }
                    break;
            }
        }
    };

    static class PassWordTimer extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(0x001);
            eventBus.post(new SmsFind());
        }
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void initSms(Context context) {
        smsContext = context;
        Log.e(TAG, "---初始化");
        SMSSDK.initSDK(smsContext, Constants.APP_KEY, Constants.APP_SECRET);
        if (!eventBus.isRegistered(smsContext)) {
            eventBus.register(smsContext);
        }
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                Message message = handler.obtainMessage(0x002);
                message.arg1 = i;
                message.arg2 = i1;
                message.obj = o;
                handler.sendMessage(message);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    public static boolean validation(String areaCode, String phone) {
        smsAreaCode = areaCode;
        smsPhone = phone;
        if (isValidation) {
            plantState.initToast(smsContext, "您已经发送了验证码", true, 0);
            return false;
        } else {
            if (phone.length() == 11) {
                if (plantState.isMobile(phone)) {
                    isValidation = true;
                    Log.e(TAG, "---areaCode---" + areaCode + "---phone---" + phone);
                    SMSSDK.getVerificationCode(areaCode, phone);
                    passWordTimer = new PassWordTimer();
                    timer.schedule(passWordTimer, 1000, 1000);
                } else {
                    plantState.initToast(smsContext, "您输入的不是手机号码", true, 0);
                    isValidation = false;
                }
                return true;
            } else {
                plantState.initToast(smsContext, "您输入的号码位数不够", true, 0);
                return false;
            }
        }
    }

    public static void passwordClose() {
        if (passWordTimer != null) {
            passWordTimer.cancel();
        }
        timerNumber = 60;
        isValidation = false;
    }

}
