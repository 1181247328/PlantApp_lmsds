package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/18.
 */

public class Av {
    private boolean isDelete = false;
    private int voiceId;
    private String voiceTitle;
    private String url;
    private String httpUrl;
    private int voiceType;
    private String strVoiceType;
    private String httpPic;
    private String total = "0%";
    private boolean isLoding = false;

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(int voiceId) {
        this.voiceId = voiceId;
    }

    public String getVoiceTitle() {
        return voiceTitle;
    }

    public void setVoiceTitle(String voiceTitle) {
        this.voiceTitle = voiceTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public int getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(int voiceType) {
        this.voiceType = voiceType;
    }

    public String getStrVoiceType() {
        return strVoiceType;
    }

    public void setStrVoiceType(String strVoiceType) {
        this.strVoiceType = strVoiceType;
    }

    public String getHttpPic() {
        return httpPic;
    }

    public void setHttpPic(String httpPic) {
        this.httpPic = httpPic;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public boolean isLoding() {
        return isLoding;
    }

    public void setLoding(boolean loding) {
        isLoding = loding;
    }
}
