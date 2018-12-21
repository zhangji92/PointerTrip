package yc.pointer.trip.bean;

/**
 * Created by moyan on 2017/11/17.
 */

public class LocationInfo {

    private String locationTitle;
    private String locationDetial;
    //经纬度
    private Double Latitude;
    private Double LonTitude;
    public Double getLonTitude() {
        return LonTitude;
    }

    public void setLonTitude(Double lonTitude) {
        LonTitude = lonTitude;
    }



    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }



    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public String getLocationDetial() {
        return locationDetial;
    }

    public void setLocationDetial(String locationDetial) {
        this.locationDetial = locationDetial;
    }




}
