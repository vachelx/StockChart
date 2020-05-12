package com.vachel.chartview.util;

/**
 * Created by jianglixuan on 2019/8/7
 *
 * 通用可动态修改变量的开关
 */
public class SwitchUtils {
    // 是否绘制昨收基准线
    public static boolean sEnableDrawLastClose = false;
    public static float sLastClose = -1;
    // 是否绘制价格线
    public static boolean sEnableDrawPrice = false;

    public static float sCurrentPrice = -1;

    public static boolean isEnableDrawLastClose() {
        return sEnableDrawLastClose;
    }

    public static void setEnableDrawLastClose(boolean enableDrawLastClose) {
        sEnableDrawLastClose = enableDrawLastClose;
    }

    public static boolean isEnableDrawPrice() {
        return sEnableDrawPrice;
    }

    public static void setEnableDrawPrice(boolean enableDrawPrice) {
        sEnableDrawPrice = enableDrawPrice;
    }

    public static void setLastClose(float lastClose) {
        sLastClose = lastClose;
    }
    public static float getLastClose() {
        return sLastClose;
    }

    public static void setCurrentPrice(float currentPrice) {
        sCurrentPrice = currentPrice;
    }

    public static float getCurrentPrice() {
        return sCurrentPrice;
    }
}
