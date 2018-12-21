package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Date;
import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.bean.MessageListBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by 张继
 * 2018/5/9
 * 14:52
 * 公司：
 * 描述：
 */

public class MessageListViewAdapter extends BaseAdapter {

    private Context mContent;
    private LayoutInflater inflate;
    private List<MessageListBean.DataBean> mList;
    private MsgCallBack mMsgCallBack;

    public MessageListViewAdapter(Context context, List<MessageListBean.DataBean> list, MsgCallBack msgCallBack) {
        this.mMsgCallBack = msgCallBack;
        this.mContent = context;
        this.inflate = LayoutInflater.from(context);
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        convertView = inflate.inflate(R.layout.adapter_msg_list, null);
        CustomCircleImage mIvHeader = (CustomCircleImage) convertView.findViewById(R.id.msg_adapter_header);
        ImageView verifyResult = (ImageView) convertView.findViewById(R.id.verify_result);//VIp标识
        ImageView mIvImg = (ImageView) convertView.findViewById(R.id.msg_adapter_img);
        TextView mTvNick = (TextView) convertView.findViewById(R.id.msg_adapter_nick);
        TextView mTvContext = (TextView) convertView.findViewById(R.id.msg_adapter_content);
        TextView mDeleteTvContent = (TextView) convertView.findViewById(R.id.msg_adapter_delete_content);//已删除的评论消息
        TextView mTvTime = (TextView) convertView.findViewById(R.id.msg_adapter_time);
        final LinearLayout mllLayout = (LinearLayout) convertView.findViewById(R.id.msg_adapter_linear);
        mllLayout.setTag(i);
        //头像
        String c_u_pic = mList.get(i).getC_u_pic();
        String is_vip = mList.get(i).getIs_vip();
        OkHttpUtils.displayGlideCircular(mContent, mIvHeader, c_u_pic, verifyResult, is_vip);

        //视频截图
        String b_pic = mList.get(i).getB_pic();
        if (!StringUtil.isEmpty(b_pic)) {
            OkHttpUtils.displayGlide(mContent, mIvImg, b_pic);
        } else {
            OkHttpUtils.displayGlide(mContent, mIvImg, "");
        }
        mTvNick.setText(mList.get(i).getC_nickname());//发表评论人的昵称
        mTvContext.setText(mList.get(i).getC_info());
        String is_del = mList.get(i).getIs_del();
        if (!StringUtil.isEmpty(is_del)) {
            if (is_del.equals("1")) {//评论已删除
                mTvContext.setVisibility(View.GONE);
                mDeleteTvContent.setVisibility(View.VISIBLE);
            } else {
                mTvContext.setVisibility(View.VISIBLE);
                mDeleteTvContent.setVisibility(View.GONE);
            }
        } else {
            mTvContext.setVisibility(View.VISIBLE);
            mDeleteTvContent.setVisibility(View.GONE);
        }

        String strTimeDate = StringUtil.getStrTimeTomm(mList.get(i).getAddtime());
        Date date = StringUtil.getStrTimeDate(strTimeDate);
//        String is_jie = mList.get(i).getIs_jie();
//        if (!StringUtil.isEmpty(is_jie)){
//            if (is_jie.equals("2")){
//                verifyResult.setVisibility(View.VISIBLE);
//            }else {
//                verifyResult.setVisibility(View.GONE);
//            }
//        }else {
//            verifyResult.setVisibility(View.GONE);
//        }
        if (strTimeDate != null) {
            String format = StringUtil.format(date);
            mTvTime.setText(format);
        }

        mIvImg.setOnClickListener(new View.OnClickListener() {//跳转路数详情
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContent, VideoDetailsActivity.class);
                intent.putExtra("dataGoodBean", mList.get(i));
                mContent.startActivity(intent);
            }
        });

        mIvHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, NewPersonalHomePageActivity.class);
                intent.putExtra("uid", mList.get(i).getUid());
                mContent.startActivity(intent);
            }
        });

        mllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMsgCallBack != null) {
                    String c_nickname = mList.get(i).getC_nickname();
                    String cid = mList.get(i).getCid();
                    String bid = mList.get(i).getBid();
                    mMsgCallBack.onClickComment(i, c_nickname, cid, bid, mllLayout);
                }
            }
        });//点击回复
        return convertView;
    }


    public interface MsgCallBack {
        void onClickComment(int position, String name, String cid, String bid, LinearLayout linearLayout);
    }
}
