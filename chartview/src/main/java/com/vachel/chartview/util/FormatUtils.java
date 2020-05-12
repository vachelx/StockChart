package com.vachel.chartview.util;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {
    private static DecimalFormat df = new DecimalFormat("#.##");

    public static String formatValue(double value) {
        return df.format(value);
    }

    // 小数点大于6位的保留6位， 大于7位的浮点数不做科学计数处理
    public static String formatFloatNumber(float value) {
        if (value>= 999999) {
            DecimalFormat df = new DecimalFormat("#");
            return df.format(value);
        } else {
            String[] temp = (value+"").split("\\.");
            if (temp.length == 2) {
                char[] tempC = temp[1].toCharArray();
                if (tempC.length>= 6) {
                    DecimalFormat df = new DecimalFormat(".000000");
                    return df.format(value);
                }
            }
        }
        return value +"";
    }

    /**
     * 讲时间字符串转换为date
     *
     * @param string
     * @return Date 日期
     */
    public static Date formatStringToTime(String string, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(string);
        } catch (ParseException e) {
        }
        return date;
    }

    public static String changedDateFormat(@NonNull Date string, String desPattern) {
        SimpleDateFormat format = new SimpleDateFormat(desPattern);
        return format.format(string);
    }

    /**
     * 确定时间数据格式类型
     *
     * @param time
     * @return 0: "yyyy-MM-dd HH:mm:ss"
     * 1： "yyyy-MM-dd"
     */
    public static int getFormatDateType(String time) {
        for (int i = 0; i < Constant.SOURCE_TIME_STRING.length; i++) {
            Date dateMin = formatStringToTime(time, Constant.SOURCE_TIME_STRING[i]);
            if (dateMin != null) {
                return i;
            }
        }
        return 0;
    }
}
