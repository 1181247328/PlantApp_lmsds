package com.cdqf.plant_state;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdqf.plant_class.Address;
import com.cdqf.plant_class.AllOrder;
import com.cdqf.plant_class.Av;
import com.cdqf.plant_class.Banners;
import com.cdqf.plant_class.Carousel;
import com.cdqf.plant_class.CartList;
import com.cdqf.plant_class.Commlist;
import com.cdqf.plant_class.Courier;
import com.cdqf.plant_class.Draft;
import com.cdqf.plant_class.Evaluate;
import com.cdqf.plant_class.Evaluation;
import com.cdqf.plant_class.ForDetails;
import com.cdqf.plant_class.ForGoods;
import com.cdqf.plant_class.ForPayment;
import com.cdqf.plant_class.GoodsDetails;
import com.cdqf.plant_class.GoodsFist;
import com.cdqf.plant_class.HasPublished;
import com.cdqf.plant_class.Integral;
import com.cdqf.plant_class.LogUser;
import com.cdqf.plant_class.News;
import com.cdqf.plant_class.OrderDetails;
import com.cdqf.plant_class.OrderMail;
import com.cdqf.plant_class.Plant;
import com.cdqf.plant_class.Record;
import com.cdqf.plant_class.Refund;
import com.cdqf.plant_class.Road;
import com.cdqf.plant_class.ScienceCollection;
import com.cdqf.plant_class.SearchShop;
import com.cdqf.plant_class.SendGoods;
import com.cdqf.plant_class.ShopColltion;
import com.cdqf.plant_class.Strategy;
import com.cdqf.plant_class.Subsidiary;
import com.cdqf.plant_class.Travel;
import com.cdqf.plant_class.TravelCollection;
import com.cdqf.plant_class.User;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_service.Province;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 状态层
 * Created by liu on 2017/7/14.
 */

public class PlantState {

    private String TAG = PlantState.class.getSimpleName();

    //中间层
    private static PlantState plantState = new PlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private List<Fragment> fragments = new CopyOnWriteArrayList<Fragment>();


    public static PlantState getPlantState() {
        return plantState;
    }

    private Map<Integer, Boolean> framentsMap = new HashMap<Integer, Boolean>();

    private LogUser logUser = null;

    private XProgressDialog xProgressDialog = null;

    //头像
    private Bitmap headBitmap = null;

    private boolean isPrssor = true;

    //省市区
    private List<Province> provinceList = new CopyOnWriteArrayList<Province>();

    private List<OrderMail> orderMailList = new CopyOnWriteArrayList<OrderMail>();

    //支付方式
    private int pay = -1;

    //资讯头部轮播图

    //首页轮播图
    private List<Carousel> carouselList = new CopyOnWriteArrayList<Carousel>();

    /**
     * 商城
     ***/
    //头部图片
    private List<Banners> banners = new CopyOnWriteArrayList<Banners>();

    //推荐
    private List<Commlist> commlist = new CopyOnWriteArrayList<Commlist>();

    //列表
    private List<Commlist> goodlist = new CopyOnWriteArrayList<Commlist>();

    //搜索商品列表
    private List<SearchShop> searchList = new CopyOnWriteArrayList<>();

    //商品详情
    private GoodsDetails goodsDetails = new GoodsDetails();

    //评论
    private GoodsFist goodsFist = new GoodsFist();

    //用户是否登录
    private boolean isLogin = false;

    //用户
    private User user = new User();

    //购物车列表
    private List<CartList> cartList = new CopyOnWriteArrayList<CartList>();

    //省
    private List<String> options1Items = new ArrayList<>();

    //市
    private List<List<String>> options2Items = new ArrayList<>();

    //区
    private List<List<List<String>>> options3Items = new ArrayList<>();

    //收货地址
    private List<Address> addressList = new CopyOnWriteArrayList<Address>();

    //新闻
    private News news = new News();

    private Plant plant = new Plant();

    private List<Strategy> strategyList = new CopyOnWriteArrayList<Strategy>();

    private List<Av> avList = new CopyOnWriteArrayList<Av>();

    //游记内容
    private List<String> travelList = new CopyOnWriteArrayList<>();

    private List<Travel> travelOneList = new CopyOnWriteArrayList<>();

