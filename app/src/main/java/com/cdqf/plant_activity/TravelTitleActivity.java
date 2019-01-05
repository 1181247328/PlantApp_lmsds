package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;

/**
 * 输入游记标题
 * Created by liu on 2017/10/26.
 */

public class TravelTitleActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = TravelTitleActivity.class.getSimpleName();

    private Context context = null;

    public static TravelTitleActivity TravelTitleActivity = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    //取消
    private TextView tvTraveltitleCancel = null;

    //下一步
    private TextView tvTraveltitleNext = null;

    //输入标题
    private EditText etTraveltitleTitle = null;

    //输入字数
    private TextView tvTraveltitleNumber = null;

    private int position = 0;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_traveltitle);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }

        initAgo();

        initView();

        initListener();

        initBack();
    }

    private void initAgo() {
        context = this;
        httpRequestWrap = new HttpRequestWrap(context);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        TravelTitleActivity = this;
    }

    private void initView() {
        tvTraveltitleCancel = this.findViewById(R.id.tv_traveltitle_cancel);
        tvTraveltitleNext = this.findViewById(R.id.tv_traveltitle_next);
        etTraveltitleTitle = this.findViewById(R.id.et_traveltitle_title);
        tvTraveltitleNumber = this.findViewById(R.id.tv_traveltitle_number);
    }

    private void initListener() {
        tvTraveltitleCancel.setOnClickListener(this);
        tvTraveltitleNext.setOnClickListener(this);
        etTraveltitleTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etTraveltitleTitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                }
                return false;
            }
        });
    }

    private void initBack() {

    }

    private void initIntent(Class<?> activity,String title) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("title",title);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //取消
            case R.id.tv_traveltitle_cancel:
                finish();
                break;

            //下一步
            case R.id.tv_traveltitle_next:
                title = etTraveltitleTitle.getText().toString();
                if(title.length()<=0){
                    plantState.initToast(context,"请输入游记标题",true,0);
                    return;
                }
                initIntent(TravelContextActivity.class,title);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "---恢复---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
    }
}
