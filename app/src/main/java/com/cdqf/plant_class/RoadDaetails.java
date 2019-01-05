package com.cdqf.plant_class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2018/1/4.
 */

public class RoadDaetails {
    private List<ListDetails> list = new ArrayList<>();
    private int vrId;
    private String vrName;
    private String pic;
    private String httpPic;
    private String introduction;

    public List<ListDetails> getList() {
        return list;
    }

    public void setList(List<ListDetails> list) {
        this.list = list;
    }

    public int getVrId() {
        return vrId;
    }

    public void setVrId(int vrId) {
        this.vrId = vrId;
    }

    public String getVrName() {
        return vrName;
    }

    public void setVrName(String vrName) {
        this.vrName = vrName;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public class ListDetails {
        private int vrdId;
        private int scenicSpotId;
        private String scenicSpotName;
        private String httpPic;
        private int sequence;

        public int getVrdId() {
            return vrdId;
        }

        public void setVrdId(int vrdId) {
            this.vrdId = vrdId;
        }

        public int getScenicSpotId() {
            return scenicSpotId;
        }

        public void setScenicSpotId(int scenicSpotId) {
            this.scenicSpotId = scenicSpotId;
        }

        public String getScenicSpotName() {
            return scenicSpotName;
        }

        public void setScenicSpotName(String scenicSpotName) {
            this.scenicSpotName = scenicSpotName;
        }

        public String getHttpPic() {
            return httpPic;
        }

        public void setHttpPic(String httpPic) {
            this.httpPic = httpPic;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }
    }
}
