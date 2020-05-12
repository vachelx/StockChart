package com.vachel.chartview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.vachel.chartview.bean.KLineData;
import com.vachel.chartview.custom.BarAXisValueFormatter;
import com.vachel.chartview.custom.SecondaryArithmeticControl;
import com.vachel.chartview.data.MyBarDataSet;
import com.vachel.chartview.renderer.BarXAxisRenderer;
import com.vachel.chartview.renderer.SecondaryChartRenderer;
import com.vachel.chartview.util.Arguments;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianglixuan on 2019/7/5
 * 用于绘制成交量的组合控件
 */
public class MyBarCombineChart extends CombinedChart implements BarXAxisRenderer.IXAxisRendererCallback, SecondaryChartRenderer.CombineChartCallback {

    private OnTextChangedListener mTextListener;
    private List<CandleEntry> mCandleEntries;
    private List<BarEntry> mBarEntries;
    private KLineData mHighlightData;
    private Highlight mhighlighted;

    public MyBarCombineChart(Context context) {
        super(context);
        initBar();
    }

    public MyBarCombineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBar();
    }

    public MyBarCombineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBar();
    }

    private void initBar() {
        BarXAxisRenderer xAxisRenderer = new BarXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);
        xAxisRenderer.getPaintAxisLabels().setColor(ResourceUtils.getLabelColor());
        setXAxisRenderer(xAxisRenderer);

        setScaleYEnabled(false);
        setDrawBorders(false);
        getLegend().setEnabled(false);
        setDrawGridBackground(false);
        setDragEnabled(true);
        setDragDecelerationFrictionCoef(Constant.DEFAULT_CHART_FRICTION_COEF);
        setAutoScaleMinMaxEnabled(true);
        Description description = new Description();
        description.setEnabled(false);
        setDescription(description);
        setTouchEnabled(true);
        setDoubleTapToZoomEnabled(false);
        XAxis xAxis = getXAxis();
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ResourceUtils.getLabelColor());
        xAxis.setLabelCount(5, true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisLineColor(ResourceUtils.getGridColor());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        setNoDataText("");
        setHighlightPerTapEnabled(false);
        YAxis axisLeft = getAxisLeft();
        axisLeft.setLabelCount(3, true);
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisLineColor(Color.TRANSPARENT);
        axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        YAxis axisRight = getAxisRight();
        axisRight.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisRight.setEnabled(false);
        SecondaryChartRenderer chartRenderer = new SecondaryChartRenderer(this, mAnimator, mViewPortHandler);
        chartRenderer.setCombineCallback(this);
        setRenderer(chartRenderer);
        setMinOffset(0);
        setExtraOffsets(Constant.EXTRA_CHART_HOR_OFFSET, 0, Constant.EXTRA_CHART_HOR_OFFSET, 2);
        setDrawOrder(new DrawOrder[]{DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.SCATTER, DrawOrder.CANDLE, DrawOrder.LINE});
    }

    @Override
    public String getDateForHighlight(Highlight highlight) {
        if (mhighlighted != null) {
            IAxisValueFormatter valueFormatter = mXAxis.getValueFormatter();
            if (valueFormatter instanceof BarAXisValueFormatter) {
                return ((BarAXisValueFormatter) valueFormatter).formatLabelTime((int) mhighlighted.getX(), mHighlightData);
            }
        }
        return "";
    }

    public void highlightDef(Highlight h, KLineData data) {
        mHighlightData = data;
        mhighlighted = h;
    }

    @Override
    public Highlight getHighlightDef() {
        return mhighlighted;
    }

    public void onThemeChanged() {
        getRendererXAxis().getPaintAxisLabels().setColor(ResourceUtils.getLabelColor());
        getXAxis().setTextColor(ResourceUtils.getLabelColor());
        getXAxis().setAxisLineColor(ResourceUtils.getGridColor());
        invalidate();
    }

    public void setSourceEntries(List<CandleEntry> candleEntries, List<BarEntry> barEntries) {
        mCandleEntries = candleEntries;
        mBarEntries = barEntries;
    }

    public void onArithmeticChanged(int type) {
        LineData lineData = getLineData();
        if (lineData == null) return;
        List<ILineDataSet> dataSets = lineData.getDataSets();
        // 清除所有DataSet
        if (dataSets != null) {
            List<ILineDataSet> markDataSets = new ArrayList<>();
            for (ILineDataSet dataSet : dataSets) {
                if (!TextUtils.isEmpty(dataSet.getLabel())) {
                    markDataSets.add(dataSet);
                }
            }
            if (markDataSets.size() != 0) {
                dataSets.removeAll(markDataSets);
            }
        }
        BarData barData = getBarData();
        if (barData == null) return;
        List<IBarDataSet> barDataSets = barData.getDataSets();
        if (barDataSets != null) {
            barDataSets.clear();
        }
        //
        SecondaryArithmeticControl.showParamLine(type, mCandleEntries, lineData, mBarEntries, getBarData());
        updateAxisMin(type);
        getData().calcMinMax();
        invalidate();
    }

    public void updateBarDataRiseFallColor() {
        BarData barData = getBarData();
        if (barData == null) return;
        List<IBarDataSet> dataSets = barData.getDataSets();
        if (dataSets == null) return;
        for (IBarDataSet dataSet : dataSets) {
            if (dataSet instanceof MyBarDataSet) {
                ((MyBarDataSet) dataSet).updateRiseFallColor();
                invalidate();
            }
        }
    }

    public void updateAxisMin(int type) {
        if (type == Arguments.SECONDARY_TYPE_VOL) {
            getAxisLeft().setAxisMinimum(0);
        } else {
            getAxisLeft().resetAxisMinimum();
        }
    }

    @Override
    public void setParameterText(CharSequence text) {
        if (mTextListener!=null) {
            mTextListener.onTextChanged(text);
        }
    }

    public void setOnTextChangedListener(OnTextChangedListener listener){
        mTextListener = listener;
    }

}
