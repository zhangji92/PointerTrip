package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2017/8/1
 * 3：24
 * 景点
 */

public class ScenicBean extends BaseBean{


    /**
     * data : {"data_type":[{"typeid":"19","title":"娱乐类","pic":"http://139.196.106.89:9999/Image/SceneryType/天津娱乐类.png"},{"typeid":"10","title":"户外类","pic":"http://139.196.106.89:9999/Image/SceneryType/天津户外类.png"},{"typeid":"1","title":"人文类","pic":"http://139.196.106.89:9999/Image/SceneryType/天津人文类.png"}],"data_ad":[{"sid":"411","pic":"http://139.196.106.89:9999/Image/Scenery/水上公园.png"},{"sid":"354","pic":"http://139.196.106.89:9999/Image/Scenery/天津方特欢乐世界.png"},{"sid":"353","pic":"http://139.196.106.89:9999/Image/Scenery/天津动物园.png"},{"sid":"352","pic":"http://139.196.106.89:9999/Image/Scenery/凯旋王国主题乐园.png"}],"data_hot":[{"sid":"411","title":"123123","pic":"http://139.196.106.89:9999/Image/Scenery/水上公园.png","brief":"简介","price":"10","zz":"指针旅游协议价"}],"data_other":[{"sid":"354","title":"天津方特欢乐世界","pic":"http://139.196.106.89:9999/Image/Scenery/天津方特欢乐世界.png","brief":"天津方特欢乐世界，坐落于天津市滨海新区中新生态城中生大道北首生态岛内，是京津冀地区独一无二的第四代高科技主题公园。","price":"40","zz":"指针旅游协议价"},{"sid":"353","title":"天津动物园","pic":"http://139.196.106.89:9999/Image/Scenery/天津动物园.png","brief":"天津动物园坐落于风景秀丽的水上公园南端，北望天塔旋云，南临奥体中心，园内树木葱郁，碧草连茵，天水一色，风光旖旎。","price":"40","zz":"指针旅游协议价"},{"sid":"352","title":"凯旋王国主题乐园","pic":"http://139.196.106.89:9999/Image/Scenery/凯旋王国主题乐园.png","brief":"凯旋王国位于天津市武清区境内，104国道旁。游乐场的吉祥物--凯凯和旋旋两个卡通马形象的人偶成为游乐场颇具亲和力的标志形象。","price":"40","zz":"指针旅游协议价"},{"sid":"351","title":"天津热带植物观光园","pic":"http://139.196.106.89:9999/Image/Scenery/天津热带植物观光园.png","brief":"天津热带植物观光园位于天津市西青区曹庄花卉市场内，2004年被国家旅游局授予国家AAAA级旅游景区 ，国家级科普教育基地。","price":"40","zz":"指针旅游协议价"},{"sid":"350","title":"欢乐谷","pic":"http://139.196.106.89:9999/Image/Scenery/欢乐谷.png","brief":"天津欢乐谷主题公园占地面积35万平方米，坐落于天津市东丽湖畔。全园拥有56项水陆两栖、老少皆宜的游乐设施。","price":"40","zz":"指针旅游协议价"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataTypeBean> data_type;
        private List<DataAdBean> data_ad;
        private List<DataHotBean> data_hot;
        private List<DataOtherBean> data_other;

        public List<DataTypeBean> getData_type() {
            return data_type;
        }

        public void setData_type(List<DataTypeBean> data_type) {
            this.data_type = data_type;
        }

        public List<DataAdBean> getData_ad() {
            return data_ad;
        }

        public void setData_ad(List<DataAdBean> data_ad) {
            this.data_ad = data_ad;
        }

        public List<DataHotBean> getData_hot() {
            return data_hot;
        }

        public void setData_hot(List<DataHotBean> data_hot) {
            this.data_hot = data_hot;
        }

        public List<DataOtherBean> getData_other() {
            return data_other;
        }

        public void setData_other(List<DataOtherBean> data_other) {
            this.data_other = data_other;
        }

        public static class DataTypeBean {
            /**
             * typeid : 19
             * title : 娱乐类
             * pic : http://139.196.106.89:9999/Image/SceneryType/天津娱乐类.png
             */

            private String typeid;
            private String title;
            private String pic;

            public String getTypeid() {
                return typeid;
            }

            public void setTypeid(String typeid) {
                this.typeid = typeid;
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
        }

        public static class DataAdBean {
            /**
             * sid : 411
             * pic : http://139.196.106.89:9999/Image/Scenery/水上公园.png
             */

            private String sid;
            private String pic;

            public String getSid() {
                return sid;
            }

            public void setSid(String sid) {
                this.sid = sid;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }
        }

        public static class DataHotBean extends BaseScenicBean{

        }

        public static class DataOtherBean extends BaseScenicBean{

        }
    }
}
