package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Create by 张继
 * 2017/9/7
 * 17:37
 */
public class ApkUpdateBean extends BaseBean {

    /**
     * data : {"mastupdate":1,"type":"android","currentV":"1.0.1","durl":"https://www.zhizhentrip.com/apk/app-debug.apk","data":[{"id":"1","info":"1、修复已知问题"},{"id":"2","info":"2、杀了2个程序员祭天"}]}
     */

    private DataBeanX data;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * mastupdate : 1
         * type : android
         * currentV : 1.0.1
         * durl : https://www.zhizhentrip.com/apk/app-debug.apk
         * data : [{"id":"1","info":"1、修复已知问题"},{"id":"2","info":"2、杀了2个程序员祭天"}]
         */

        private int mastupdate;
        private String type;
        private String currentV;
        private String durl;
        private List<DataBean> data;

        public int getMastupdate() {
            return mastupdate;
        }

        public void setMastupdate(int mastupdate) {
            this.mastupdate = mastupdate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCurrentV() {
            return currentV;
        }

        public void setCurrentV(String currentV) {
            this.currentV = currentV;
        }

        public String getDurl() {
            return durl;
        }

        public void setDurl(String durl) {
            this.durl = durl;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 1
             * info : 1、修复已知问题
             */

            private String id;
            private String info;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getInfo() {
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }
        }
    }
}
