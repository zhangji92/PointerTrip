package yc.pointer.trip.bean;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;

/**
 * Created by 张继
 * 2018/8/14
 * 18:47
 * 公司：
 * 描述：
 */
@Table
public class CommentBean extends SugarRecord implements Serializable {
    /**
     * c_nickname : 七淼
     * c_info : 不错
     */
    @Expose
    private String c_nickname;
    @Expose
    private String c_info;

    public String getC_nickname() {
        return c_nickname;
    }

    public void setC_nickname(String c_nickname) {
        this.c_nickname = c_nickname;
    }

    public String getC_info() {
        return c_info;
    }

    public void setC_info(String c_info) {
        this.c_info = c_info;
    }
}
