package yc.pointer.trip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import butterknife.BindView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.ScenicAdapter;
import yc.pointer.trip.adapter.ScenicSearchAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.ScenicBean;
import yc.pointer.trip.bean.ScenicSearchBean;
import yc.pointer.trip.bean.SearchAdviceBean;
import yc.pointer.trip.event.ScenicEvent;
import yc.pointer.trip.event.ScenicProposalEvent;
import yc.pointer.trip.event.ScenicSearchEvent;
import yc.pointer.trip.event.SearchAdviceEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 刘佳伟
 * 2017/7/13
 * 17:35
 * 景点选择
 */
public class ScenicActivity extends BaseActivity {

    @BindView(R.id.edit_search)
    EditText editSearch;//输入框
    @BindView(R.id.search_commint)
    ImageView searchCommint;//搜索
    @BindView(R.id.scenic_commit)
    Button bnt_commit;//提交
    @BindView(R.id.no_search_result)
    TextView noSearchResult;//没找到内容的结果
    @BindView(R.id.refresh_linear)
    LinearLayout mLinearLayout;
    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;//RecyclerView正常数据
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;//刷新
    @BindView(R.id.scenic_advice_view)
    ListView mListView_advice;//搜索建议的视图
    @BindView(R.id.scenic_result_smart)
    SmartRefreshLayout mSmart_result;//搜索结果的SmartRefreshLayout
    @BindView(R.id.scenic_result_recycler)
    RecyclerView mRecyclerView_result;//搜索结果的RecyclerView


    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String mUserId;//用户id
    private String cityId;//城市的id
    private int page = 0;//页数
    private List<ScenicBean> mList;
    private List<ScenicBean.DataBean.DataHotBean> mListHot;//热门景点
    private List<ScenicBean.DataBean.DataOtherBean> mListOther;//全部数据
    private ScenicAdapter scenicAdapter;//适配器
    private boolean aBoolean = true;//true 刷新 fals加载
    private String typeId = "";
    private ArrayList<String> mListSelect;//选中的的数据
    private List<String> mListAdvice;//搜索建议数据
    private ArrayAdapter<String> adapterAdvice;//搜索建议适配器
    private String keyword;//搜索关键字
    private ScenicSearchAdapter mScenicSearchAdapter;//搜索结果适配器
    private List<ScenicSearchBean.DataBean> mListSearch;//搜所结果数据
    private LoadDialog mLoadDialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_scenic;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        new ToolbarWrapper(this);
        mList = new ArrayList<>();
        mListHot = new ArrayList<>();//热门数据
        mListOther = new ArrayList<>();//全部数据
        mListSelect = new ArrayList<>();//选中的数据
        mListSearch = new ArrayList<>();//搜索结果的
        //隐藏视图
        mSmart_result.setVisibility(View.GONE);
        mListView_advice.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);

        //城市的id
        cityId = getIntent().getStringExtra("cityId");
        mDevcode = ((MyApplication) getApplication()).getDevcode();//设备识别码
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();//时间戳
        mUserId = ((MyApplication) getApplication()).getUserId();//用户id
        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        sendScenic(page, "");//请求网络数据
        //适配器
        scenicAdapter = new ScenicAdapter(mList, mListHot, mListOther, this);
        scenicAdapter.setScenicCallBack(new ScenicAdapter.scenicCallBack() {
            @Override
            public void onClickType(String type) {
                typeId = type;
                aBoolean = true;
                page = 0;
                sendScenic(page, typeId);
            }

            @Override
            public void onClickSelectOther(int position, ImageView imageVIew) {
                mListOther.get(position).setSelect(!mListOther.get(position).isSelect());
                if (mListOther.get(position).isSelect()) {
                    imageVIew.setBackgroundResource(R.mipmap.selected_scenic);
                    mListSelect.add(mListOther.get(position).getTitle());
                } else {
                    imageVIew.setBackgroundResource(R.mipmap.unselect_scenic);
                    for (int i = 0; i < mListSelect.size(); i++) {
                        if (mListSelect.get(i).equals(mListOther.get(position).getTitle())) {
                            mListSelect.remove(i);
                        }
                    }
                }
            }

            @Override
            public void onClickSelectHot(int position, ImageView imageVIew) {
                mListHot.get(position).setSelect(!mListHot.get(position).isSelect());
                if (mListHot.get(position).isSelect()) {
                    imageVIew.setBackgroundResource(R.mipmap.selected_scenic);
                    mListSelect.add(mListHot.get(position).getTitle());
                } else {
                    imageVIew.setBackgroundResource(R.mipmap.unselect_scenic);
                    for (int i = 0; i < mListSelect.size(); i++) {
                        if (mListSelect.get(i).equals(mListHot.get(position).getTitle())) {
                            mListSelect.remove(i);
                        }
                    }
                }
            }
        });
        //上拉加载 下拉刷新
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                aBoolean = false;
                ++page;
                sendScenic(page, typeId);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);//设置之后，将不会再触发加载事件
                aBoolean = true;
                page = 0;
                sendScenic(page, typeId);
            }
        });
        //搜索景点
        searchScenic();
    }


    //搜索景点
    public void searchScenic() {

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() == null || (s.toString()).equals("")) {//无搜索请求
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mListView_advice.setVisibility(View.GONE);
                    mSmart_result.setVisibility(View.GONE);
                    aBoolean = true;
                    page = 0;
                    sendScenic(page, typeId);
                } else {//有搜索请求
                    mLinearLayout.setVisibility(View.GONE);
                    mListView_advice.setVisibility(View.VISIBLE);
                    mSmart_result.setVisibility(View.GONE);
                    scenicAdvice(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                judgeEdit(keyword);
            }
        });
        //搜索建议
        mListView_advice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editSearch.setText(mListAdvice.get(i));
                page = 0;
                scenicSearch(page, mListAdvice.get(i));

            }
        });
        mRecyclerView_result.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mScenicSearchAdapter = new ScenicSearchAdapter(this, mListSearch);
        mRecyclerView_result.setAdapter(mScenicSearchAdapter);
        //选中事件
        mScenicSearchAdapter.setSearchCallBack(new ScenicSearchAdapter.searchCallBack() {
            @Override
            public void onClickSearch(int position, ImageView imageView) {
                mListSearch.get(position).setSelect(!mListSearch.get(position).isSelect());
                if (mListSearch.get(position).isSelect()) {
                    imageView.setBackgroundResource(R.mipmap.selected_scenic);
                    mListSelect.add(mListSearch.get(position).getTitle());
                } else {
                    imageView.setBackgroundResource(R.mipmap.unselect_scenic);
                    for (int i = 0; i < mListSelect.size(); i++) {
                        if (mListSelect.get(i).equals(mListOther.get(position).getTitle())) {
                            mListSelect.remove(i);
                        }
                    }
                }
            }
        });
        mSmart_result.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                ++page;
                aBoolean = false;
                scenicSearch(page, keyword);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mSmart_result.setLoadmoreFinished(false);
                page = 0;
                aBoolean = true;
                scenicSearch(page, keyword);
            }
        });

        searchCommint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = editSearch.getText().toString();
                judgeEdit(keyword);//判断exitText
                page = 0;
                scenicSearch(page, keyword);
            }
        });

    }

    @OnClick({R.id.scenic_commit})
    public void onClickVIew(View view) {
        switch (view.getId()) {
            case R.id.scenic_commit:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("scenicNameList", mListSelect);
                setResult(7, intent);
                finish();
                break;
            default:
                break;
        }

    }


    /**
     * 判断EditText有没有内容
     *
     * @param editable
     */
    private void judgeEdit(String editable) {
        if (StringUtil.isEmpty(editable)) {
            searchCommint.setEnabled(false);
        } else {
            searchCommint.setEnabled(true);
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 景点界面的数据
     *
     * @param page   分页
     * @param typeId 类型
     */
    private void sendScenic(int page, String typeId) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("cityid=" + cityId + "devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "typeid=" + typeId +
                    "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("cityid", cityId)
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("typeid", typeId)
                    .add("uid", String.valueOf(mUserId))
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.SCENIC, requestBody, new HttpCallBack(new ScenicEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scenicBean(ScenicEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            searchCommint.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ScenicBean bean = event.getData();
        if (bean.getStatus() == 0) {
            searchCommint.setEnabled(true);
            if (aBoolean) {
                mListSelect.clear();//刷新清空选中数据
                mList.clear();
                mListHot.clear();
                mListOther.clear();
                if (bean.getData().getData_other().size() > 0) {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    noSearchResult.setVisibility(View.GONE);
                    mList.add(bean);
                    mListHot.addAll(bean.getData().getData_hot());
                    mListOther.addAll(bean.getData().getData_other());
                    refreshRecycler.setAdapter(scenicAdapter);
                    scenicAdapter.notifyDataSetChanged();
                    refreshSmart.finishRefresh();
                } else {
                    refreshRecycler.setVisibility(View.GONE);
                    noSearchResult.setVisibility(View.VISIBLE);
                }
            } else {
                if (bean.getData().getData_other().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mListOther.addAll(bean.getData().getData_other());
                    scenicAdapter.notifyDataSetChanged();
                }
                refreshSmart.finishLoadmore();
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            searchCommint.setEnabled(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 建议
     *
     * @param keyWord
     */
    private void scenicAdvice(String keyWord) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("cityid=" + cityId + "devcode=" + mDevcode + "keyword=" + keyWord + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("cityid", cityId)
                    .add("devcode", mDevcode)
                    .add("keyword", keyWord)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.SCENIC_ADVICE, requestBody, new HttpCallBack(new ScenicProposalEvent()));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scenicAdviceBean(ScenicProposalEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SearchAdviceBean bean = event.getData();
        if (bean.getStatus() == 0) {

            mListAdvice = bean.getData();
            adapterAdvice = new ArrayAdapter<String>(ScenicActivity.this, android.R.layout.simple_list_item_1, mListAdvice);
            mListView_advice.setAdapter(adapterAdvice);//搜索建议的结果
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 模糊查询
     *
     * @param page    分页
     * @param keyword 搜索关键字
     */
    private void scenicSearch(int page, String keyword) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("cityid=" + cityId + "devcode=" + mDevcode + "keyword=" + keyword + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("cityid", cityId)
                    .add("devcode", mDevcode)
                    .add("keyword", keyword)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.SCENIC_SEARCH, requestBody, new HttpCallBack(new ScenicSearchEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sceincSearchBean(ScenicSearchEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ScenicSearchBean bean = event.getData();

        if (bean.getStatus() == 0) {
            if (aBoolean) {
                if (bean.getD_status() == 0) {
                    mListSearch.clear();//清空搜索结果的数据
                    mListSelect.clear();//刷新清空选中数据
                    mScenicSearchAdapter.setSearchErrorResult(true);
                    mLinearLayout.setVisibility(View.GONE);
                    mListView_advice.setVisibility(View.GONE);
                    mSmart_result.setVisibility(View.VISIBLE);
                    noSearchResult.setVisibility(View.GONE);
                    mListSearch.addAll(bean.getData());
                    mRecyclerView_result.setAdapter(mScenicSearchAdapter);
                } else {

                    mListSearch.clear();
                    mScenicSearchAdapter.setSearchErrorResult(false);
                    mLinearLayout.setVisibility(View.GONE);
                    mListView_advice.setVisibility(View.GONE);
                    mSmart_result.setVisibility(View.VISIBLE);
                    noSearchResult.setVisibility(View.VISIBLE);
                    mListSearch.addAll(bean.getData());
                    mRecyclerView_result.setAdapter(mScenicSearchAdapter);
                }
                mScenicSearchAdapter.notifyDataSetChanged();
                mSmart_result.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
                    mSmart_result.setLoadmoreFinished(true);
                } else {
                    mListSearch.addAll(bean.getData());
                    mScenicSearchAdapter.notifyDataSetChanged();
                }
                mSmart_result.finishLoadmore();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }
}
