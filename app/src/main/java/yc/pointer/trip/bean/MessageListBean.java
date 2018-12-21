package yc.pointer.trip.bean;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2018/4/2
 * 16:49
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

public class MessageListBean extends BaseBean {

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

    public  class DataBean extends BookBean{

        /**
         * c_info : 都补补
         */

        private String c_info;
        /**
         * cid : 229
         */

        private String cid;

        public String getC_info() {
            return c_info;
        }

        public void setC_info(String c_info) {
            this.c_info = c_info;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }
    }
    public static class UnMsgBean extends BaseUnMsgBean implements Serializable{

    }
}
