package yc.pointer.trip.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.ApkUpdateBean;

import java.util.List;

/**
 * Created by moyan on 2017/9/13.
 */
public class DialogMustUpteApk extends Dialog implements View.OnClickListener {
    private DialogApk.CallBackListener mCallBackListener;
    private Button mPositiveButton;//确定按钮
        private Button mNegativeButton;//取消按钮
    private ListView mListViewMsg;
    private TextView mTextTitle;
    private View line;


    private String mTitle;
    private String mSureMsg;
    private String mCancelMsg;

    private List<ApkUpdateBean.DataBeanX.DataBean> mListBean;

    public DialogMustUpteApk(Context context) {
        super(context);
    }

    public DialogMustUpteApk(Context context, int themeResId,  DialogApk.CallBackListener callBackListener) {
        super(context, themeResId);
        this.mCallBackListener = callBackListener;
    }

    public DialogMustUpteApk setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public DialogMustUpteApk setMsg(List<ApkUpdateBean.DataBeanX.DataBean> listBean) {
        this.mListBean = listBean;
        return this;
    }

    public DialogMustUpteApk setPositiveButton(String sureMsg) {
        this.mSureMsg = sureMsg;
        return this;
    }

    public DialogMustUpteApk setNegativeButton(String cancelMsg) {
        this.mCancelMsg = cancelMsg;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_apk);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView() {
        //mTextTitle = (TextView) findViewById(R.id.dialog_apk_title);
        //mTextTitle.setText(mTitle);
        mListViewMsg = (ListView) findViewById(R.id.dialog_apk_msg);
        mListViewMsg.setAdapter(baseAdapter);
        mPositiveButton = (Button) findViewById(R.id.dialog_apk_sure);
        mPositiveButton.setText(mSureMsg);
        mPositiveButton.setOnClickListener(this);
        mNegativeButton = (Button) findViewById(R.id.dialog_apk_cancel);
        mNegativeButton.setVisibility(View.GONE);
        line=findViewById(R.id.line);
        line.setVisibility(View.GONE);
//        mNegativeButton.setText(mCancelMsg);
//        mNegativeButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_apk_sure:
                if (mCallBackListener != null) {
                    mCallBackListener.onClickButton(true);
                }
                dismiss();
                break;
//            case R.id.dialog_apk_cancel:
//                if (mCallBackListener != null) {
//                    mCallBackListener.onClickButton(false);
//                }
//                dismiss();
//                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {



            System.exit(0);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mListBean.size() == 0 ? 0 : mListBean.size();
        }

        @Override
        public Object getItem(int i) {
            return mListBean.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item_apk, null);
                holder.textView = (TextView) view.findViewById(R.id.item_apk_text);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (mListBean.size() > 0) {
                holder.textView.setText(mListBean.get(i).getInfo());
            }

            return view;
        }

        class ViewHolder {
            TextView textView;
        }
    };

    public interface CallBackListener {
        void onClickButton(boolean enableStatus);
    }
}
