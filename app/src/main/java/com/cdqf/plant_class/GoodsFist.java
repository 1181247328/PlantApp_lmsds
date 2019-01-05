package com.cdqf.plant_class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/12/6.
 */

public class GoodsFist {

    private int commentCount;

    private List<Commentlist> commentList = new ArrayList<Commentlist>();

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<Commentlist> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Commentlist> commentList) {
        this.commentList = commentList;
    }
}
