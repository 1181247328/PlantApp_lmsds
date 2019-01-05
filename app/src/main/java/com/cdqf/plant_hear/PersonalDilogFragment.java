package com.cdqf.plant_hear;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;
import tech.gaolinfeng.imagecrop.lib.IOUtil;
import tech.gaolinfeng.imagecrop.lib.ImageCropActivity;

import static android.app.Activity.RESULT_OK;


/**
 * 更换头像对话框
 * Created by XinAiXiaoWen on 2017/3/21.
 */

public class PersonalDilogFragment extends DialogFragment implements View.OnClickListener {

    //输出日志
    private String TAG = PersonalDilogFragment.class.getSimpleName();

    //逻辑层
    private PlantState planttate = PlantState.getPlantState();

    private EventBus personalEventBus = EventBus.getDefault();

    //相册
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;

    //相机
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;

    private static final int REQUEST_CODE_CROP = 3;

    //组件
    private View personalDialog = null;

    //本地相册
    private TextView tvSeabedHeadLocal = null;

    //拍照上传
    private TextView tvSeabedHeadTake = null;

    private boolean cropCircle = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        personalDialog = inflater.inflate(R.layout.personal_dilog_head, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //初始化前
        initPersonalDilogAgo();

        //初始化控件
        initPersonalDilogView();

        //注册监听器
        initPersonalDilogListener();

        //初始化后
        initPersonalDilogBack();

        return personalDialog;
    }

    /**
     * 初始化前
     */
    private void initPersonalDilogAgo() {

    }

    /**
     * 初始化控件
     */
    private void initPersonalDilogView() {
        //本地相册
        tvSeabedHeadLocal = (TextView) personalDialog.findViewById(R.id.tv_seabed_head_local);
        //拍照上传
        tvSeabedHeadTake = (TextView) personalDialog.findViewById(R.id.tv_seabed_head_take);
    }

    /**
     * 注册监听
     */
    private void initPersonalDilogListener() {
        //本地相册
        tvSeabedHeadLocal.setOnClickListener(this);
        //拍照上传
        tvSeabedHeadTake.setOnClickListener(this);
    }

    /**
     * 初始化后
     */
    private void initPersonalDilogBack() {

    }

    /**
     * 本地相册
     */
    private void photo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        i.putExtra("crop", false);
        i.putExtra("return-data", true);
        startActivityForResult(i, REQUEST_CODE_SELECT_IMAGE);
    }

    /**
     * 相机
     */
    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.PUBLIC_CACHE);
        if (file.exists()) {
            file.delete();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // set the image file name
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    private String getCropAreaStr() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int rectWidth = screenWidth / 2;
        int left = screenWidth / 2 - rectWidth / 2;
        int right = screenWidth / 2 + rectWidth / 2;
        int top = screenHeight / 2 - rectWidth / 2;
        int bottom = screenHeight / 2 + rectWidth / 2;
        return left + ", " + top + ", " + right + ", " + bottom;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //本地相册
            case R.id.tv_seabed_head_local:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
                    } else {
                        photo();
                    }
                } else {
                    photo();
                }
                break;
            //相机操作
            case R.id.tv_seabed_head_take:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 8);
                    } else {
                        camera();
                    }
                } else {
                    camera();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //相册
            case REQUEST_CODE_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    ContentResolver cr = getContext().getContentResolver();
                    InputStream is = null;
                    FileOutputStream fos = null;
                    boolean writeSucceed = true;
                    try {
                        is = cr.openInputStream(uri);
                        fos = new FileOutputStream(FileUtil.IMG_CACHE1);
                        int read = 0;
                        byte[] buffer = new byte[4096];
                        while ((read = is.read(buffer)) > 0) {
                            fos.write(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        writeSucceed = false;
                    } finally {
                        IOUtil.closeQuietly(is);
                        IOUtil.closeQuietly(fos);
                    }
                    if (writeSucceed) {
                        Intent intent = ImageCropActivity.createIntent(getActivity(), FileUtil.IMG_CACHE1, FileUtil.IMG_CACHE2, getCropAreaStr(), cropCircle);
                        startActivityForResult(intent, REQUEST_CODE_CROP);
                    } else {
                        Toast.makeText(getActivity(), "无法打开图片文件，您的sd卡是否已满？", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
                break;
            //相机返回操作
            case REQUEST_CODE_TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Intent intent = ImageCropActivity.createIntent(getActivity(), FileUtil.PUBLIC_CACHE, FileUtil.IMG_CACHE2, getCropAreaStr(), cropCircle);
                    startActivityForResult(intent, REQUEST_CODE_CROP);
                } else {
                    // do nothing
                }
                break;
            //裁剪返回
            case REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.IMG_CACHE2);
                    planttate.setHeadBitmap(bitmap);
                    planttate.saveBitmapFile(bitmap, FileUtil.IMG_CACHE4);
                    personalEventBus.post(new ShelvesImageFind((bitmap)));
                    dismiss();
                }
                break;
        }
    }
}
