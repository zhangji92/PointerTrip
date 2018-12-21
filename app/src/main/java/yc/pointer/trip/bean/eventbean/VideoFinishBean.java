package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 11:17
 * 公司：
 * 描述：使用EventBus  代替广播 广播action是Video Finish
 */

public class VideoFinishBean {
    private String mVideoFinish;

    public VideoFinishBean(String mVideoFinish) {
        this.mVideoFinish = mVideoFinish;
    }
    public String getVideoFinish(){
        return mVideoFinish;
    }
}
