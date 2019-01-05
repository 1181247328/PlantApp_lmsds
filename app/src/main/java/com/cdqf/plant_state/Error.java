package com.cdqf.plant_state;

import android.content.Context;

/**
 * Created by liu on 2017/7/19.
 */

public class Error {

    private static String TAG = Error.class.getSimpleName();

    private static PlantState plantState = PlantState.getPlantState();

    /**
     * 通过error提示给用户
     * @param context
     * @param error
     * @return
     */
    public static boolean errorResult(Context context,int error){
        switch(error){
            case 0:
                return true;
            case 1:
                plantState.initToast(context,"",true,0);
                return false;
            default:
                plantState.initToast(context,"",true,0);
                return false;
        }
    }

    /**
     * 通过resultError提示给自己
     * @param context
     * @param tast
     */
    public static void error(Context context,String tast){
        plantState.initToast(context,tast,true,0);
    }
}
