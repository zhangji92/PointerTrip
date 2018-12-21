package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
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
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.MessageListBean;
import yc.pointer.trip.bean.PrasieMessageBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by moyan on 2018/6/14.
 */

public class PraiseMessageListViewAdapter extends BaseAdapter {

    private LayoutInflater inflate;
    private List<PrasieMessageBean.DataBean> mList;
    private Context mContent;

    public PraiseMessageListViewAdapter(List<PrasieMessageBean.DataBean> mList, Context mContent) {

        this.mList = mList;
        this.mContent = mContent;
        this.inflate=LayoutInflater.from(mContent);
    }


    @Override
    public int getCount() {
        return mList.size()==0?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflate.inflate(R.layout.adapter_msg_list, null);
        CustomCircleImage mIvHeader = (CustomCircleImage) convertView.findViewById(R.id.msg_adapter_header);
        ImageView verifyResult = (ImageView) convertView.findViewById(R.id.verify_result);//VIp标识
        ImageView mIvImg = (ImageView) convertView.findViewById(R.id.msg_adapter_img);
        TextView mTvNick = (TextView) convertView.findViewById(R.id.msg_adapter_nick);
        TextView mTvContext = (TextView) convertView.findViewById(R.id.msg_adapter_content);
        TextView mTvTime = (TextView) convertView.findViewById(R.id.msg_adapter_time);
        LinearLayout mLinerItem = (LinearLayout) convertView.findViewById(R.id.liner_item);
        final LinearLayout mllLayout = (LinearLayout) convertView.findViewById(R.id.msg_adapter_linear);
        mllLayout.setTag(position);
        //头像
        String z_u_pic = mList.get(position).getZ_u_pic();
        String is_vip = mList.get(position).getIs_vip();

        OkHttpUtils.displayGlideCircular(mContent, mIvHeader,z_u_pic,verifyResult,is_vip);
        //视频截图
        OkHttpUtils.displayGlide(mContent, mIvImg, mList.get(position).getB_pic());
        mTvNick.setText(mList.get(position).getZ_nickname());//发表评论人的昵称
        mTvContext.setText(mList.get(position).getZ_info());

        String strTimeDate = StringUtil.getStrTimeTomm(mList.get(position).getAddtime());
        Date date = StringUtil.getStrTimeDate(strTimeDate);

//        String is_jie = mList.get(position).getIs_jie();
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
        mIvImg.setOnClickListener(new View.OnClickListener() {//跳转游记详情
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContent, VideoDetailsActivity.class);
                intent.putExtra("dataGoodBean", mList.get(position));
                mContent.startActivity(intent);
            }
        });
        mLinerItem.setOnClickListener(new View.OnClickListener() {//跳转游记详情
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContent, VideoDetailsActivity.class);
                intent.putExtra("dataGoodBean", mList.get(position));
                mContent.startActivity(intent);
            }
        });

        mIvHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, NewPersonalHomePageActivity.class);
                intent.putExtra("uid", mList.get(position).getUid());
                mContent.startActivity(intent);
            }
        });

        return convertView;
    }
}
