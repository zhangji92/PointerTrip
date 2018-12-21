package yc.pointer.trip.bean;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;

/**
 * Created by 张继
 * 2018/8/15
 * 10:39
 * 公司：
 * 描述：
 */
@Table
public class DataAdBean extends SugarRecord  implements Serializable {
    /**
     * aid : 7
     * title : 月入过万，我教你
     * pic : /Uploads/15236008244886.png
     */
    @Expose
    private String aid;
    @Expose
    private String title;
    @Expose
    private String pic;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
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
