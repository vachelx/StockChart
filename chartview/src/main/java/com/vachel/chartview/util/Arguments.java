package com.vachel.chartview.util;

/**
 * Created by jianglixuan on 2019/7/30
 *
 * 技术指标参数，用于扩展后续设置功能
 *
 */
public class Arguments {
    /**
     * 主图指标
     */
    public static String[] MASTER_ARGUMENTS = new String[]{"MA", "EMA", "BOLL", "NONE"};
    // 主图指标对应的type类型 需要与以上一一对应
    public static final int MASTER_TYPE_MA = 0;
    public static final int MASTER_TYPE_EMA = 1;
    public static final int MASTER_TYPE_BOLL = 2;
    public static final int MASTER_TYPE_NONE = 3;

    /**
     * 副图指标
     */
    public static String[] SECONDARY_ARGUMENTS = new String[]{"VOL", "MACD", "KDJ", "RSI"};
    public static final int SECONDARY_TYPE_VOL = 0;
    public static final int SECONDARY_TYPE_MACD = 1;
    public static final int SECONDARY_TYPE_KDJ = 2;
    public static final int SECONDARY_TYPE_RSI = 3;

    // 主图MA参数 动态多个参数
    public static int[] KINDS_MASTER_MA = new int[]{5, 10, 20, 30};
    // 主图BOLL参数 只需要2个参数 n， k
    public static int[] KINDS_MASTER_BOLL = new int[]{20, 2};
    // 主图EMA参数 只需要2个参数 short，long
    public static int[] KINDS_MASTER_EMA = new int[]{12, 26};

    // 副图MACD参数 只需要3个参数 short，long, mid
    public static int[] KINDS_SECONDARY_MACD = new int[]{12, 26, 9};
    // 副图KDJ参数
    public static int[] KINDS_SECONDARY_KDJ = new int[]{9, 3, 3};
    // kdj权重
    public static int KINDS_SECONDARY_KDJ_WEIGHT = 1;
    // RSI
    public static int[] KINDS_SECONDARY_RSI = new int[]{6, 12, 24};
    // RSI权重
    public static int KINDS_SECONDARY_RSI_WEIGHT = 1;

    public static void setMacdParams(int[] macd) {
        KINDS_SECONDARY_MACD = macd;
    }

    public static void setKdjParams(int[] kdj) {
        KINDS_SECONDARY_KDJ = kdj;
    }

    public static void setRsiParams(int[] rsi) {
        KINDS_SECONDARY_RSI = rsi;
    }

    public static void setKdjWeight(int kdjWeight) {
        KINDS_SECONDARY_KDJ_WEIGHT = kdjWeight;
    }

    public static void setRsiWeight(int rsiWeight) {
        KINDS_SECONDARY_RSI_WEIGHT = rsiWeight;
    }

    public static void setMasterMaParams(int[] ma) {
        KINDS_MASTER_MA = ma;
    }

    public static void setBollParams(int[] boll) {
        KINDS_MASTER_BOLL = boll;
    }

    public static void setEmaParams(int[] ema) {
        KINDS_MASTER_EMA = ema;
    }

}
