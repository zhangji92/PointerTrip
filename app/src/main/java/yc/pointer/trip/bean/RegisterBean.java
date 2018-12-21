package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2017/7/14
 * 14:11
 */
public class RegisterBean extends BaseBean {


    /**
     * data : {"uid":"1","phone":"13230848209","pwd":"b120ee43ce0a6d6a131ddcb4f5f5f096","pwdming":"7956b9f37e6f999bd9a6f80bf26b5f97","addtime":"1500020407","logintime":"1500020407","updatetime":null,"login_status":"1","out_time":null,"devcode":"4d5302de3b5c582e7d76f7079442b122","pic":null,"nickname":null}
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
         * uid : 1
         * phone : 13230848209
         * pwd : b120ee43ce0a6d6a131ddcb4f5f5f096
         * pwdming : 7956b9f37e6f999bd9a6f80bf26b5f97
         * addtime : 1500020407
         * logintime : 1500020407
         * updatetime : null
         * login_status : 1
         * out_time : null
         * devcode : 4d5302de3b5c582e7d76f7079442b122
         * pic : null
         * nickname : null
         */

        private String uid;
        private String phone;
        private String pwd;
        private String pwdming;
        private String addtime;
        private String logintime;
        private Object updatetime;
        private String login_status;
        private Object out_time;
        private String devcode;
        private Object pic;
        private Object nickname;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getPwdming() {
            return pwdming;
        }

        public void setPwdming(String pwdming) {
            this.pwdming = pwdming;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getLogintime() {
            return logintime;
        }

        public void setLogintime(String logintime) {
            this.logintime = logintime;
        }

        public Object getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(Object updatetime) {
            this.updatetime = updatetime;
        }

        public String getLogin_status() {
            return login_status;
        }

        public void setLogin_status(String login_status) {
            this.login_status = login_status;
        }

        public Object getOut_time() {
            return out_time;
        }

        public void setOut_time(Object out_time) {
            this.out_time = out_time;
        }

        public String getDevcode() {
            return devcode;
        }

        public void setDevcode(String devcode) {
            this.devcode = devcode;
        }

        public Object getPic() {
            return pic;
        }

        public void setPic(Object pic) {
            this.pic = pic;
        }

        public Object getNickname() {
            return nickname;
        }

        public void setNickname(Object nickname) {
            this.nickname = nickname;
        }
    }
}
