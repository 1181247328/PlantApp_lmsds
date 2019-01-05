package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/18.
 */

public class NewsContext {
    private int nId;
    private String title;
    private String content;
    private String pushDate;
    private String strPushDate;
    private String httpDefaultPic;

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getHttpDefaultPic() {
        return httpDefaultPic;
    }

    public void setHttpDefaultPic(String httpDefaultPic) {
        this.httpDefaultPic = httpDefaultPic;
    }
}
