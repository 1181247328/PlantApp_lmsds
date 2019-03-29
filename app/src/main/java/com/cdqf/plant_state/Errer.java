package com.cdqf.plant_state;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_utils.RequestStatus;

/**
 * Created by liu on 2017/11/27.
 */

public class Errer {

    private static String TAG = Errer.class.getSimpleName();

    private static PlantState plantState = PlantState.getPlantState();

    //状态
    public static String isColltion(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //提示
                String message = resultJSON.getString("Message");
                if (isStatus) {
                    return message;
                } else {
                    return message;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    //判断是不是合法JSON并解密
    public static String isResult(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //提示
                String message = resultJSON.getString("Message");
                if (isStatus) {
                    String dataJSON = null;
                    int statusCode = resultJSON.getInteger("StatusCode");
                    if (!code(context, statusCode)) {
                        if (statusCode == 1001) {
                            return 1001 + "";
                        }
                        return null;
                    }
                    try {
                        dataJSON = DESUtils.decodeDES(resultJSON.getString("Data"), Constants.secretKey.substring(0, 8));
                        Log.e(TAG, "---解密成功---" + dataJSON);
                    } catch (Exception e) {
                        Log.e(TAG, "---解密失败---" + dataJSON);
                        e.printStackTrace();
                    }
                    if (dataJSON == null) {
                        plantState.initToast(context, "解密失败", true, 0);
                        return null;
                    }
                    return dataJSON;
                } else {
                    plantState.initToast(context, message, true, 0);
                    return null;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    public static String isSett(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //提示
                String message = resultJSON.getString("Message");
                if (isStatus) {
                    String dataJSON = null;
                    int statusCode = resultJSON.getInteger("StatusCode");
                    if (!code(context, statusCode)) {
                        if (statusCode == 1001) {
                            return 1001 + "";
                        }
                        return null;
                    }
                    try {
                        dataJSON = DESUtils.decodeDES(resultJSON.getString("Data"), Constants.secretKey.substring(0, 8));
                        Log.e(TAG, "---解密成功---" + dataJSON);
                    } catch (Exception e) {
                        Log.e(TAG, "---解密失败---" + dataJSON);
                        e.printStackTrace();
                    }
                    if (dataJSON == null) {
                        plantState.initToast(context, "解密失败", true, 0);
                        return null;
                    }
                    return dataJSON;
                } else {
                    plantState.initToast(context, message, true, 0);
                    return null;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    /**
     * 状态码提示
     *
     * @param code
     */
    private static boolean code(Context context, int code) {
        switch (code) {
            //执行中出错
            case 0001:
                plantState.initToast(context, context.getResources().getString(R.string.code_one), true, 0);
                return false;
            //执行成功
            case 1000:
                Log.e(TAG, "---执行成功----");
                return true;
            //列表为空
            case 1001:
                Log.e(TAG, "---列表为空----");
                return false;
            default:
                return true;
        }
    }

    /**
     * 收货地址
     *
     * @param context
     * @param result
     * @param status
     * @return
     */
    public static String isAddress(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //内容
                String data = resultJSON.getString("Data");
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //状态码
                int StatusCode = resultJSON.getInteger("StatusCode");
                //提示
                String message = resultJSON.getString("Message");
                if (data == null) {
                    if (StatusCode == 1001) {
                        return "列表为空";
                    } else {
                        return StatusCode + "";
                    }
                } else {
                    String dataJSON = null;
                    try {
                        dataJSON = DESUtils.decodeDES(resultJSON.getString("Data"), Constants.secretKey.substring(0, 8));
                        Log.e(TAG, "---解密成功---" + dataJSON);
                    } catch (Exception e) {
                        Log.e(TAG, "---解密失败---" + dataJSON);
                        e.printStackTrace();
                    }
                    if (dataJSON == null) {
                        plantState.initToast(context, "解密失败", true, 0);
                        return null;
                    }
                    return dataJSON;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    public static String isCode(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //内容
                String data = resultJSON.getString("Data");
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //状态码
                int StatusCode = resultJSON.getInteger("StatusCode");
                //提示
                String message = resultJSON.getString("Message");
                if (data == null) {
                    if (StatusCode == 1000) {
                        return message;
                    } else {
                        return message;
                    }
                } else {
                    String dataJSON = null;
                    try {
                        dataJSON = DESUtils.decodeDES(resultJSON.getString("Data"), Constants.secretKey.substring(0, 8));
                        Log.e(TAG, "---解密成功---" + dataJSON);
                    } catch (Exception e) {
                        Log.e(TAG, "---解密失败---" + dataJSON);
                        e.printStackTrace();
                    }
                    if (dataJSON == null) {
                        plantState.initToast(context, "解密失败", true, 0);
                        return null;
                    }
                    return dataJSON;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    public static String isTrave(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //内容
                String data = resultJSON.getString("Data");
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //状态码
                int StatusCode = resultJSON.getInteger("StatusCode");
                //提示
                String message = resultJSON.getString("Message");
                if (StatusCode == 1000) {
                    return message;
                } else {
                    return message;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    public static String isCart(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //提示
                String message = resultJSON.getString("Message");
                if (isStatus) {
                    String dataJSON = null;
                    int statusCode = resultJSON.getInteger("StatusCode");
                    String data = resultJSON.getString("Data");
                    if (data == null) {
                        return null;
                    }
                    try {
                        dataJSON = DESUtils.decodeDES(data, Constants.secretKey.substring(0, 8));
                        Log.e(TAG, "---解密成功---" + dataJSON);
                    } catch (Exception e) {
                        Log.e(TAG, "---解密失败---" + dataJSON);
                        e.printStackTrace();
                    }
                    if (dataJSON == null) {
                        plantState.initToast(context, "解密失败", true, 0);
                        return null;
                    }
                    return dataJSON;
                } else {
//                    plantState.initToast(context, message, true, 0);
                    return null;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

    /**
     * 车辆信息
     *
     * @param context
     * @param result
     * @param status
     * @return
     */
    public static String isCarletter(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject resultJSON = JSON.parseObject(result);
                //状态
                boolean isStatus = resultJSON.getBoolean("Status");
                //提示
                String message = resultJSON.getString("Message");
                if (isStatus) {
                    String dataJSON = null;
                    int statusCode = resultJSON.getInteger("StatusCode");
                    String data = resultJSON.getString("Data");
                    if (data == null) {
                        return null;
                    }
                    if (statusCode != 1) {
                        return statusCode + "";
                    }
                    if (!TextUtils.equals(message, "成功")) {
                        return message;
                    }
                    try {
                        dataJSON = DESUtils.decodeDES(data, Constants.secretKey.substring(0, 8));
                        Log.e(TAG, "---解密成功---" + dataJSON);
                    } catch (Exception e) {
                        Log.e(TAG, "---解密失败---" + dataJSON);
                        e.printStackTrace();
                    }
                    if (dataJSON == null) {
                        plantState.initToast(context, "解密失败", true, 0);
                        return null;
                    }
                    return dataJSON;
                } else {
                    plantState.initToast(context, message, true, 0);
                    return null;
                }
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }
    //天气
    public static String isWeather(Context context, String result, RequestStatus status) {
        Log.e(TAG, "---Errer---" + result);
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                return result;
            } else {
                plantState.initToast(context, "获取数据失败,请检查网络", true, 0);
                return null;
            }
        } else {
            plantState.initToast(context, "请求失败,请检查网络", true, 0);
            return null;
        }
    }

}
