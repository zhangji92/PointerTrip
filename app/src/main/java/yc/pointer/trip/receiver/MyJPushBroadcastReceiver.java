package yc.pointer.trip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.service.PushService;
import yc.pointer.trip.activity.MainActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;

/**
 * Create by 张继
 * 2017/9/4
 * 13:16
 * 极光推送的广播
 *
 */
public class MyJPushBroadcastReceiver extends BroadcastReceiver {

    private String devcode;//设备识别码
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent pushintent=new Intent(context,PushService.class);//启动极光推送的服务
        context.startService(pushintent);

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {//收到了自定义消息
            // bundle.getString(JPushInterface.EXTRA_MESSAGE)
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {//用户收到通知
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {//打开通知

            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                JSONObject jsonObject = new JSONObject(extras);
                String type = jsonObject.getString("type");
                String oid = jsonObject.getString("oid");
                String uid = jsonObject.getString("uid");
                Intent intentMain = new Intent(context, MainActivity.class);
                intentMain.putExtra("oid", oid);
                intentMain.putExtra("type", type);
                intentMain.putExtra("uid", uid);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentMain);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
