package yc.pointer.trip.bean;

import java.io.Serializable;

/**
 * Created by moyan on 2018/6/15.
 */

public class BaseUnMsgBean implements Serializable{
    /**
     * news : 1
     * comment : 12
     * zan : 39
     * fans : 0
     */

    private String news;
    private String comment;
    private String zan;
    private String fans;

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getZan() {
        return zan;
    }

    public void setZan(String zan) {
        this.zan = zan;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }
}
