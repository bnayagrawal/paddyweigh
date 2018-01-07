package xyz.bnayagrawal.paddyweigh;

import java.util.HashMap;

/**
 * Created by binay on 6/1/18.
 */

public class WeightList {
    private String date;
    private HashMap<Long,People> peoples;

    public WeightList(HashMap<Long,People> peoples,String date) {
        this.peoples = peoples;
        this.date = date;
    }

    public String getDate() {return date;}
    public HashMap<Long,People> getPeoples() {return peoples;}
}
