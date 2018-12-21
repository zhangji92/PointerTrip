package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继 on 2017/11/20.
 */

public class SetServiceWrateBean extends BaseBean {


    /**
     * hb : {"id":"1","title":"该游记已进入后台审核，审核通过将给予您红包奖励！","info":"绑定手机号可以让更多用户关注您，发布游记通过审核后还能够获取红包奖励哦！","is_hb":"1","price":"0.50","info_text":"<p>1、用户可通过指针自由行APP内置的\u201c拍世界\u201d功能拍摄视频，每拍摄一段视频，我们将为拍摄者送上现金红包。现金红包每日累计满5元后可以提现，每日提现上限额度为50元。<\/p><p>\r\n2、上传视频仅限通过指针自由行内置的\u201c拍世界\u201d功能实时拍摄并上传的视频，不支持上传用户手机内的本地视频。<\/p>\r\n<p>3、拍摄视频内容主题需为城市街景、景点风貌、自然风光、历史古迹等旅游特色内容，且上传视频需严格遵守国家法律法规，禁止上传含有淫秽、色情、低俗、垃圾广告、恶意推广、以及国家相关部门禁止传播的相关内容。<\/p>","pic":"/Images/hb1.jpg","pic1":"/Images/hb2.jpg","info_yj":null}
     */

    private HbBean hb;

    public HbBean getHb() {
        return hb;
    }

    public void setHb(HbBean hb) {
        this.hb = hb;
    }

    public static class HbBean {
        /**
         * id : 1
         * title : 该游记已进入后台审核，审核通过将给予您红包奖励！
         * info : 绑定手机号可以让更多用户关注您，发布游记通过审核后还能够获取红包奖励哦！
         * is_hb : 1
         * price : 0.50
         * info_text : <p>1、用户可通过指针自由行APP内置的“拍世界”功能拍摄视频，每拍摄一段视频，我们将为拍摄者送上现金红包。现金红包每日累计满5元后可以提现，每日提现上限额度为50元。</p><p>
         2、上传视频仅限通过指针自由行内置的“拍世界”功能实时拍摄并上传的视频，不支持上传用户手机内的本地视频。</p>
         <p>3、拍摄视频内容主题需为城市街景、景点风貌、自然风光、历史古迹等旅游特色内容，且上传视频需严格遵守国家法律法规，禁止上传含有淫秽、色情、低俗、垃圾广告、恶意推广、以及国家相关部门禁止传播的相关内容。</p>
         * pic : /Images/hb1.jpg
         * pic1 : /Images/hb2.jpg
         * info_yj : null
         */

        private String id;
        private String title;
        private String info;
        private String is_hb;
        private String price;
        private String info_text;
        private String pic;
        private String pic1;
        private Object info_yj;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getIs_hb() {
            return is_hb;
        }

        public void setIs_hb(String is_hb) {
            this.is_hb = is_hb;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getInfo_text() {
            return info_text;
        }

        public void setInfo_text(String info_text) {
            this.info_text = info_text;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getPic1() {
            return pic1;
        }

        public void setPic1(String pic1) {
            this.pic1 = pic1;
        }

        public Object getInfo_yj() {
            return info_yj;
        }

        public void setInfo_yj(Object info_yj) {
            this.info_yj = info_yj;
        }
    }
}
