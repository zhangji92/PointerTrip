package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Create By 张继
 * 2017/7/31 0031
 * 上午 10:11
 */
public class CityBean extends BaseBean {


    /**
     * data : {"data_hot":[{"cityid":"12","cityname":"北京市","citycode":"beijingshi"},{"cityid":"13","cityname":"上海市","citycode":"shanghaishi"}],"data_all":[{"cityid":"1","cityname":"天津市","citycode":"tianjinshi"},{"cityid":"2","cityname":"台北市","citycode":"taibeishi"},{"cityid":"3","cityname":"高雄市","citycode":"gaoxiongshi"},{"cityid":"4","cityname":"台中市","citycode":"taizhongshi"},{"cityid":"5","cityname":"新竹市","citycode":"xinzhushi"},{"cityid":"6","cityname":"基隆市","citycode":"jilongshi"},{"cityid":"7","cityname":"台南市","citycode":"tainanshi"},{"cityid":"8","cityname":"嘉义市","citycode":"jiayishi"},{"cityid":"9","cityname":"香港特别行政区","citycode":"xianggangtebiexingzhengqu"},{"cityid":"10","cityname":"澳门特别行政区","citycode":"aomentebiexingzhengqu"},{"cityid":"12","cityname":"北京市","citycode":"beijingshi"},{"cityid":"13","cityname":"上海市","citycode":"shanghaishi"},{"cityid":"14","cityname":"七台河市","citycode":"qitaiheshi"},{"cityid":"15","cityname":"大兴安岭地区","citycode":"daxinganlingdiqu"},{"cityid":"16","cityname":"伊春市","citycode":"yichunshi"},{"cityid":"18","cityname":"绥化市","citycode":"suihuashi"},{"cityid":"19","cityname":"鹤岗市","citycode":"hegangshi"},{"cityid":"20","cityname":"黑河市","citycode":"heiheshi"},{"cityid":"21","cityname":"鸡西市","citycode":"jixishi"},{"cityid":"22","cityname":"牡丹江市","citycode":"mudanjiangshi"},{"cityid":"23","cityname":"双鸭山市","citycode":"shuangyashanshi"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataHotBean> data_hot;
        private List<DataAllBean> data_all;

        public List<DataHotBean> getData_hot() {
            return data_hot;
        }

        public void setData_hot(List<DataHotBean> data_hot) {
            this.data_hot = data_hot;
        }

        public List<DataAllBean> getData_all() {
            return data_all;
        }

        public void setData_all(List<DataAllBean> data_all) {
            this.data_all = data_all;
        }

        public static class DataHotBean extends BaseCityBean{


            public DataHotBean(String cityname, String citycode) {
                super(cityname, citycode);
            }
        }

        public static class DataAllBean extends BaseCityBean{

            public DataAllBean(String cityname, String citycode) {
                super(cityname, citycode);
            }
        }
    }
}