    private List<HasPublished> hasPublishedList = new CopyOnWriteArrayList<>();

    private List<Draft> draftList = new CopyOnWriteArrayList<>();

    private List<TravelCollection> travelCollectionList = new CopyOnWriteArrayList<>();

    private List<ScienceCollection> scienceCollectionList = new CopyOnWriteArrayList<>();

    private List<ShopColltion> shopColltionList = new CopyOnWriteArrayList<>();

    //全部
    private List<AllOrder> allOrderList = new CopyOnWriteArrayList<>();

    //待付款
    private List<ForPayment> forPaymentList = new CopyOnWriteArrayList<>();

    //待发货
    private List<SendGoods> sendGoodsList = new CopyOnWriteArrayList<>();

    //待收货
    private List<ForGoods> forGoodsList = new CopyOnWriteArrayList<>();

    //待评价
    private List<Evaluate> evaluateList = new CopyOnWriteArrayList<>();

    //评论
    private List<String> EvaluCommentList = new CopyOnWriteArrayList<>();

    //退款列表
    private List<Refund> refundList = new CopyOnWriteArrayList<>();

    //快递公司
    private List<Courier> courierList = new CopyOnWriteArrayList<>();

    private List<Road> roadList = new CopyOnWriteArrayList<>();

    //商品评论
    private List<Evaluation> evaluationList = new CopyOnWriteArrayList<>();

    //订单详情
    private OrderDetails orderDetails = null;

    //景点详情
    private ForDetails forDetails = null;

    //积分列表
    private List<Integral> integralList = new CopyOnWriteArrayList<>();

    //兑换记录
    private List<Record> recordList = new CopyOnWriteArrayList<>();

    //积分明细
    private List<Subsidiary> subsidiaryList = new CopyOnWriteArrayList<>();

    /**
     * 提示信息
     *
     * @param context
     * @param toast
     */
    public void initToast(Context context, String toast, boolean isShort, int type) {
        Toast initToast = null;
        if (isShort) {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        } else {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_LONG);
        }

