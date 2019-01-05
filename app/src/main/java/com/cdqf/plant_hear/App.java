package com.cdqf.plant_hear;

import com.mob.MobApplication;

/**
 * Created by gaolf on 15/12/24.
 */
public class App extends MobApplication {
    private static App sInstance = new App();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static App getInstance() {
        return sInstance;
    }
}
