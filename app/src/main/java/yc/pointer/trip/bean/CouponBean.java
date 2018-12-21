package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Create by 张继
 * 2017/9/8
 * 15:14
 */
public class CouponBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2
         * pic : /Images/dis.jpg
         * url:第三方链接
         * title: 标题
         */

        private String mid;
        private String pic;
        private String url;
        private String title;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public String getId() {
            return mid;
        }

        public void setId(String id) {
            this.mid = id;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }
}
