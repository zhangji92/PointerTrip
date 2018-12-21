package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/6/14
 * 18:21
 * 公司：
 * 描述：更新首页游记
 *评论数
 */

public class VideoDetailsToHomePagerEvent {

    private String num;
    private int position;

    private boolean isLike=false;

    private String c_nickName;
    private String c_info;

    public String getC_nickName() {
        return c_nickName;
    }

    public void setC_nickName(String c_nickName) {
        this.c_nickName = c_nickName;
    }

    public String getC_info() {
        return c_info;
    }

    public void setC_info(String c_info) {
        this.c_info = c_info;
    }




    public VideoDetailsToHomePagerEvent(String num,int position) {
        this.position=position;
        this.num = num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getNum() {
        return num;
    }

    public int getPosition() {
        return position;
    }
}
