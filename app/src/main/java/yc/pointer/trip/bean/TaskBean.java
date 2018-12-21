package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2018/9/25
 * 10:59
 * 公司：
 * 描述：
 */

public class TaskBean extends BaseBean{


    /**
     * data : [{"pic":"/Uploads/1514282498057.png","nickname":"Button","money":"0.20"}]
     * user : {"money":"0","num":"0","end_time":1725,"type":0,"pic":"/Images/task_zan.png","tips":"温馨提示：每条视频仅可获取一次点赞收益，用户需在当天任务结束后24点之前手动领取相应收益，次日当前收益将清零。"}
     */

    private UserBean user;
    private List<DataBean> data;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class UserBean {
        /**
         * money : 0
         * num : 0
         * end_time : 1725
         * type : 0
         * pic : /Images/task_zan.png
         * tips : 温馨提示：每条视频仅可获取一次点赞收益，用户需在当天任务结束后24点之前手动领取相应收益，次日当前收益将清零。
         */

        private String money;
        private String num;
        private int end_time;
        private int type;
        private String pic;
        private String tips;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public int getEnd_time() {
            return end_time;
        }

        public void setEnd_time(int end_time) {
            this.end_time = end_time;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }

    public static class DataBean {
        /**
         * pic : /Uploads/1514282498057.png
         * nickname : Button
         * money : 0.20
         */

        private String pic;
        private String nickname;
        private String money;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
