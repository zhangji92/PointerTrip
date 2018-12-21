package yc.pointer.trip.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Create By 张继
 * 2017/7/30 0030
 * 下午 3:53
 * 路书的bean
 */
public class BookBean extends SugarRecord implements Serializable {
    /**
     * bid : 1015
     * uid : 2
     * title : 我的Mac
     * type :
     * city : 天津
     * spot :
     * b_pic : /Uploads/15113213555603.png
     * y_pic : /Uploads/15113213556785.png
     * is_order : 1
     * is_ad : 1
     * is_index : 1
     * look_num : 1
     * zan_num : 0
     * ord_num : 0
     * status : 1
     * info : 我是小破哦婆婆哦
     * is_cao : 0
     * nickname : 佚名
     * phone : 18222396725
     * pic : /Uploads/15058754861558.png
     * cp : 指针出品
     * video : /Video/2_15113213554695.mp4
     * col_num : 0
     * width : 0
     * height : 0
     * is_video : 1
     * location : 所在位置
     * addtime : 2017-11-22
     * addtime1 : 1511321355
     * "f_status":"0"
     * "new_url":"https:\/\/www.zhizhentrip.com\/Home\/Book\/bookDetails?bid=1135"
     */

    @Expose
    private String bid;
    @Expose
    private String uid;
    @Expose
    private String title;
    @Expose
    private String type;
    @Expose
    private String city;
    @Expose
    private String spot;
    @Expose
    private String b_pic;
    @Expose
    private String y_pic;
    @Expose
    private String is_order;
    @Expose
    private String is_ad;
    @Expose
    private String is_index;
    @Expose
    private String look_num;
    @Expose
    private String zan_num;
    @Expose
    private String ord_num;
    @Expose
    private String statusX;
    @Expose
    private String info;
    @Expose
    private String is_cao;
    @Expose
    private String nickname;
    @Expose
    private String phone;
    @Expose
    private String pic;
    @Expose
    private String cp;
    @Expose
    private String video;
    @Expose
    private String col_num;
    @Expose
    private String width;
    @Expose
    private String height;
    @Expose
    private String is_video;
    @Expose
    private String addtime;
    @Expose
    private String location;
    @Expose
    private String addtime1;
    @Expose
    private String length;//视频时长
    @Expose
    private String c_num;//评论数
    /**
     * f_status : 0 为关注 1已关注
     */
    @Expose
    private String f_status;
    /**
     * new_url : https://www.zhizhentrip.com/Home/Book/bookDetails?bid=1135
     */
    @Expose
    private String share_url;
    /**
     * s_num : 30 转发数
     */
    @Expose
    private String s_num;

    @Expose
    private String c_nickname;
    @Expose
    private String c_u_pic;
    @Expose
    private String is_jie;//判断是否为VIP
    @Expose
    private String is_vip;//VIP用户的身份类型  0：不是会员  1：黄金会员 2：白金会员
    @Expose
    private String is_del = "0";//判断评论消息是否已删除  0：未删除  1：已删除
    @Expose
    private String z_status = "0";//判断该游记是否已点赞  0：未点赞  1：已点赞
    @Expose
    private String book_num;//判断该游记是否已点赞  0：未点赞  1：已点赞
    @Expose
    private String u_money;//判断该游记是否已点赞  0：未点赞  1：已点赞

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getBook_num() {
        return book_num;
    }

    public void setBook_num(String book_num) {
        this.book_num = book_num;
    }

    public String getU_money() {
        return u_money;
    }

    public void setU_money(String u_money) {
        this.u_money = u_money;
    }

    public String getZ_status() {
        return z_status;
    }

    public void setZ_status(String z_status) {
        this.z_status = z_status;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getIs_jie() {
        return is_jie;
    }

    public void setIs_jie(String is_jie) {
        this.is_jie = is_jie;
    }

    public String getC_u_pic() {
        return c_u_pic;
    }

    public void setC_u_pic(String c_u_pic) {
        this.c_u_pic = c_u_pic;
    }

    public String getC_nickname() {
        return c_nickname;
    }

    public void setC_nickname(String c_nickname) {
        this.c_nickname = c_nickname;
    }

    public String getC_num() {
        return c_num;
    }

    public void setC_num(String c_num) {
        this.c_num = c_num;
    }

    public String getLength() {
        //if (!StringUtil.isEmpty(length)){
        //    return StringUtil.timeFormat(Integer.valueOf(length));
        //}else {
        //    return  StringUtil.timeFormat(Integer.valueOf(0));
        //}
        return length;
    }

    public void setLength(String length) {

        this.length = length;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getB_pic() {
        return b_pic;
    }

    public void setB_pic(String b_pic) {
        this.b_pic = b_pic;
    }

    public String getY_pic() {
        return y_pic;
    }

    public void setY_pic(String y_pic) {
        this.y_pic = y_pic;
    }

    public String getIs_order() {
        return is_order;
    }

    public void setIs_order(String is_order) {
        this.is_order = is_order;
    }

    public String getIs_ad() {
        return is_ad;
    }

    public void setIs_ad(String is_ad) {
        this.is_ad = is_ad;
    }

    public String getIs_index() {
        return is_index;
    }

    public void setIs_index(String is_index) {
        this.is_index = is_index;
    }

    public String getLook_num() {
        return look_num;
    }

    public void setLook_num(String look_num) {
        this.look_num = look_num;
    }

    public String getZan_num() {
        return zan_num;
    }

    public void setZan_num(String zan_num) {
        this.zan_num = zan_num;
    }

    public String getOrd_num() {
        return ord_num;
    }

    public void setOrd_num(String ord_num) {
        this.ord_num = ord_num;
    }

    public String getStatusX() {
        return statusX;
    }

    public void setStatusX(String statusX) {
        this.statusX = statusX;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIs_cao() {
        return is_cao;
    }

    public void setIs_cao(String is_cao) {
        this.is_cao = is_cao;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getCol_num() {
        return col_num;
    }

    public void setCol_num(String col_num) {
        this.col_num = col_num;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getIs_video() {
        return is_video;
    }

    public void setIs_video(String is_video) {
        this.is_video = is_video;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getAddtime1() {
        return addtime1;
    }

    public void setAddtime1(String addtime1) {
        this.addtime1 = addtime1;
    }

    public String getF_status() {
        return f_status;
    }

    public void setF_status(String f_status) {
        this.f_status = f_status;
    }

    public String getNew_url() {
        return share_url;
    }

    public void setNew_url(String new_url) {
        this.share_url = new_url;
    }

    public String getS_num() {
        return s_num;
    }

    public void setS_num(String s_num) {
        this.s_num = s_num;
    }
}
