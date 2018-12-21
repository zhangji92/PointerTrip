package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/10/11.
 * 总任务界面数据
 */

public class AllTaskDestilsBean extends BaseBean {


    /**
     * pic : /Images/task_all.png
     * data : [{"money":"","btn_status":"","time":68362},{"money":"","btn_status":"","time":77362},{"money":"","btn_status":"","time":17962}]
     */

    private String pic;
    private String url;
    private List<DataBean> data;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * money :
         * btn_status :
         * time : 68362
         */

        private String money;
        private String btn_status;
        private int time;

        private int img;
        private int taskType;//  0点赞  1：分享  2：评论
        private String taskTitle;
        private String taskIntroduction;


        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public String getTaskTitle() {
            return taskTitle;
        }

        public void setTaskTitle(String taskTitle) {
            this.taskTitle = taskTitle;
        }

        public String getTaskIntroduction() {
            return taskIntroduction;
        }

        public void setTaskIntroduction(String taskIntroduction) {
            this.taskIntroduction = taskIntroduction;
        }

        public int getTaskType() {
            return taskType;
        }

        public void setTaskType(int taskType) {
            this.taskType = taskType;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getBtn_status() {
            return btn_status;
        }

        public void setBtn_status(String btn_status) {
            this.btn_status = btn_status;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}
