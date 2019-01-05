package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/19.
 */

public class Draft {
    private int travelId;
    private String title;
    private String httpPic;
    private int commentCount;
    private int consumerId;
    private String consumerAvatar;
    private String httpConsumerAvatar;
    private String consumerNickName;
    private int collectedCount;
    private boolean isCollected;
    private String editDate;
    private String strEditDate;

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHttpPic() {
        return httpPic;
    }

    public void setHttpPic(String httpPic) {
        this.httpPic = httpPic;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
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

    public String getConsumerNickName() {
        return consumerNickName;
    }

    public void setConsumerNickName(String consumerNickName) {
        this.consumerNickName = consumerNickName;
    }

    public int getCollectedCount() {
        return collectedCount;
    }

    public void setCollectedCount(int collectedCount) {
        this.collectedCount = collectedCount;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    public String getStrEditDate() {
        return strEditDate;
    }

    public void setStrEditDate(String strEditDate) {
        this.strEditDate = strEditDate;
    }
}
