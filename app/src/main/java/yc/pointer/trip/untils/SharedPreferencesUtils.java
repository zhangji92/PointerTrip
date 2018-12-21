package yc.pointer.trip.untils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.*;
import java.util.ArrayList;

/**
 * Create By 张继
 * 2017/7/31 0031
 * 下午 3:32
 */
public class SharedPreferencesUtils {

    private static SharedPreferences mSharedPreferences=null;
    private static SharedPreferencesUtils mSharedPreferencesUtils=null;

    private SharedPreferencesUtils() {
    }

    /**
     * 单例模式
     * @return
     */
    public static SharedPreferencesUtils getInstance(){
        if (mSharedPreferences==null){
            mSharedPreferencesUtils=new SharedPreferencesUtils();
        }
        return mSharedPreferencesUtils;
    }

    private static  SharedPreferences getSharedPreferences(Context context){
        if(mSharedPreferences ==null){
            mSharedPreferences = context.getSharedPreferences("HistorySearch",Context.MODE_APPEND);
        }
        return  mSharedPreferences;
    }


    /**
     * 保存历史信息
     * @param historyValue 城市信息
     */
    public void saveHistory(Context context,String historyKey,String historyValue){
        if (StringUtil.isEmpty(historyValue)) {
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        String keywords = getInstance().getString(context,historyKey);
        if (keywords != "") {
            //15个字符串
            String[] key = keywords.split("_", 10);
            if (key.length > 9) {
                //第15个去除
                keywords = keywords.replace(key[9], "");
            }
            //判断是否相同
            for (int i = 0; i < key.length; i++) {
                if (historyValue.equals(key[i])) {
                    keywords = keywords.replace(historyValue + "_", "");
                }
            }
        }
        //分隔符
        editor.putString(historyKey, historyValue + "_" + keywords);
        editor.putLong("historyKey_bak", System.currentTimeMillis());
        editor.commit();
    }
    /**
     * 读取城市数据
     * @return 返回集合
     */
    public  ArrayList<String> readHistory(Context context,String historyKey) {
        ArrayList<String> list = new ArrayList<>();
        String keywords = getInstance().getString(context,historyKey);
        if (null != keywords) {
            String[] key = keywords.split("_");
            for (int i = 0; i < key.length; i++) {
                list.add(key[i]);
            }
        }
        return list;
    }
    public void putString(Context context, String key, String value){
        SharedPreferences.Editor editor =getSharedPreferences(context).edit();
        editor.putString(key,value).commit();
    }
    public void putBoolean(Context context,String key,boolean value){
        SharedPreferences.Editor editor =getSharedPreferences(context).edit();
        editor.putBoolean(key,value).commit();
    }
    public void putFloat(Context context,String key,float value){
        SharedPreferences.Editor editor =getSharedPreferences(context).edit();
        editor.putFloat(key,value).commit();
    }
    public void putInt(Context context,String key,int value){
        SharedPreferences.Editor editor =getSharedPreferences(context).edit();
        editor.putInt(key,value).commit();
    }
    public void putLong(Context context,String key,long value){
        SharedPreferences.Editor editor =getSharedPreferences(context).edit();
        editor.putLong(key,value).commit();
    }
    public String getString(Context context,String key){
        return getSharedPreferences(context).getString(key,"not find");
    }
    public boolean getBoolean(Context context,String key){
        return getSharedPreferences(context).getBoolean(key,false);
    }
    public int getInt(Context context,String key){
        return getSharedPreferences(context).getInt(key,0);
    }
    public long getLong(Context context,String key){
        return getSharedPreferences(context).getLong(key,0);
    }
    public float getFloat(Context context,String key){
        return getSharedPreferences(context).getFloat(key,0);
    }

    public void remove(Context context,String key){
        SharedPreferences.Editor  editor= getSharedPreferences(context).edit();
        editor.remove(key).commit();
    }

}
