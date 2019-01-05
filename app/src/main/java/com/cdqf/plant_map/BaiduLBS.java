package com.cdqf.plant_map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;

import de.greenrobot.event.EventBus;

/**
 * Created by liu on 2017/7/22.
 */

public class BaiduLBS {

    private static String TAG = BaiduLBS.class.getSimpleName();

    private static LocationClient locationClient;

    public static MyLocationListenner myListener = new MyLocationListenner();

    private static EventBus eventBus = EventBus.getDefault();

    private static boolean isLaction = true;

    private static Map map = null;

    //经度
    private static double longitude;

    //纬度
    private static double latitude;

    //城市
    private static String city;

    //省
    private static String province;

    //当前位置
    private static String locationx;

    //区以下
    private static String area;

    //区
    private static String district;

    //街
    private static String street;

    //号
    private static String streetNumber;

    public static void init(Activity activity) {
        //声明定位SDK核心类
        locationClient = new LocationClient(activity);
        //注册监听
        locationClient.registerLocationListener(myListener);
        initLaction();
    }

    private static void initLaction() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        locationClient.setLocOption(option);
        locationClient.start();
    }

    public static Map getMap() {
        return map;
    }

    /**
     * 经度
     *
     * @return
     */
    public static double getLongitude() {
        return longitude;
    }

    /**
     * 纬度
     *
     * @return
     */
    public static double getLatitude() {
        return latitude;
    }

    public static String getCity() {
        return city;
    }

    /**
     * 控制经度和地区的获取
     * @param isLaction
     */
    public static void setLaction(boolean isLaction){
        BaiduLBS.isLaction = isLaction;
    }

    /**
     * 定位SDK监听函数
     */
    static class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e(TAG, "---返回码---" + location.getLocType() + "---经度---" + location.getLongitude() + "---纬度---" + location.getLatitude() + "---城市---" + location.getCity() + "---当前位置---" + location.getAddrStr());
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //省
            province = location.getProvince();
            //城市
            city = location.getCity();
            //当前位置
            locationx = location.getAddrStr();
            //区
            district = location.getDistrict();
            //街
            street = location.getStreet();
            //号
            streetNumber = location.getStreetNumber();
            if (longitude != 4.9E-324 && latitude != 4.9E-324
                    && province != null && city != null && locationx != null
                    && district != null && street != null && streetNumber != null) {
                if (isLaction) {
                    isLaction = false;
                    map = new Map();
                    //经度
                    map.setLongitude(location.getLongitude());
                    //纬度
                    map.setLatitude(location.getLatitude());
                    //省
                    map.setProvince(location.getProvince());
                    //当前城市
                    map.setCity(location.getCity());
                    //当前位置
                    map.setLocation(location.getAddrStr());
                    //区
                    map.setDistrict(location.getDistrict());
                    //街
                    map.setStreet(location.getStreet());
                    //号
                    map.setStreetNumber(location.getStreetNumber());
                    map.setArea(location.getDistrict() + location.getStreet() + location.getStreetNumber());
                    eventBus.post(new AddrStrFind(map));
                }
            }

        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    /**
     * 权限
     */
    public static void permission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.CALL_PHONE,},
                        0
                );
            }
        }
    }

}
