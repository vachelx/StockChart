package com.vachel.chartview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.vachel.chartview.bean.KLineData;
import com.vachel.chartview.custom.FingerTouchListener;
import com.vachel.chartview.custom.MyChartTouchListener;
import com.vachel.chartview.custom.MasterArithmeticControl;
import com.vachel.chartview.custom.SyncChartGestureListener;
import com.vachel.chartview.renderer.MasterChartRenderer;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;
import com.vachel.chartview.util.SwitchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianglixuan on 2019/7/5
 */
public class MyCombineChart extends CombinedChart implements FingerTouchListener.TouchCallback, MyChartTouchListener.ITransChangedListener, MasterChartRenderer.CombineChartCallback {

    private MasterChartRenderer mMasterChartRenderer;
    private Bitmap mCrossBitmap;
    private int mScaleEdge;
    private NativeChartCallback mNativeCallback;
    private OnTextChangedListener mTextListener;
    private List<CandleEntry> mCandleEntries;

    public MyCombineChart(Context context) {
        super(context);
        initMyChart();
    }

    public MyCombineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyChart();
    }

    public MyCombineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMyChart();
    }

    private void initMyChart() {
        setDrawGridBackground(false);
        setDrawBorders(false);
        setDragEnabled(true);
        setTouchEnabled(true);
        Description description = new Description();
        description.setEnabled(false);
        setDescription(description);
        setAutoScaleMinMaxEnabled(true);

        XAxis xAxis = getXAxis();
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        xAxis.setAxisLineColor(ResourceUtils.getGridColor());
        YAxis axisLeft = getAxisLeft();
        axisLeft.setSpaceBottom(2);
        axisLeft.setSpaceTop(5);
        axisLeft.setLabelCount(4, true);
//        axisLeft.setDrawGridLinesBehindData(false);
        axisLeft.setGridColor(ResourceUtils.getGridColor());
        axisLeft.setAxisLineColor(Color.TRANSPARENT);
        axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeft.setTextColor(ResourceUtils.getLabelColor());
        axisLeft.setYOffset(-5);
        getAxisRight().setEnabled(false);
        getAxisRight().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        getLegend().setEnabled(false);
        setScaleYEnabled(false);
        setDoubleTapToZoomEnabled(false);
        setNoDataText(getResources().getString(R.string.market_string_chart_data_null));
        setNoDataTextColor(Color.GRAY);
        setHighlightPerTapEnabled(false);
        setDragDecelerationFrictionCoef(Constant.DEFAULT_CHART_FRICTION_COEF);

        mMasterChartRenderer = new MasterChartRenderer(this, mAnimator, mViewPortHandler);
        mMasterChartRenderer.setCombineCallback(this);
        setRenderer(mMasterChartRenderer);
        setOnTouchListener(new MyChartTouchListener(this, this));
        setMinOffset(0);
        setExtraOffsets(Constant.EXTRA_CHART_HOR_OFFSET, 0, Constant.EXTRA_CHART_HOR_OFFSET, 2);
        setDrawOrder(new DrawOrder[]{DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.SCATTER, DrawOrder.CANDLE, DrawOrder.LINE});
        setDrawLastCloseEnable(false, false);
        setDrawPriceEnable(true, false);
    }



    @Override
    public void enableHighlight() {

    }

    @Override
    public void disableHighlight() {

    }

    @Override
    public void onDoubleTap() {
        if (mNativeCallback!=null) {
            mNativeCallback.onChangeOrientation();
        }
    }

    @Override
    public int getTransEdge() {
        return mScaleEdge;
    }

    public int setTransEdge(int edge) {
        return mScaleEdge = edge;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCrossBitmap != null) {
            mCrossBitmap.recycle();
            mCrossBitmap = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            mViewPortHandler.setChartDimens(w, h * 0.72f);
        }
    }

    @Override
    public Bitmap getCrossBitmap() {
        if (mCrossBitmap == null) {
            mCrossBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.stock_high_line_cross_icon);
        }
        return mCrossBitmap;
    }

    @Override
    public void setParameterText(CharSequence text) {
        if (mTextListener!=null) {
            mTextListener.onTextChanged(text);
        }
    }

    public void setSyncChart(final MyBarCombineChart syncChart, final NativeChartCallback callback) {
        mNativeCallback = callback;
        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                KLineData data = (KLineData) e.getData();
                if (callback != null) {
                    callback.onHighLine(data);
                }
                // 用于底部时间坐标显示
                syncChart.highlightDef(h, data);
                // 两个chart的dataIndex不同，坐标系页不同，需要修正highlight才能同步高亮
                Highlight highlight = new Highlight(h.getX(), Float.NaN, 0);
                highlight.setDataIndex(0);
                Highlight highlight1 = new Highlight(h.getX(), Float.NaN, 0);
                highlight1.setDataIndex(1);
                syncChart.highlightValues(new Highlight[]{highlight, highlight1});
            }

            @Override
            public void onNothingSelected() {
                if (callback != null) {
                    callback.onHighLine(null);
                }
                syncChart.highlightDef(null, null);
                syncChart.highlightValue(null);
            }
        });
        setOnChartGestureListener(new SyncChartGestureListener(this, new Chart[]{syncChart}));
        setOnTouchListener(new FingerTouchListener(this, this));
    }

    public void setCurrentPrice(float price) {
        if (SwitchUtils.isEnableDrawPrice()) {
            SwitchUtils.setCurrentPrice(price);
            invalidate();
        }
    }

    public void setLastClose(float price) {
        // 可能只更新一次
        SwitchUtils.setLastClose(price);
        invalidate();
    }

    public void onThemeChanged() {
        getAxisLeft().setTextColor(ResourceUtils.getLabelColor());
        getAxisLeft().setGridColor(ResourceUtils.getGridColor());
        getXAxis().setAxisLineColor(ResourceUtils.getGridColor());
        invalidate();
    }

    public void setOnTextChangedListener(OnTextChangedListener listener){
        mTextListener = listener;
    }

    public void setSourceEntries(List<CandleEntry> candleEntries) {
        mCandleEntries = candleEntries;
    }

    public void onArithmeticChanged(int type) {
        LineData lineData = getLineData();
        if (lineData == null) return;
        List<ILineDataSet> dataSets = lineData.getDataSets();
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
        MasterArithmeticControl.showParamLine(type, mCandleEntries, lineData);
        getData().calcMinMax();
        invalidate();
    }

    public void setDrawPriceEnable(boolean draw) {
        setDrawPriceEnable(draw, true);
    }

    public void setDrawPriceEnable(boolean draw, boolean invalidate) {
        SwitchUtils.setEnableDrawPrice(draw);
        if (invalidate) {
            invalidate();
        }
    }

    public void setDrawLastCloseEnable(boolean draw) {
        setDrawLastCloseEnable(draw, true);
    }

    public void setDrawLastCloseEnable(boolean draw, boolean invalidate) {
        SwitchUtils.setEnableDrawLastClose(draw);
        if (invalidate) {
            invalidate();
        }
    }
}
