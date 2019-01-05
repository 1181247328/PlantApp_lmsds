package com.cdqf.plant_class;

/**
 * 首页图片
 * Created by liu on 2017/11/27.
 */

public class Banners {

    private String banner;

    private String imgbanner;

    private int bannertocommid;

    public Banners(String banner, String imgbanner, int bannertocommid) {
        this.banner = banner;
        this.imgbanner = imgbanner;
        this.bannertocommid = bannertocommid;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getImgbanner() {
        return imgbanner;
    }

    public void setImgbanner(String imgbanner) {
        this.imgbanner = imgbanner;
    }

    public int getBannertocommid() {
        return bannertocommid;
    }

    public void setBannertocommid(int bannertocommid) {
        this.bannertocommid = bannertocommid;
    }
}
