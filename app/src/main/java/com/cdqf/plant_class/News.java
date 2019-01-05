package com.cdqf.plant_class;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2017/12/18.
 */

public class News {

    private int allCount;

    private List<NewsContext> list = new CopyOnWriteArrayList<NewsContext>();

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public List<NewsContext> getList() {
        return list;
    }

    public void setList(List<NewsContext> list) {
        this.list = list;
    }

    public class NewsContext {

        private int newsId;
        private String title;
        private String brief;
        private String pushDate;
        private String strPushDate;
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

        public String getHttpDefaultPic() {
            return httpDefaultPic;
        }

        public void setHttpDefaultPic(String httpDefaultPic) {
            this.httpDefaultPic = httpDefaultPic;
        }
    }

}
