package com.cdqf.plant_state;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_niengridview.NineGridTestLayout;
import com.cdqf.plant_view.ListViewForScrollView;
import com.gcssloop.widget.RCRelativeLayout;
import com.haozhang.lib.SlantedTextView;
import com.lnyp.imgdots.view.ImageLayout;

import me.codeboy.android.aligntextview.AlignTextView;

/**
 * Created by liu on 2017/7/14.
 */

public class PlantViewHolder {

    /**
     * 资讯
     **/
    //收藏布局
    public RelativeLayout rlAskItemCollection = null;

    //收藏
    public ImageView ivAskItemCollection = null;

    //图片
    public ImageView ivASkItemFigure = null;

    //药用
    public TextView tvAskItemName = null;

    //介绍
    public TextView tvAskItemContent = null;

    /**
     * 情况
     **/
    //图片
    public ImageView ivSituationItemPicture = null;

    //内容
    public TextView tvSituationItemContext = null;

    //日期
    public TextView tvSituationItemData = null;

    /**
     * 游记攻略
     **/
    //图片
    public ImageView ivStrategyItemPicture = null;

    //收藏
    public LinearLayout llStrategyItemCollection = null;

    //收藏图片
    public ImageView ivStrategyItemCollection = null;

    //标题
    public TextView tvStrategyItemTitle = null;

    //头像
    public ImageView ivStrategyItemHear = null;

    //名称
    public TextView tvStrategyItemName = null;

    //分享
    public LinearLayout llStrategyItemShare = null;

    //评论数量
    public TextView tvStrategyItemNumber = null;

    /**
     * 线路规划
     **/
    //图片
    public ImageView ivPlanningItemPicture = null;

    //线路名称
    public SlantedTextView stvPlanningItemName = null;

    //名称背景
    public TextView tvPlanningItemSpecific = null;

    //目的名称
    public TextView tvPlanningItemDetails = null;

    /**
     * 评论
     **/
    //头像
    public ImageView ivCommentsHear = null;

    //名称
    public TextView tvCommentsName = null;

    //评论
    public RelativeLayout rlCommentsItemIn = null;

    //评论内容
    public TextView tvCommentsItemContext = null;

    //评论时间
    public TextView tvCommentsItemTmer = null;

    /**游记内容**/
    /**
     * 文字
     **/
    //添加文字
    public RelativeLayout rlTextItemText = null;

    //插入图片
    public RelativeLayout rlTextItemPicture = null;

    //文字
    public TextView tvTextItemContext = null;

    //编辑
    public TextView tvTextItemedit = null;

    /**
     * 图片
     */
    //添加文字
    public RelativeLayout rlPictureItemText = null;

    //插入图片
    public RelativeLayout rlPictureItemPicture = null;

    //图片
    public ImageView ivPictrueItemContext = null;

    /**
     * 最后
     */
    //添加文字
    public RelativeLayout rlLastItemText = null;

    //插入图片
    public RelativeLayout rlLastItemPicture = null;

    /**
     * 订单
     **/
    //图标
    public ImageView ivMyItemLogo = null;

    //名
    public TextView tvMyItemName = null;

    /**
     * 商城
     **/
    //图标
    public ImageView ivShopItemLogo = null;

    //名
    public TextView tvShopItemName = null;

    /**
     * 为您推荐
     **/
    //图标
    public ImageView ivShopItemFigure = null;

    //门票名
    public TextView tvShopItemTickets = null;

    //是否包邮
    public TextView tvShopItemMail = null;

    //金额
    public TextView tvShopItemPrice = null;

    //付款人
    public TextView tvShopItemPayment = null;

    /**
     * 保证
     **/
    //保证
    public TextView tvDetailsItemEnsure = null;

    /**
     * 购物车
     **/
    //选择
    public CheckBox cbCartItemCheckbox = null;

    //图片
    public ImageView ivCartItemFigure = null;

    //商品名
    public TextView tvCartItemName = null;

    //价格
    public TextView tvCartItemPrice = null;

    //减
    public TextView tvCartItemReduction = null;

    //数量
    public TextView tvCartItemNumber = null;

    //加
    public TextView tvCartItemAdd = null;

