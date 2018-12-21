package yc.pointer.trip.bean;

/**
 * Created by moyan on 2018/7/6.
 * 2018.7.6版本出发吧信息储存类
 */

public class GoTravelMesgBeanNew {


    private String destinCity = "";//目的地
    private String scenic = "";//景点
    private String travelperson = "";//出行人数
    private String demand = "";//具体要求
    private String travelDateTime = "";//出行日期
    private String traveltype = "";//计价方式
    private String travelTime = "";//游玩时长
    private String travelType = "";// 出行类型  1代表小时 0代表天 2代表取消
    private String guideCar="";//导游证是否需要
    private String needCar="";//是否需要用车服务

    private String noGuideByHourPrice = "";//无导游证按小时价格
    private String GuideByHourPrice = "";//有导游证按小时价格
    private String noGuideByDayPrice = "";//无导游证按天价格
    private String GuideByDayPrice = "";//有导游证按天价格

    private String carByHourPrice = "";//租车价格按小时
    private String carByDayPrice = "";//租车价格按天
    private String   pickupSex = "";//接单人性别




    public String getCarByHourPrice() {
        return carByHourPrice;
    }

    public void setCarByHourPrice(String carByHourPrice) {
        this.carByHourPrice = carByHourPrice;
    }

    public String getCarByDayPrice() {
        return carByDayPrice;
    }

    public void setCarByDayPrice(String carByDayPrice) {
        this.carByDayPrice = carByDayPrice;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }


    public String getGuideCar() {
        return guideCar;
    }

    public void setGuideCar(String guideCar) {
        this.guideCar = guideCar;
    }

    public String getNeedCar() {
        return needCar;
    }

    public void setNeedCar(String needCar) {
        this.needCar = needCar;
    }



    public String getNoGuideByHourPrice() {
        return noGuideByHourPrice;
    }

    public void setNoGuideByHourPrice(String noGuideByHourPrice) {
        this.noGuideByHourPrice = noGuideByHourPrice;
    }

    public String getGuideByHourPrice() {
        return GuideByHourPrice;
    }

    public void setGuideByHourPrice(String guideByHourPrice) {
        GuideByHourPrice = guideByHourPrice;
    }

    public String getNoGuideByDayPrice() {
        return noGuideByDayPrice;
    }

    public void setNoGuideByDayPrice(String noGuideByDayPrice) {
        this.noGuideByDayPrice = noGuideByDayPrice;
    }

    public String getGuideByDayPrice() {
        return GuideByDayPrice;
    }

    public void setGuideByDayPrice(String guideByDayPrice) {
        GuideByDayPrice = guideByDayPrice;
    }



    public String getDestinCity() {
        return destinCity;
    }

    public void setDestinCity(String destinCity) {
        this.destinCity = destinCity;
    }

    public String getScenic() {
        return scenic;
    }

    public void setScenic(String scenic) {
        this.scenic = scenic;
    }

    public String getTravelperson() {
        return travelperson;
    }

    public void setTravelperson(String travelperson) {
        this.travelperson = travelperson;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getTravelDateTime() {
        return travelDateTime;
    }

    public void setTravelDateTime(String travelDateTime) {
        this.travelDateTime = travelDateTime;
    }

    public String getTraveltype() {
        return traveltype;
    }

    public void setTraveltype(String traveltype) {
        this.traveltype = traveltype;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }


    public String getPickupSex() {
        return pickupSex;
    }

    public void setPickupSex(String pickupSex) {
        this.pickupSex = pickupSex;
    }

}
