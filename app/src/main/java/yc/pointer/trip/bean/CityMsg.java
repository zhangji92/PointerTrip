package yc.pointer.trip.bean;

import java.util.List;

/**
 * Create by 张继
 * 2017/9/7
 * 11:28
 */
public class CityMsg{


    private List<CitylistBean> citylist;

    public List<CitylistBean> getCitylist() {
        return citylist;
    }

    public void setCitylist(List<CitylistBean> citylist) {
        this.citylist = citylist;
    }

    public static class CitylistBean {
        /**
         * p : 请选择
         * c : [{"n":"请选择"}]
         */

        private String p;
        private List<CBean> c;

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public List<CBean> getC() {
            return c;
        }

        public void setC(List<CBean> c) {
            this.c = c;
        }

        public static class CBean {
            /**
             * n : 请选择
             */

            private String n;

            public String getN() {
                return n;
            }

            public void setN(String n) {
                this.n = n;
            }
        }
    }
}
