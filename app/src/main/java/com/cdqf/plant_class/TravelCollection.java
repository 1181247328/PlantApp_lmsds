package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/19.
 */

public class TravelCollection {
    private int trId;
    private String pic;
    private String httpPic;
    private String title;
    private int consumerId;
    private String consumerNickName;
    private String consumerAvatar;
    private String httpConsumerAvatar;
    private int commentCount;
    private int collectedId;

    public int getTrId() {
        return trId;
    }

    public void setTrId(int trId) {
        this.trId = trId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getHttpPic() {
        return httpPic;
    }

    public void setHttpPic(String httpPic) {
        this.httpPic = httpPic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerNickName() {
        return consumerNickName;
    }

    public void setConsumerNickName(String consumerNickName) {
        this.consumerNickName = consumerNickName;
    }

    public String getConsumerAvatar() {
        return consumerAvatar;
    }

    public void setConsumerAvatar(String consumerAvatar) {
        this.consumerAvatar = consumerAvatar;
    }

    public String getHttpConsumerAvatar() {
        return httpConsumerAvatar;
    }

    public void setHttpConsumerAvatar(String httpConsumerAvatar) {
        this.httpConsumerAvatar = httpConsumerAvatar;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCollectedId() {
        return collectedId;
    }

    public void setCollectedId(int collectedId) {
        this.collectedId = collectedId;
    }
}
