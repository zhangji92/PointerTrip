package yc.pointer.trip.bean;

/**
 * Created by 张继
 * 2017/7/13
 * 10:43
 */
public class BaseCityBean {
    /**
     * cityid : 12
     * cityname : 北京市
     * citycode : beijingshi
     */

    private String cityid;
    private String cityname;
    private String citycode;

    public BaseCityBean(String cityid, String cityname, String citycode) {
        this.cityid = cityid;
        this.cityname = cityname;
        this.citycode = citycode;
    }

    public BaseCityBean(String cityname, String citycode) {
        this.cityname = cityname;
        this.citycode = citycode;
    }

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

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
