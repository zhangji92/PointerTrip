package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 刘佳伟
 * 2017/8/3
 * 17:42
 */
public class DraftBookBean extends BaseBean {


    /**
     * data : {"bid":"113","title":"天津测试14:00","type":"骑行","city":"北京","spot":"","info":"<div id=\"zss_editor_content\" style=\"width: 100%; height: 100%; -webkit-overflow-scrolling: touch; overflow: auto;\">测试新加富文本编辑器<\/div><div id=\"zss_editor_footer\" style=\"height: 0px;\"><\/div>"}
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
         * bid : 113
         * title : 天津测试14:00
         * type : 骑行
         * city : 北京
         * spot :
         * info : <div id="zss_editor_content" style="width: 100%; height: 100%; -webkit-overflow-scrolling: touch; overflow: auto;">测试新加富文本编辑器</div><div id="zss_editor_footer" style="height: 0px;"></div>
         */

        private String bid;
        private String title;
        private String type;
        private String city;
        private String spot;
        private String info;

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getSpot() {
            return spot;
        }

        public void setSpot(String spot) {
            this.spot = spot;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

}
