package yc.pointer.trip.network;

/**
 * Created by Intellij IDEA
 * User: 93836
 * DATA: 2017/6/22
 * TIME: 14:24
 * 网络连接地址
 */
public class URLUtils {


    public final static String WK_APP_KEY = "5965d74fa6098";//签名的key
    public final static String WK_PWD_KEY = "59687a9192ddd";//密码的key
    //public final static String BASE_URL="http://139.196.106.89:1001";//url头
    public final static String BASE_URL = "https://www.zhizhentrip.com";//url头

    public final static String REGISTER = BASE_URL + "/Home/index/register";//注册
    public final static String BINDING_PHONE = BASE_URL + "/Home/Book/bdPhone";//绑定手机号
    public final static String CHANGE_BINDING_PHONE = BASE_URL + "/Home/Book/pwd";//更改绑定手机号(验证密码)
    public final static String GET_CASH = BASE_URL + "/Home/Book/new_cash";//红包提现
    public final static String CASH = BASE_URL + "/Home/Book/cashMoney";//发起提现
    public final static String CHOOSE_CASH = BASE_URL + "/Home/Book/chooseCash";//选择提现类型
    public final static String CHANGE_BINDING_PHONE_CODE = BASE_URL + "/Home/Book/upPhone";//更改绑定手机号(验证新手机)
    public final static String PHONE_CODE = BASE_URL + "/Home/index/yzm";//验证码
    public final static String LOGIN = BASE_URL + "/Home/index/login";//登陆
    public final static String THIRD_LOGIN = BASE_URL + "/Home/Book/login";//第三方登陆
    public final static String RETRIEVE = BASE_URL + "/Home/index/forgetPwd";//重置密码
    public final static String WRITE_BOOK = BASE_URL + "/Home/index/addbook";//新建路书
    public final static String READ_BOOK = BASE_URL + "/Home/index/bookList";//看游记
    public final static String READ_VIDEO_BOOK = BASE_URL + "/Home/Book/bookList";//视频版看游记
    public final static String HOME_DATA = BASE_URL + "/Home/index/index";//首页
    public final static String HOME_VIDEO_DATA = BASE_URL + "/Home/Book/index";//视频类首页
    public final static String HOME_VIDEO_NEW_DATA = BASE_URL + "/Home/Work/newIndex";//视频类首页(0306版)
    //    public final static String BOOK_DETAILS=BASE_URL+"/Home/Index/bookDetails?bid=";//路书详情
    public final static String BOOK_DETAILS = BASE_URL + "/Home/index/book";//路书详情
    public final static String BOOK_COLLECTION = BASE_URL + "/Home/index/collect";//路书收藏
    public final static String BOOK_FABULOUS = BASE_URL + "/Home/index/zan";//点赞
    public final static String SEARCH_HOT_LABEL = BASE_URL + "/Home/index/hot";//搜索界面的热门标签的url
    public final static String SEARCH_RESULT = BASE_URL + "/Home/index/searchBook";//搜索界面结果url
    public final static String SEARCH_VIDEO_RESULT = BASE_URL + "/Home/Book/searchBook";//搜索界面结果url
    public final static String SEARCH_ADVICE = BASE_URL + "/Home/index/searchIdea";//搜索建议url
    public final static String SEARCH_VIDEO_ADVICE = BASE_URL + "/Home/Book/searchIdea";//搜索建议url
    public final static String CITY = BASE_URL + "/Home/index/city";//城市界面的url
    public final static String MY_COLLECTION = BASE_URL + "/Home/index/myCollect";//我的收藏
    public final static String MY_VIDEO_COLLECTION = BASE_URL + "/Home/Book/myCollect";//我的收藏(视频版)
    public final static String BOOK_TYPE = BASE_URL + "/Home/index/bookType";//路书类型
    public final static String SCENIC = BASE_URL + "/Home/index/spotList";//景点页面
    public final static String SCENIC_ADVICE = BASE_URL + "/Home/index/ideaSpot";//景点建议
    public final static String SCENIC_SEARCH = BASE_URL + "/Home/index/searchSpot";//景点搜索接口
    public final static String SCENIC_DETAILS = BASE_URL + "/Home/Index/spotDetails?sid=";//景点详情接口
    public final static String MY_ROAD_BOOK = BASE_URL + "/Home/index/myBook";//我的路书已发表
    public final static String MY_VIDEO_ROAD_BOOK = BASE_URL + "/Home/Book/myBook";//我的路书已发表(视频版)
    public final static String MY_ROAD_BOOK_DRAFT = BASE_URL + "/Home/index/myCg";//我的路书草稿
    public final static String BOOK_DRAFT_MSG = BASE_URL + "/Home/index/cg";//草稿信息
    public final static String MY_PERSON_SETTING = BASE_URL + "/Home/index/my";//个人设置界面
    public final static String DELETE_BOOK = BASE_URL + "/Home/index/delBook";//删除路书、草稿
    public final static String SAVE_PERSION_MESG = BASE_URL + "/Home/index/myEdit";//修改保存个人信息接口
    public final static String GET_PERSON_MESG = BASE_URL + "/Home/index/my";//个人信息
    public final static String COMMIT_ORDER = BASE_URL + "/Home/index/addOrder";//订单预览界面提交订单
    public final static String COMMIT_ORDER_NEW = BASE_URL + "/Home/Order/addOrder";//2.0.2版本订单预览界面提交订单
    public final static String PAY_ORDER = BASE_URL + "/Home/index/order";//支付订单页面数据
    public final static String PAY_SAVE = BASE_URL + "/Home/index/saveOrder";//订单预览付款
    public final static String GET_PRICEMSG = BASE_URL + "/Home/index/start";//出发页面获取价格信息
    public final static String UNPAID_ORDER_LIST = BASE_URL + "/Home/index/myOrder";//未支付订单列表
    public final static String NEW_ORDER_LIST = BASE_URL + "/Home/Order/myOrder";//2.0.2版本我的订单列表
    public final static String UNPAID_DELETE_ORDER = BASE_URL + "/Home/index/delOrder";//未支付订单列表中删除订单
    public final static String ORDER_DETAIL = BASE_URL + "/Home/index/orderDetails";//订单详情
    public final static String READED_RULES = BASE_URL + "/Home/index/saveMz";//阅读发单须知的状态
    public final static String READED_VERIFY_RULES = BASE_URL + "/Home/Work/saveSm";//阅读实名认证服务协议的状态
    public final static String READED_DEPOSIT_RULES = BASE_URL + "/Home/Work/saveYj";//阅读押金服务协议的状态
    public final static String MAKE_MONEY = BASE_URL + "/Home/index/moneyList";//赚一赚
    public final static String NEW_MAKE_MONEY = BASE_URL + "/Home/Order/moneyList";//2.0.2版本赚一赚
    public final static String MAKE_MONEY_GRAD = BASE_URL + "/Home/index/makeMoney";//赚一赚抢单
    public final static String NEW_MAKE_MONEY_GRAD = BASE_URL + "/Home/Order/makeMoney";//2.0.2赚一赚抢单
    public final static String CANCEL_ORDER = BASE_URL + "/Home/index/cancelOrder";//取消订单
    public final static String BILL_START_ORDER = BASE_URL + "/Home/index/startOrder";//开始订单
    public final static String NEW_BILL_START_ORDER = BASE_URL + "/Home/Order/startOrder";//2.0.2版本开始订单
    public final static String BILL_END_ORDER = BASE_URL + "/Home/index/endOrder";//结束订单z
    public final static String BILL_COMMENT_MSG = BASE_URL + "/Home/index/comment";//获取评价信息
    public final static String BILL_COMMIT_COMMENT_MSG = BASE_URL + "/Home/index/addComment";//提交评价信息
    public final static String WX_PAY = BASE_URL + "/Home/Wxpay/index";//订单预览微信支付
    public final static String MY_TRAVEL = BASE_URL + "/Home/index/Mytrip";//我对旅行
    public final static String RECOMPOSE_PASSWORD = BASE_URL + "/Home/index/savePwd";//修改密码
    public final static String COMMIT_FEEDBACK = BASE_URL + "/Home/index/addHelp";//提交反馈
    public final static String BOOK_RESERVE = BASE_URL + "/Home/index/bookStart";//预约路书的信息
    public final static String GET_CONTINUE_PLAY = BASE_URL + "/Home/index/jxPlay";//继续玩
    public final static String SEND_CONTINUE_PLAY = BASE_URL + "/Home/Jxplay/addOrder";//提交继续玩的订单给服务器
    public final static String CONTINUE_PLAY_PAY = BASE_URL + "/Home/Jxplay/saveOrder";//续费支付宝支付接口
    public final static String CONTINUE_PLAY_WECHAT_PAY = BASE_URL + "/Home/Jxplay/saveOrder";//续费微信支付接口
    public final static String MY_RESERVE = BASE_URL + "/Home/Index/myYuyue";//预约订单
    public final static String GET_COMPLAIN = BASE_URL + "/Home/Index/comOrder";//获取投诉标签
    public final static String APPOINTMENT_URL = BASE_URL + "/Home/Index/argeeOrder";//预约同意 拒绝接口 appointment
    public final static String APK_UPDATE = BASE_URL + "/Home/Version/getVersion";//apk更新接口
    public final static String GET_COUPON = BASE_URL + "/Home/index/myDis";//获取优惠券接口
    public final static String USE_COUPON = BASE_URL + "//Home/index/useDis";//使用优惠券接口
    public final static String COMMINT_COMPLAIN = BASE_URL + "/Home/index/addCom";//提交投诉信息
    public final static String GET_ADVERT_PIC = BASE_URL + "/Home/Index/ad";//获取广告
    public final static String GET_USER_HEADIMG = BASE_URL + "/Home/Index/getPic";//获取用户头像
    public final static String ACTION_ALIPAY = BASE_URL + "/Home/Ad/addOrder";//活动页支付宝支付
    public final static String ACTION_WEIXINPAY = BASE_URL + "/Home/Wxpay/adIndex";//活动页微信支付
    public final static String VIDEO = BASE_URL + "/Home/Index/addVideoBook";//上传视频
    public final static String ACTIVITY_TRIP = BASE_URL + "/Home/Book/ad";//活动界面接口
    public final static String PERSONAL_PAGE = BASE_URL + "/Home/Book/my";//个人主页
    public final static String PERSONAL_FOLLOW = BASE_URL + "/Home/Book/att";//关注
    public final static String PERSONAL_FOLLOW_LIST = BASE_URL + "/Home/Book/attList";//关注列表
    public final static String DEPOSIT_AILPAY = BASE_URL + "/Home/Book/addOrder";//押金支付宝支付
    public final static String DEPOSIT_WXPAY = BASE_URL + "/Home/Wxpay/yjIndex";//押金微信支付
    public final static String BACK_DEPOSIT_MONEY = BASE_URL + "/Home/Book/yjRefund";//退押金接口
    public final static String PUBLISH_COMMENTS = BASE_URL + "/Home/Work/comment";//发表评论
    public final static String COMMENTS_LIST = BASE_URL + "/Home/Work/commentList";//评论列表
    public final static String DELETE_COMMENTS = BASE_URL + "/Home/Work/delComment";//删除评论
    public final static String MESSAGE_LIST_ACTIVITY = BASE_URL + "/Home/Work/myCommentList";//个人消息评论列表
    public final static String TRAVLE_SHARE_SUCCESS = BASE_URL + "/Home/Work/incBook";//转发数
    public final static String REPORT_INTERFACE = BASE_URL + "/Home/Work/addReport";//举报接口
    public final static String INCOME_DETAILS = BASE_URL + "/Home/Work/earnings";//收益明细列表
    public final static String HOME_INVITATION_CODE = BASE_URL + "/Home/Work/invitationCode";//首页邀请码
    public final static String SYSTEM_MESSAGE = BASE_URL + "/Home/Work/newsList";//系统通知
    public final static String PRAISE_MESSAGE = BASE_URL + "/Home/Work/zanList";//点赞通知
    public final static String UPLOAD_HEAD_BACK = BASE_URL + "/Home/Work/upFengImg";//上传封面背景图
    public final static String NEW_GO_TRAVEL = BASE_URL + "/Home/Order/startCity";//2018.7.3版出发吧
    public final static String WRATE_VIDEO_NEW = BASE_URL + "/Home/Index/addVideoBookNew";//上传视频新接口
    public final static String NEW_MAKE_MONEY_DATE = BASE_URL + "/Home/Task/task";//新版赚一赚（0919版）
    public final static String TASK_DETAILS = BASE_URL + "/Home/Task/details";//任务详情界面
    public final static String ALL_TASK_DETAILS = BASE_URL + "/Home/Task/all_task";//总任务界面
    public final static String GET_TASK_MONEY = BASE_URL + "/Home/Task/takeTask";//总任务界面全部领取

}
