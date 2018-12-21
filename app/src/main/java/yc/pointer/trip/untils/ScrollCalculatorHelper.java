package yc.pointer.trip.untils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import yc.pointer.trip.R;
import yc.pointer.trip.view.DialogSure;

/**
 * 计算滑动，自动播放的帮助类
 * Created by guoshuyu on 2017/11/2.
 */

public class ScrollCalculatorHelper {

    private PlayRunnable runnable;
    boolean inPosition = true;
    private Handler playHandler = new Handler();
    private static ScrollCalculatorHelper instance = null;

    public static ScrollCalculatorHelper getInstance() {
        if (instance == null) {
            synchronized (ScrollCalculatorHelper.class) {
                if (instance == null) {
                    instance = new ScrollCalculatorHelper();
                }
            }
        }
        return instance;
    }


    public void playVideo(RecyclerView view, int visibleCount, int playId) {

        if (view == null) {
            return;
        }

        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        GSYBaseVideoPlayer gsyBaseVideoPlayer = null;
        boolean needPlay = false;

        for (int i = 0; i < visibleCount; i++) {
            if (layoutManager.getChildAt(i) != null && layoutManager.getChildAt(i).findViewById(playId) != null) {
                GSYBaseVideoPlayer player = (GSYBaseVideoPlayer) layoutManager.getChildAt(i).findViewById(playId);
                int height = player.getHeight();
                Rect rect = new Rect(0, 0, 0, height);
                player.getLocalVisibleRect(rect);
                //说明第一个完全可视
                int top = rect.top;
                int bottom = rect.bottom;
                int currentState = player.getCurrentPlayer().getCurrentState();
                int currentStateNormal = GSYBaseVideoPlayer.CURRENT_STATE_NORMAL;
                int currentStateError = GSYBaseVideoPlayer.CURRENT_STATE_ERROR;
                //if (top == 0 && bottom == height) {
                if (bottom == height) {
                    gsyBaseVideoPlayer = player;
                    if ((currentState == currentStateNormal || currentState == currentStateError)) {
                        needPlay = true;
                    }
                    break;
                } else if (rect.top == -1 && rect.bottom == height - 1) {
                    gsyBaseVideoPlayer = player;
                    if ((currentState == currentStateNormal || currentState == currentStateError)) {
                        needPlay = true;
                    }
                    break;
                }

            }
        }

        if (gsyBaseVideoPlayer != null && needPlay) {
            if (runnable != null) {
                GSYBaseVideoPlayer tmpPlayer = runnable.gsyBaseVideoPlayer;
                playHandler.removeCallbacks(runnable);
                runnable = null;
                if (tmpPlayer == gsyBaseVideoPlayer) {
                    return;
                }
            }
            runnable = new PlayRunnable(gsyBaseVideoPlayer);
            //降低频率
            playHandler.postDelayed(runnable, 400);
        }
    }

    private class PlayRunnable implements Runnable {

        GSYBaseVideoPlayer gsyBaseVideoPlayer;

        public PlayRunnable(GSYBaseVideoPlayer gsyBaseVideoPlayer) {
            this.gsyBaseVideoPlayer = gsyBaseVideoPlayer;
        }

        @Override
        public void run() {
            //如果未播放，需要播放
            if (gsyBaseVideoPlayer != null) {
                int[] screenPosition = new int[2];
                gsyBaseVideoPlayer.getLocationOnScreen(screenPosition);
                int halfHeight = gsyBaseVideoPlayer.getHeight() / 2;
                int rangePosition = screenPosition[1] + halfHeight;
                //中心点在播放区域内
                if (rangePosition >= 200 && rangePosition <= 200) {
                    inPosition = true;
                }
                if (inPosition) {
                    startPlayLogic(gsyBaseVideoPlayer, gsyBaseVideoPlayer.getContext());
                }
            }
        }
    }


    /***************************************自动播放的点击播放确认******************************************/
    public void startPlayLogic(GSYBaseVideoPlayer gsyBaseVideoPlayer, Context context) {
        if (!com.shuyu.gsyvideoplayer.utils.CommonUtil.isWifiConnected(context)) {
            //这里判断是否wifi
            showWifiDialog(gsyBaseVideoPlayer, context);
            return;
        }
        gsyBaseVideoPlayer.startPlayLogic();
    }

    private void showWifiDialog(final GSYBaseVideoPlayer gsyBaseVideoPlayer, Context context) {
        if (!NetworkUtils.isAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(com.shuyu.gsyvideoplayer.R.string.no_net), Toast.LENGTH_LONG).show();
            return;
        }

        new DialogSure(context, R.style.user_default_dialog, new DialogSure.CallBackListener() {
            @Override
            public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                if (trueEnable) {
                    gsyBaseVideoPlayer.startPlayLogic();
                } else {
                    inPosition = false;
                }
            }
        }).setTitle("温馨提示")
                .setMsg("当前您正在使用移动网络,继续播放将可能产生额外流量消耗")
                .setPositiveButton("继续")
                .setNegativeButton("取消")
                .show();


//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage(context.getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi));
//        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                gsyBaseVideoPlayer.startPlayLogic();
//            }
//        });
//
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                inPosition=false;
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
    }
}
