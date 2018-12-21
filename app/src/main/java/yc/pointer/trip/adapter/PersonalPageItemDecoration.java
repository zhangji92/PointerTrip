package yc.pointer.trip.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 张继
 * 2018/1/3
 * 18:46
 */

public class PersonalPageItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean spaceFlag = true;

    public PersonalPageItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = layoutParams.getSpanIndex();

        if (spanIndex == 0) {
            outRect.left = 0;
            outRect.bottom = space;
        } else {
            outRect.left = space;
            outRect.bottom = space;
        }
    }
}
