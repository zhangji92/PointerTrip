package yc.pointer.trip.untils;


import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 裁剪视频工具类
 */
public class TrimVideoUtils {

    /**
     * 单例对象
     */
    private static TrimVideoUtils instance = null;

    /**
     * 私有的默认构造函数
     */
    private TrimVideoUtils() {
    }

    /**
     * 获取单例实体对象
     */
    public static TrimVideoUtils getInstance() {
        if (instance == null) {
            instance = new TrimVideoUtils();
        }
        return instance;
    }

    // =============== 回调事件  ===============
    /**
     * 裁剪回调接口
     */
    private TrimFileCallBack trimCallBack;

    /**
     * 设置裁剪回调
     *
     * @param trimCallBack
     */
    public void setTrimCallBack(TrimFileCallBack trimCallBack) {
        this.trimCallBack = trimCallBack;
    }

    /**
     * 裁剪文件回调接口
     */
    public interface TrimFileCallBack {
        /**
         * 裁剪回调
         *
         * @param isNew    是否新剪辑
         * @param startS   开始时间(秒)
         * @param endS     结束时间(秒)
         * @param vTotal   视频长度
         * @param file     需要裁剪的文件路径
         * @param trimFile 裁剪后保存的文件路径
         */
        public void trimCallback(boolean isNew, double startS, double endS,
                                 double vTotal, File file, File trimFile);

        /**
         * 裁剪失败回调
         *
         * @param eType 错误类型
         */
        public void trimError(int eType);
    }

    // =============== 对外公开方法  ===============
    /**
     * 是否暂停裁剪
     */
    private boolean isStopTrim = false;
    // -- 常量 --
    /**
     * 裁剪保存的文件地址
     */
    public static final String TRIM_SAVE_PATH = "trimSavePath";
    /**
     * 裁剪选择
     */
    public static final int TRIM_SWITCH = -8;
    /**
     * 停止裁剪
     */
    public static final int TRIM_STOP = -9;
    /**
     * 文件不存在
     */
    public static final int FILE_NOT_EXISTS = -10;
    /**
     * 裁剪失败
     */
    public static final int TRIM_FAIL = -11;
    /**
     * 裁剪成功
     */
    public static final int TRIM_SUCCESS = -12;

    /**
     * 暂停裁剪
     */
    public void stopTrim() {
        isStopTrim = true;
    }

    /**
     * 裁剪回调
     *
     * @param isNew    是否新剪辑(true = 新剪辑，false = 修改原剪辑-覆盖)
     * @param startS   开始时间(秒)
     * @param endS     结束时间(秒)
     * @param file     需要裁剪的文件路径
     * @param trimFile 裁剪后保存的文件路径
     */
    public void startTrim(boolean isNew, double startS, double endS, File file, File trimFile) {
        // 默认非暂停裁剪
        isStopTrim = false;
        // 需要裁剪的视频必须存在
        if (file != null && file.exists()) {
            try {

                //将要剪辑的视频文件
                Movie movie = MovieCreator.build(file.getAbsolutePath());
                List<Track> tracks = movie.getTracks();
                movie.setTracks(new LinkedList<Track>());
                //时间是否修正
                boolean timeCorrected = false;
                //计算并换算剪切时间
                for (Track track : tracks) {
                    if (track.getSyncSamples() != null
                            && track.getSyncSamples().length > 0) {
                        if (timeCorrected) {
                            throw new RuntimeException(
                                    "The startTime has already been corrected by another track with SyncSample. Not Supported.");
                        }
                        //true,false表示短截取；false,true表示长截取
                        startS = correctTimeToSyncSample(track, startS, false);//修正后的开始时间
                        endS = correctTimeToSyncSample(track, endS, true);     //修正后的结束时间
                        timeCorrected = true;
                    }
                }
                //根据换算到的开始时间和结束时间来截取视频
                for (Track track : tracks) {
                    long currentSample = 0; //视频截取到的当前的位置的时间
                    double currentTime = 0; //视频的时间长度
                    double lastTime = -1;    //上次截取到的最后的时间
                    long startSample1 = -1;  //截取开始的时间
                    long endSample1 = -1;    //截取结束的时间

                    //设置开始剪辑的时间和结束剪辑的时间  避免超出视频总长
                    for (int i = 0; i < track.getSampleDurations().length; i++) {
                        long delta = track.getSampleDurations()[i];
                        if (currentTime > lastTime && currentTime <= startS) {
                            startSample1 = currentSample;//编辑开始的时间
                        }
                        if (currentTime > lastTime && currentTime <= endS) {
                            endSample1 = currentSample;  //编辑结束的时间
                        }
                        lastTime = currentTime;          //上次截取到的时间（避免在视频最后位置了还在增加编辑结束的时间）
                        currentTime += (double) delta
                                / (double) track.getTrackMetaData().getTimescale();//视频的时间长度
                        currentSample++;                 //当前位置+1
                    }
                    movie.addTrack(new CroppedTrack(track, startSample1, endSample1));// 创建一个新的视频文件
                }
                //合成视频mp4
                Container out = new DefaultMp4Builder().build(movie);

                FileOutputStream fos = new FileOutputStream(new File(trimFile.getAbsolutePath()));
                FileChannel fco = fos.getChannel();
                out.writeContainer(fco);
                //关闭流
                fco.close();
                fos.close();

                // 裁剪成功
                if (this.trimCallBack != null) {
                    // 裁剪视频的总时间
                    int vTotal = (int) (endS - startS);
                    // 触发回调
                    this.trimCallBack.trimCallback(isNew, startS, endS, vTotal, file, trimFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // --
                if (this.trimCallBack != null) {
                    this.trimCallBack.trimError(TRIM_FAIL);
                }
                // --
                try {
                    if (trimFile.exists()) {
                        trimFile.delete();
                    }
                } catch (Exception e2) {
                }
            }
        } else {
            if (this.trimCallBack != null) {
                this.trimCallBack.trimError(FILE_NOT_EXISTS);
            }
        }
    }

    //换算剪切时间
    public static double correctTimeToSyncSample(Track track, double cutHere,
                                                 boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];
            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(),
                        currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta
                    / (double) track.getTrackMetaData().getTimescale();
            currentSample++;
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }





}
