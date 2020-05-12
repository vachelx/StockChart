package com.vachel.chartview.renderer;

import android.graphics.Canvas;
import android.text.TextUtils;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.vachel.chartview.util.FormatUtils;
import com.vachel.chartview.util.SpannableStringFormat;

import java.util.List;

/**
 * Created by jianglixuan on 2019/7/5
 */
public class SecondaryChartRenderer extends CombinedChartRenderer {

    private CombineChartCallback mCallback;

    public SecondaryChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public interface CombineChartCallback {
        void setParameterText(CharSequence text);
    }

    public void setCombineCallback(CombineChartCallback callback) {
        mCallback = callback;
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
        Chart chart = mChart.get();
        if (!(chart instanceof CombinedChart)) {
            return;
        }
        CombinedChart combinedChart = (CombinedChart) chart;

        int highLightX = -1;
        Highlight[] indices = chart.getHighlighted();
        if (indices != null) {
            for (Highlight high : indices) {
                highLightX = (int) high.getX();
            }
        }

        // 绘制指标线的指示值， 有高亮时显示高亮的对应值
        onDrawIndicatorLabel(combinedChart, highLightX);
    }

    private void onDrawIndicatorLabel(CombinedChart combinedChart, int highLightX) {
        if (mCallback == null) return;
        SpannableStringFormat.Build build = SpannableStringFormat.createBuild();

        LineData lineData = combinedChart.getLineData();
        if (lineData != null) {
            for (ILineDataSet dataSet : lineData.getDataSets()) {
                LineDataSet set = (LineDataSet) dataSet;
                String label = set.getLabel();
                if (set.isVisible() && !TextUtils.isEmpty(label)) {
                    List<Entry> values = set.getValues();
                    if (values != null && values.size() > 0) {
                        float x = values.get(0).getX();
                        int index = highLightX;
                        if (index != -1) {
                            index -= x;
                            if (index < 0) {
                                continue;
                            }
                        }
                        if (index >= values.size() || index < 0) {
                            index = Math.min((int) (combinedChart.getHighestVisibleX() - x), values.size() - 1);
                        }
                        Entry entry = values.get(index);
                        String text = label + ": " + entry.getY() + "  ";
                        build.addSpannable(text, set.getColor());
                    }
                }
            }
        }

        BarData barData = combinedChart.getBarData();
        if (barData != null) {
            IBarDataSet barDataSet = barData.getDataSetByIndex(0);
            if (barDataSet != null && barDataSet.isVisible() && barDataSet.getValueTextSize() > 0) {
                BarEntry entry = barDataSet.getEntryForIndex(0);
                int entryCount = barDataSet.getEntryCount();
                boolean noData = false;
                float x = entry.getX();
                int index = highLightX;
                if (index != -1) {
                    index -= x;
                    if (index < 0) {
                        noData = true;
                    }
                }
                if (index >= entryCount || index < 0) {
                    index = Math.min((int) (combinedChart.getHighestVisibleX() - x), entryCount - 1);
                }
                if (!noData) {
                    Entry tarEntry = barDataSet.getEntryForIndex(index);
                    String text = barDataSet.getLabel() + ": " + FormatUtils.formatFloatNumber(tarEntry.getY()) + "  ";
                    build.addSpannable(text, barDataSet.getColor(index));
                }
            }
        }
        mCallback.setParameterText(build.build());
    }
}
