package yc.pointer.trip.untils;

import android.app.Activity;
import android.content.Intent;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.ActivityTrip;
import yc.pointer.trip.activity.BillActivity;
import yc.pointer.trip.activity.CouponActivity;
import yc.pointer.trip.activity.MyMoneyActivity;
import yc.pointer.trip.activity.MyOrderNewActivity;
import yc.pointer.trip.activity.MyReserveActivity;
import yc.pointer.trip.activity.NewOrderDetailActivity;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.activity.SystemMessageActivity;
import yc.pointer.trip.activity.VerifyActivity;
import yc.pointer.trip.view.DialogSure;

/**
 * Created by 张继
 * 2018/7/17
 * 16:29
 * 公司：
 * 描述：推送业务逻辑
 */

public class ReceiverIntentUtils {

    private static ReceiverIntentUtils mInstance = null;

    public static ReceiverIntentUtils getInstance() {
        if (mInstance == null) {
            synchronized (ReceiverIntentUtils.class) {
                if (mInstance == null) {
                    mInstance = new ReceiverIntentUtils();
                }
            }
        }
        return mInstance;
    }

    private ReceiverIntentUtils() {
    }

    public void ReceiverType(Activity activity, String mUserId, String uuid, String mOid, String mReceiverType) {
        if (mReceiverType.equals("0")) {//预约
            Intent intentMain = new Intent(activity, NewOrderDetailActivity.class);
            intentMain.putExtra("oid", mOid);
            intentMain.putExtra("flag", "appointment");
            intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intentMain);
        } else if (mReceiverType.equals("1")) {//被抢单成功之后进发单状态
            Intent intentOrder = new Intent(activity, NewOrderDetailActivity.class);
            intentOrder.putExtra("oid", mOid);
            intentOrder.putExtra("flag", "receiverBill");
            activity.startActivity(intentOrder);
        } else if (mReceiverType.equals("2")) {//发单人取消订单进抢单状态
            Intent intentOrder = new Intent(activity, NewOrderDetailActivity.class);
            intentOrder.putExtra("oid", mOid);
            intentOrder.putExtra("flag", "receiverGrabCancel");
            activity.startActivity(intentOrder);
        } else if (mReceiverType.equals("3")) {//接单人取消订单进发单状态
            Intent intentOrder = new Intent(activity, NewOrderDetailActivity.class);
            intentOrder.putExtra("oid", mOid);
            intentOrder.putExtra("flag", "receiverBillCancel");
            activity.startActivity(intentOrder);
        } else if (mReceiverType.equals("4")) {//续费跳进发单状态
            Intent intentMain = new Intent(activity, BillActivity.class);
            intentMain.putExtra("oid", mOid);
            activity.startActivity(intentMain);
        } else if (mReceiverType.equals("5")) {//同意给预约用户推送订单详情
            Intent intentMain = new Intent(activity, NewOrderDetailActivity.class);
            intentMain.putExtra("oid", mOid);
            intentMain.putExtra("flag", "agree");
            activity.startActivity(intentMain);
        } else if (mReceiverType.equals("6")) {//拒绝给预约用户推送 订单详情
            Intent intent = new Intent(activity, NewOrderDetailActivity.class);
            intent.putExtra("oid", mOid);
            intent.putExtra("flag", "no");
            activity.startActivity(intent);
        } else if (mReceiverType.equals("7") || mReceiverType.equals("8")) {
            Intent intent = new Intent(activity, NewOrderDetailActivity.class);
            intent.putExtra("oid", mOid);
            intent.putExtra("flag", "auto");
            activity.startActivity(intent);
        } else if (mReceiverType.equals("9")) {
            Intent intent = new Intent(activity, CouponActivity.class);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("10")) {//认证未通过
            //                                Toast.makeText(MainActivity.this, "您的身份证件照认证未通过", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, VerifyActivity.class);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("13")) {//导游证认证未通过
            //                                Toast.makeText(MainActivity.this, "您的导游证件照认证未通过", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, VerifyActivity.class);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("14")) {//参加活动
            Intent intent = new Intent(activity, ActivityTrip.class);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("15")) {//路书审核通过
            Intent intent = new Intent(activity, MyMoneyActivity.class);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("16")) {//游记审核未通过
            Intent intent = new Intent(activity, NewPersonalHomePageActivity.class);
            intent.putExtra("uid", mUserId);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("17")) {
            Intent intent = new Intent(activity, SystemMessageActivity.class);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("18")) {
            Intent intent = new Intent(activity, NewPersonalHomePageActivity.class);
            intent.putExtra("uid", uuid);
            activity.startActivity(intent);
        } else if (mReceiverType.equals("19")) {//发单方已付款，进入订单详情，查看状态进入抢单状态
            Intent intent = new Intent(activity, NewOrderDetailActivity.class);
            intent.putExtra("oid", mOid);
            intent.putExtra("flag", "Gograb");
            activity.startActivity(intent);
        }
    }

    public void pushMsg(final Activity activity, String mReceiverType, final String userId, final String uuid){
        if (mReceiverType.equals("5") || mReceiverType.equals("6")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intentMain = new Intent(activity, MyReserveActivity.class);
                        activity.startActivity(intentMain);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【预约订单】中查看订单信息").setPositiveButton("前往").setNegativeButton("取消").show();
        } else if (mReceiverType.equals("9")) {

            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, CouponActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【指针大礼包】中查看礼包信息").setPositiveButton("前往").setNegativeButton("取消").show();
        } else if (mReceiverType.equals("10") || mReceiverType.equals("11") || mReceiverType.equals("12") || mReceiverType.equals("13")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, VerifyActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【指针认证】中查看认证信息").setPositiveButton("前往").setNegativeButton("取消").show();
        } else if (mReceiverType.equals("14")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, ActivityTrip.class);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【疯狂旅游年】中参与活动").setPositiveButton("前往").setNegativeButton("取消").show();
        } else if (mReceiverType.equals("15")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, MyMoneyActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【我的收益】中查看收益奖金").setPositiveButton("前往").setNegativeButton("取消").show();
        } else if (mReceiverType.equals("16")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, NewPersonalHomePageActivity.class);
                        intent.putExtra("uid", userId);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【个人主页】中查看游记审核情况").setPositiveButton("前往").setNegativeButton("取消").show();

        } else if (mReceiverType.equals("17")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, SystemMessageActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【消息通知】中查看消息内容").setPositiveButton("前往").setNegativeButton("取消").show();

        } else if (mReceiverType.equals("18")) {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, NewPersonalHomePageActivity.class);
                        intent.putExtra("uid", uuid);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【用户主页】中查看游记内容").setPositiveButton("前往").setNegativeButton("取消").show();
        } else {
            new DialogSure(activity, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    if (trueEnable) {
                        Intent intent = new Intent(activity, MyOrderNewActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }).setTitle("温馨提示").setMsg("请前往【我的订单】中查看订单信息").setPositiveButton("前往").setNegativeButton("取消").show();
        }
    }
}

