package com.vachel.chartview.util;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import java.util.ArrayList;


/**
 * 2种用法
 * 1. 一条文本前后样式不一： text的前三个字为颜色"#000000"，15sp字体
 *      后面的剩下的字体为颜色"#23262F"，20sp的粗体字
 *
 * SpannableString spannableString = SpannableStringFormat.createBuild(text)
 *                     .textColorSize(0 , 3, Color.parseColor("#000000"), 15, true)
 *                     .textColorStyleSize( 3,  text.length(), Color.parseColor("#23262F"), Typeface.BOLD, 20, true)
 *                     .build();
 *
 * 2.不确定有多少文本，每一个文本段添加不同颜色
 * SpannableString spannableString = SpannableStringFormat.createBuild()
 *                     .addSpannable(text1, color1)
 *                     .addSpannable(text2, color2)
 *                     .build();
 *
 */
public class SpannableStringFormat {
    public static class Build {
        private SpannableString mSpannableString;
        private ArrayList<String> mStrings;
        private ArrayList<Integer> mColors;

        private Build(String string) {
            mSpannableString = new SpannableString(string);
        }

        private Build() {
            mStrings = new ArrayList<String>();
            mColors = new ArrayList<Integer>();
        }

        public Build textSize(int start, int end, int size, boolean isDip) {
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, isDip);
            mSpannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build textColor(int start, int end, @ColorInt int color) {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            mSpannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build textStyle(int start, int end, int style) {
            StyleSpan styleSpan = new StyleSpan(style);
            mSpannableString.setSpan(styleSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build textColorStyleSize(int start, int end, @ColorInt int color, int style, int size, boolean sizeDip) {
            StyleSpan styleSpan = new StyleSpan(style);
            mSpannableString.setSpan(styleSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            mSpannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, sizeDip);
            mSpannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build textColorSize(int start, int end, @ColorInt int color, int size, boolean sizeDip) {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            mSpannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, sizeDip);
            mSpannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build textColorStyle(int start, int end, @ColorInt int color, int style) {
            StyleSpan styleSpan = new StyleSpan(style);
            mSpannableString.setSpan(styleSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            mSpannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return this;
        }

        public Build textStyleSize(int start, int end, int style, int size, boolean sizeDip) {
            StyleSpan styleSpan = new StyleSpan(style);
            mSpannableString.setSpan(styleSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, sizeDip);
            mSpannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build url(int start, int end, @NonNull String url) {
            URLSpan urlSpan = new URLSpan(url);
            mSpannableString.setSpan(urlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build span(int start, int end, @NonNull Object span) {
            mSpannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Build addSpannable(String text, int color){
            mStrings.add(text);
            mColors.add(color);
            return this;
        }

        public SpannableString build() {
            if (mSpannableString != null) {
                return mSpannableString;
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mStrings.size(); i++) {
                    stringBuffer.append(mStrings.get(i));
                }
                mSpannableString = new SpannableString(stringBuffer);
                int num = 0;
                for (int i = 0; i < mColors.size(); i++) {
                    int color = mColors.get(i);
                    String str = mStrings.get(i);
                    mSpannableString.setSpan(new ForegroundColorSpan(color), num, num + str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    num += str.length();
                }
                return mSpannableString;
            }
        }
    }

    private SpannableStringFormat() {

    }

    public static Build createBuild(String string) {
        Build build = new Build(string);
        return build;
    }

    public static Build createBuild() {
        Build build = new Build();
        return build;
    }

}
