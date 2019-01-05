package com.cdqf.plant_class;

import java.util.List;

/**
 * Created by liu on 2017/12/18.
 */

public class PlantDetails {
    private int botanyId;
    private String botanyName;
    private String botanyPic;
    private String introduction;
    private String updateDate;
    private Piclist picList;
    private boolean isCollection;

    public int getBotanyId() {
        return botanyId;
    }

    public void setBotanyId(int botanyId) {
        this.botanyId = botanyId;
    }

    public String getBotanyName() {
        return botanyName;
    }

    public void setBotanyName(String botanyName) {
        this.botanyName = botanyName;
    }

    public String getBotanyPic() {
        return botanyPic;
    }

    public void setBotanyPic(String botanyPic) {
        this.botanyPic = botanyPic;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Piclist getPicList() {
        return picList;
    }

    public void setPicList(Piclist picList) {
        this.picList = picList;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public class Piclist{
        private int allCount;
        private List<PiclistList> list;

        public int getAllCount() {
            return allCount;
        }

        public void setAllCount(int allCount) {
            this.allCount = allCount;
        }

        public List<PiclistList> getList() {
            return list;
        }

        public void setList(List<PiclistList> list) {
            this.list = list;
        }

        public class PiclistList{
            private int picId;
            private String url;
            private String httpUrl;

            public int getPicId() {
                return picId;
            }

            public void setPicId(int picId) {
                this.picId = picId;
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
        }
    }
}
