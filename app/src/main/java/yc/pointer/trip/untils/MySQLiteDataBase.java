package yc.pointer.trip.untils;

import java.util.ArrayList;
import java.util.List;

import yc.pointer.trip.bean.CommentBean;
import yc.pointer.trip.bean.DataAdBean;
import yc.pointer.trip.bean.DataGoodBean;
import yc.pointer.trip.bean.NewHomeVideoData;
import yc.pointer.trip.bean.ZanBean;

/**
 * Create by 张继
 * 2017/9/20
 * 11:32
 * 数据库操作
 */
public class MySQLiteDataBase {
    /**
     * 保存网络上获取的数据到本地
     *
     * @param data 网络数据
     */
    public static void saveGoodData(NewHomeVideoData data) {
        for (DataGoodBean goodBean : data.getData().getData_good()) {
            DataGoodBean dataGoodBean = new DataGoodBean();
            dataGoodBean.setBid(goodBean.getBid());
            dataGoodBean.setUid(goodBean.getUid());
            dataGoodBean.setTitle(goodBean.getTitle());
            dataGoodBean.setType(goodBean.getType());
            dataGoodBean.setCity(goodBean.getCity());
            dataGoodBean.setSpot(goodBean.getSpot());
            dataGoodBean.setB_pic(goodBean.getB_pic());
            dataGoodBean.setY_pic(goodBean.getY_pic());
            dataGoodBean.setIs_order(goodBean.getIs_order());
            dataGoodBean.setIs_ad(goodBean.getIs_ad());
            dataGoodBean.setIs_index(goodBean.getIs_index());
            dataGoodBean.setLook_num(goodBean.getLook_num());
            dataGoodBean.setZan_num(goodBean.getZan_num());
            dataGoodBean.setOrd_num(goodBean.getOrd_num());
            dataGoodBean.setStatusX(goodBean.getStatusX());
            dataGoodBean.setInfo(goodBean.getInfo());
            dataGoodBean.setIs_cao(goodBean.getIs_cao());
            dataGoodBean.setNickname(goodBean.getNickname());
            dataGoodBean.setPhone(goodBean.getPhone());
            dataGoodBean.setPic(goodBean.getPic());
            dataGoodBean.setCp(goodBean.getCp());
            dataGoodBean.setVideo(goodBean.getVideo());
            dataGoodBean.setCol_num(goodBean.getCol_num());
            dataGoodBean.setWidth(goodBean.getWidth());
            dataGoodBean.setHeight(goodBean.getHeight());
            dataGoodBean.setIs_video(goodBean.getIs_video());
            dataGoodBean.setLocation(goodBean.getLocation());
            dataGoodBean.setAddtime(goodBean.getAddtime());
            dataGoodBean.setAddtime1(goodBean.getAddtime1());
            dataGoodBean.setLength(goodBean.getLength());
            dataGoodBean.setC_num(goodBean.getC_num());
            dataGoodBean.setF_status(goodBean.getF_status());
            dataGoodBean.setNew_url(goodBean.getNew_url());
            dataGoodBean.setS_num(goodBean.getS_num());
            dataGoodBean.setC_nickname(goodBean.getC_nickname());
            dataGoodBean.setC_u_pic(goodBean.getC_u_pic());
            dataGoodBean.setIs_jie(goodBean.getIs_jie());
            dataGoodBean.setIs_vip(goodBean.getIs_vip());
            dataGoodBean.setIs_del(goodBean.getIs_del());
            dataGoodBean.setZ_status(goodBean.getZ_status());

            for (CommentBean bean : goodBean.getComment()) {
                CommentBean commentBean = new CommentBean();
                commentBean.setC_info(bean.getC_info());
                commentBean.setC_nickname(bean.getC_nickname());
                commentBean.save();
            }
            for (ZanBean bean : goodBean.getZan()) {
                ZanBean zanBean = new ZanBean();
                zanBean.setZ_u_pic(bean.getZ_u_pic());
                zanBean.save();
            }
            dataGoodBean.save();
        }
    }

