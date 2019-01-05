package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/20.
 */

public class ScienceCollection {
    private int pId;
    private int collectedId;
    private String pic;
    private String httpPic;
    private String plantName;
    private String brif;

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getCollectedId() {
        return collectedId;
    }

    public void setCollectedId(int collectedId) {
        this.collectedId = collectedId;
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

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getBrif() {
        return brif;
    }

    public void setBrif(String brif) {
        this.brif = brif;
    }
}
