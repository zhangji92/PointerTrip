package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/6/14.
 */

public class PrasieMessageBean extends BaseBean {


    /**
     * data : [{"zid":"797","bid":"1918","z_nickname":"芦山在线站长","z_u_pic":"/Uploads/1528444573118.png","addtime":"1528289014","uid":"3","video":"/Video/3_15154641232841.mp4","title":"一座雪山，峨眉山","type":"户外","city":"","b_pic":"/Uploads/15154641233564.png","is_order":"1","look_num":"4466","zan_num":"183","ord_num":"0","info":"","nickname":"小七","pic":"/Uploads/15174813996338.png","col_num":"0","location":"","length":"9","addtime1":"1515464053","width":"640","height":"360"},{"zid":"651","bid":"4570","z_nickname":"小七","z_u_pic":"/Uploads/15174813996338.png","addtime":"1526023627","uid":"3","video":"/Video/3_15252572954777.mp4","title":"转转","type":"民间","city":"北京","b_pic":"/Uploads/15252572955129.png","is_order":"1","look_num":"5019","zan_num":"191","ord_num":"0","info":"","nickname":"小七","pic":"/Uploads/15174813996338.png","col_num":"0","location":"","length":"23","addtime1":"1525257296","width":"1080","height":"1920"},{"zid":"499","bid":"2628","z_nickname":"wynn","z_u_pic":"/Images/log.png","addtime":"1521626192","uid":"3","video":"/Video/3_15162447028148.mp4","title":"这是民宿吗？漂亮","type":"民间","city":"云南丽江","b_pic":"/Uploads/15162447029223.png","is_order":"1","look_num":"4972","zan_num":"177","ord_num":"0","info":"","nickname":"小七","pic":"/Uploads/15174813996338.png","col_num":"0","location":"","length":"10","addtime1":"1516244658","width":"540","height":"960"},{"zid":"357","bid":"2547","z_nickname":"wynn","z_u_pic":"/Images/log.png","addtime":"1518068617","uid":"3","video":"/Video/3_15162022881607.mp4","title":"不止我说，真是太美了","type":"民间","city":"","b_pic":"/Uploads/15162022882642.png","is_order":"1","look_num":"4102","zan_num":"106","ord_num":"0","info":"雅鲁藏布江是中国最长的高原河流，位于西藏自治区境内，也是世界上海拔最高的大河之一。","nickname":"小七","pic":"/Uploads/15174813996338.png","col_num":"0","location":"","length":"10","addtime1":"1516202204","width":"540","height":"960"}]
     * unMsg : {"news":"0","comment":"12","zan":"0","fans":"0"}
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

    public  class DataBean extends BookBean {
        /**
         * zid : 797
         * bid : 1918
         * z_nickname : 芦山在线站长
         * z_u_pic : /Uploads/1528444573118.png
         * addtime : 1528289014
         * uid : 3
         * video : /Video/3_15154641232841.mp4
         * title : 一座雪山，峨眉山
         * type : 户外
         * city :
         * b_pic : /Uploads/15154641233564.png
         * is_order : 1
         * look_num : 4466
         * zan_num : 183
         * ord_num : 0
         * info :
         * nickname : 小七
         * pic : /Uploads/15174813996338.png
         * col_num : 0
         * location :
         * length : 9
         * addtime1 : 1515464053
         * width : 640
         * height : 360
         */

        private String zid;
        private String z_nickname;
        private String z_u_pic;
        private String z_info;






        /***********************************BookBean***********************/





        public String getZ_info() {
            return z_info;
        }

        public void setZ_info(String z_info) {
            this.z_info = z_info;
        }
        public String getZid() {
            return zid;
        }

        public void setZid(String zid) {
            this.zid = zid;
        }


        public String getZ_nickname() {
            return z_nickname;
        }

        public void setZ_nickname(String z_nickname) {
            this.z_nickname = z_nickname;
        }

        public String getZ_u_pic() {
            return z_u_pic;
        }

        public void setZ_u_pic(String z_u_pic) {
            this.z_u_pic = z_u_pic;
        }

    }
}
