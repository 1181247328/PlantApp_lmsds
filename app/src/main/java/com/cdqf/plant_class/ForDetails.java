package com.cdqf.plant_class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2018/1/8.
 */

public class ForDetails {
    private int scenicSpotId;
    private String spotName;
    private String brief;
    private String introduction;
    private String remark;
    private String proportionalPositionX;
    private String proportionalPositionY;
    private String longitude;
    private String latitude;
    private List<Piclist> picList = new ArrayList<>();
    private List<Voicelist> voiceList = new ArrayList<>();

    public int getScenicSpotId() {
        return scenicSpotId;
    }

    public void setScenicSpotId(int scenicSpotId) {
        this.scenicSpotId = scenicSpotId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProportionalPositionX() {
        return proportionalPositionX;
    }

    public void setProportionalPositionX(String proportionalPositionX) {
        this.proportionalPositionX = proportionalPositionX;
    }

    public String getProportionalPositionY() {
        return proportionalPositionY;
    }

    public void setProportionalPositionY(String proportionalPositionY) {
        this.proportionalPositionY = proportionalPositionY;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public List<Piclist> getPicList() {
        return picList;
    }

    public void setPicList(List<Piclist> picList) {
        this.picList = picList;
    }

    public List<Voicelist> getVoiceList() {
        return voiceList;
    }

    public void setVoiceList(List<Voicelist> voiceList) {
        this.voiceList = voiceList;
    }

    public class Piclist {
        private int picId;
        private boolean isDefault;
        private String httpPic;

        public int getPicId() {
            return picId;
        }

        public void setPicId(int picId) {
            this.picId = picId;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }

        public String getHttpPic() {
            return httpPic;
        }

        public void setHttpPic(String httpPic) {
            this.httpPic = httpPic;
        }
    }

    public class Voicelist {

        private int voiceId;
        private boolean isDefault;
        private String voice;
        private String httpVoice;

        public int getVoiceId() {
            return voiceId;
        }

        public void setVoiceId(int voiceId) {
            this.voiceId = voiceId;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }

        public String getVoice() {
            return voice;
        }

        public void setVoice(String voice) {
            this.voice = voice;
        }

        public String getHttpVoice() {
            return httpVoice;
        }

        public void setHttpVoice(String httpVoice) {
            this.httpVoice = httpVoice;
        }
    }
}
