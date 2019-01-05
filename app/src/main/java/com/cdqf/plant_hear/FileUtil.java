package com.cdqf.plant_hear;

import android.os.Environment;

import java.io.File;

/**
 * Created by gaolf on 15/12/24.
 */
public abstract class FileUtil {

    public static final String IMG_CACHE1 = App.getInstance().getFilesDir().getAbsolutePath() + "/img_cache1";
    public static final String IMG_CACHE2 = App.getInstance().getFilesDir().getAbsolutePath() + "/img_cache2";

    //宝贝上架图片路径
    public static final String IMG_CACHE3 = Environment.getExternalStorageDirectory() + File.separator + "shelves.png";

    //头像的路径
    public static final String IMG_CACHE4 = Environment.getExternalStorageDirectory() + File.separator + "head.png";

    //上传的图片
    public static final String IMG_TRAVE = Environment.getExternalStorageDirectory() + File.separator;

    //QQ头像的路径
    public static final String IMG_CACHE5 = Environment.getExternalStorageDirectory() + File.separator + "QQhead.png";

    public static final String APK = Environment.getExternalStorageDirectory() + File.separator;

    public static final String PUBLIC_CACHE = App.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/zyb_cache";

}
