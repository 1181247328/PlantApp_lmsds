package com.cdqf.plant_state;

import android.content.Context;
import android.content.SharedPreferences;

import com.cdqf.plant_class.User;
import com.google.gson.Gson;

/**
 * Created by liu on 2017/7/19.
 */

public class PlantPreferences {

    private String TAG = PlantPreferences.class.getSimpleName();

    private static final String LOG_USER = "hotel_loguser";

    private static Gson gson = new Gson();

    private static SharedPreferences seabedPreferences = null;

    private static SharedPreferences.Editor seabedEditor = null;

    /**
     * 保存用户的id,头像，昵称
     *
     * @param context
     * @param user
     */
    public static void setLogUserComment(Context context, User user) {
        seabedPreferences = context.getSharedPreferences(LOG_USER, 0);
        seabedEditor = seabedPreferences.edit();
        String loguser = gson.toJson(user);
        seabedEditor.putString("user",loguser);
        seabedEditor.commit();
    }

    public static User getLogUserComment(Context context){
        seabedPreferences = context.getSharedPreferences(LOG_USER, 0);
        String logUser = seabedPreferences.getString("user","");
        if(logUser.equals("")){
            return null;
        }
        User user = gson.fromJson(logUser,User.class);
        return user ;
    }

    /**
     * 删除登录用户的信息
     * @param context
     */
    public static void clearLogUserComment(Context context){
        seabedPreferences = context.getSharedPreferences(LOG_USER, 0);
        seabedEditor = seabedPreferences.edit().clear();
        seabedEditor.commit();
    }
}
