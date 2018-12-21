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
public class ZanBean extends SugarRecord implements Serializable {
    /**
     * z_u_pic : /Uploads/1528444573118.png
     */
    @Expose
    private String z_u_pic;

    public String getZ_u_pic() {
        return z_u_pic;
    }

    public void setZ_u_pic(String z_u_pic) {
        this.z_u_pic = z_u_pic;
    }
}
