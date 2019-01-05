package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_adapter.TravelContextAdapter;
import com.cdqf.plant_class.PublishedFind;
import com.cdqf.plant_class.SaveFind;
import com.cdqf.plant_class.Travel;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.TraveImageFind;
import com.cdqf.plant_find.TraveReturnFind;
import com.cdqf.plant_find.TravelEditTextFind;
import com.cdqf.plant_find.TravelFind;
import com.cdqf.plant_find.TravelTextFind;
import com.cdqf.plant_find.TravelTitileFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.ListViewForScrollView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 开始游记
 * Created by liu on 2017/10/27.
 */

public class TravelContextActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = TravelContextActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    //取消
    private RelativeLayout rlTravelcontextReturn = null;

    //发表游记
    private RelativeLayout rlTravelcontextPublished = null;

    //保存草稿
    private RelativeLayout rlTravelcontextSave = null;

    //头部
    private RelativeLayout rlTravelcontextHear = null;

    //头部图片
    private ImageView ivTravelcontextHear = null;

    //更换封面
    private TextView tvTravelcontextReplace = null;

    //标题
    private TextView tvTravelcontextTitle = null;

    //编辑
    private TextView tvTravelcontextEdit = null;

    private LinearLayout llTravelcontextLayout = null;

    //添加文字
    private LinearLayout llTravelcontextText = null;

    //插入图片
    private LinearLayout llTravelcontextPicture = null;

    //游记内容
    private ListViewForScrollView lvfsvTravelcontextList = null;

    private TravelContextAdapter travelContextAdapter = null;

    //游记内容
    private String title = null;

    private HttpRequestWrap httpRequestWrap = null;

    private Gson gson = new Gson();

    private int travelPosition = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    rlTravelcontextHear.setVisibility(View.VISIBLE);
                    imageLoader.displayImage("file://" + defalutPath, ivTravelcontextHear, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                    break;
            }
        }
    };

    private String defalutPath = null;

    private boolean isDefalutPath = false;

    private int type = 0;

    private int draftPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_travelcontext);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();
    }

    private void initAgo() {
        context = this;
        ButterKnife.bind(this);
        imageLoader = plantState.getImageLoader(context);
        httpRequestWrap = new HttpRequestWrap(context);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        if (type == 0) {
            title = intent.getStringExtra("title");
        } else if (type == 1) {
            draftPosition = intent.getIntExtra("position", 0);
        }
    }

    private void initView() {
        rlTravelcontextReturn = this.findViewById(R.id.rl_travelcontext_return);
        rlTravelcontextPublished = this.findViewById(R.id.rl_travelcontext_published);
        rlTravelcontextSave = this.findViewById(R.id.rl_travelcontext_save);
        rlTravelcontextHear = this.findViewById(R.id.rl_travelcontext_hear);
        ivTravelcontextHear = this.findViewById(R.id.iv_travelcontext_hear);
        tvTravelcontextReplace = this.findViewById(R.id.tv_travelcontext_replace);
        tvTravelcontextTitle = this.findViewById(R.id.tv_travelcontext_title);
        tvTravelcontextEdit = this.findViewById(R.id.tv_travelcontext_edit);
        llTravelcontextLayout = this.findViewById(R.id.ll_travelcontext_layout);
        llTravelcontextText = this.findViewById(R.id.ll_travelcontext_text);
        llTravelcontextPicture = this.findViewById(R.id.ll_travelcontext_picture);
        lvfsvTravelcontextList = this.findViewById(R.id.lvfsv_travelcontext_list);
    }

    private void initAdapter() {
        travelContextAdapter = new TravelContextAdapter(context);
        lvfsvTravelcontextList.setAdapter(travelContextAdapter);
    }

    private void initListener() {
        rlTravelcontextReturn.setOnClickListener(this);
        rlTravelcontextPublished.setOnClickListener(this);
        rlTravelcontextSave.setOnClickListener(this);
        tvTravelcontextTitle.setOnClickListener(this);
        tvTravelcontextEdit.setOnClickListener(this);
        llTravelcontextText.setOnClickListener(this);
        llTravelcontextPicture.setOnClickListener(this);
        tvTravelcontextReplace.setOnClickListener(this);
        lvfsvTravelcontextList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void initBack() {
        if (type == 0) {
            rlTravelcontextHear.setVisibility(View.GONE);
            tvTravelcontextTitle.setText(title);
        } else if (type == 1) {
            tvTravelcontextTitle.setText(plantState.getDraftList().get(draftPosition).getTitle());
            //编辑
            if (plantState.getDraftList().get(draftPosition).getHttpPic() != null) {
                defalutPath = plantState.getDraftList().get(draftPosition).getHttpPic();
                rlTravelcontextHear.setVisibility(View.VISIBLE);
                imageLoader.displayImage(defalutPath, ivTravelcontextHear, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
            } else {
                rlTravelcontextHear.setVisibility(View.GONE);
            }
        }
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int type) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void initTravel(boolean isTravel, int toast) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(toast), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户发表游记解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户发表游记解密成功---" + data);
                if (TextUtils.equals(data, "游记编辑成功")) {
                    if (TravelTitleActivity.TravelTitleActivity != null) {
                        TravelTitleActivity.TravelTitleActivity.finish();
                    }
                    plantState.getTravelList().clear();
                    plantState.getTravelOneList().clear();
                    finish();
                } else {
                    //TODO
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int tId = 0;
        if (isTravel) {
            tId = 0;
        } else {
            tId = 1;
        }
        params.put("tId", tId);
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        params.put("title", title);
        String content = gson.toJson(plantState.getTravelList());
        params.put("content", content);
        for (int i = 0; i < plantState.getTravelOneList().size(); i++) {
            if (plantState.getTravelOneList().get(i).isText()) {
                params.put(plantState.getTravelOneList().get(i).getPutName(), new File(plantState.getTravelOneList().get(i).getPictureList().get(0)));
            }
        }
        int savingType = 0;
        if (isTravel) {
            savingType = 0;
        } else {
            savingType = 1;
        }
        params.put("savingType", savingType);
        String defaultPicName = "image0";
        params.put(defaultPicName, new File(defalutPath));
        params.put("defaultPic", defaultPicName);
        httpRequestWrap.send(PlantAddress.STRATE_TRAVE, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //取消
            case R.id.rl_travelcontext_return:
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt("是否取消", 9);
                promptDilogFragment.show(getSupportFragmentManager(), "是否取消");
                break;
            //发表游记
            case R.id.rl_travelcontext_published:
                PromptDilogFragment promptDilogFragmentTwo = new PromptDilogFragment();
                promptDilogFragmentTwo.initPrompt("是否发表游记", 20);
                promptDilogFragmentTwo.show(getSupportFragmentManager(), "是否发表游记");
                break;
            //保存草稿
            case R.id.rl_travelcontext_save:
                PromptDilogFragment promptDilogFragmentThree = new PromptDilogFragment();
                promptDilogFragmentThree.initPrompt("是否保存游记为草稿", 21);
                promptDilogFragmentThree.show(getSupportFragmentManager(), "是否保存游记为草稿");
                break;
            //标题
            case R.id.tv_travelcontext_title:
                break;
            //编辑
            case R.id.tv_travelcontext_edit:
                initIntent(EditTextActivity.class, 0);
                break;
            //添加文字
            case R.id.ll_travelcontext_text:
                initIntent(AddTextActivity.class);
                break;
            //插入图片
            case R.id.ll_travelcontext_picture:
                break;
            //更换封面
            case R.id.tv_travelcontext_replace:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 111);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否取消", 9);
        promptDilogFragment.show(getSupportFragmentManager(), "是否取消");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = null;
        try {
            imageUri = data.getData();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        Log.e("---获得一张图片---", picturePath);
        cursor.close();
        switch (requestCode) {
            case 222:
                try {
                    if (!isDefalutPath) {
                        defalutPath = picturePath;
                    }
                    Travel travel = new Travel();
                    travel.setName("#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
                    travel.setPutName("#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
                    travel.setText(true);
                    travel.getPictureList().add(picturePath);
                    if (travelPosition == 0) {
                        plantState.getTravelList().add(0, "#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
                        plantState.getTravelOneList().add(0, travel);
                    } else {
                        plantState.getTravelOneList().add(travelPosition, travel);
                        plantState.getTravelList().add(travelPosition, "#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
                    }

                    if (travelContextAdapter != null) {
                        travelContextAdapter.notifyDataSetChanged();
                    }
                    if (travelPosition == 0) {
                        handler.sendEmptyMessage(0x00);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 111:
                isDefalutPath = true;
                defalutPath = picturePath;
                Log.e(TAG,"---"+defalutPath);
                imageLoader.displayImage("file://" + defalutPath, ivTravelcontextHear, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                break;
            default:
                plantState.initToast(context, "点击取消从相册选择", true, 0);
                break;
        }
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
        eventBus.unregister(this);
        plantState.getTravelList().clear();
        plantState.getTravelOneList().clear();
    }

    /**
     * 游记图片选择
     *
     * @param t
     */
    public void onEventMainThread(TravelFind t) {
        travelPosition = t.position;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 222);
//        TravelDilogFragment travelDilogFragment = new TravelDilogFragment();
//        travelDilogFragment.show(getSupportFragmentManager(), "游记图片");
    }

    /**
     * 图片
     *
     * @param s
     */
    public void onEventMainThread(TraveImageFind s) {
        Travel travel = new Travel();
        travel.setName("#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
        travel.setPutName("#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
        travel.setText(true);
        Log.e(TAG, "---图片---" + s.path);
        travel.getPictureList().add(s.path);
        if (travelPosition == 0) {
            plantState.getTravelOneList().add(0, travel);
        } else {
            plantState.getTravelOneList().add(travelPosition, travel);
        }
        plantState.getTravelList().add("#QFKJZWYTravelCommentImage_" + "image" + travelPosition);
        if (travelContextAdapter != null) {
            travelContextAdapter.notifyDataSetChanged();
        }
        if (travelPosition == 0) {
            handler.sendEmptyMessage(0x00);
        }
    }

    /**
     * 文字
     */
    public void onEventMainThread(TravelTextFind s) {
        Travel travel = new Travel();
        travel.setText(false);
        travel.setText(s.text);
        if (s.position == 0) {
            plantState.getTravelList().add(0, s.text);
            plantState.getTravelOneList().add(0, travel);
        } else {
            plantState.getTravelList().add(s.position, s.text);
            plantState.getTravelOneList().add(s.position, travel);
        }
        if (travelContextAdapter != null) {
            travelContextAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 返回操作
     *
     * @param t
     */
    public void onEventMainThread(TraveReturnFind t) {
        plantState.getTravelList().clear();
        plantState.getTravelOneList().clear();
        TravelTitleActivity.TravelTitleActivity.finish();
        finish();
    }

    /**
     * 编辑标题
     */
    public void onEventMainThread(TravelTitileFind t) {
        title = t.title;
        tvTravelcontextTitle.setText(title);
    }

    /**
     * 编辑内容
     */
    public void onEventMainThread(TravelEditTextFind t) {
        plantState.getTravelList().set(t.position, t.text);
        Travel travel = plantState.getTravelOneList().get(t.position);
        travel.setText(t.text);
        plantState.getTravelOneList().set(t.position, travel);
        if (travelContextAdapter != null) {
            travelContextAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 发表游记
     * @param t
     */
    public void onEventMainThread(PublishedFind t){
        initTravel(true, R.string.published);
    }

    /**
     * 发表保存草稿
     * @param t
     */
    public void onEventMainThread(SaveFind t){
        initTravel(false, R.string.drafts);
    }
}
