package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/3/7.
 */

public class HomeVideoNewDataBean extends BaseBean {
    /**
     * unmsg : {"msg_num":""}
     * data : {"data_good":[{"bid":"3880","uid":"4937","title":"青海之美尽人皆知，在星空下望穿银河，在夜空里的璀璨流星","type":"精选","city":"果洛","spot":"","b_pic":"https://zztrip.oss-cn-shanghai.aliyuncs.com/images/15235955164354.png","y_pic":"","is_order":"0","is_ad":"0","is_index":"1","look_num":"4897","zan_num":"203","ord_num":"0","status":"1","info":"从草原到戈壁，从平原到山峦。在沙漠里望穿银河，在夜空下的璀璨流星。怀着对天空之镜的向往，一路上收获着美景和快乐 \u200b","is_cao":"0","nickname":"旅行美食酱","phone":"17611490886","pic":"/Uploads/15235932978649.png","cp":"指针出品","is_jie":"0","video":"https://zztrip.oss-cn-shanghai.aliyuncs.com/video/4937_15235955164218.mp4","col_num":"0","width":"480","height":"272","is_video":"1","location":"","is_del":"0","is_hb":"1","length":"29","c_num":"1","s_num":"31","addtime":"2018-04-13","addtime1":"1523595516","updatetime":"1523598912","is_heng":"1","is_admin":"0","is_cf":"0","f_status":"0","share_url":"https://www.zhizhentrip.com/Home/Book/bookDetails?bid=3880"}],"data_ad":[{"aid":"7","title":"月入过万，我教你","pic":"/Uploads/15236008244886.png"}],"hb":{"is_hb":"1","pic":"/Uploads/15257770222421.png","pic1":"/Uploads/1525777022287.png"}}
     */

    private UnmsgBean unmsg;
    private DataBean data;

    public UnmsgBean getUnmsg() {
        return unmsg;
    }

