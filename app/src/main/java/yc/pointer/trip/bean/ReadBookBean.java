package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 张继
 * 2017/7/20
 * 13:23
 */
public class ReadBookBean extends BaseBean {





    /**
     * data : {"data_ad":[{"bid":"18","uid":"6","title":"创意餐厅","type":"美食","city":"天津","spot":"风景区","b_pic":"Uploads/15005182787255.png","is_order":"1","is_ad":"1","look_num":"0","zan_num":"0","ord_num":"0","nickname":"asd","phone":"18222396725","pic":null,"cp":"指针旅游出品"}],"data_r":[{"bid":"19","uid":"5","title":"天津10:46分","type":"人文","city":"天津","spot":"","b_pic":"","is_order":"0","is_ad":"0","look_num":"0","zan_num":"0","ord_num":"0","nickname":null,"phone":"13230848209","pic":null,"cp":"指针旅游出品"}],"data_q":[{"bid":"26","uid":"5","title":"今天13:50","type":"自驾","city":"天津","spot":"","b_pic":"Images/no_photo.png","is_order":"0","is_ad":"0","look_num":"0","zan_num":"0","ord_num":"0","nickname":null,"phone":"13230848209","pic":null,"cp":"指针旅游出品"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataAdBean> data_ad;
        private List<DataRBean> data_r;
        private List<DataQBean> data_q;

        public List<DataAdBean> getData_ad() {
            return data_ad;
        }

        public void setData_ad(List<DataAdBean> data_ad) {
            this.data_ad = data_ad;
        }

        public List<DataRBean> getData_r() {
            return data_r;
        }

        public void setData_r(List<DataRBean> data_r) {
            this.data_r = data_r;
        }

        public List<DataQBean> getData_q() {
            return data_q;
        }

        public void setData_q(List<DataQBean> data_q) {
            this.data_q = data_q;
        }

        public static class DataAdBean extends BookBean{

        }

        public static class DataRBean extends BookBean{

        }

        public static class DataQBean extends BookBean implements Serializable{

        }
    }
}
