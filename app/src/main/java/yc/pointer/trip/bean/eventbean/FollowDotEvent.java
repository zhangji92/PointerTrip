package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/25
 * 14:23
 * 公司：
 * 描述：关注小红点
 */

public class FollowDotEvent {
    private String att_msg_num;

    public FollowDotEvent(String att_msg_num) {
        this.att_msg_num = att_msg_num;
    }

    public String getAtt_Num_msg() {
        return att_msg_num;
    }
}
