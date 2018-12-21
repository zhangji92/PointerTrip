package yc.pointer.trip.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import butterknife.BindView;

import com.amap.api.location.AMapLocation;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.adapter.HitCityAdapter;
import yc.pointer.trip.adapter.HotCityAdapter;
import yc.pointer.trip.adapter.ResultListAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.BaseCityBean;
import yc.pointer.trip.bean.CityBean;
import yc.pointer.trip.event.CityEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.LocationDialog;
import yc.pointer.trip.view.MyLetterView;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 张继
 * 2017/7/12
 * 11:16
 * 城市
 */
public class CityActivity extends BaseActivity implements AbsListView.OnScrollListener {

    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.search_commint)
    ImageView searchCommit;
    @BindView(R.id.list_view)
    ListView mListView;
    @BindView(R.id.search_result)
    ListView searchResult;
    @BindView(R.id.tv_noresult)
    TextView tv_no_result;
    @BindView(R.id.MyLetterView)
    MyLetterView mLetterView;

    private BaseAdapter adapter;
    private ResultListAdapter resultListAdapter;
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private ArrayList<BaseCityBean> allCity_lists; // 所有城市列表
    private ArrayList<CityBean.DataBean.DataAllBean> city_lists;// 城市列表
    private ArrayList<BaseCityBean> city_hot;
    private ArrayList<BaseCityBean> city_result;
    private ArrayList<BaseCityBean> city_history;
    public static String currentCity; // 用于保存定位到的城市
    private int locateProcess = 1; // 记录当前定位的状态 正在定位-定位成功-定位失败
    private boolean isNeedFresh;
    private boolean isScroll = false;

    private String mDevcode;//设备识别码
    private String mUserId;//用户id
    private long mTimestamp;//时间戳
    private Intent intent;
    private LoadDialog mLoadDialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_city;
    }

    @Override
    protected void initView() {

        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        if (APPUtils.isOPen(this)) {
            getLocation();//获取位置信息
        } else {
            LocationDialog locationDialog = new LocationDialog(this, R.style.user_default_dialog, this, 456);
            locationDialog.setMsg("检测到GPS未开启,请开启GPS获取当前城市信息");
            locationDialog.setLocationCallBack(new LocationDialog.LocationCallBack() {
                @Override
                public void closeDialog() {
                    getLocation();//获取位置信息
                }
            });
            locationDialog.show();
        }


        intent = new Intent();
        mDevcode = ((MyApplication) getApplication()).getDevcode();//设备识别码
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();//时间戳
        mUserId = ((MyApplication) getApplication()).getUserId();//用户id
        new ToolbarWrapper(this);
        allCity_lists = new ArrayList<>();//全部城市的集合
        city_hot = new ArrayList<>();//热门城市
        city_result = new ArrayList<>();//搜索结果集合
        city_history = new ArrayList<>();//历史城市集合
        city_lists = new ArrayList<>();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString() == null || "".equals(charSequence.toString())) {
                    mLetterView.setVisibility(View.VISIBLE);//字母控件
                    mListView.setVisibility(View.VISIBLE);//所有数据
                    searchResult.setVisibility(View.GONE);//搜索结果数据
                    tv_no_result.setVisibility(View.GONE);//弹窗
                } else {
                    city_result.clear();//清空搜索结果数据
                    mLetterView.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                    getResultCityList(charSequence.toString());
                    if (city_result.size() <= 0) {
                        tv_no_result.setVisibility(View.VISIBLE);
                        searchResult.setVisibility(View.GONE);
                    } else {
                        tv_no_result.setVisibility(View.GONE);
                        searchResult.setVisibility(View.VISIBLE);
                        resultListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mLetterView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        isNeedFresh = true;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 4) {

                    //全部城市弹窗
                    InsertCity(allCity_lists.get(position).getCityname());

                    intent.putExtra("cityName", allCity_lists.get(position).getCityname());
                    intent.putExtra("cityId", allCity_lists.get(position).getCityid());
                    setResult(6, intent);
                    finish();

                }
            }
        });
        locateProcess = 1;
        mListView.setAdapter(adapter);
        mListView.setOnScrollListener(this);
        resultListAdapter = new ResultListAdapter(this, city_result);
        searchResult.setAdapter(resultListAdapter);
        //搜索结果的接口回调
        resultListAdapter.setICallBackResult(new ResultListAdapter.ICallBackResult() {
            @Override
            public void OnClickResult(int position) {
                InsertCity(city_result.get(position).getCityname());
                intent.putExtra("cityName", city_result.get(position).getCityname());
                intent.putExtra("cityId", city_result.get(position).getCityid());
                setResult(6, intent);
                finish();

            }
        });


        sendCity();//请求城市数据
    }

    /**
     * 获取位置信息
     */
    private void getLocation() {
        LocationUtil.getLocationUtilInstance().initLocation(this).setmICallLocation(new LocationUtil.ICallLocation() {
            @Override
            public void locationMsg(AMapLocation aMapLocation) {
                if (isNeedFresh == false) {
                    return;
                }
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    currentCity = aMapLocation.getCity();//城市信息
                    locateProcess = 2; // 定位成功
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isNeedFresh = false;
                } else {
                    isNeedFresh = true;
                    currentCity = "定位失败";//城市信息
                    locateProcess = 3; // 定位失败
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 请求城市数据
     */
    private void sendCity() {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("uid", mUserId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.CITY, requestBody, new HttpCallBack(new CityEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cityBean(CityEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CityBean bean = event.getData();
        if (bean.getStatus() == 0) {


//            hotCityInit();
            hisCityInit(bean.getData());
            city_hot.addAll(bean.getData().getData_hot());
            city_lists.addAll(bean.getData().getData_all());
            ArrayList<CityBean.DataBean.DataAllBean> lista = getAllCityList(city_lists);
            cityInit(lista);
            setAdapter(allCity_lists, city_hot, city_history);
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 插入去过的城市
     *
     * @param name 城市名称
     */
    public void InsertCity(String name) {
        SharedPreferencesUtils.getInstance().saveHistory(CityActivity.this, "historyCity", name);//历史数据插入
    }

    private void cityInit(ArrayList<CityBean.DataBean.DataAllBean> list) {
        BaseCityBean city = new BaseCityBean("定", "0"); // 当前定位城市
        allCity_lists.add(city);
        city = new BaseCityBean("最", "1"); // 最近访问的城市
        allCity_lists.add(city);
        city = new BaseCityBean("热", "2"); // 热门城市
        allCity_lists.add(city);
        city = new BaseCityBean("全", "3"); // 全部城市
        allCity_lists.add(city);
        allCity_lists.addAll(list);
    }


    /**
     * 历史城市查询
     */
    private void hisCityInit(CityBean.DataBean bean) {
        List<String> list = SharedPreferencesUtils.getInstance().readHistory(this, "historyCity");
        int i = 0;
        if (list.size() > 0) {
            for (int posit = 0; posit < bean.getData_all().size(); posit++) {
                if (bean.getData_all().get(posit).getCityname().contains(list.get(i))) {
                    BaseCityBean cityMode = new BaseCityBean(bean.getData_all().get(posit).getCityid(), bean.getData_all().get(posit).getCityname(), bean.getData_all().get(posit).getCitycode());
                    city_history.add(cityMode);
                    posit = -1;
                    if (list.get(i).contains(list.get(list.size() - 1))) {
                        return;
                    }
                    ++i;
                }
            }
        }
    }

    /**
     * 所有数据
     *
     * @return 返回的数据
     */
    private ArrayList<CityBean.DataBean.DataAllBean> getAllCityList(ArrayList<CityBean.DataBean.DataAllBean> list) {
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * 搜索之后的数据
     *
     * @param strEditText 输入的内容
     */
    private void getResultCityList(String strEditText) {
        for (int i = 0; i < allCity_lists.size(); i++) {
            if (allCity_lists.get(i).getCitycode().contains(strEditText) || allCity_lists.get(i).getCityname().contains(strEditText)) {
                city_result.add(allCity_lists.get(i));
            }
        }
        Collections.sort(city_result, comparator);
    }

    /**
     * a-z排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<BaseCityBean>() {
        @Override
        public int compare(BaseCityBean lhs, BaseCityBean rhs) {
            String a = lhs.getCitycode().substring(0, 1);
            String b = rhs.getCitycode().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    private void setAdapter(List<BaseCityBean> list, List<BaseCityBean> hotList,
                            List<BaseCityBean> hisCity) {
        adapter = new ListAdapter(this, list, hotList, hisCity);
        mListView.setAdapter(adapter);
    }


    public class ListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<BaseCityBean> list;
        private List<BaseCityBean> hotList;
        private List<BaseCityBean> hisCity;
        final int VIEW_TYPE = 5;

        public ListAdapter(Context context, List<BaseCityBean> list,
                           List<BaseCityBean> hotList, List<BaseCityBean> hisCity) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            this.context = context;
            this.hotList = hotList;
            this.hisCity = hisCity;
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = getAlpha(list.get(i).getCitycode());
                // 上一个汉语拼音首字母，如果不存在为" "
                String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
                        .getCitycode()) : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = getAlpha(list.get(i).getCitycode());
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            return position < 4 ? position : 4;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final TextView city;
            int viewType = getItemViewType(position);
            if (viewType == 0) { // 定位
                convertView = inflater.inflate(R.layout.city_first_list, null);
                TextView locateHint = (TextView) convertView
                        .findViewById(R.id.locateHint);
                city = (TextView) convertView.findViewById(R.id.lng_city);
                ProgressBar pbLocate = (ProgressBar) convertView
                        .findViewById(R.id.pbLocate);
                city.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateProcess == 2) {
//							Toast.makeText(getApplicationContext(),
//                                    "选中城市:"+city.getText().toString(),
//                                    Toast.LENGTH_SHORT).show();
                        } else if (locateProcess == 3) {
                            locateProcess = 1;
                            mListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            isNeedFresh = true;
                            currentCity = "";
                        }
                    }
                });

                if (locateProcess == 1) { // 正在定位
                    locateHint.setText("正在定位");
                    city.setVisibility(View.GONE);
                    pbLocate.setVisibility(View.VISIBLE);
                } else if (locateProcess == 2) { // 定位成功
                    locateHint.setText("当前定位城市");
                    city.setVisibility(View.VISIBLE);
                    city.setText(currentCity);


                    city.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent intent = new Intent(getActivity(), ScenicSpotFragment.class);
//                            intent.putExtra("cityName", currentCity);
//                            startActivity(intent);
//                            mICallBackCityFragment.OnClickCityFragment(currentCity,currentCity);
//                            InsertCity(currentCity);

                        }
                    });
                    pbLocate.setVisibility(View.GONE);
                } else if (locateProcess == 3) {
                    locateHint.setText("未定位到城市,请选择");
                    city.setVisibility(View.VISIBLE);
                    city.setText("重新选择");
                    currentCity = city.getText().toString();
                    pbLocate.setVisibility(View.GONE);
                }

            } else if (viewType == 1) { // 最近访问城市
                convertView = inflater.inflate(R.layout.city_recent_city, null);
                GridView rencentCity = (GridView) convertView.findViewById(R.id.recent_city);
                HitCityAdapter hitCityAdapter = new HitCityAdapter(context, hisCity);
                rencentCity.setAdapter(hitCityAdapter);
                //接口回调
                hitCityAdapter.setICallBack(new HitCityAdapter.ICallBackHit() {
                    @Override
                    public void OnClickHot(int position) {
                        InsertCity(city_history.get(position).getCityname());
                        intent.putExtra("cityName", hisCity.get(position).getCityname());
                        intent.putExtra("cityId", hisCity.get(position).getCityid());
                        setResult(6, intent);
                        finish();

                    }
                });
                TextView recentHint = (TextView) convertView.findViewById(R.id.recentHint);
                recentHint.setText("最近访问的城市");
            } else if (viewType == 2) {
                convertView = inflater.inflate(R.layout.city_recent_city, null);
                GridView hotCity = (GridView) convertView
                        .findViewById(R.id.recent_city);
                if (viewType == 2) {
                    TextView textView = (TextView) convertView.findViewById(R.id.recentHint);
                    Drawable drawable = context.getResources().getDrawable(R.mipmap.city_hot_city);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    textView.setCompoundDrawables(drawable, null, null, null);
                }
                //适配器
                HotCityAdapter hotCityAdapter = new HotCityAdapter(context, this.hotList);
                hotCity.setAdapter(hotCityAdapter);
                //接口回调
                hotCityAdapter.setICallBackHot(new HotCityAdapter.ICallBackHot() {
                    @Override
                    public void OnClickHot(int position) {
                        InsertCity(city_hot.get(position).getCityname());
                        intent.putExtra("cityName", hotList.get(position).getCityname());
                        intent.putExtra("cityId", hotList.get(position).getCityid());
                        setResult(6, intent);
                        finish();
                    }
                });

                TextView hotHint = (TextView) convertView
                        .findViewById(R.id.recentHint);
                hotHint.setText("热门城市");

            } else if (viewType == 3) {
                convertView = inflater.inflate(R.layout.city_total_item, null);
            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (position >= 1) {

                    String name = list.get(position).getCityname();
                    if (name.contains("市")) {
                        String[] newCityName = name.split("市");
                        String allName = newCityName[0];
                        holder.name.setText(allName);
                    } else if (name.contains("特别行政区")) {
                        String[] newCityName = name.split("特");
                        String allName = newCityName[0];
                        holder.name.setText(allName);
                    } else if (name.contains("地区")) {
                        String[] newCityName = name.split("地");
                        String allName = newCityName[0];
                        holder.name.setText(allName);
                    } else {
                        holder.name.setText(name);
                    }
                    String currentStr = getAlpha(list.get(position).getCitycode());
                    String previewStr = (position - 1) >= 0 ? getAlpha(list
                            .get(position - 1).getCitycode()) : " ";
                    if (!previewStr.equals(currentStr)) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        holder.alpha.setText(currentStr);
                    } else {
                        holder.alpha.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }


    private class LetterListViewListener implements
            MyLetterView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                mListView.setSelection(position);
            }
        }
    }


    // 获得汉语拼音首字母
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else if (str.equals("0")) {
            return "定";
        } else if (str.equals("1")) {
            return "最";
        } else if (str.equals("2")) {
            return "热";
        } else if (str.equals("3")) {
            return "全";
        } else {
            return "#";
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                || scrollState == SCROLL_STATE_FLING) {
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!isScroll) {
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 456) {
            getLocation();
        }
    }
}