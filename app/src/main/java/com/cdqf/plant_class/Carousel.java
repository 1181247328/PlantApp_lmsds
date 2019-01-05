package com.cdqf.plant_class;

/**
 * 首页轮播图
 * Created by XinAiXiaoWen on 2017/4/10.
 */

public class Carousel {
    private int newsId;
    private String title;
    private String brief;
    private String pushDate;
    private String strPushDate;
    private String defaultPic;
    private String httpDefaultPic;

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    public String getStrPushDate() {
        return strPushDate;
    }

    public void setStrPushDate(String strPushDate) {
        this.strPushDate = strPushDate;
    }

    public String getDefaultPic() {
        return defaultPic;
    }

    public void setDefaultPic(String defaultPic) {
        this.defaultPic = defaultPic;
    }

    public String getHttpDefaultPic() {
        return httpDefaultPic;
    }

    public void setHttpDefaultPic(String httpDefaultPic) {
        this.httpDefaultPic = httpDefaultPic;
    }
}
