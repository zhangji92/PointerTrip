package yc.pointer.trip.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.GridViewAdapter;
import yc.pointer.trip.adapter.ListAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.HotLabelBean;
import yc.pointer.trip.bean.SearchAdviceBean;
import yc.pointer.trip.bean.SearchResultBean;
import yc.pointer.trip.event.HotLabelEvent;
import yc.pointer.trip.event.SearchAdviceEvent;
import yc.pointer.trip.event.SearchResultEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.*;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面
 */

public class SearchActivity extends BaseActivity {
    @BindView(R.id.clear_history_search)
    TextView clearHistory;//清除历史数据
    @BindView(R.id.edit_search)
    EditText editSearch;//搜索输入框
    @BindView(R.id.search_commint)
    ImageView mSearchCommit; //搜索按钮
    @BindView(R.id.hot_search_title)
    TextView hotSearchTitle;//热门搜索标题
    @BindView(R.id.gv_hot_search)
    GridView gvHotSearch;//热门搜索词汇列表
    @BindView(R.id.history_search_title)
    TextView historySearchTitle;//历史搜索标题
    @BindView(R.id.gv_history_search_search)
    GridView gvHistorySearchSearch;//历史搜索记录列表
    @BindView(R.id.layout_history)
    LinearLayout layoutHistory;
    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;//相关路书
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.refresh_linear)
    LinearLayout mLinear;//隐藏最外围的布局
    @BindView(R.id.search_retrieval)
    TextView search_retrieval;//未检测到结果
    @BindView(R.id.search_advice)
    ListView search_advice;//搜索建议


    private List<String> hotList = new ArrayList();//热门集合
    private List<SearchResultBean.DataBean> mListRelevant = new ArrayList();//热门集合
    private List<String> hisList = new ArrayList();


    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private String mUserId;//用户id
    private GridViewAdapter gridViewAdapter;
    private ListAdapter searchResultAdp;//搜索结果适配器
    private int paging = 0;
    private boolean flag;
    private boolean proposalFlag=true;//搜索建议标志 true走建议接口 false 不走建议接口

    private String keyword;
    private ArrayAdapter<String> adapterAdvice;
    private List<String> dataAdvice;
    private ArrayList<String> shareHistList;
    private GridViewAdapter hisSearchAdapter;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        if (StringUtil.isEmpty(mUserId)) {
            mUserId = "0";
        }
//从本地获取历史搜索记录
        getHistortData();

        historySearchTitle.setVisibility(View.VISIBLE);
        gvHotSearch.setVisibility(View.VISIBLE);
        mLinear.setVisibility(View.GONE);
        sendSearchLabel();//请求热门标签的方法
        gridViewAdapter = new GridViewAdapter(this, hotList);


        hisSearchAdapter = new GridViewAdapter(this, hisList);
        gvHistorySearchSearch.setAdapter(hisSearchAdapter);
        gvHistorySearchSearch.setVisibility(View.VISIBLE);

        keyword = editSearch.getText().toString().trim();
        judgeEdit(keyword);//判断exitText

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layoutHistory.setVisibility(View.GONE);
                hotSearchTitle.setVisibility(View.GONE);//热门搜索标题
                gvHistorySearchSearch.setVisibility(View.GONE);//历史搜索记录列表
                gvHotSearch.setVisibility(View.GONE);//热门搜索词汇列表
                mLinear.setVisibility(View.GONE);//隐藏最外围的布局
                search_retrieval.setVisibility(View.GONE);////未检测到结果
                search_advice.setVisibility(View.VISIBLE);//搜索建议
                if (!StringUtil.isEmpty(charSequence.toString())) {
                    if (proposalFlag){
                        sendSearchAdvice(charSequence.toString());
                    }else {
                        search_advice.setVisibility(View.GONE);
                        refreshRecycler.setVisibility(View.VISIBLE);
                        layoutHistory.setVisibility(View.GONE);
                        hotSearchTitle.setVisibility(View.GONE);
                        gvHistorySearchSearch.setVisibility(View.GONE);
                        gvHotSearch.setVisibility(View.GONE);
                        paging = 0;
                        flag = true;
                        proposalFlag=true;//是否走搜索建议接口
                        sendSearchResult(charSequence.toString(), paging);
                    }
                } else {
                    search_advice.setVisibility(View.GONE);
                    mLinear.setVisibility(View.GONE);
                    hotSearchTitle.setVisibility(View.VISIBLE);
                    hisList.clear();
                    getHistortData();
                    gridViewAdapter.notifyDataSetChanged();
                    hisSearchAdapter.notifyDataSetChanged();
                    gvHotSearch.setVisibility(View.VISIBLE);
                    layoutHistory.setVisibility(View.VISIBLE);
                    historySearchTitle.setVisibility(View.VISIBLE);
                    gvHistorySearchSearch.setVisibility(View.VISIBLE);
                    clearHistory.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                keyword = editable.toString();
                judgeEdit(keyword);//判断exitText
                mListRelevant.clear();
            }
        });
        //搜索建议点击事件
        search_advice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editSearch.setText(dataAdvice.get(i));
                paging = 0;
                flag = true;
                sendSearchResult(dataAdvice.get(i), paging);
            }
        });
        //搜索路书
        mSearchCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_advice.setVisibility(View.GONE);
                refreshRecycler.setVisibility(View.VISIBLE);
                layoutHistory.setVisibility(View.GONE);
                hotSearchTitle.setVisibility(View.GONE);
                gvHistorySearchSearch.setVisibility(View.GONE);
                gvHotSearch.setVisibility(View.GONE);
                paging = 0;
                flag = true;
                sendSearchResult(keyword, paging);
            }
        });
        //历史搜索的点击时间
        gvHistorySearchSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                proposalFlag=false;
                editSearch.setText(hisList.get(position));
            }
        });
        //热门搜索的点击事件
        gvHotSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                proposalFlag=false;
                editSearch.setText(hotList.get(i));

            }
        });
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.getInstance().remove(SearchActivity.this, "historyKey");
                hisList.clear();
                getHistortData();
                hisSearchAdapter.notifyDataSetChanged();
            }
        });
        refreshRecycler.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
        searchResultAdp = new ListAdapter(SearchActivity.this, mListRelevant);


        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                flag = false;
