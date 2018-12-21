package yc.pointer.trip.untils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/16.
 * 字符串的工具类
 */

public class StringUtil {

    private StringUtil() {
        /** cannot be instantiated 不能被实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 将Null 转化为空
     *
     * @param str 字符串
     * @return 空
     */
    public static String parseEmpty(String str) {
        if (str == null || "null".equals(str.trim())) {
            str = "";
        }
        return str.trim();
    }

    /**
     * 判断字符串是否为空或null
     *
     * @param str 字符串
     * @return true为空或null----false不为空或null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static int chineseLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";//正则表达式

        if (!isEmpty(str)) {// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (temp.matches(chinese)) {
                    valueLength += 2;
                } else {
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    /**
     * 获取指定长度的字符所在位置.
     *
     * @param str  指定的字符串
     * @param maxL 要取到的长度（字符长度，中文字符计2个）
     * @return 字符的所在位置
     */
    public static int subStringLength(String str, int maxL) {
        int currentIndex = 0;
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";//正则表达式
        if (!isEmpty(str)) { // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (temp.matches(chinese)) {
                    valueLength += 2;
                } else {
                    valueLength += 1;
                }
                if (valueLength >= maxL) {
                    currentIndex = i;
                    break;
                }
            }
        }
        return currentIndex;
    }

    /**
     * 是否只是字母和数字.
     *
     * @param str 指定的字符串
     * @return 是否只是字母和数字:是为true，否则false
     */
    public static boolean isNumberLetter(String str) {
        boolean isNoLetter = false;
        String expr = "^[A-Za-z0-9]+$";
        if (str.matches(expr)) {//判断是否匹配指定的字符串，如果匹配则为true,否则为false;
            isNoLetter = true;
        }
        return isNoLetter;
    }

    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    /**
     * 描述：是否是中文.
     *
     * @param str 指定的字符串
     * @return 是否是中文:是为true，否则false
     */
    public static Boolean isChinese(String str) {
        Boolean isChinese = true;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                } else {
                    isChinese = false;
                }
            }
        }
        return isChinese;
    }

    /**
     * 描述：是否包含中文.
     *
     * @param str 指定的字符串
     * @return 是否包含中文:是为true，否则false
     */
    public static Boolean isContainChinese(String str) {
        Boolean isChinese = false;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                    isChinese = true;
                } else {

                }
            }
        }
        return isChinese;
    }

    /**
     * 描述：手机号格式验证.
     *
     * @param str 指定的手机号码字符串
     * @return 是否为手机号码格式:是为true，否则false
     */
    public static Boolean isMobileNo(Context context, String str) {
        Pattern pattern = Pattern.compile("^[1][0-9][0-9]{9}$");
        if (str == null || str.equals("")) {
            Toast.makeText(context, "请输入手机号", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!pattern.matcher(str).matches()) {
            Toast.makeText(context, "手机号码有误", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 描述：日期格式验证是否正确
     *
     * @param str
     * @return 是否为日期格式:YYYY-MM-dd
     */
    public static Boolean isDate(String str) {
        Boolean isDate = true;
        Pattern pattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");

        if (str == null || str.equals("")) {
            return false;
        }
        if (!pattern.matcher(str).matches()) {
            return false;
        }
        //判断是否闰年
        String dateFormat = StringUtil.dateTimeFormat(str);
        Integer year = Integer.valueOf(dateFormat.substring(0, 4));
        String month = dateFormat.substring(5, 7);
        Integer day = Integer.valueOf(dateFormat.substring(8, dateFormat.length()));
        int d = 0;
        //获取月份首位
        String m = month.substring(0, 1);
        if (m.equals("0") || m.equals("1")) {
            if (month.equals("01") || month.equals("03") || month.equals("05") || month.equals("07")
                    || month.equals("08") || month.equals("10") || month.equals("12")) {
                if (day - 31 <= 0) {
                    d = day;
                } else {
                    d = 31;
                    isDate = false;
                }
            } else {
                if (day - 30 <= 0) {
                    d = day;
                } else {
                    d = 30;
                    isDate = false;
                }
            }
        } else {
            isDate = false;
        }
        //判断闰年的2月份天数
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            //闰年
            if (month.equals("02")) {
                if (day - 29 <= 0) {
                    d = day;
                } else {
                    d = 29;
                    isDate = false;
                }
            }
        } else {
            //平年
            if (month.equals("02")) {
                if (day - 28 <= 0) {
                    d = (day + 20);
                } else {
                    d = 28;
                    isDate = false;
                }
            }
        }

        return isDate;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(date);
        return time;
    }

    /**
     * 判断输入时间日期是否已过期
     *
     * @param str 输入的日期
     * @return true为没有过期，false为已过期日期
     */
    public static boolean isEffect(String str) {
        Boolean isEffect = true;
        String nowTime = getNowTime();
        int data = Integer.valueOf(str.substring(0, 4));
        int newData = Integer.valueOf(nowTime.substring(0, 4));
        int month = Integer.valueOf(str.substring(5, 7));
        int nowMonth = Integer.valueOf(nowTime.substring(5, 7));
        Integer day = Integer.valueOf(str.substring(8, str.length()));
        Integer nowDay = Integer.valueOf(nowTime.substring(8, nowTime.length()));
        if (data > newData) {
            isEffect = true;
        } else if (data == newData) {
            if (month > nowMonth) {
                isEffect = true;
            } else if (month == nowMonth) {
                if (day >= nowDay) {
                    isEffect = true;
                } else {
                    isEffect = false;
                }
            } else {
                isEffect = false;
            }

        } else {
            isEffect = false;
        }
        return isEffect;
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {

        Pattern pattern = Pattern.compile("[0-9]*");

        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {

            return false;
        }
        return true;
    }

    /**
     * 描述：从输入流中获得String.
     *
     * @param is 输入流
     * @return 获得的String
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            //最后一个\n删除
            if (sb.indexOf("\n") != -1 && sb.lastIndexOf("\n") == sb.length() - 1) {
                sb.delete(sb.lastIndexOf("\n"), sb.lastIndexOf("\n") + 1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 描述：标准化日期时间类型的数据，不足两位的补0.
     *
     * @param dateTime 预格式的时间字符串，如:2012-3-2 12:2:20
     * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
     */
    public static String dateTimeFormat(String dateTime) {
        StringBuilder sb = new StringBuilder();
        try {
            if (isEmpty(dateTime)) {
                return null;
            }
            String[] dateAndTime = dateTime.split(" ");
            if (dateAndTime.length > 0) {
                for (String str : dateAndTime) {
                    if (str.indexOf("-") != -1) {
                        String[] date = str.split("-");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append("-");
                            }
                        }
                    } else if (str.indexOf(":") != -1) {
                        sb.append(" ");
                        String[] date = str.split(":");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append(":");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    /**
     * 描述：不足2个字符的在前面补“0”.
     *
     * @param str 指定的字符串
     * @return 至少2个字符的字符串
     */
    public static String strFormat2(String str) {
        try {
            if (str.length() <= 1) {
                str = "0" + str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 描述：截取字符串到指定字节长度.
     *
     * @param str    the str
     * @param length 指定字节长度
     * @return 截取后的字符串
     */
    public static String cutString(String str, int length) {
        return cutString(str, length, "");
    }

    /**
     * java转换数字以万为单位
     *
     * @param num   要转化的数字
     * @param digit 保留的位数 可传null
     * @return
     */
    public static String conversion(int num, Integer digit) {
        if (num < 9999) {
            return num + "";
        }
        String unit = "w";
        double newNum = num / 10000.0;
        if (digit != null) {
            String numStr = String.format("%." + digit + "f", newNum);
            return numStr + unit;
        }
        return newNum + unit;
    }
    public static String conversion(double num, Integer digit) {
        if (num < 9999) {
            return num + "";
        }
        String unit = "w";
        double newNum = num / 10000.0;
        if (digit != null) {
            String numStr = String.format("%." + digit + "f", newNum);
            return numStr + unit;
        }
        return newNum + unit;
    }

    /**
     * 描述：截取字符串到指定字节长度.
     *
     * @param str    文本
     * @param length 字节长度
     * @param dot    省略符号
     * @return 截取后的字符串
     */
    public static String cutString(String str, int length, String dot) {
        int strBLen = strlen(str, "GBK");
        if (strBLen <= length) {
            return str;
        }
        int temp = 0;
        StringBuffer sb = new StringBuffer(length);
        char[] ch = str.toCharArray();
        for (char c : ch) {
            sb.append(c);
            if (c > 256) {
                temp += 2;
            } else {
                temp += 1;
            }
            if (temp >= length) {
                if (dot != null) {
                    sb.append(dot);
                }
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 描述：截取字符串从第一个指定字符.
     *
     * @param str1   原文本
     * @param str2   指定字符
     * @param offset 偏移的索引
     * @return 截取后的字符串
     */
    public static String cutStringFromChar(String str1, String str2, int offset) {
        if (isEmpty(str1)) {
            return "";
        }
        int start = str1.indexOf(str2);
        if (start != -1) {
            if (str1.length() > start + offset) {
                return str1.substring(start + offset);
            }
        }
        return "";
    }

    /**
     * 描述：获取字节长度.
     *
     * @param str     文本
     * @param charset 字符集（GBK）
     * @return the int
     */
    public static int strlen(String str, String charset) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int length = 0;
        try {
            length = str.getBytes(charset).length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    /**
     * 获取大小的描述.
     *
     * @param size 字节个数
     * @return 大小的描述
     */
    public static String getSizeDesc(long size) {
        String suffix = "B";
        if (size >= 1024) {
            suffix = "K";
            size = size >> 10;
            if (size >= 1024) {
                suffix = "M";
//size /= 1024;
                size = size >> 10;
                if (size >= 1024) {
                    suffix = "G";
                    size = size >> 10;
                    //size /= 1024;
                }
            }
        }
        return size + suffix;
    }

    /**
     * 描述：ip地址转换为10进制数.
     *
     * @param ip the ip
     * @return the long
     */
    public static long ip2int(String ip) {
        ip = ip.replace(".", ",");
        String[] items = ip.split(",");
        return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }

    // 将时间戳转为字符串
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 例如：
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    // 将时间戳转为字符串到秒级
    public static String getStrTimeTomm(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    // 将时间戳转为Date
    public static Date getStrTimeDate(String cc_time) {
        Date parse = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            parse = sdf.parse(cc_time.toString());
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    /**
     * 将String类型的时间转换成时间戳
     *
     * @param cc_time String类型的时间 2018-07-03
     * @return 返回时间戳
     */
    public static long getStrTimeDateTom(String cc_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(cc_time.toString());
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @param targetStr 要处理的字符串
     * @description 切割字符串，将文本和img标签碎片化，如"ab<img>cd"转换为"ab"、"<img>"、"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        //Pattern pattern = Pattern.compile("src=.*?");
        //Pattern pattern = Pattern.compile(" <img(.*?)>");
//        Pattern pattern = Pattern.compile("<img.*?src=\"(.*?)\".*?>");
        List<String> splitTextList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<img.*?src=\\\"(.*?)\\\".*?>");
        Matcher matcher = pattern.matcher(targetStr);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                splitTextList.add(targetStr.substring(lastIndex, matcher.start()));
            }
            splitTextList.add(targetStr.substring(matcher.start(), matcher.end()));
            lastIndex = matcher.end();
        }
        if (lastIndex != targetStr.length()) {
            splitTextList.add(targetStr.substring(lastIndex, targetStr.length()));
        }
        return splitTextList;
    }

    /**
     * 获取img标签中的src值
     *
     * @param content
     * @return
     */
    public static String getImgSrc(String content) {
        String str_src = null;
        //目前img标签标示有3种表达式
        //<img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
        //开始匹配content中的<img />标签
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //获取到匹配的<img />标签中的内容
                String str_img = m_img.group(2);

                //开始匹配<img />标签中的src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    str_src = m_src.group(3);
                }
                //结束匹配<img />标签中的src

                //匹配content中是否存在下一个<img />标签，有则继续以上步骤匹配<img />标签中的src
                result_img = m_img.find();
            }
        }
        return str_src;
    }

    /**
     * 关键字高亮显示
     *
     * @param target 需要高亮的关键字
     * @param text   需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     * SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * vHolder.tv_itemName_search.setText(textString);
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 截取字符串中的数字
     *
     * @param str 传入的字符串
     * @return 返回数字的字符串
     */
    public static String RegNum(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim().toString();
    }

    /**
     * 从输入流中获得String.
     *
     * @param inputStream
     * @return
     */
    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "GB2312");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String format(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * 60000L) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + "秒前";
        }
        if (delta < 45L * 60000L) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + "分钟前";
        }
        if (delta < 24L * 3600000L) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + "小时前";
        }
        if (delta < 48L * 3600000L) {
            return "昨天";
        }
        if (delta < 30L * 86400000L) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + "天前";
        }
        if (delta < 12L * 4L * 604800000L) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + "月前";
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + "年前";
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }


    /**
     * 时间格式化
     *
     * @param timeMs
     * @return
     */
    public static String timeFormat(int timeMs) {
        int totalSeconds = timeMs;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否为JSON形式
     *
     * @param json
     * @return
     */
    public static boolean isJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
//            System.out.println("bad json: " + json);
            return false;
        }
    }
}
