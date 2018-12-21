package yc.pointer.trip.bean;

/**
 * Created by moyan on 2017/8/11.
 * 保存本地出发吧信息
 */
public class GoTravelMesgBean {

    private String currentCity = "";//出发城市
    private String destinCity = "";//目的地
    private String scenic = "";//景点
    private String travelperson = "";//出行人数
    private String demand = "";//具体要求
    private String travelDateTime = "";//出行日期
    private String traveltype = "";//计价方式
    private String travelTime = "";//游玩时长
    private String isNeedGuide = "";//是否需要导游证
    private String  guidePrice = "";//导游价钱（单价）
    private String  pickupType = "";//接待方式
    private String pickupPrice = "";//接待价格
    private String startTime = "";//接待具体时间
    private String   pickupSex = "";//接单人性别

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
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

    public String getIsNeedGuide() {
        return isNeedGuide;
    }

    public void setIsNeedGuide(String isNeedGuide) {
        this.isNeedGuide = isNeedGuide;
    }

    public String getGuidePrice() {
        return guidePrice;
    }

    public void setGuidePrice(String guidePrice) {
        this.guidePrice = guidePrice;
    }

    public String getPickupType() {
        return pickupType;
    }

    public void setPickupType(String pickupType) {
        this.pickupType = pickupType;
    }

    public String getPickupPrice() {
        return pickupPrice;
    }

    public void setPickupPrice(String pickupPrice) {
        this.pickupPrice = pickupPrice;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPickupSex() {
        return pickupSex;
    }

    public void setPickupSex(String pickupSex) {
        this.pickupSex = pickupSex;
    }


}
