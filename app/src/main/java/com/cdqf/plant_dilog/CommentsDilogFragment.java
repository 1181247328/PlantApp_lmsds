package com.cdqf.plant_dilog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;

/**
 * 评论对话框
 * Created by liu on 2017/10/25.
 */

public class CommentsDilogFragment extends DialogFragment implements View.OnClickListener{

    private String TAG = CommentsDilogFragment.class.getSimpleName();

    private View view = null;

    //取消
    private ImageView ivCommentsDilogCancel = null;

    //数量
    private TextView tvCommentsDilogNumber = null;

    //评论内容
    private EditText etCommentsDilogContext = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_comments, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //初始化前
        initAgo();

        //初始化控件
        initView();

        //注册监听器
        initListener();

        //初始化后
        initBack();

        return view;
    }

    /**
     * 初始化前
     */
    private void initAgo() {

    }

    /**
     * 初始化控件
     */
    private void initView() {
        ivCommentsDilogCancel = (ImageView) view.findViewById(R.id.iv_comments_dilog_cancel);
        tvCommentsDilogNumber = (TextView) view.findViewById(R.id.tv_comments_dilog_number);
        etCommentsDilogContext = (EditText) view.findViewById(R.id.et_comments_dilog_context);
    }


    /**
     * 注册监听器
     */
    private void initListener() {
        ivCommentsDilogCancel.setOnClickListener(this);
        etCommentsDilogContext.addTextChangedListener(new TextWatcher() {
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
        etCommentsDilogContext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    dismiss();
                }
                return false;
            }
        });
    }

    /**
     * 初始化后
     */
    private void initBack() {
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_comments_dilog_cancel:
                dismiss();
                break;
        }
    }
}
