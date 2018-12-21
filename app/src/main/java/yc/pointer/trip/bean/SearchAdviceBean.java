package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Create By 张继
 * 2017/7/31 0031
 * 上午 11:50
 * 搜索建议
 */
public class SearchAdviceBean extends BaseBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
