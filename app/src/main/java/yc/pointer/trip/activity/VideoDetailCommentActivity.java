package yc.pointer.trip.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.VideoDetailCommentAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.untils.PermissionHelper;

/**
 * Created by moyan on 2018/3/27.
 * 视频详情 （带评论功能）
 */

public class VideoDetailCommentActivity extends BaseActivity implements PermissionHelper.OnAlterApplyPermission {

    @BindView(R.id.comments_main_list)
    RecyclerView commentsMainList;//评论列表
    @BindView(R.id.edit_comments_bottom)
    TextView editCommentsBottom;//点击写评论（弹出软键盘）
    @BindView(R.id.share_video)
    ImageButton shareVideo;//分享
    @BindView(R.id.like_video)
    ImageButton likeVideo;//点赞
    @BindView(R.id.collection_video)
    ImageButton collectionVideo;//收藏
    @BindView(R.id.exit_video)
    ImageButton exitVideo;//返回
    @BindView(R.id.comments_bottom)
    LinearLayout commentsBottom;//底部正常展示的布局
    @BindView(R.id.edit_comments_up)
    EditText editCommentsUp;//评论内容输入框
    @BindView(R.id.comments_publish)
    Button commentsPublish;//发送按钮
    @BindView(R.id.comments_up)
    LinearLayout commentsUp;//输入时展示的布局


    private BookBean dataGoodBean;
    private String mBuid;
    private String mUserId;
    private String mBid;
    private boolean islogin;
    private String mDevcode;
    private long mTimestamp;
    private int collectionStatus;
    private String isOrder;//发游记人的
    private int zStatus;
    private String mShareUrl;
    private String shareTitle;
    private String shareContent;
    private Bitmap bitMBitmap;//封面
    private PermissionHelper mHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {new PermissionHelper.PermissionModel(1, Manifest.permission.READ_EXTERNAL_STORAGE, "存储"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "写出外部存储"),
    };
    private boolean isPermission = true;//权限申请标志，防止一直TOAST
    private UMWeb web;
    private String sharePic;
    //播放相关
    //OrientationUtils orientationUtils;
    private boolean isTransition;
    private Transition transition;
    private Bitmap bitmap;
    private String mIsOrder;//用户可不可以发单
//    private VideoDetailsActivity.MyBroadcastReciver myBroadcastReciver;

    private boolean isDeposited;


    @Override
    protected int getContentViewLayout() {
        return R.layout.video_detail_comments;
    }

    @Override
    protected void initView() {

        //视频信息
        dataGoodBean = (BookBean) getIntent().getSerializableExtra("dataGoodBean");
        isTransition = getIntent().getBooleanExtra("TRANSITION", false);
        mUserId = ((MyApplication) getApplication()).getUserId();
        mBuid = dataGoodBean.getUid();
        mBid = dataGoodBean.getBid();
        mDevcode = ((MyApplication) getApplication()).getDevcode();//设备识别码
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();//时间戳
        islogin = ((MyApplication) getApplication()).isIslogin();
        mHelper = new PermissionHelper(this, this, permissionModels);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentsMainList.setLayoutManager(layoutManager);

        commentsMainList.setAdapter(new VideoDetailCommentAdapter(this,dataGoodBean,isTransition));

        commentsBottom.setVisibility(View.VISIBLE);
        commentsUp.setVisibility(View.GONE);
        editCommentsBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentsBottom.setVisibility(View.GONE);
                commentsUp.setVisibility(View.VISIBLE);
                editCommentsUp.setFocusable(true);
                editCommentsUp.setFocusableInTouchMode(true);
                editCommentsUp.requestFocus();
               getSystemService(Context.INPUT_METHOD_SERVICE);

            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }



    @OnClick({R.id.share_video, R.id.like_video, R.id.collection_video, R.id.exit_video, R.id.comments_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_video:
                break;
            case R.id.like_video:
                break;
            case R.id.collection_video:
                break;
            case R.id.exit_video:
                break;
            case R.id.comments_publish:
                break;
        }
    }

    @Override
    public void OnAlterApplyPermission() {

    }

    @Override
    public void cancelListener() {

    }
}
