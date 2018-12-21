package yc.pointer.trip.untils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/12/16.
 * 屏幕、像素相关的封装
 */

public class DensityUtil {

    private DensityUtil() {
        /** cannot be instantiated 不能被实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取屏幕的高度
     *
     * @param context 上下文菜单
     * @return 屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMeterics = new DisplayMetrics();//屏幕分辨率的信息
        wm.getDefaultDisplay().getMetrics(outMeterics);
        return outMeterics.heightPixels;
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context 上下文菜单
     * @return 屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMeterics = new DisplayMetrics();//屏幕分辨率的信息
        wm.getDefaultDisplay().getMetrics(outMeterics);
        return outMeterics.widthPixels;
    }

    /**
     * 获取状态栏的高度
     *
     * @param context 上下文
     * @return 状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            //使用反射拿到dimen中status_bar_height的对应的id资源
            Class<?> clazz = Class.forName("com.android.internal.R&dimen");
            Object obj = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(obj).toString());
            //通过getResources()方法拿到该id对应的值
            statusHeight = context.getResources().getDimensionPixelSize(height);//状态栏的高度
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取状态栏的高度
     *
     * @param activity 上下文
     * @return 状态栏的高度
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = -1;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        statusHeight = dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度
        return statusHeight;
    }

    /**
     * 截取当前的屏幕截图(包括状态栏)

     * @param activity 当前的屏幕
     * @return 返回截图(bitmap)类型
     */
    public static Bitmap snapShortWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();//获取windows中最顶层的view
        view.setDrawingCacheEnabled(true);//运行当前窗口保存缓存信息
        view.buildDrawingCache();//启用DrawingCache并创建位图
        Bitmap bitmap = view.getDrawingCache();//把窗口的位图转成bitmap类型
        //屏幕的宽高
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bitmap, 0, 0, width, height);//屏幕截图
        //销毁缓存信息
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 去掉状态栏的屏幕截图
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShortWithOutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();//获取windows中最顶层的view
        view.setDrawingCacheEnabled(true);//运行当前窗口保存缓存信息
        view.buildDrawingCache();//启用DrawingCache并创建位图
        Bitmap bitmap = view.getDrawingCache();//把窗口的位图转成bitmap类型

        //获取状态栏的高度
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusHeight = rect.top;
        //屏幕的宽高
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        //去掉状态栏的屏幕截图
        bp = Bitmap.createBitmap(bitmap, 0, statusHeight, width, height
                - statusHeight);
        //销毁缓存信息
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpVal   dp值
     * @return 返回的px值
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context 上下文
     * @param spVal   sp值
     * @return 返回px值
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal   px值
     * @param context 上下文
     * @return 返回的px值
     */
    public static float px2dp(Context context, float pxVal) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param context 上下文
     * @param pxVal   px值
     * @return 返回sp值
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param mContext
     * @param dpValue
     */
    public static int dipConvertPx(Context mContext, float dpValue) {
        try {
            float scale = mContext.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {

        }
        return -1;
    }


    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        }
    }
}
