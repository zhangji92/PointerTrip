package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/3/7.
 * 新版首页0809
 */

public class NewHomeVideoData extends BaseBean {

    private DataBean data;

    public UnmsgBean getUnmsg() {
        return unmsg;
    }

    public void setUnmsg(UnmsgBean unmsg) {
        this.unmsg = unmsg;
    }

    private UnmsgBean unmsg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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
        }

        public static class UnmsgBean {
            /**
             * att_msg_num : 0
             */

            private String att_msg_num;

            public String getAtt_msg_num() {
                return att_msg_num;
            }

            public void setAtt_msg_num(String att_msg_num) {
                this.att_msg_num = att_msg_num;
            }
        }
    }
