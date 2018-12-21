package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/6/13.
 */

public class SystemMessageBean extends BaseBean {


    /**
     * data : [{"nid":"1","uid":"3","n_info":"您的游记\u201c美丽拉萨\u201d被选为精选视频","addtime":"2018-06-13 10:52:11","bid":"5719","video":"/Video/0517/4_15268860302013.mp4","title":"芒果蘸酱油，厦门人才懂的美味","type":"精选","city":"","b_pic":"/Uploads/0517/芒果蘸酱油，厦门人才懂的美味.png","is_order":"0","look_num":"4144","zan_num":"136","ord_num":"0","info":"芒果蘸酱油，厦门人才懂的美味","nickname":"老骥伏枥","pic":"/Uploads/15260111094943.png","col_num":"0","location":"","length":"17","addtime1":"1526886030","width":"1280","height":"820"}]
     * unMsg : {"news":"1","comment":"12","zan":"39","fans":"0"}
     */

    private UnMsgBean unMsg;
    private List<DataBean> data;

    public UnMsgBean getUnMsg() {
        return unMsg;
    }

    public void setUnMsg(UnMsgBean unMsg) {
        this.unMsg = unMsg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class UnMsgBean extends BaseUnMsgBean implements Serializable {

    }

    public static class DataBean extends BookBean {
        /**
         * nid : 1
         * uid : 3
         * n_info : 您的游记“美丽拉萨”被选为精选视频
         * addtime : 2018-06-13 10:52:11
         * bid : 5719
         * video : /Video/0517/4_15268860302013.mp4
         * title : 芒果蘸酱油，厦门人才懂的美味
         * type : 精选
         * city :
         * b_pic : /Uploads/0517/芒果蘸酱油，厦门人才懂的美味.png
         * is_order : 0
         * look_num : 4144
         * zan_num : 136
         * ord_num : 0
         * info : 芒果蘸酱油，厦门人才懂的美味
         * nickname : 老骥伏枥
         * pic : /Uploads/15260111094943.png
         * col_num : 0
         * location :
         * length : 17
         * addtime1 : 1526886030
         * width : 1280
         * height : 820
         */

        private String nid;
        private String n_info;


        public String getNid() {
            return nid;
        }

        public void setNid(String nid) {
            this.nid = nid;
        }

        public String getN_info() {
            return n_info;
        }

        public void setN_info(String n_info) {
            this.n_info = n_info;
        }

    }
}
