package com.cdqf.plant_state;

/**
 * Created by liu on 2018/3/14.
 */

public class PlantAddress {

    //线下
    public static final String ADDRESS = "http://192.168.31.172:9001";

    //老版本
//    public static final String ADDRESS = "http://rlmsdapi.quanyubao.cn";

  //  public static final String ADDRESS = "https://lmsdapi.tuogouchebao.com";

    public static final String CIPHER = "";

    /**
     * 首页
     **/
    //新闻列表
    public static final String ASK_NEWS = ADDRESS + "/api/News/GetNewsList";

    //新闻详情
    public static final String ASK_NEWSCONTEXT = ADDRESS + "/api/News/GetNewsDetails";

    //植物列表
    public static final String ASK_PLANTS = ADDRESS + "/api/ScenicSpot/GetBotanyList";

    //收藏
    public static final String ASK_COLLTION = ADDRESS + "/api/Collection/EditCollection";

    //植物详情
    public static final String ASK_DETAILS = ADDRESS + "/api/ScenicSpot/GetBotanyDetails";

    //资质轮播图
    public static final String ASK_NEWHEEL = ADDRESS + "/api/News/RGetTopNews";

    /**
     * 游记
     */
    //游记列表
    public static final String STRATE_LIST = ADDRESS + "/api/Travels/GetTravelsList";

    //游记详情
    public static final String STRATE_DETAILS = ADDRESS + "/api/Travels/GetTravelsDetails";

    //发表游记
    public static final String STRATE_TRAVE = ADDRESS + "/api/Travels/EditTravel";

    /**
     * 商城
     **/
    //首页
    public static final String SHOP_HOME = ADDRESS + "/api/Order/GetShopIndex";

    //商品列表
    public static final String SHOP_LIST = ADDRESS + "/api/Order/GetCommList";

    //商品详情
    public static final String SHOP_DATILS = ADDRESS + "/api/Order/GetCommDetails";

    //商品评论
    public static final String SHOP_FIST = ADDRESS + "/api/Order/GetCommComment";

    //收藏
    public static final String COLLECTION = ADDRESS + "/api/Collection/EditCollection";

    //加入购物车
    public static final String CART = ADDRESS + "/api/Order/SetShoppingCartCommNumber";

    //购物车列表
    public static final String CARTLIST = ADDRESS + "/api/Order/GetShoppingCartList";

    //删除购物车商品
    public static final String DELETEL_CART = ADDRESS + "/api/Order/DeleteShoppingCartComm";

    //确认订单
    public static final String FOR_ORDER = ADDRESS + "/api/Order/GetCommInfoForOrder";

    //添加订单
    public static final String FOR_ADDORDER = ADDRESS + "/api/Order/SubOrder";

    /**
     * 用户
     */
    //发送验证码
    public static final String USER_REGISTERED = ADDRESS + "/api/ShortMessage/SendVerificationCode";

    //验证验证码
    public static final String USER_CODE = ADDRESS + "/api/ShortMessage/CheckVerificationCode";

    //注册
    public static final String USER_REGISTERED_TWO = ADDRESS + "/api/Consumer/ConsumerRegister";

    //登录
    public static final String USER_LOGIN = ADDRESS + "/api/Consumer/ConsumerLogin";

    //微信登录
    public static final String WX_LOGIN = ADDRESS + "/api/Consumer/ConsumerRegisterWithOutMobile";

    //头像
    public static final String USER_HEAR = ADDRESS + "/api/Consumer/UpdateConsumerAvatarForAndroid";

    //昵称
    public static final String USER_NICKNAME = ADDRESS + "/api/Consumer/UpdateConsumerNickName";

    //性别修改
    public static final String USER_AGE = ADDRESS + "/api/Consumer/UpdateConsumerGender";

    //地址
    public static final String USER_ADDRESS = ADDRESS + "/api/Consumer/GetConsumerReceivingAddressList";

    //省市区列表
    public static final String USER_REGION = ADDRESS + "/api/ZhiWYInfo/GetAllAreaRegion";

    //添加收货地址
    public static final String USER_RECEIVING = ADDRESS + "/api/Consumer/EditConsumerReceivingAddress";

    //设置默认收货地址
    public static final String USER_DEFAULT = ADDRESS + "/api/Consumer/SetConsumerReceivingAddressIsDefault";

