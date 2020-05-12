package com.vachel.chartview.util;

import android.content.Context;
import android.content.res.Resources;

import com.vachel.chartview.R;

/**
 * 资源准备
 */
public class ResourceUtils {
    private static int mColorRed;
    private static int mColorGreen;
    private static int mBarColorRed;
    private static int mBarColorGreen;
    private static int mLinePathColorGreen;
    private static int mLinePathColorRed;
    private static int mHighLineBlack;
    private static String mTheme;
    private static int mThemeBlackColor;
    private static int mThemeWhiteColor;
    private static int mLabelBlack;

    private static int mLabelWhite;
    private static int mHighLineWhite;

    private static long mDoubleTapTime = 0;
    private static int[] mParameterLineColors;
    // false 红涨绿跌， true为红跌绿涨
    private static boolean mRiseColorType;
    private static int mGridColorBlack;
    private static int mGridColorWhite;

    public static void initResource(Context context, String theme, boolean riseFallType) {
        mTheme = theme;
        mRiseColorType = riseFallType;
        Resources res = context.getResources();
        mColorRed = res.getColor(R.color.kline_red);
        mColorGreen = res.getColor(R.color.kline_green);
        mBarColorRed = res.getColor(R.color.market_color_bar_chart_positive);
        mBarColorGreen = res.getColor(R.color.market_color_bar_chart_negative);
        mLinePathColorGreen = res.getColor(R.color.line_path_green);
        mLinePathColorRed = res.getColor(R.color.line_path_red);

        mHighLineBlack = res.getColor(R.color.color_high_line_black);
        mHighLineWhite = res.getColor(R.color.color_high_line_white);

        mThemeBlackColor = res.getColor(R.color.theme_black);
        mThemeWhiteColor = res.getColor(R.color.theme_white);

        mLabelBlack = res.getColor(R.color.label_font_black);
        mLabelWhite = res.getColor(R.color.label_font_white);
        mParameterLineColors = new int[]{
                res.getColor(R.color.parameter_line_a),
                res.getColor(R.color.parameter_line_b),
                res.getColor(R.color.parameter_line_c),
                res.getColor(R.color.parameter_line_d),
                res.getColor(R.color.parameter_line_e),
                res.getColor(R.color.parameter_line_f)};

        mGridColorBlack = res.getColor(R.color.black_line_divider);
        mGridColorWhite = res.getColor(R.color.white_line_divider);
    }

    public static int getColorRise() {
        return mRiseColorType ? mColorGreen : mColorRed;
    }

    public static int getColorFall() {
        return mRiseColorType ? mColorRed: mColorGreen;
    }

    public static int getBarColorRise() {
        return mRiseColorType ? mBarColorGreen: mBarColorRed;
    }

    public static int getBarColorFall() {
        return mRiseColorType ? mBarColorRed: mBarColorGreen;
    }

    public static int getLinePathColor(boolean isUp) {
        return mRiseColorType ^ isUp ? mLinePathColorRed : mLinePathColorGreen;
    }

    public static int getGridColor() {
        return mTheme.equals(Constant.THEME_WHITE) ? mGridColorWhite : mGridColorBlack;
    }

    public static int getHighLineColor() {
        return mTheme.equals(Constant.THEME_WHITE) ? mHighLineWhite : mHighLineBlack;
    }

    public static int getThemeColor() {
        return mTheme.equals(Constant.THEME_WHITE) ? mThemeWhiteColor : mThemeBlackColor;
    }

    public static int getThemeColorReverse() {
        return mTheme.equals(Constant.THEME_WHITE) ? mThemeBlackColor : mThemeWhiteColor;
    }

    public static int getLabelColor() {
        return mTheme.equals(Constant.THEME_WHITE) ? mLabelWhite : mLabelBlack;
    }

    public static int[] getParameterLineColors() {
        return mParameterLineColors;
    }

    public static void setTheme(String theme) {
        mTheme = theme;
    }

    public static String getTheme() {
        return mTheme;
    }

    public static long getDoubleTapTime() {
        return mDoubleTapTime;
    }

    public static void setDoubleTapTime(long doubleTapTime) {
        mDoubleTapTime = doubleTapTime;
    }

    public static void setRiseColorType(boolean riseColorType) {
        mRiseColorType = riseColorType;
    }
}
