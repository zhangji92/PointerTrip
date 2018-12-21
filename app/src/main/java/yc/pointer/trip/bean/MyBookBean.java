package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/8/2
 * 17:00
 */
public class MyBookBean extends BaseBean {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends BookBean{
        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        private String updatetime;
    }

}
