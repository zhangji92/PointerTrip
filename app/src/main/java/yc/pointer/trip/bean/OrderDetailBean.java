package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;

import yc.pointer.trip.base.BaseBean;

/**
 * Create by 张继
 * 2017/8/15
 * 13:08
 */
public class OrderDetailBean extends BaseBean {


    /**
     * data : {"oid":"449","number":"162154228","uid":"3","spot":"不限","saddress":"","maddress":"天津市","amount":"1","time_num":"4","time_way":"1","jie_way":"0","jprice":"0","jsex":"不","money":"160","money_y":"128","quid":"0","money_status":"0","send_time":"2018-07-18","is_del":"0","is_dao":"0","ord_status":"0","addtime":"2018-07-11 16:21:54","addtime1":"1531297314","send_time1":"1531843200","pic":"/Uploads/15305282647294.png","nickname":"小七","sex":"男","phone":"18701408576","card_pic":"/Uploads/15178841791173.png","is_com":"0","tui_status":"0","notify_time":"","pay_way":"1","price":"40","start_time":"","end_time":"","tui_status1":"0","bid":"0","play_num":"0","ask":"无","real_name":"刘佳伟","is_car":"0","car_price":"0","2wm_pic":"","te":"接单酬劳将由七个工作日内返到你的支付宝账户，如超时未到账请联系我们，由于第三方收取手续费，取消订单时将扣除手续费后退款。","s_pinyin":"","m_pinyin":"tianjinshi","ord_end":"2018-07-18 18:00:00","q_phone":"","q_pic":"","q_nickname":"","q_dpic":"","q_card_pic":""}
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
         * oid : 449
         * number : 162154228
         * uid : 3
         * spot : 不限
         * saddress :
         * maddress : 天津市
         * amount : 1
         * time_num : 4
         * time_way : 1
         * jie_way : 0
         * jprice : 0
         * jsex : 不
         * money : 160
         * money_y : 128
         * quid : 0
         * money_status : 0
         * send_time : 2018-07-18
         * is_del : 0
         * is_dao : 0
         * ord_status : 0
         * addtime : 2018-07-11 16:21:54
         * addtime1 : 1531297314
         * send_time1 : 1531843200
         * pic : /Uploads/15305282647294.png
         * nickname : 小七
         * sex : 男
         * phone : 18701408576
         * card_pic : /Uploads/15178841791173.png
         * is_com : 0
         * tui_status : 0
         * notify_time :
         * pay_way : 1
         * price : 40
         * start_time :
         * end_time :
         * tui_status1 : 0
         * bid : 0
         * play_num : 0
         * ask : 无
         * real_name : 刘佳伟
         * is_car : 0
         * car_price : 0
         * 2wm_pic :
         * te : 接单酬劳将由七个工作日内返到你的支付宝账户，如超时未到账请联系我们，由于第三方收取手续费，取消订单时将扣除手续费后退款。
         * s_pinyin :
         * m_pinyin : tianjinshi
         * ord_end : 2018-07-18 18:00:00
         * q_phone :
         * q_pic :
         * q_nickname :
         * q_dpic :
         * q_card_pic :
         */

        private String oid;
        private String number;
        private String uid;
        private String spot;
        private String saddress;
        private String maddress;
        private String amount;
        private String time_num;
        private String time_way;
        private String jie_way;
        private String jprice;
        private String jsex;
        private String money;
        private String money_y;
        private String quid;
        private String money_status;
        private String send_time;
        private String is_del;
        private String is_dao;
        private String ord_status;
        private String addtime;
        private String addtime1;
        private String send_time1;
        private String pic;
        private String nickname;
        private String sex;
        private String phone;
        private String card_pic;
        private String is_com;
        private String tui_status;
        private String notify_time;
        private String pay_way;
        private String price;
        private String start_time;
        private String end_time;
        private String tui_status1;
        private String bid;
        private String play_num;
        private String ask;
        private String real_name;
        private String is_car;
        private String car_price;
        private String erwm_pic;
        private String te;
        private String s_pinyin;
        private String m_pinyin;
        private String ord_end;
        private String q_phone;
        private String q_pic;
        private String q_nickname;
        private String q_dpic;
        private String q_card_pic;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSpot() {
            return spot;
        }

        public void setSpot(String spot) {
            this.spot = spot;
        }

        public String getSaddress() {
            return saddress;
        }

        public void setSaddress(String saddress) {
            this.saddress = saddress;
        }

        public String getMaddress() {
            return maddress;
        }

