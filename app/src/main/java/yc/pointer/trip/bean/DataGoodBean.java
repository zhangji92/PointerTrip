package yc.pointer.trip.bean;

import com.google.gson.annotations.Expose;
import com.orm.dsl.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 张继
 * 2018/8/14
 * 18:46
 * 公司：
 * 描述：
 */
@Table
public class DataGoodBean extends BookBean implements Serializable {
    @Expose
    private List<CommentBean> comment;
    @Expose
    private List<ZanBean> zan;

    public List<CommentBean> getComment() {
        return comment;
    }

    public void setComment(List<CommentBean> comment) {
        this.comment = comment;
    }

    public List<ZanBean> getZan() {
        return zan;
    }

    public void setZan(List<ZanBean> zan) {
        this.zan = zan;
    }



    /**
     * bid : 3880
     * uid : 4937
     * title : 青海之美尽人皆知，在星空下望穿银河，在夜空里的璀璨流星
     * type : 精选
     * city : 果洛
     * spot :
     * b_pic : https://zztrip.oss-cn-shanghai.aliyuncs.com/images/15235955164354.png
     * y_pic :
     * is_order : 0
     * is_ad : 0
     * is_index : 1
     * look_num : 4897
     * zan_num : 203
     * ord_num : 0
     * status : 1
     * info : 从草原到戈壁，从平原到山峦。在沙漠里望穿银河，在夜空下的璀璨流星。怀着对天空之镜的向往，一路上收获着美景和快乐 ​
     * is_cao : 0
     * nickname : 旅行美食酱
     * phone : 17611490886
     * pic : /Uploads/15235932978649.png
     * cp : 指针出品
     * is_jie : 0
     * video : https://zztrip.oss-cn-shanghai.aliyuncs.com/video/4937_15235955164218.mp4
     * col_num : 0
     * width : 480
     * height : 272
     * is_video : 1
     * location :
     * is_del : 0
     * is_hb : 1
     * length : 29
     * c_num : 1
     * s_num : 31
     * addtime : 2018-04-13
     * addtime1 : 1523595516
     * updatetime : 1523598912
     * is_heng : 1
     * is_admin : 0
     * is_cf : 0
     * f_status : 0
     * share_url : https://www.zhizhentrip.com/Home/Book/bookDetails?bid=3880
     */

}