    /**
     * 查询所有数据
     *
     * @return 返回数据集合
     */
    public static List<DataGoodBean> findAll() {
        List<DataGoodBean> dataGoodBeans = DataGoodBean.listAll(DataGoodBean.class);

        List<DataGoodBean> goodBeanList = new ArrayList<>();
        List<CommentBean> commentBeanList = new ArrayList<>();
        List<ZanBean> zanBeanList = new ArrayList<>();

        for (DataGoodBean goodBean : dataGoodBeans) {
            DataGoodBean dataGoodBean = new DataGoodBean();
            dataGoodBean.setBid(goodBean.getBid());
            dataGoodBean.setUid(goodBean.getUid());
            dataGoodBean.setTitle(goodBean.getTitle());
            dataGoodBean.setType(goodBean.getType());
            dataGoodBean.setCity(goodBean.getCity());
            dataGoodBean.setSpot(goodBean.getSpot());
            dataGoodBean.setB_pic(goodBean.getB_pic());
            dataGoodBean.setY_pic(goodBean.getY_pic());
            dataGoodBean.setIs_order(goodBean.getIs_order());
            dataGoodBean.setIs_ad(goodBean.getIs_ad());
            dataGoodBean.setIs_index(goodBean.getIs_index());
            dataGoodBean.setLook_num(goodBean.getLook_num());
            dataGoodBean.setZan_num(goodBean.getZan_num());
            dataGoodBean.setOrd_num(goodBean.getOrd_num());
            dataGoodBean.setStatusX(goodBean.getStatusX());
            dataGoodBean.setInfo(goodBean.getInfo());
            dataGoodBean.setIs_cao(goodBean.getIs_cao());
            dataGoodBean.setNickname(goodBean.getNickname());
            dataGoodBean.setPhone(goodBean.getPhone());
            dataGoodBean.setPic(goodBean.getPic());
            dataGoodBean.setCp(goodBean.getCp());
            dataGoodBean.setVideo(goodBean.getVideo());
            dataGoodBean.setCol_num(goodBean.getCol_num());
            dataGoodBean.setWidth(goodBean.getWidth());
            dataGoodBean.setHeight(goodBean.getHeight());
            dataGoodBean.setIs_video(goodBean.getIs_video());
            dataGoodBean.setLocation(goodBean.getLocation());
            dataGoodBean.setAddtime(goodBean.getAddtime());
            dataGoodBean.setAddtime1(goodBean.getAddtime1());
            dataGoodBean.setLength(goodBean.getLength());
            dataGoodBean.setC_num(goodBean.getC_num());
            dataGoodBean.setF_status(goodBean.getF_status());
            dataGoodBean.setNew_url(goodBean.getNew_url());
            dataGoodBean.setS_num(goodBean.getS_num());
            dataGoodBean.setC_nickname(goodBean.getC_nickname());
            dataGoodBean.setC_u_pic(goodBean.getC_u_pic());
            dataGoodBean.setIs_jie(goodBean.getIs_jie());
            dataGoodBean.setIs_vip(goodBean.getIs_vip());
            dataGoodBean.setIs_del(goodBean.getIs_del());
            dataGoodBean.setZ_status(goodBean.getZ_status());

            List<CommentBean> commentBeans = CommentBean.listAll(CommentBean.class);
            for (CommentBean bean : commentBeans) {
                CommentBean commentBean = new CommentBean();
                commentBean.setC_info(bean.getC_info());
                commentBean.setC_nickname(bean.getC_nickname());
                commentBeanList.add(commentBean);
            }
            List<ZanBean> zanBeans = ZanBean.listAll(ZanBean.class);
            for (ZanBean bean : zanBeans) {
                ZanBean zanBean = new ZanBean();
                zanBean.setZ_u_pic(bean.getZ_u_pic());
                zanBeanList.add(zanBean);
            }

            dataGoodBean.setZan(zanBeanList);
            dataGoodBean.setComment(commentBeanList);
            goodBeanList.add(dataGoodBean);

        }
        return goodBeanList;
    }

    /**
     * 保存广告信息
     *
     * @param dataBean 网络数据
     */
    public static void saveAd(NewHomeVideoData dataBean) {
        for (DataAdBean bean : dataBean.getData().getData_ad()) {
            DataAdBean adBean = new DataAdBean();
            adBean.setAid(bean.getAid());
            adBean.setPic(bean.getPic());
            adBean.setTitle(bean.getTitle());
            adBean.save();
        }
    }

    public static List<DataAdBean> findDataAdAll() {
        List<DataAdBean> beans = new ArrayList<>();
        List<DataAdBean> beanList = DataAdBean.listAll(DataAdBean.class);
        for (DataAdBean dataAdBean : beanList) {
            DataAdBean adBean = new DataAdBean();
            adBean.setAid(dataAdBean.getAid());
            adBean.setPic(dataAdBean.getPic());
            adBean.setTitle(dataAdBean.getTitle());
            beans.add(adBean);
        }
        return beans;
    }

    public static void deleteGoodBean() {
        DataGoodBean.deleteAll(DataGoodBean.class);
        CommentBean.deleteAll(CommentBean.class);
        ZanBean.deleteAll(ZanBean.class);
    }

    public static void deleteAdBean() {
        DataAdBean.deleteAll(DataAdBean.class);
    }



}