        public void setMaddress(String maddress) {
            this.maddress = maddress;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTime_num() {
            return time_num;
        }

        public void setTime_num(String time_num) {
            this.time_num = time_num;
        }

        public String getTime_way() {
            return time_way;
        }

        public void setTime_way(String time_way) {
            this.time_way = time_way;
        }

        public String getJie_way() {
            return jie_way;
        }

        public void setJie_way(String jie_way) {
            this.jie_way = jie_way;
        }

        public String getJprice() {
            return jprice;
        }

        public void setJprice(String jprice) {
            this.jprice = jprice;
        }

        public String getJsex() {
            return jsex;
        }

        public void setJsex(String jsex) {
            this.jsex = jsex;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMoney_y() {
            return money_y;
        }

        public void setMoney_y(String money_y) {
            this.money_y = money_y;
        }

        public String getQuid() {
            return quid;
        }

        public void setQuid(String quid) {
            this.quid = quid;
        }

        public String getMoney_status() {
            return money_status;
        }

        public void setMoney_status(String money_status) {
            this.money_status = money_status;
        }

        public String getSend_time() {
            return send_time;
        }

        public void setSend_time(String send_time) {
            this.send_time = send_time;
        }

        public String getIs_del() {
            return is_del;
        }

        public void setIs_del(String is_del) {
            this.is_del = is_del;
        }

        public String getIs_dao() {
            return is_dao;
        }

        public void setIs_dao(String is_dao) {
            this.is_dao = is_dao;
        }

        public String getOrd_status() {
            return ord_status;
        }

        public void setOrd_status(String ord_status) {
            this.ord_status = ord_status;
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

        public String getSend_time1() {
            return send_time1;
        }

        public void setSend_time1(String send_time1) {
            this.send_time1 = send_time1;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCard_pic() {
            return card_pic;
        }

        public void setCard_pic(String card_pic) {
            this.card_pic = card_pic;
        }

        public String getIs_com() {
            return is_com;
        }

        public void setIs_com(String is_com) {
            this.is_com = is_com;
        }

        public String getTui_status() {
            return tui_status;
        }

        public void setTui_status(String tui_status) {
            this.tui_status = tui_status;
        }

        public String getNotify_time() {
            return notify_time;
        }

        public void setNotify_time(String notify_time) {
            this.notify_time = notify_time;
        }

        public String getPay_way() {
            return pay_way;
        }

        public void setPay_way(String pay_way) {
            this.pay_way = pay_way;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getTui_status1() {
            return tui_status1;
        }

        public void setTui_status1(String tui_status1) {
            this.tui_status1 = tui_status1;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getPlay_num() {
            return play_num;
        }

        public void setPlay_num(String play_num) {
            this.play_num = play_num;
        }

        public String getAsk() {
            return ask;
        }

        public void setAsk(String ask) {
            this.ask = ask;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getIs_car() {
            return is_car;
        }

        public void setIs_car(String is_car) {
            this.is_car = is_car;
        }

        public String getCar_price() {
            return car_price;
        }

        public void setCar_price(String car_price) {
            this.car_price = car_price;
        }

        public String getErwm_pic() {
            return erwm_pic;
        }

        public void setErwm_pic(String erwm_pic) {
            this.erwm_pic = erwm_pic;
        }

        public String getTe() {
            return te;
        }

        public void setTe(String te) {
            this.te = te;
        }

        public String getS_pinyin() {
            return s_pinyin;
        }

        public void setS_pinyin(String s_pinyin) {
            this.s_pinyin = s_pinyin;
        }

        public String getM_pinyin() {
            return m_pinyin;
        }

        public void setM_pinyin(String m_pinyin) {
            this.m_pinyin = m_pinyin;
        }

        public String getOrd_end() {
            return ord_end;
        }

        public void setOrd_end(String ord_end) {
            this.ord_end = ord_end;
        }

        public String getQ_phone() {
            return q_phone;
        }

        public void setQ_phone(String q_phone) {
            this.q_phone = q_phone;
        }

        public String getQ_pic() {
            return q_pic;
        }

        public void setQ_pic(String q_pic) {
            this.q_pic = q_pic;
        }

        public String getQ_nickname() {
            return q_nickname;
        }

        public void setQ_nickname(String q_nickname) {
            this.q_nickname = q_nickname;
        }

        public String getQ_dpic() {
            return q_dpic;
        }

        public void setQ_dpic(String q_dpic) {
            this.q_dpic = q_dpic;
        }

        public String getQ_card_pic() {
            return q_card_pic;
        }

        public void setQ_card_pic(String q_card_pic) {
            this.q_card_pic = q_card_pic;
        }
    }
}
