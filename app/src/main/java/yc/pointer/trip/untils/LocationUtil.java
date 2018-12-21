package yc.pointer.trip.untils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 高德定位工具类
 * Created by Administrator on 2017/5/22.
 */

public class LocationUtil implements AMapLocationListener {

    private static AMapLocationClientOption mLocationOption = null;
    private static AMapLocationClient mlocationClient;
    private static LocationUtil locationUtil = null;


    public static LocationUtil getLocationUtilInstance() {
        if (locationUtil == null) {
            synchronized (LocationUtil.class){
                if (locationUtil==null){
                    locationUtil = new LocationUtil();
                }
            }
        }
        return locationUtil;
    }

    private LocationUtil() {

    }

    private ICallLocation mICallLocation;

    public void setmICallLocation(ICallLocation mICallLocation) {
        this.mICallLocation = mICallLocation;
    }

    /**
     * 初始化定位
     */
    public LocationUtil initLocation(Context context) {
        //声明mLocationOption对象
        mlocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(3000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
        return locationUtil;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mICallLocation!=null){
            mICallLocation.locationMsg(aMapLocation);
        }

    }


    public interface ICallLocation {
        void locationMsg(AMapLocation aMapLocation);
    }

    public  void stopLocation(){
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
        mlocationClient.unRegisterLocationListener(this);
    }
}
