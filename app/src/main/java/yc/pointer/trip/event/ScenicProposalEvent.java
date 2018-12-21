package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.SearchAdviceBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/27
 * 13:11
 */
public class ScenicProposalEvent extends BaseHttpEvent<SearchAdviceBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SearchAdviceBean>(){}.getType();
    }
}