        switch (type) {
            case 0:
                break;
            //显示中间
            case 1:
                initToast.setGravity(Gravity.CENTER, 0, 0);
                initToast.show();
                break;
            //顶部显示
            case 2:
                initToast.setGravity(Gravity.TOP, 0, 0);
                break;
        }
        initToast.show();
    }

    public void initToast(Context context, String toast, boolean isShort, int type, int timer) {
        Toast initToast = null;
        if (isShort) {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        } else {
            initToast = Toast.makeText(context, toast, timer);
        }

        switch (type) {
            case 0:
                break;
            //显示中间
            case 1:
                initToast.setGravity(Gravity.CENTER, 0, 0);
                initToast.show();
                break;
            //顶部显示
            case 2:
                initToast.setGravity(Gravity.TOP, 0, 0);
                break;
        }
        initToast.show();
    }

    public String getPlantString(Context context, int resId) {
        return context.getResources().getString(resId);
    }


    /**
     * @param loading 加载图片时的图片
     * @param empty   没图片资源时的默认图片
     * @param fail    加载失败时的图片
     * @return
     */
    public DisplayImageOptions getImageLoaderOptions(int loading, int empty, int fail) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(empty)
                .showImageOnFail(fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }

    /**
     * 为头像而准备
     *
     * @param loading
     * @param empty
     * @param fail
     * @return
     */
    public DisplayImageOptions getHeadImageLoaderOptions(int loading, int empty, int fail) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(empty)
                .showImageOnFail(fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        return options;
    }

    /**
     * 保存图片的配置
     *
     * @param context
     * @param cache   "imageLoaderworld/Cache"
     */
    public ImageLoaderConfiguration getImageLoaderConfing(Context context, String cache) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, cache);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(10)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .discCache(new UnlimitedDiskCache(cacheDir))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .writeDebugLogs()
                .build();
        return config;
    }

    /**
     * 初始化imageLoad
     *
     * @param context
     * @return
     */
    public ImageLoaderConfiguration getConfiguration(Context context) {
        ImageLoaderConfiguration configuration = getImageLoaderConfing(context, "imageLoaderword/Chace");
        return configuration;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本有错";
        }
    }

    public LogUser getLogUser() {
        return logUser;
    }

    public void setLogUser(LogUser logUser) {
        this.logUser = logUser;
    }

    /**
     * 将用户信息转化为json
     *
     * @return
     */
    public String getLogUserJson() {
        if (logUser == null) {
            return "0";
        }
        return JSON.toJSONString(logUser);
    }

    /**
     * 权限
     */
    public void permission(Activity activity) {
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

    /**
     * webView处理
     *
     * @param wvDrunkCarrier
     */
    public WebSettings webSettings(WebView wvDrunkCarrier) {
        WebSettings wsDrunkCarrier = wvDrunkCarrier.getSettings();
        //自适应屏幕
        wsDrunkCarrier.setUseWideViewPort(true);
        wsDrunkCarrier.setLoadWithOverviewMode(true);
        //支持网页放大缩小
        wsDrunkCarrier.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        wsDrunkCarrier.setUseWideViewPort(true);
        wsDrunkCarrier.setLoadWithOverviewMode(true);
        wsDrunkCarrier.setSavePassword(true);
        wsDrunkCarrier.setSaveFormData(true);
        wsDrunkCarrier.setJavaScriptEnabled(true);
        wsDrunkCarrier.setGeolocationEnabled(true);
        wsDrunkCarrier.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        wsDrunkCarrier.setDomStorageEnabled(true);
//        wsDrunkCarrier.setBuiltInZoomControls(true);
//        wsDrunkCarrier.setSupportZoom(true);
//        wsDrunkCarrier.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //把图片加载放在最后来加载
        wsDrunkCarrier.setBlockNetworkImage(false);
        //可以加载javascript
        wsDrunkCarrier.setJavaScriptEnabled(true);
        //设置缓存模式
        wsDrunkCarrier.setAppCacheEnabled(true);
        wsDrunkCarrier.setAllowFileAccess(true);
        wsDrunkCarrier.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启 DOM storage API 功能
        wsDrunkCarrier.setDomStorageEnabled(true);
        //开启 database storage API 功能
        wsDrunkCarrier.setDatabaseEnabled(true);
        //开启 Application Caches 功能
        wsDrunkCarrier.setAppCacheEnabled(false);
        //是否调用jS中的代码
        wsDrunkCarrier.setJavaScriptEnabled(true);
        wsDrunkCarrier.setJavaScriptCanOpenWindowsAutomatically(true);
        wsDrunkCarrier.setAllowFileAccess(true);
        //支持多点触摸
        wsDrunkCarrier.setBuiltInZoomControls(false);
        wsDrunkCarrier.setDefaultTextEncodingName("UTF-8");
        //自动加载图片
        wsDrunkCarrier.setLoadsImagesAutomatically(true);
        wsDrunkCarrier.setLoadWithOverviewMode(true);
        wsDrunkCarrier.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wsDrunkCarrier.setUseWideViewPort(true);
        wsDrunkCarrier.setSaveFormData(true);
        wsDrunkCarrier.setSavePassword(true);
        return wsDrunkCarrier;
    }

    public ImageLoader getImageLoader(Context context) {
        imageLoader.init(getConfiguration(context));
        return imageLoader;
    }

    /**
     * 判断是不是手机号码
     *
     * @param str
     * @return
     */
    public boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("[1][3456789]\\d{9}"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public boolean passwordJudge(Context context, String password, String passwordTwo) {
        if (password.length() <= 0) {
            initToast(context, "请输入新密码", true, 0);
            return false;
        }
        if (password.length() >= 17) {
            initToast(context, "密码长度不大于16", true, 0);
            return false;
        }
        if (passwordTwo.length() <= 0) {
            initToast(context, "请确认密码", true, 0);
            return false;
        }
        if (passwordTwo.length() >= 17) {
            initToast(context, "确认密码长度不大于16", true, 0);
            return false;
        }
        if (!TextUtils.equals(password, passwordTwo)) {
            initToast(context, "两次密码不一致", true, 0);
            return false;
        }
        return true;
    }

    /**
     * 将Bitmap保存在本地
     *
     * @param bitmap
     */
    public void saveBitmapFile(Bitmap bitmap, String uri) {
        File file = new File(uri);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一张图片
     *
     * @param context
     */
    public void headFail(Context context, String path) {
        plantState.setHeadBitmap(null);
        if (TextUtils.isEmpty(path)) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            file.delete();
        }
    }

    /**
     * 获取随机数
     *
     * @return
     */
    public int getRandom() {
        Random random = new Random();
        return random.nextInt(1000) + 1;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */

    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码，11位数字，1开通，第二位数必须是3456789这些数字之一 *
     *
     * @param mobileNumber
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            // Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;

        }
        return flag;
    }

    public void dialogProgress(final Context context, String Toast) {
        xProgressDialog = new XProgressDialog(context, Toast);
        xProgressDialog.show();
        isPrssor = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (xProgressDialog != null) {
                    if (isPrssor) {
                        initToast(context, "加载失败,请检查网络", true, 0);
                    }
                    xProgressDialog.dismiss();
                }
            }
        }, 6000);
    }

    public void dialogProgressClose() {
        if (xProgressDialog != null) {
            isPrssor = false;
            xProgressDialog.dismiss();
        }
    }

    /**
     * 判断是不是网络地址
     * @param url
     * @return
     */
    public boolean isUrl(String url) {
        return url.indexOf("http://") != -1;
    }

    /**
     * 获取年月时分
     *
     * @param onDay
     * @param start
     * @param end
     * @return
     */
    public String getOnDay(String onDay, int start, int end) {
        return onDay.substring(start, end);
    }

    /**
     * 分享
     *
     * @param context
     * @param content
     */
    public void initShar(Context context, String content) {
        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.putExtra(Intent.EXTRA_TEXT, content);
        intent1.setType("text/plain");
        context.startActivity(Intent.createChooser(intent1, getPlantString(context, R.string.share)));
    }

    /**
     * 判断是否是正确的车牌号
     * @param license
     * @return
     */
    public boolean licensePlate(String license){
        // 验证规则
        String regEx = "^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(license);
        return matcher.matches();
    }

    /**
     * 手机号码隐藏中间
     */
    public String phoneEmpty(String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return null;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public Map<Integer, Boolean> getFramentsMap() {
        return framentsMap;
    }

    public void setFramentsMap(Map<Integer, Boolean> framentsMap) {
        this.framentsMap = framentsMap;
    }

    public Bitmap getHeadBitmap() {
        return headBitmap;
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    public List<OrderMail> getOrderMailList() {
        return orderMailList;
    }

    public void setOrderMailList(List<OrderMail> orderMailList) {
        this.orderMailList = orderMailList;
    }

    public List<Province> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public List<Carousel> getCarouselList() {
        return carouselList;
    }

    public void setCarouselList(List<Carousel> carouselList) {
        this.carouselList = carouselList;
    }

    public void setBanners(List<Banners> banners) {
        this.banners = banners;
    }

    public List<Banners> getBanners() {
        return banners;
    }

    public List<Commlist> getCommlist() {
        return commlist;
    }

    public void setCommlist(List<Commlist> commlist) {
        this.commlist = commlist;
    }

    public List<Commlist> getGoodlist() {
        return goodlist;
    }

    public void setGoodlist(List<Commlist> goodlist) {
        this.goodlist = goodlist;
    }

    public GoodsDetails getGoodsDetails() {
        return goodsDetails;
    }

    public void setGoodsDetails(GoodsDetails goodsDetails) {
        this.goodsDetails = goodsDetails;
    }

    public GoodsFist getGoodsFist() {
        return goodsFist;
    }

    public void setGoodsFist(GoodsFist goodsFist) {
        this.goodsFist = goodsFist;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public List<CartList> getCartList() {
        return cartList;
    }

    public void setCartList(List<CartList> cartList) {
        this.cartList = cartList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getOptions1Items() {
        return options1Items;
    }

    public void setOptions1Items(List<String> options1Items) {
        this.options1Items = options1Items;
    }

    public List<List<String>> getOptions2Items() {
        return options2Items;
    }

    public void setOptions2Items(List<List<String>> options2Items) {
        this.options2Items = options2Items;
    }

    public List<List<List<String>>> getOptions3Items() {
        return options3Items;
    }

    public void setOptions3Items(List<List<List<String>>> options3Items) {
        this.options3Items = options3Items;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public List<Strategy> getStrategyList() {
        return strategyList;
    }

    public void setStrategyList(List<Strategy> strategyList) {
        this.strategyList = strategyList;
    }

    public List<Av> getAvList() {
        return avList;
    }

    public void setAvList(List<Av> avList) {
        this.avList = avList;
    }

    public List<String> getTravelList() {
        return travelList;
    }

    public void setTravelList(List<String> travelList) {
        this.travelList = travelList;
    }

    public List<Travel> getTravelOneList() {
        return travelOneList;
    }

    public void setTravelOneList(List<Travel> travelOneList) {
        this.travelOneList = travelOneList;
    }

    public List<HasPublished> getHasPublishedList() {
        return hasPublishedList;
    }

    public void setHasPublishedList(List<HasPublished> hasPublishedList) {
        this.hasPublishedList = hasPublishedList;
    }

    public List<Draft> getDraftList() {
        return draftList;
    }

    public void setDraftList(List<Draft> draftList) {
        this.draftList = draftList;
    }

    public List<TravelCollection> getTravelCollectionList() {
        return travelCollectionList;
    }

    public void setTravelCollectionList(List<TravelCollection> travelCollectionList) {
        this.travelCollectionList = travelCollectionList;
    }

    public List<ScienceCollection> getScienceCollectionList() {
        return scienceCollectionList;
    }

    public void setScienceCollectionList(List<ScienceCollection> scienceCollectionList) {
        this.scienceCollectionList = scienceCollectionList;
    }

    public List<ShopColltion> getShopColltionList() {
        return shopColltionList;
    }

    public void setShopColltionList(List<ShopColltion> shopColltionList) {
        this.shopColltionList = shopColltionList;
    }

    public List<ForPayment> getForPaymentList() {
        return forPaymentList;
    }

    public void setForPaymentList(List<ForPayment> forPaymentList) {
        this.forPaymentList = forPaymentList;
    }

    public List<SendGoods> getSendGoodsList() {
        return sendGoodsList;
    }

    public void setSendGoodsList(List<SendGoods> sendGoodsList) {
        this.sendGoodsList = sendGoodsList;
    }

    public List<ForGoods> getForGoodsList() {
        return forGoodsList;
    }

    public void setForGoodsList(List<ForGoods> forGoodsList) {
        this.forGoodsList = forGoodsList;
    }

    public List<Evaluate> getEvaluateList() {
        return evaluateList;
    }

    public void setEvaluateList(List<Evaluate> evaluateList) {
        this.evaluateList = evaluateList;
    }

    public List<String> getEvaluCommentList() {
        return EvaluCommentList;
    }

    public void setEvaluCommentList(List<String> evaluCommentList) {
        EvaluCommentList = evaluCommentList;
    }

    public List<AllOrder> getAllOrderList() {
        return allOrderList;
    }

    public void setAllOrderList(List<AllOrder> allOrderList) {
        this.allOrderList = allOrderList;
    }

    public List<Refund> getRefundList() {
        return refundList;
    }

    public void setRefundList(List<Refund> refundList) {
        this.refundList = refundList;
    }

    public List<Courier> getCourierList() {
        return courierList;
    }

    public void setCourierList(List<Courier> courierList) {
        this.courierList = courierList;
    }

    public List<Road> getRoadList() {
        return roadList;
    }

    public void setRoadList(List<Road> roadList) {
        this.roadList = roadList;
    }

    public List<SearchShop> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<SearchShop> searchList) {
        this.searchList = searchList;
    }

    public List<Evaluation> getEvaluationList() {
        return evaluationList;
    }

    public void setEvaluationList(List<Evaluation> evaluationList) {
        this.evaluationList = evaluationList;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public ForDetails getForDetails() {
        return forDetails;
    }

    public void setForDetails(ForDetails forDetails) {
        this.forDetails = forDetails;
    }

    public List<Integral> getIntegralList() {
        return integralList;
    }

    public void setIntegralList(List<Integral> integralList) {
        this.integralList = integralList;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public List<Subsidiary> getSubsidiaryList() {
        return subsidiaryList;
    }

    public void setSubsidiaryList(List<Subsidiary> subsidiaryList) {
        this.subsidiaryList = subsidiaryList;
    }
}
