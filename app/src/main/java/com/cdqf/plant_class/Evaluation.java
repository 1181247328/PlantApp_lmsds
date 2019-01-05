package com.cdqf.plant_class;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2018/1/4.
 */

public class Evaluation {
    private int commCommentId;
    private int commenterId;
    private String commenterName;
    private String commenterAvatar;
    private String httpCommenterAvatar;
    private String commentDate;
    private String strCommentDate;
    private String commentContent;
    private List<Picture> picList = new CopyOnWriteArrayList<>();
    private List<String> urlList = new CopyOnWriteArrayList<>();

    public int getCommCommentId() {
        return commCommentId;
    }

    public void setCommCommentId(int commCommentId) {
        this.commCommentId = commCommentId;
    }

    public int getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterAvatar() {
        return commenterAvatar;
    }

    public void setCommenterAvatar(String commenterAvatar) {
        this.commenterAvatar = commenterAvatar;
    }

    public String getHttpCommenterAvatar() {
        return httpCommenterAvatar;
    }

    public void setHttpCommenterAvatar(String httpCommenterAvatar) {
        this.httpCommenterAvatar = httpCommenterAvatar;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getStrCommentDate() {
        return strCommentDate;
    }

    public void setStrCommentDate(String strCommentDate) {
        this.strCommentDate = strCommentDate;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public List<Picture> getPicList() {
        return picList;
    }

    public void setPicList(List<Picture> picList) {
        this.picList = picList;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public class Picture {
        private int commId;

        private int picId;

        private String url;

        private String httpUrl;

        public int getCommId() {
            return commId;
        }

        public void setCommId(int commId) {
            this.commId = commId;
        }

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
