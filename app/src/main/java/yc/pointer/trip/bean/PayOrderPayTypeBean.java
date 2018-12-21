package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Create by 张继
 * 2017/8/11
 * 15:41
 */
public class PayOrderPayTypeBean extends BaseBean {

    /**
     * orderString : alipay_sdk=alipay-sdk-php-20161101&amp;app_id=2017010304822574&amp;biz_content=%7B%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22subject%22%3A+%22App%E6%94%AF%E4%BB%98%E6%B5%8B%E8%AF%95%22%2C%22out_trade_no%22%3A+%2220170811135144087%22%2C%22timeout_express%22%3A+%2230m%22%2C%22total_amount%22%3A+%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&amp;charset=UTF-8&amp;format=json&amp;method=alipay.trade.app.pay&amp;notify_url=http%3A%2F%2F139.196.106.89%3A1001%2F%2FHome%2FIndex%2FaliNotify&amp;sign_type=RSA&amp;timestamp=2017-08-11+16%3A46%3A41&amp;version=1.0&amp;sign=lNmmvpylku8E7Zms47YMiTpC2PoE8qJW7RlowaT4xZmq6EZE2%2BZDWESrn3ZOSINnCzshVRQ8uwDzW%2BRytHF5ZxyeNZ02Qw83oKkcDaFZBuWMoFu4YeU%2BIDIzZXhs73bUHiBEc3G8X5X6LDDYoqqW5wVlagUb6Cm%2BVNaYFsw2Qn0%3D
     */

    private String orderString;

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }
}
