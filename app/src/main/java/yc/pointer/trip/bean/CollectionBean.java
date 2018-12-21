package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 刘佳伟
 * 2017/7/27
 * 17:07
 */
public class CollectionBean extends BaseBean {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends BookBean implements Serializable {

    }

}