    public void setUnmsg(UnmsgBean unmsg) {
        this.unmsg = unmsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class UnmsgBean {


        /**
         * att_msg_num :关注用户的发布最新游记数量
         */

        private String att_msg_num;
        public String getAtt_msg_num() {
            return att_msg_num;
        }

        public void setAtt_msg_num(String att_msg_num) {
            this.att_msg_num = att_msg_num;
        }




    }

    public static class DataBean {
        /**
         * data_good : [{"bid":"3880","uid":"4937","title":"青海之美尽人皆知，在星空下望穿银河，在夜空里的璀璨流星","type":"精选","city":"果洛","spot":"","b_pic":"https://zztrip.oss-cn-shanghai.aliyuncs.com/images/15235955164354.png","y_pic":"","is_order":"0","is_ad":"0","is_index":"1","look_num":"4897","zan_num":"203","ord_num":"0","status":"1","info":"从草原到戈壁，从平原到山峦。在沙漠里望穿银河，在夜空下的璀璨流星。怀着对天空之镜的向往，一路上收获着美景和快乐 \u200b","is_cao":"0","nickname":"旅行美食酱","phone":"17611490886","pic":"/Uploads/15235932978649.png","cp":"指针出品","is_jie":"0","video":"https://zztrip.oss-cn-shanghai.aliyuncs.com/video/4937_15235955164218.mp4","col_num":"0","width":"480","height":"272","is_video":"1","location":"","is_del":"0","is_hb":"1","length":"29","c_num":"1","s_num":"31","addtime":"2018-04-13","addtime1":"1523595516","updatetime":"1523598912","is_heng":"1","is_admin":"0","is_cf":"0","f_status":"0","share_url":"https://www.zhizhentrip.com/Home/Book/bookDetails?bid=3880"}]
         * data_ad : [{"aid":"7","title":"月入过万，我教你","pic":"/Uploads/15236008244886.png"}]
         * hb : {"is_hb":"1","pic":"/Uploads/15257770222421.png","pic1":"/Uploads/1525777022287.png"}
         */

        private HbBean hb;
        private List<DataGoodBean> data_good;
        private List<DataAdBean> data_ad;

        public HbBean getHb() {
            return hb;
        }

        public void setHb(HbBean hb) {
            this.hb = hb;
        }

        public List<DataGoodBean> getData_good() {
            return data_good;
        }

        public void setData_good(List<DataGoodBean> data_good) {
            this.data_good = data_good;
        }

        public List<DataAdBean> getData_ad() {
            return data_ad;
        }

        public void setData_ad(List<DataAdBean> data_ad) {
            this.data_ad = data_ad;
        }

        public static class HbBean {
            /**
             * is_hb : 1
             * pic : /Uploads/15257770222421.png
             * pic1 : /Uploads/1525777022287.png
             */

            private String is_hb;
            private String pic;
            private String pic1;

            public String getIs_hb() {
                return is_hb;
            }

            public void setIs_hb(String is_hb) {
                this.is_hb = is_hb;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getPic1() {
                return pic1;
            }

            public void setPic1(String pic1) {
                this.pic1 = pic1;
            }
        }

        public static class DataGoodBean extends BookBean implements Serializable {
            /**
             * bid : 3880
             * uid : 4937
             * title : 青海之美尽人皆知，在星空下望穿银河，在夜空里的璀璨流星
             * type : 精选
             * city : 果洛
             * spot :
             * b_pic : https://zztrip.oss-cn-shanghai.aliyuncs.com/images/15235955164354.png
             * y_pic :
             * is_order : 0
             * is_ad : 0
             * is_index : 1
             * look_num : 4897
             * zan_num : 203
             * ord_num : 0
             * status : 1
             * info : 从草原到戈壁，从平原到山峦。在沙漠里望穿银河，在夜空下的璀璨流星。怀着对天空之镜的向往，一路上收获着美景和快乐 ​
             * is_cao : 0
             * nickname : 旅行美食酱
             * phone : 17611490886
             * pic : /Uploads/15235932978649.png
             * cp : 指针出品
             * is_jie : 0
             * video : https://zztrip.oss-cn-shanghai.aliyuncs.com/video/4937_15235955164218.mp4
             * col_num : 0
             * width : 480
             * height : 272
             * is_video : 1
             * location :
             * is_del : 0
             * is_hb : 1
             * length : 29
             * c_num : 1
             * s_num : 31
             * addtime : 2018-04-13
             * addtime1 : 1523595516
             * updatetime : 1523598912
             * is_heng : 1
             * is_admin : 0
             * is_cf : 0
             * f_status : 0
             * share_url : https://www.zhizhentrip.com/Home/Book/bookDetails?bid=3880
             */

        }

        public static class DataAdBean extends SugarRecord {
            /**
             * aid : 7
             * title : 月入过万，我教你
             * pic : /Uploads/15236008244886.png
             */

            private String aid;
            private String title;
            private String pic;

            public String getAid() {
                return aid;
            }

            public void setAid(String aid) {
                this.aid = aid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }
        }
    }
//
//    /**
//     * data : {"data_good":[{"bid":"2560","uid":"3345","title":"一定要看!不会失望的","type":"深度","city":"新西兰","spot":"奥克兰鸟岛","b_pic":"/Uploads/1516238884566.png","y_pic":"/Uploads/15162388846471.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"4705","zan_num":"201","ord_num":"0","status":"1","info":"奥克兰鸟岛除了临海的两面沙滩外，全岛被植被覆盖，各种鸟类聚集于此，行走在森林中，有天空林间掠过的鸟还有不时传来的鸟鸣，总会让你喜出望外。","is_cao":"0","nickname":"夕颜","phone":"","pic":"/Uploads/15102199770205.png","cp":"指针出品","video":"/Video/3345_15162388844684.mp4","col_num":"0","width":"540","height":"1168","is_video":"1","location":"","is_del":"0","is_hb":"0","addtime":"2018-01-18","addtime1":"1516238819","updatetime":"1519630290"}],"data_ad":[{"aid":"4","title":"奇妙飞行","pic":"/Uploads/15130647029212.png"}],"hb":{"is_hb":"1","pic":"/Uploads/15183344465644.png","pic1":"/Uploads/15183344466142.png"}}
//     */
//
//    private DataBean data;
//
//    private UnMsg unMsg;
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * data_good : [{"bid":"2560","uid":"3345","title":"一定要看!不会失望的","type":"深度","city":"新西兰","spot":"奥克兰鸟岛","b_pic":"/Uploads/1516238884566.png","y_pic":"/Uploads/15162388846471.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"4705","zan_num":"201","ord_num":"0","status":"1","info":"奥克兰鸟岛除了临海的两面沙滩外，全岛被植被覆盖，各种鸟类聚集于此，行走在森林中，有天空林间掠过的鸟还有不时传来的鸟鸣，总会让你喜出望外。","is_cao":"0","nickname":"夕颜","phone":"","pic":"/Uploads/15102199770205.png","cp":"指针出品","video":"/Video/3345_15162388844684.mp4","col_num":"0","width":"540","height":"1168","is_video":"1","location":"","is_del":"0","is_hb":"0","addtime":"2018-01-18","addtime1":"1516238819","updatetime":"1519630290"}]
//         * data_ad : [{"aid":"4","title":"奇妙飞行","pic":"/Uploads/15130647029212.png"}]
//         * hb : {"is_hb":"1","pic":"/Uploads/15183344465644.png","pic1":"/Uploads/15183344466142.png"}
//         */
//
//        private HbBean hb;
//        private List<DataGoodBean> data_good;
//        private List<DataAdBean> data_ad;
//
//        public HbBean getHb() {
//            return hb;
//        }
//
//        public void setHb(HbBean hb) {
//            this.hb = hb;
//        }
//
//        public List<DataGoodBean> getData_good() {
//            return data_good;
//        }
//
//        public void setData_good(List<DataGoodBean> data_good) {
//            this.data_good = data_good;
//        }
//
//        public List<DataAdBean> getData_ad() {
//            return data_ad;
//        }
//
//        public void setData_ad(List<DataAdBean> data_ad) {
//            this.data_ad = data_ad;
//        }
//
//        public static class HbBean {
//            /**
//             * is_hb : 1
//             * pic : /Uploads/15183344465644.png
//             * pic1 : /Uploads/15183344466142.png
//             */
//
//            private String is_hb;
//            private String pic;
//            private String pic1;
//
//            public String getIs_hb() {
//                return is_hb;
//            }
//
//            public void setIs_hb(String is_hb) {
//                this.is_hb = is_hb;
//            }
//
//            public String getPic() {
//                return pic;
//            }
//
//            public void setPic(String pic) {
//                this.pic = pic;
//            }
//
//            public String getPic1() {
//                return pic1;
//            }
//
//            public void setPic1(String pic1) {
//                this.pic1 = pic1;
//            }
//        }
//
//        public static class DataGoodBean extends BookBean implements Serializable {
//            /**
//             * bid : 2560
//             * uid : 3345
//             * title : 一定要看!不会失望的
//             * type : 深度
//             * city : 新西兰
//             * spot : 奥克兰鸟岛
//             * b_pic : /Uploads/1516238884566.png
//             * y_pic : /Uploads/15162388846471.png
//             * is_order : 0
//             * is_ad : 0
//             * is_index : 1
//             * look_num : 4705
//             * zan_num : 201
//             * ord_num : 0
//             * status : 1
//             * info : 奥克兰鸟岛除了临海的两面沙滩外，全岛被植被覆盖，各种鸟类聚集于此，行走在森林中，有天空林间掠过的鸟还有不时传来的鸟鸣，总会让你喜出望外。
//             * is_cao : 0
//             * nickname : 夕颜
//             * phone :
//             * pic : /Uploads/15102199770205.png
//             * cp : 指针出品
//             * video : /Video/3345_15162388844684.mp4
//             * col_num : 0
//             * width : 540
//             * height : 1168
//             * is_video : 1
//             * location :
//             * is_del : 0
//             * is_hb : 0
//             * addtime : 2018-01-18
//             * addtime1 : 1516238819
//             * updatetime : 1519630290
//             */
//
//        }
//
//        public static class DataAdBean {
//            /**
//             * aid : 4
//             * title : 奇妙飞行
//             * pic : /Uploads/15130647029212.png
//             */
//
//            private String aid;
//            private String title;
//            private String pic;
//
//            public String getAid() {
//                return aid;
//            }
//
//            public void setAid(String aid) {
//                this.aid = aid;
//            }
//
//            public String getTitle() {
//                return title;
//            }
//
//            public void setTitle(String title) {
//                this.title = title;
//            }
//
//            public String getPic() {
//                return pic;
//            }
//
//            public void setPic(String pic) {
//                this.pic = pic;
//            }
//        }
//    }
//
//    public UnMsg getUnMsg() {
//        return unMsg;
//    }
//
//    public void setUnMsg(UnMsg unMsg) {
//        this.unMsg = unMsg;
//    }
//
//    public class UnMsg {
//        private String msg_num;
//
//        public String getMsg_num() {
//            return msg_num;
//        }
//
//        public void setMsg_num(String msg_num) {
//            this.msg_num = msg_num;
//        }
//    }





}
