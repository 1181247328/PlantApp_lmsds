package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_find.TravelTextFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;

import de.greenrobot.event.EventBus;

/**
 * 添加文字
 * Created by liu on 2017/10/27.
 */

public class AddTextActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = TravelTitleActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    private RelativeLayout rlAddtextReturn = null;

    private TextView tvAddtextComplete = null;

    private EditText etAddtextContext = null;

    private int position;

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
        setContentView(R.layout.activity_addtext);

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
        position = intent.getIntExtra("position",0);
    }

    private void initView() {
        rlAddtextReturn = this.findViewById(R.id.rl_addtext_return);
        tvAddtextComplete = this.findViewById(R.id.tv_addtext_complete);
        etAddtextContext = this.findViewById(R.id.et_addtext_context);
    }

    private void initListener() {
        rlAddtextReturn.setOnClickListener(this);
        tvAddtextComplete.setOnClickListener(this);
    }

    private void initBack() {

    }

    private void initIntent(Class<?> activity, String text) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("text", text);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_addtext_return:
                finish();
                break;

            //完成
            case R.id.tv_addtext_complete:
                if (etAddtextContext.length() <= 0) {
                    plantState.initToast(context, "内容不能为空", true, 0);
                    return;
                }
                String text = etAddtextContext.getText().toString();
                eventBus.post(new TravelTextFind(text,position));
                finish();
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
