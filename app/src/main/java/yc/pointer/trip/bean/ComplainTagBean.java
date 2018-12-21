package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Created by moyan on 2017/9/8.
 */
public class ComplainTagBean extends BaseBean {


    /**
     * kf_phone : 400-688-2959
     * data : [{"title":"态度恶劣"},{"title":"强制购物"},{"title":"违约早退"},{"title":"注册信息不符"},{"title":"未准时到达"},{"title":"索要额外费用"}]
     */

    private String kf_phone;
    private List<DataBean> data;

    public String getKf_phone() {
        return kf_phone;
    }

    public void setKf_phone(String kf_phone) {
        this.kf_phone = kf_phone;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {


        /**
         * title : 态度恶劣
         */
        private boolean isSelect;
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}
