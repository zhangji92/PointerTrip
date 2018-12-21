package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/7/3.
 * 2018.7.3.版出发吧数据接收
 */

public class NewGotravelBean extends BaseBean {


    /**
     * city : {"cityid":"1","cityname":"天津市","pic":"/Images/city/天津市.jpg","fa_num":"0"}
     * data : [{"sid":"294","title":"天津之眼","pic":"/Images/Scenery/天津之眼.png","fa_num":"0"},{"sid":"305","title":" 西开教堂","pic":"/Images/Scenery/西开教堂.png","fa_num":"0"},{"sid":"306","title":" 津湾广场","pic":"/Images/Scenery/津湾广场.png","fa_num":"0"},{"sid":"307","title":" 南开大学","pic":"/Images/Scenery/南开大学.png","fa_num":"0"},{"sid":"308","title":"盘山","pic":"/Images/Scenery/盘山.png","fa_num":"0"},{"sid":"309","title":"天津大学","pic":"/Images/Scenery/天津大学.png","fa_num":"0"}]
     */

    private CityBean city;
    private List<DataBean> data;

    public CityBean getCity() {
        return city;
    }

    public void setCity(CityBean city) {
        this.city = city;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class CityBean {
        /**
         * cityid : 1
         * cityname : 天津市
         * pic : /Images/city/天津市.jpg
         * fa_num : 0
         */

        private String cityid;
        private String cityname;
        private String pic;
        private String fa_num;

        public String getCityid() {
            return cityid;
        }

        public void setCityid(String cityid) {
            this.cityid = cityid;
        }

        public String getCityname() {
            return cityname;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getFa_num() {
            return fa_num;
        }

        public void setFa_num(String fa_num) {
            this.fa_num = fa_num;
        }
    }

    public static class DataBean {
        /**
         * sid : 294
         * title : 天津之眼
         * pic : /Images/Scenery/天津之眼.png
         * fa_num : 0
         */

        private String sid;
        private String title;
        private String pic;
        private String fa_num;

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
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

        public String getFa_num() {
            return fa_num;
        }

        public void setFa_num(String fa_num) {
            this.fa_num = fa_num;
        }
    }
}
