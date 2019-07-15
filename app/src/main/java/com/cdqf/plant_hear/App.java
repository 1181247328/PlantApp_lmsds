package com.cdqf.plant_hear;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.cdqf.exception.LogToFile;
import com.wanjian.cockroach.Cockroach;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by gaolf on 15/12/24.
 */
public class App extends MultiDexApplication {
    private String TAG = App.class.getSimpleName();
    private static App sInstance = new App();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        LogToFile.init(getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();

        OkHttpUtils.initClient(okHttpClient);

        Cockroach.install(new Cockroach.ExceptionHandler() {
            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e(TAG, "--->CockroachException:" + thread + "<---", throwable);
                            LogToFile.e(TAG, "Exception Happend\n" + thread + "\n" + throwable.toString());
                            Toast.makeText(App.this, "Exception Happend\n" + thread + "\n" + throwable.toString(), Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App getInstance() {
        return sInstance;
    }
}
