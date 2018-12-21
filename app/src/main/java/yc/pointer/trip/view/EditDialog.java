package yc.pointer.trip.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/4/10
 * 13:59
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 分享的dialog
 */

public class EditDialog extends Dialog implements View.OnClickListener {

    private Button mBntCommit;
    private EditText mEditText;
    private EditDialogCallBack mEditDialogCallBack;
    private Activity activity;

    public EditDialog(Activity activity, EditDialogCallBack editDialogCallBack) {
        this(activity, R.style.user_default_dialog);
        this.mEditDialogCallBack=editDialogCallBack;
        this.activity =activity;
    }

    private EditDialog(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
        initData();
    }

    private void initView() {
        Window window = this.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setContentView(R.layout.edit_dialog);
        mBntCommit = (Button) findViewById(R.id.msg_activity_commit);
        mEditText = (EditText) findViewById(R.id.msg_activity_edit);
    }

    private void initData() {
        mBntCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String str = mEditText.getText().toString();
        if (v.getId() == R.id.msg_activity_commit) {
            this.dismiss();
            if (mEditDialogCallBack!=null){
                if (!StringUtil.isEmpty(str)) {
                    mEditDialogCallBack.commitComment(str);
                } else {
                    Toast.makeText(MyApplication.mApp, "评论信息不能为空哦", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    public interface EditDialogCallBack{
        void commitComment(String meg);
    }
}
