package yc.pointer.trip.bean;

import android.graphics.Bitmap;

/**
 * Created by moyan on 2018/3/19.
 *
 */

public class EntityVideo {
//    String thumbPath;//视频缩略图路径
    String path;//视频路径
    int duration;//视频时长
    private Bitmap frameAtTime;//视频缩略图

    public Bitmap getFrameAtTime() {
        return frameAtTime;
    }

    public void setFrameAtTime(Bitmap frameAtTime) {
        this.frameAtTime = frameAtTime;
    }



//    public String getThumbPath() {
//        return thumbPath;
//    }
//
//    public void setThumbPath(String thumbPath) {
//        this.thumbPath = thumbPath;
//    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
