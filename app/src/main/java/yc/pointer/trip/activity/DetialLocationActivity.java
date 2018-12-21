package yc.pointer.trip.activity;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.DetialLocationAdapter;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.LocationInfo;
import yc.pointer.trip.fragment.MakeMoneyFragment;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.LocationDialog;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2017/11/17.
 */

public class DetialLocationActivity extends BaseActivity implements AMapLocationListener, PoiSearch.OnPoiSearchListener {

    private List<LocationInfo> locationTitle = new ArrayList<>();//详细地址简称

    @BindView(R.id.location_listview)
    ListView locationListview;
    private DetialLocationAdapter adapter;
    private AMapLocationClient mlocationClient;
    private String title;

    private LoadDialog mLoadDialog;

    LocationManager locationManager;

    @Override
    protected int getContentViewLayout() {
        return R.layout.detial_location_layout;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.video_location);

//        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean providerEnabled1 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (APPUtils.isOPen(this)) {
            getData();


        } else {
            LocationDialog locationDialog = new LocationDialog(this, R.style.user_default_dialog, this, 123);
            locationDialog.setMsg("你的GPS定位服务已关闭，请检查开启");
            locationDialog.setLocationCallBack(new LocationDialog.LocationCallBack() {
                @Override
                public void closeDialog() {
                    finish();
                }
            });
            locationDialog.show();
        }
    }

    private void getData() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        initLocate();
        adapter = new DetialLocationAdapter(locationTitle, this);
        locationListview.setAdapter(adapter);
        locationListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                title = DetialLocationActivity.this.locationTitle.get(position).getLocationTitle();
                if (!StringUtil.isEmpty(title)) {
                    Intent intent = new Intent();
                    intent.putExtra("location", title);
                    setResult(1, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (APPUtils.isOPen(this)) {
                getData();
            } else {
                finish();
            }
        }
    }

    /**
     * 初始化定位
     */
    private void initLocate() {
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = null;
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        mLocationOption.setOnceLocationLatest(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        PoiSearch.Query query = poiResult.getQuery();
        ArrayList<PoiItem> pois = poiResult.getPois();
        for (PoiItem poi : pois) {
            String name = poi.getCityName();
            String snippet = poi.getSnippet();
            LocationInfo info = new LocationInfo();
            info.setLocationDetial(snippet);
            info.setLocationTitle(snippet);
            LatLonPoint point = poi.getLatLonPoint();

            info.setLatitude(point.getLatitude());
            info.setLonTitude(point.getLongitude());
            locationTitle.add(info);
//            Log.d("haha", "poi" + snippet);

        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            mLoadDialog.dismiss();

            aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
            double latitude = aMapLocation.getLatitude();//获取纬度
            double longitude = aMapLocation.getLongitude();//获取经度
            aMapLocation.getAccuracy();//获取精度信息
//                    Log.d("haha", amapLocation.getAddress());
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setLocationTitle(aMapLocation.getPoiName());
            locationInfo.setLocationDetial(aMapLocation.getAddress());

            locationInfo.setLatitude(latitude);
            locationInfo.setLonTitude(longitude);
            locationTitle.clear();
            locationTitle.add(locationInfo);
            adapter.notifyDataSetChanged();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(aMapLocation.getTime());
            df.format(date);//定位时间
            PoiSearch.Query query = new PoiSearch.Query("", "", "");
            query.setPageSize(20);
            PoiSearch search = new PoiSearch(DetialLocationActivity.this, query);
            search.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 10000));
            search.setOnPoiSearchListener(DetialLocationActivity.this);
            search.searchPOIAsyn();
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (APPUtils.isOPen(this)) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }

    }


}
