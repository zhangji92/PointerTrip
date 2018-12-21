package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import yc.pointer.trip.base.BaseViewHolder;

/**
 * Created by moyan on 2018/4/4.
 */

public class HomeVideoNewFragmentAdapter extends RecyclerView.Adapter<HomeVideoNewFragmentAdapter.VideoViewHolder> {


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return null;

    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {

        return 0;

    }

    public static class VideoViewHolder extends BaseViewHolder{
        public VideoViewHolder(View itemView, Context context) {
            super(itemView, context);
        }
    }
}
