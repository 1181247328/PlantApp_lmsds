package com.cdqf.plant_search;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2018/1/3.
 */

public class Search {
    private static Search search = new Search();

    private List<String> searchList = new CopyOnWriteArrayList<String>();

    public static Search getSearch() {
        return search;
    }

    public List<String> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<String> searchList) {
        this.searchList = searchList;
    }
}
