package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2017/8/2 0002
 * 下午 6:32
 */

public class ScenicSearchBean extends BaseBean {


    /**
     * d_status : 0
     * data : [{"sid":"354","title":"天津方特欢乐世界","pic":"/Images/Scenery/天津方特欢乐世界.png","brief":"天津方特欢乐世界，坐落于天津市滨海新区中新生态城中生大道北首生态岛内，是京津冀地区独一无二的第四代高科技主题公园。","price":"40","zz":"指针旅游协议价"}]
     */

    private int d_status;
    private List<DataBean> data;

    public int getD_status() {
        return d_status;
    }

    public void setD_status(int d_status) {
        this.d_status = d_status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends BaseScenicBean{

    }
}
