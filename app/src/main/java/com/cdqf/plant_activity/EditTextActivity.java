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

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_find.TravelEditTextFind;
import com.cdqf.plant_find.TravelTitileFind;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 编辑游记标题和内容
 * Created by liu on 2017/12/19.
 */

public class EditTextActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = EditTextActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    //返回
    @BindView(R.id.rl_addtext_return)
    public RelativeLayout rlAddtextReturn = null;

    //标题
    @BindView(R.id.tv_addtext_title)
    public TextView tvAddtextTitle = null;

    //完成
    @BindView(R.id.tv_addtext_complete)
    public TextView tvAddtextComplete = null;

    //内容
    @BindView(R.id.et_addtext_context)
    public EditText etAddtextContext = null;

    private int type;

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
        setContentView(R.layout.activity_edittext);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }
        initAgo();

        initBack();
    }

    private void initAgo() {
        context = this;
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        if (type == 1) {
            position = intent.getIntExtra("position", 0);
        }
    }

    private void initBack() {
        //发表游记
        if (type == 0) {
            etAddtextContext.setSingleLine(true);
            tvAddtextTitle.setText(plantState.getPlantString(context, R.string.travel_edit));
        }
    }


    @OnClick({R.id.rl_addtext_return,R.id.tv_addtext_complete})
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
                if (type == 0) {
                    eventBus.post(new TravelTitileFind(etAddtextContext.getText().toString()));
                } else if (type == 1) {
                    eventBus.post(new TravelEditTextFind(etAddtextContext.getText().toString(), position));
                }
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
