package com.vachel.chartview.custom;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.vachel.chartview.bean.KLineData;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用于格式化横轴时间
 */
public class BarAXisValueFormatter extends ValueFormatter {
    private List<BarEntry> mBarEntries;
    private IValueFormatterCallback mCallback;
    //横轴显示的时间格式：3种
    // "HH:mm"，"MM-dd"，"yyyy-MM"
    public String mDisplayTimeFormat = Constant.TIME_SHARING_YY_MM;

    public BarAXisValueFormatter(List<BarEntry> entries, IValueFormatterCallback callback) {
        mBarEntries = entries;
        mCallback = callback;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index >= mBarEntries.size()) {
            return "";
        }
        BarEntry barEntry = mBarEntries.get(index);
        Date time = ((KLineData) barEntry.getData()).getDate();
        return new SimpleDateFormat(mDisplayTimeFormat).format(time);
    }

    public void needUpdateValueRange() {
        if (mCallback != null) {
            int highestVisibleX = (int) mCallback.getHighestVisibleX();
            int lowestVisibleX = (int) mCallback.getLowestVisibleX();
            mDisplayTimeFormat = getRangeTimeFormat(highestVisibleX, lowestVisibleX);
        }
    }

    public String formatLabelTime(int index, @NonNull KLineData kline) {
        String formatTime = "";
        Date date = kline.getDate();
        if (!TextUtils.isEmpty(kline.getTime())) {
            switch (mDisplayTimeFormat) {
                case Constant.TIME_SHARING_YY_MM:
                    formatTime = FormatUtils.changedDateFormat(date, Constant.SOURCE_TIME_STRING[1]);
                    break;
                case Constant.TIME_SHARING_HH_MM:
                    formatTime = FormatUtils.changedDateFormat(date, Constant.TIME_LABEL_MARK_TIME);
                    break;
                case Constant.TIME_SHARING_MM_DD:
                    int formatDateType = FormatUtils.getFormatDateType(kline.getTime());
                    formatTime = FormatUtils.changedDateFormat(date, formatDateType == 1 ? Constant.SOURCE_TIME_STRING[1] : Constant.TIME_LABEL_MARK_TIME);
                    break;
            }
        }
        if (TextUtils.isEmpty(formatTime)) {
            formatTime = getFormattedValue(index);
        }
        return formatTime;
    }

    private String getRangeTimeFormat(int highestVisibleX, int lowestVisibleX) {
        if (lowestVisibleX < 0) {
            lowestVisibleX = 0;
        }
        if (highestVisibleX >= mBarEntries.size()) {
            highestVisibleX = mBarEntries.size() - 1;
        }

        Date dateMin = ((KLineData) mBarEntries.get(lowestVisibleX).getData()).getDate();
        Date dateMax = ((KLineData) mBarEntries.get(highestVisibleX).getData()).getDate();
        long diffTime = dateMax.getTime() - dateMin.getTime();
        String displayTimeFormat;
        if (diffTime < Constant.MILLI_SECOND_2_DAY) {
            displayTimeFormat = Constant.TIME_SHARING_HH_MM;
        } else if (diffTime < Constant.MILLI_SECOND_1_YEAR) {
            displayTimeFormat = Constant.TIME_SHARING_MM_DD;
        } else {
            displayTimeFormat = Constant.TIME_SHARING_YY_MM;
        }
        return displayTimeFormat;
    }

    public interface IValueFormatterCallback {
        float getHighestVisibleX();

        float getLowestVisibleX();
    }
}
