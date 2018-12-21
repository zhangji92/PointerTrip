package yc.pointer.trip.untils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.umeng.socialize.utils.ContextUtil.getPackageName;

/**
 * Created by moyan on 2017/8/23.
 */
public class AppExit2Back {
    private static Boolean isExit = false;


    /**
     * 退出App程序应用
     *
     * @param context 上下文
     * @return boolean True退出|False提示
     */

    public static boolean exitApp(Context context) {
//        AppDavikActivityMgr.getScreenManager().removeAllActivity();
//        ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        activityMgr.restartPackage(context.getPackageName());
//        System.exit(0);
        ////创建ACTION_MAIN
        //Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_HOME);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Context content = ((Activity) context);
        ////启动ACTION_MAIN
        //content.startActivity(intent);
        //android.os.Process.killProcess(android.os.Process.myPid());

//        AppLogMessageMgr.i("AppExit2Back-->>exitApp", isExit + "");
//        AppLogMessageMgr.i("AppExit2Back-->>exitApp", "最大内存：" + Runtime.getRuntime().maxMemory());
//        AppLogMessageMgr.i("AppExit2Back-->>exitApp", "占用内存：" + Runtime.getRuntime().totalMemory());
//        AppLogMessageMgr.i("AppExit2Back-->>exitApp", "空闲内存：" + Runtime.getRuntime().freeMemory());

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());
        }


        return isExit;
    }
}
