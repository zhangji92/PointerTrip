package yc.pointer.trip.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.EntityVideo;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.untils.UpLoaderLocalUtils;

/**
 * Created by moyan on 2018/3/19.
 * <p>
 * 本地视频资源列表
 */

public class UploadLoactionVideoAdapter extends RecyclerView.Adapter<UploadLoactionVideoAdapter.LocationVideoViewHolder> {


    private List<EntityVideo> list;
    private Context context;

    private onClickListener listener;
    private UpLoaderLocalUtils mUtils;

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public UploadLoactionVideoAdapter(Context context, List<EntityVideo> list) {
        this.list = list;
        this.context = context;
        this.mUtils = UpLoaderLocalUtils.getInstance();
    }

    @Override
    public LocationVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        LocationVideoViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.location_video_item, parent, false);
            holder = new LocationVideoViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (LocationVideoViewHolder) view.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(LocationVideoViewHolder holder, final int position) {

        if (list.size() > 0) {
            holder.videoThumbPath.setTag(list.get(position).getPath());
            //异步线程去加载图片，图片一张一张显示
            mUtils.showImageByAsyncTask(holder.videoThumbPath,
                    list.get(position).getPath(), position);
            int duration = list.get(position).getDuration();
            String s = StringUtil.timeFormat(duration);
            holder.textDuration.setText(s);
            holder.videoThumbPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : list.size();
    }


    public class LocationVideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_thumbPath)
        ImageView videoThumbPath;
        @BindView(R.id.text_duration)
        TextView textDuration;

        public LocationVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface onClickListener {
        void OnClick(int position);
    }

}
