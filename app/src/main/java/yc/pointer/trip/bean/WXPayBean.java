package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;
import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2017/8/22.
 */
public class WXPayBean extends BaseBean {

    /**
     * data : {"appid":"wxe44b2ed1e16f131c","partnerid":"1451807202","prepayid":"wx201708221417029cd4fc64f90509623965","noncestr":"50gwyvWHCLHvPY6c","timestamp":1503382622,"package":"Sign=WXPay","sign":"B0EC55DCF7FAE8838B54520A3E2469B8"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appid : wxe44b2ed1e16f131c
         * partnerid : 1451807202 //商户号
         * prepayid : wx201708221417029cd4fc64f90509623965  //预支付会话ID
         * noncestr : 50gwyvWHCLHvPY6c //随机数
         * timestamp : 1503382622 //时间戳
         * package : Sign=WXPay //固定值
         * sign : B0EC55DCF7FAE8838B54520A3E2469B8
         */

        private String appid;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;
        @SerializedName("package")
        private String packageX;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
