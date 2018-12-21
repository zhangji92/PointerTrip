package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Create by 张继
 * 2017/8/18
 * 13:57
 */
public class BillCommentBean extends BaseBean{

    /**
     * data : {"commentid":"16","starlevel":"1","levelcomment1":"不及时支付导游费","levelcomment2":"态度恶劣，一味责怪导游","levelcomment3":"不为别人考虑，要求过分","levelcomment4":"擅自改变行程，不予通知","levelmemo":"非常不满意，各方面都很差","type":"1","createtime":"2017-05-19 12:12:57","updatetime":"2017-05-19 12:12:57"}
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
         * commentid : 16
         * starlevel : 1
         * levelcomment1 : 不及时支付导游费
         * levelcomment2 : 态度恶劣，一味责怪导游
         * levelcomment3 : 不为别人考虑，要求过分
         * levelcomment4 : 擅自改变行程，不予通知
         * levelmemo : 非常不满意，各方面都很差
         * type : 1
         * createtime : 2017-05-19 12:12:57
         * updatetime : 2017-05-19 12:12:57
         */

        private String commentid;
        private String starlevel;
        private String levelcomment1;
        private String levelcomment2;
        private String levelcomment3;
        private String levelcomment4;
        private String levelmemo;
        private String type;
        private String createtime;
        private String updatetime;

        public String getCommentid() {
            return commentid;
        }

        public void setCommentid(String commentid) {
            this.commentid = commentid;
        }

        public String getStarlevel() {
            return starlevel;
        }

        public void setStarlevel(String starlevel) {
            this.starlevel = starlevel;
        }

        public String getLevelcomment1() {
            return levelcomment1;
        }

        public void setLevelcomment1(String levelcomment1) {
            this.levelcomment1 = levelcomment1;
        }

        public String getLevelcomment2() {
            return levelcomment2;
        }

        public void setLevelcomment2(String levelcomment2) {
            this.levelcomment2 = levelcomment2;
        }

        public String getLevelcomment3() {
            return levelcomment3;
        }

        public void setLevelcomment3(String levelcomment3) {
            this.levelcomment3 = levelcomment3;
        }

        public String getLevelcomment4() {
            return levelcomment4;
        }

        public void setLevelcomment4(String levelcomment4) {
            this.levelcomment4 = levelcomment4;
        }

        public String getLevelmemo() {
            return levelmemo;
        }

        public void setLevelmemo(String levelmemo) {
            this.levelmemo = levelmemo;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }
    }
}