    //删除用户地址
    public static final String USER_DELETE_ADDRESS = ADDRESS + "/api/Consumer/DeleteConsumerReceivingAddress";

    //修改手机
    public static final String USER_PHONE = ADDRESS + "/api/Consumer/UdpateConsumerMobile";

    //修改密码
    public static final String USER_PASSWORD = ADDRESS + "/api/Consumer/UpdateConsumerPassword";

    //影音管理
    public static final String USER_AV = ADDRESS + "/api/Voice/GetAllVoiceList";

    //收藏
    public static final String USER_COLLECTION = ADDRESS + "/api/Collection/GetCollectionList";

    //订单列表
    public static final String USER_ORDER = ADDRESS + "/api/Order/GetOrderList";

    //取消订单
    public static final String USER_CACENL = ADDRESS + "/api/Order/CancelOrder";

    //删除订单
    public static final String USER_ORDERDELETE = ADDRESS + "/api/Order/DeleteOrders";

    //确认收货
    public static final String USER_SIGN = ADDRESS + "/api/Order/SignOrder";

    //查询快递
    public static final String USER_LOGISTICS = ADDRESS + "/api/Order/GetExpressParameter";
    public static final String LOGISTICS = "https://poll.kuaidi100.com/poll/query.do";

    //订单详情
    public static final String USER_DETAILSORDER = ADDRESS + "/api/Order/GetOrderDetaills_PC";

    //发表商品评价
    public static final String USER_SUB = ADDRESS + "/api/Order/SubCommCommentForAndroid";

    //提交订单
    public static final String USER_RETURN = ADDRESS + "/api/Order/SubReturnGoods";

    //退款列表
    public static final String USER_REFUND = ADDRESS + "/api/Order/GetReturnGoodsList";

    //退款详情
    public static final String USER_REFUNDETILS = ADDRESS + "/api/Order/GetReturnGoodsDetails";

    //撤销退款
    public static final String USER_CANCEL = ADDRESS + "/api/Order/CancelReturnGoods";

    //物流公司
    public static final String USER_COURIER = ADDRESS + "/api/Order/GetExpressList";

    //提交物流信息
    public static final String USER_NUM = ADDRESS + "/api/Order/SubLogisticsNum";

    /**
     * 支付
     **/
    public static final String PAY_SING = ADDRESS + "/api/Pay/GetSign";

    //景区和参观
    public static final String USER_INTRODUCTION = ADDRESS + "/api/ZhiWYInfo/GetDescribeInfo";

    /**
     * 地图导航
     ***/
    //地图各位置
    public static final String FOR_WY = ADDRESS + "/api/ScenicSpot/GetScenicSpotListForAndroid";

    //浏览路线
    public static final String FOR_ROAD = ADDRESS + "/api/VisitRoad/GetVisitRoadList";

    //浏览路线详情
    public static final String FOR_DETAILS = ADDRESS + "/api/VisitRoad/GetVisitRoadDetails";

    //距离人最近的景点
    public static final String FOR_SPOT = ADDRESS + "/api/ScenicSpot/GetUserLocationAtScenicSpot";

    //路线详情
    public static final String FOR_GETDETAILS = ADDRESS + "/api/ScenicSpot/GetScenicSpotDetails";

    /****
     * 车辆管理
     ******/
    //绑定车牌号
    public static final String CART_BINDING = ADDRESS + "/api/Consumer/SetConsumerPlatNumber";

    //得到预付定单
    public static final String CART_PAYORDER = ADDRESS + "/api/Parking/GetParkingPayOrder";

    /**
     * 天气
     ***/
    public static final String WEATHER = "https://free-api.heweather.com/s6/weather/now";

    /***
     * 积分商城
     ****/
    //税分列表
    public static final String COMMLIST = ADDRESS + "/api/Commodity/GetIntegralCommList";

    //积分明细
    public static final String RECORD = ADDRESS + "/api/Consumer/GetConsumerIntegralRecord";

    //兑换记录
    public static final String COMMODITY = ADDRESS + "/api/Consumer/GetConsumerIntegralRecordCommodity";

    //积分详情
    public static final String COMMDETAILS = ADDRESS + "/api/Commodity/GetIntegralCommDetails";

    //确定兑换
    public static final String INTEGRAL_ORDER = ADDRESS + "/api/Order/SubIntegralOrder";

    //支付返回
    public static final String RETURN_PAY = ADDRESS + "/api/Order/UpOrderDealStatus";
}