//                String keyword = editSearch.getText().toString();
                ++paging;
                sendSearchResult(keyword, paging);
                refreshSmart.finishRefresh();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);//设置之后，将不会再触发加载事件
                flag = true;
//                String keyword = editSearch.getText().toString();
                paging = 0;
                if (!StringUtil.isEmpty(keyword)) {
                    sendSearchResult(keyword, paging);
                }
                refreshSmart.finishRefresh();

            }
        });
    }

    /**
     * 从本地获取历史搜索记录
     */
    public void getHistortData() {
        shareHistList = SharedPreferencesUtils.getInstance().readHistory(this,"historyKey");//查询历史数据
        for (String str : shareHistList) {
            if (!str.equals("not find")) {
                hisList.add(str);//查询历史数据
            }
        }
    }

    /**
     * 判断EditText有没有内容
     *
     * @param editable
     */
    private void judgeEdit(String editable) {
        if (StringUtil.isEmpty(editable)) {
            mSearchCommit.setEnabled(false);
        } else {
            mSearchCommit.setEnabled(true);
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    /**
     * 请求热门标签
     */
    private void sendSearchLabel() {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(mDevcode))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", sign)
                    .add("uid", mUserId)
                    .build();

            OkHttpUtils.getInstance().post(URLUtils.SEARCH_HOT_LABEL, requestBody, new HttpCallBack(new HotLabelEvent()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchMsgBean(HotLabelEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }

        HotLabelBean bean = event.getData();
        if (bean.getStatus() == 0) {
            hotList.addAll(bean.getData());
            gvHotSearch.setAdapter(gridViewAdapter);
            gridViewAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 搜索结果
     *
     * @param keyWord 搜索关键字
     * @param paging  分页
     */
    private void sendSearchResult(String keyWord, int paging) {

        SharedPreferencesUtils.getInstance().saveHistory(SearchActivity.this,"historyKey",keyWord);//历史数据插入

        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "keyword=" + keyWord + "p=" + paging + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(mDevcode))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", sign)
                    .add("keyword", keyWord)
                    .add("p", String.valueOf(paging))
                    .add("uid", mUserId)
                    .build();

            OkHttpUtils.getInstance().post(URLUtils.SEARCH_VIDEO_RESULT, requestBody, new HttpCallBack(new SearchResultEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchResultBean(SearchResultEvent event) {
        if (event.isTimeOut()) {
            mSearchCommit.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SearchResultBean bean = event.getData();
        if (bean.getStatus() == 0) {
            mSearchCommit.setEnabled(true);
            if (flag) {
                if (bean.getD_status() == 1) {
                    mListRelevant.clear();
                    mListRelevant.addAll(bean.getData());
                    refreshRecycler.setAdapter(searchResultAdp);
                    searchResultAdp.notifyDataSetChanged();
                    mLinear.setVisibility(View.VISIBLE);
                    search_retrieval.setVisibility(View.VISIBLE);
                    search_advice.setVisibility(View.GONE);
                    searchResultAdp.setFlag(true);
                } else {
                    searchResultAdp.setFlag(false);
                    mListRelevant.clear();
                    mListRelevant.addAll(bean.getData());
                    refreshRecycler.setAdapter(searchResultAdp);
                    if (mListRelevant.size() == 0) {
                        search_retrieval.setVisibility(View.VISIBLE);
                        mLinear.setVisibility(View.GONE);
                        search_advice.setVisibility(View.GONE);
                    } else {
                        mLinear.setVisibility(View.VISIBLE);
                        search_retrieval.setVisibility(View.GONE);
                        search_advice.setVisibility(View.GONE);
                    }
                }
                searchResultAdp.notifyDataSetChanged();
                refreshSmart.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);//设置之后，将不会再触发加载事件
                }else {
                    //添加适配器
                    mListRelevant.addAll(bean.getData());
                    //添加适配器
                    searchResultAdp.notifyDataSetChanged();
                }
                refreshSmart.finishLoadmore();//停止加载
            }

        } else {
            mSearchCommit.setEnabled(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 搜索建议
     *
     * @param key 关键字
     */
    private void sendSearchAdvice(String key) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "keyword=" + key + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(mDevcode))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", sign)
                    .add("keyword", key)
                    .add("uid", mUserId)
                    .build();

            OkHttpUtils.getInstance().post(URLUtils.SEARCH_VIDEO_ADVICE, requestBody, new HttpCallBack(new SearchAdviceEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchAdviceBean(SearchAdviceEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SearchAdviceBean bean = event.getData();
        if (bean.getStatus() == 0) {
            dataAdvice = bean.getData();
            //搜索建议适配器
            adapterAdvice = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, dataAdvice);
            search_advice.setAdapter(adapterAdvice);
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

}
