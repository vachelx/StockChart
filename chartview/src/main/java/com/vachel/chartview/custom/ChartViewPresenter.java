package com.vachel.chartview.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vachel.chartview.bean.KLineData;
import com.vachel.chartview.data.entity.MyCandleEntry;
import com.vachel.chartview.data.entity.MyLineEntry;
import com.vachel.chartview.util.Arguments;
import com.vachel.chartview.util.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChartViewPresenter {
    private IStockChartView mCallback;
    private Gson mGson;
    public ChartViewPresenter(IStockChartView callback) {
        mCallback = callback;
    }

    // 使用假数据
    public void refresh(int type) {
        refresh(Constant.DATA_INDEX[type - 1], type);
    }

    @SuppressLint("StaticFieldLeak")
    public void refresh(final String data, final int type) {
        saveChartType(type);
        new AsyncTask<String, Void, List[]>() {
            @Override
            protected List[] doInBackground(String... strings) {
                ArrayList<KLineData> list = geDataList(strings[0]);
                ArrayList<Entry> lineEntries = new ArrayList<>();
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                ArrayList<CandleEntry> candleEntries = new ArrayList<>();
                try {
                    if (list != null && !list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            KLineData data = list.get(i);
                            lineEntries.add(new MyLineEntry(i, Float.valueOf(data.getClose()), data));
                            barEntries.add(new BarEntry(i, Float.valueOf(data.getVolume()), data));
                            if (type != Constant.TYPE_TIME_SHARING_M1 && data.getHight() != null) {
                                candleEntries.add(new MyCandleEntry(i, Float.valueOf(data.getHight()), Float.valueOf(data.getLow()), Float.valueOf(data.getOpen()), Float.valueOf(data.getClose()), data));
                            }
                        }
                    }
                } catch (Exception e) {
                    return new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
                }
                return new ArrayList[]{lineEntries,barEntries,candleEntries};
            }

            @Override
            protected void onPostExecute(List[] lists) {
                mCallback.refreshContent(lists[0], lists[1], lists[2], type);
            }
        }.execute(data);
    }

    private ArrayList<KLineData> geDataList(String data) {
        try {
            return getGson().fromJson(data, new TypeToken<List<KLineData>>() {
            }.getType());
        } catch (Exception ignored) {
        }
        return null;
    }

    private Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder().create();
        }
        return mGson;
    }

    public int getTypeByItemIndex(int index) {
        int type;
        if (index == 0) {
            type = Constant.TYPE_TIME_SHARING_M1;
        } else if (index == 2) {
            type = Constant.TYPE_K_LINE_DAY;
        } else if (index == 3) {
            type = Constant.TYPE_K_LINE_WEEK;
        } else if (index == 4) {
            type = Constant.TYPE_K_LINE_MONTH;
        } else {
            type = Constant.TYPE_K_LINE_M5;
        }
        return type;
    }

    public interface IStockChartView {
        void refreshContent(List<Entry> lineEntries, List<BarEntry> barEntries, List<CandleEntry> candleEntries, int type);

        Context getContext();
    }

    public String getRangeTimeFormat(int highestVisibleX, int lowestVisibleX, List<BarEntry> barEntries) {
        if (highestVisibleX >= 0 && lowestVisibleX >= 0 && lowestVisibleX < highestVisibleX) {
            if (lowestVisibleX < 0) {
                lowestVisibleX = 0;
            }
            if (highestVisibleX >= barEntries.size()) {
                highestVisibleX = barEntries.size() - 1;
            }
            Date dateMin = ((KLineData) barEntries.get(lowestVisibleX).getData()).getDate();
            Date dateMax = ((KLineData) barEntries.get(highestVisibleX).getData()).getDate();
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
        return null;
    }

    public int getLastChartType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("chart", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constant.KEY_CHART_TYPE, 1);
    }

    public void saveChartType(final int type) {
        SharedPreferences sharedPreferences = mCallback.getContext().getSharedPreferences("chart", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constant.KEY_CHART_TYPE, type).apply();
    }

    /**
     * 更新主图选择的指标文本
     * @param type
     * @param v
     */
    public void setSelectedText(int type, TextView v) {
        v.setText(Arguments.MASTER_ARGUMENTS[type]);
    }

    /**
     * 更新副图选择的指标文本
     * @param type
     * @param v
     */
    public void setSecondaryText(int type, TextView v) {
        v.setText(Arguments.SECONDARY_ARGUMENTS[type]);
    }
}
