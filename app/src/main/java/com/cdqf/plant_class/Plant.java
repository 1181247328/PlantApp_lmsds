package com.cdqf.plant_class;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2017/12/18.
 */

public class Plant {
    private int allcount;
    private List<PlantNumber> list = new CopyOnWriteArrayList<PlantNumber>();

    public int getAllcount() {
        return allcount;
    }

    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }

    public List<PlantNumber> getList() {
        return list;
    }

    public void setList(List<PlantNumber> list) {
        this.list = list;
    }

    public class PlantNumber{
        private int botanyId;
        private String botanyName;
        private String botanyPic;
        private String httpBotanyPic;
        private String brief;

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

        public String getHttpBotanyPic() {
            return httpBotanyPic;
        }

        public void setHttpBotanyPic(String httpBotanyPic) {
            this.httpBotanyPic = httpBotanyPic;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }
    }
}