    /**
     * 管理收货地址
     **/
    //姓名
    public TextView tvAddressItemName = null;

    //电话
    public TextView tvAddressItemPhone = null;

    //地址
    public TextView tvAddressItemAddress = null;

    //默认
    public CheckBox cbAddressItemCheckbox = null;

    //编辑
    public LinearLayout llAddressItemEdit = null;

    //删除
    public LinearLayout llAddressItemDelete = null;

    /**
     * 地区
     **/
    public TextView tvRegionDilogItemName = null;

    /**
     * 我的游记
     **/
    //图片
    public ImageView ivHasbeenItemPicture = null;

    //标题
    public TextView tvHasbeenItemTitle = null;

    //日期
    public TextView tvHasbeenItemData = null;

    /**
     * 我的订单
     **/
    //图标
    public ImageView ivOrderItemIcon = null;

    //商品名称
    public TextView tvOrderItemTitle = null;

    //价格
    public TextView tvOrderItemPrice = null;

    //数量
    public TextView tvOrderItemNumber = null;

    //商品集合
    public ListViewForScrollView lvOrderItemList = null;

    //合计
    public TextView tvOrderItemCombined = null;

    //状态
    public TextView tvOrderItemForpayment = null;

    //订单操作
    public RCRelativeLayout rcrlOrderItemOne = null;
    public TextView tvOrderItemOne = null;

    //物流
    public RCRelativeLayout rcrlOrderItemTwo = null;

    //订单操作
    public RCRelativeLayout rcrlOrderItemThree = null;

    /**
     * 分享
     **/
    //布局
    public LinearLayout llDilogItemLayout = null;

    //图片
    public ImageView ivDilogItemImage = null;

    //分享名
    public TextView tvDilogItemName = null;

    /**影音管理**/

    //删除
    public RelativeLayout rlAvItemDelete = null;

    //图片
    public ImageView ivAvItemVideo = null;

    //标题
    public TextView tvAvItemTitle = null;

    //状态
    public TextView tvAvItemState = null;

    /**地图**/
    public ImageLayout ivPlantItemMap = null;

    /**商品评论**/
    //头像
    public ImageView ivGoodsevItemHear = null;

    //名字
    public TextView tvGoodsevItemName = null;

    //时间
    public TextView tvGoodsevItemData = null;

    //内容
    public AlignTextView atvGoodsevItemContent = null;

    //图片九宫格
    public NineGridTestLayout NglGoodsevItemList = null;

    /**买家评论图片**/
    public ImageView ivEvaluateItemImage = null;

    /**退款**/
    //商品图片
    public ImageView ivRefundItemIcon = null;

    //商品名称
    public TextView tvRefundItemTitle = null;

    //商品价格
    public TextView tvRefundItemPrice = null;

    //商品数量
    public TextView tvRefundItemNumber= null;

    //集合
    public ListViewForScrollView lvRefundItemList = null;

    //状态
    public TextView tvRefundItemState = null;

    //查看详情
    public RCRelativeLayout rcrlRefundDetails = null;

    /***商品订单****/
    //图片
    public ImageView ivSettlementItemFigure = null;

    //商品名称
    public TextView tvSettlementItemName = null;

    //价格
    public TextView tvSettlementItemPrice = null;

    //数量
    public TextView tvSettlementItemReduction = null;

    /***订单详情****/
    //图片
    public ImageView ivOrderdetailsItemFigure = null;

    //商品名称
    public TextView tvOrderdetailsItemName = null;

    //价格
    public TextView tvOrderdetailstemPrice = null;

    //数量
    public TextView tvOrderdetailsItemReduction = null;

    //退款
    public RCRelativeLayout rcrlOrderdetailsItemRefund = null;

    public TextView tvOrderdetailsItemRefund = null;

    /*****积分明细******/
    //名称
    public TextView tvSubsidiaryItemName = null;

    //时间
    public TextView tvSubsidiaryItemTimer = null;

    //兑换数量
    public TextView tvSubsidiaryItemMoney = null;

    /*****兑换记录******/
    //图片
    public ImageView ivRecordItemName = null;

    //名称
    public TextView tvRecordItemTimer = null;

    //时间
    public TextView tvRecordItemMoney = null;
}
