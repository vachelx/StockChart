package com.vachel.chartview.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;
import com.vachel.chartview.util.SpannableStringFormat;
import com.vachel.chartview.util.SwitchUtils;

import java.util.List;

/**
 * Created by jianglixuan on 2018/11/15
 */
public class MasterChartRenderer extends CombinedChartRenderer {
    private CombineChartCallback mCallback;
    private float mVLength;
    private Paint mHLValuePaint;
    private float mHLTextSize;
    private Paint mPriceRenderPaint;

    public MasterChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        init();
        initDefaultValues();
    }

    private void init() {
        mPriceRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPriceRenderPaint.setStyle(Paint.Style.STROKE);
        mPriceRenderPaint.setStrokeWidth(1);
        mPriceRenderPaint.setColor(Color.rgb(97, 130, 255));
        mPriceRenderPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        mPriceRenderPaint.setTextSize(30);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        super.drawHighlighted(c, indices);
        Bitmap bitmap;
        if (mCallback == null || (bitmap = mCallback.getCrossBitmap()) == null) {
            return;
        }
        for (Highlight high : indices) {
            float drawX = high.getDrawX();
            float drawY = high.getDrawY();
            if (bitmap.isRecycled()) {
                return;
            }
            // ui给的图片不是居中的。。。 中心点比例2/5
            c.drawBitmap(bitmap, drawX - bitmap.getWidth() / 2, drawY - bitmap.getHeight() * 2 / 5, mDrawPaint);
        }
    }

    public void setCombineCallback(CombineChartCallback callback) {
        mCallback = callback;
    }

    public interface CombineChartCallback {
        Bitmap getCrossBitmap();
        void setParameterText(CharSequence text);
    }

    private void initDefaultValues() {
        mVLength = Utils.convertDpToPixel(15f);
        mHLTextSize = Utils.convertDpToPixel(10f);
    }

    @Override
    public void drawValues(Canvas c) {
        super.drawValues(c);
        Chart chart = mChart.get();
        if (!(chart instanceof CombinedChart)) {
            return;
        }
        CombinedChart combinedChart = (CombinedChart) chart;
        CandleData candleData = combinedChart.getCandleData();
        // 此时为k线模式
        if (candleData != null) {
            ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);
            if (candleDataSet instanceof CandleDataSet && candleDataSet.isVisible()) {
                Transformer trans = combinedChart.getTransformer(candleDataSet.getAxisDependency());
                float[] minMaxFloat = getMinMaxFloat(combinedChart, ((CandleDataSet) candleDataSet).getValues());
                drawLowOrHighValue(new float[]{minMaxFloat[0], minMaxFloat[1]}, true, trans, c);
                drawLowOrHighValue(new float[]{minMaxFloat[2], minMaxFloat[3]}, false, trans, c);
                return;
            }
        }

        // 分时模式   (分时模式暂时去掉最高最低点）
//        LineData lineData = combinedChart.getLineData();
//        if (lineData == null) {
//            return;
//        }
//        List<ILineDataSet> dataSets = lineData.getDataSets();
//        for (ILineDataSet lineDataSet : dataSets) {
//            LineDataSet set = (LineDataSet) lineDataSet;
//            if (set != null) {
//                List<Entry> values = set.getValues();
//                // 分时模式下有多条数据线，均线无需绘制最值
//                if (values.get(0).getData() != null) {
//                    Transformer trans = combinedChart.getTransformer(set.getAxisDependency());
//                    float[] minMaxFloat = getMinMaxFloat(combinedChart, values);
//                    drawLowOrHighValue(new float[]{minMaxFloat[0], minMaxFloat[1]}, true, trans, c);
//                    drawLowOrHighValue(new float[]{minMaxFloat[2], minMaxFloat[3]}, false, trans, c);
//                }
//            }
//        }
    }

    private void drawLowOrHighValue(float[] targetPoint, boolean isLow, Transformer trans, Canvas c) {
        Paint paint = getPaint();
        paint.setColor(ResourceUtils.getThemeColorReverse());
        MPPointD point = trans.getPixelForValues(targetPoint[0], targetPoint[1]);
        float x = (float) point.x;
        float y = (float) point.y;
        String text = targetPoint[1] + "";
        float textWidth = paint.measureText(text);

        float targetX = x - textWidth / 2;
        if (targetX < 0) {
            targetX = 0;
        }
        if (targetX + textWidth > mViewPortHandler.contentRight()) {
            targetX = mViewPortHandler.contentRight() - textWidth;
        }

        c.drawText(text, targetX, isLow ? y + mVLength : y - mVLength < mVLength ? mVLength : y - mVLength, paint);
    }

    @NonNull
    private Paint getPaint() {
        if (mHLValuePaint == null) {
            mHLValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mHLValuePaint.setTextSize(mHLTextSize);
        }
        return mHLValuePaint;
    }

    /**
     * 可见范围内的最值获取
     *
     * @param lists
     * @return entry[lowX, lowY, highX, highY]
     */
    private float[] getMinMaxFloat(CombinedChart combinedChart, List<? extends Entry> lists) {
        int highestVisibleX = (int) combinedChart.getHighestVisibleX();
        int lowestVisibleX = (int) combinedChart.getLowestVisibleX();
        float[] result = new float[4];

        Entry lowestEntry = lists.get(lowestVisibleX);
        // k线模式下，最值使用getLow和getHigh处理
        if (lowestEntry instanceof CandleEntry) {
            CandleEntry candleEntry = (CandleEntry)lowestEntry;
            result[0] = candleEntry.getX();
            result[1] = candleEntry.getLow();
            result[2] = candleEntry.getX();
            result[3] = candleEntry.getHigh();

        for (int i = lowestVisibleX + 1; i <= highestVisibleX && i < lists.size(); i++) {
            candleEntry = (CandleEntry) lists.get(i);
            if (result[1] > candleEntry.getLow()) {
                result[0] = candleEntry.getX();
                result[1] = candleEntry.getLow();
            }
            if (result[3] < candleEntry.getHigh()) {
                result[2] = candleEntry.getX();
                result[3] = candleEntry.getHigh();
            }
        }
        return result;
        }

        // 分时模式下，最值使用getY处理，即昨收值
        result[0] = lowestEntry.getX();
        result[1] = lowestEntry.getY();
        result[2] = lowestEntry.getX();
        result[3] = lowestEntry.getY();

        for (int i = lowestVisibleX + 1; i <= highestVisibleX && i < lists.size(); i++) {
            Entry tmpEntry = lists.get(i);
            if (result[1] > tmpEntry.getY()) {
                result[0] = tmpEntry.getX();
                result[1] = tmpEntry.getY();
            }
            if (result[3] < tmpEntry.getY()) {
                result[2] = tmpEntry.getX();
                result[3] = tmpEntry.getY();
            }
        }
        return result;
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
        Chart chart = mChart.get();
        if (!(chart instanceof CombinedChart)) {
            return;
        }
        CombinedChart combinedChart = (CombinedChart)chart;

        int highLightX = -1;
        // 绘制高亮超出限制的范围
        Highlight[] indices = chart.getHighlighted();
        if (indices != null) {
            for (Highlight high : indices) {
                float drawX = high.getDrawX();
                mHighlightPaint.setColor(Color.GRAY);
                mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(Constant.CHART_HIGH_LINE_WIDTH));
                mHighlightPaint.setPathEffect(new DashPathEffect(new float[]{10, 15}, 0));
                Path path = new Path();
                path.reset();
                path.moveTo(drawX, mViewPortHandler.contentBottom());
                path.lineTo(drawX, chart.getHeight() - mViewPortHandler.offsetBottom() - chart.getPaddingBottom() - chart.getExtraBottomOffset());
                c.drawPath(path, mHighlightPaint);
                highLightX = (int) high.getX();
            }
        }

        // 绘制实时价格线
        if (SwitchUtils.isEnableDrawPrice()) {
            onDrawCurrentPrice(c, combinedChart);
        }

        // 绘制昨收基准线
        if (SwitchUtils.isEnableDrawLastClose()) {
            onDrawLastClose(c, combinedChart);
        }

        // 绘制指标线的指示值， 有高亮时显示高亮的对应值
        onDrawIndicatorLabel(combinedChart, highLightX);
    }

    private void onDrawIndicatorLabel(CombinedChart combinedChart, int highLightX) {
        LineData lineData = combinedChart.getLineData();
        if (lineData == null) {
            return;
        }

        SpannableStringFormat.Build build = SpannableStringFormat.createBuild();
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
                            // case 高亮时高亮点没有对应指标值
                            continue;
                        }
                    }
                    // case 无高亮时显示屏幕中最后一个点的指标值
                    if (index >= values.size() || index < 0) {
                        index = Math.min((int) (combinedChart.getHighestVisibleX() - x), values.size() - 1);
                    }
                    Entry entry = values.get(index);
                    String text = label + ": " + entry.getY()+"  ";
                    build.addSpannable(text, set.getColor());
                }
            }
        }
        if (mCallback != null) {
            mCallback.setParameterText(build.build());
        }
    }

    private void onDrawCurrentPrice(Canvas c, CombinedChart combinedChart) {
        CandleData candleData = combinedChart.getCandleData();
        if (candleData != null) {
            for (ICandleDataSet set : candleData.getDataSets()) {
                if (set.isVisible()) {
                    int xEnd = (int) candleData.getXMax();
                    float highestVisibleX = combinedChart.getHighestVisibleX();
                    float currentPrice = SwitchUtils.getCurrentPrice();
                    float price = currentPrice < 0 ? set.getEntryForIndex(xEnd).getClose(): currentPrice;
                    float[] shadowBuffers = new float[]{xEnd, price, highestVisibleX, price};
                    drawPriceLine(c, combinedChart, price, shadowBuffers);
                    return;
                }
            }
        }

        // 分时模式
        LineData lineData = combinedChart.getLineData();
        if (lineData == null) {
            return;
        }
        List<ILineDataSet> dataSets = lineData.getDataSets();
        for (ILineDataSet lineDataSet : dataSets) {
            LineDataSet set = (LineDataSet) lineDataSet;
            if (set != null && set.isVisible()) {
                List<Entry> values = set.getValues();
                // 分时模式下有多条数据线
                if (values.size() > 0 && values.get(0).getData() != null) {
                    int xEnd = (int) set.getXMax();
                    float highestVisibleX = combinedChart.getHighestVisibleX();
                    float currentPrice = SwitchUtils.getCurrentPrice();
                    float price = currentPrice == -1 ? set.getEntryForIndex(xEnd).getY() : currentPrice;
                    float[] shadowBuffers = new float[]{xEnd, price, highestVisibleX, price};
                    drawPriceLine(c, combinedChart, price, shadowBuffers);
                    return;
                }
            }
        }
    }

    private void onDrawLastClose(Canvas c, CombinedChart combinedChart) {
        CandleData candleData = combinedChart.getCandleData();
        if (candleData != null) {
            for (ICandleDataSet set : candleData.getDataSets()) {
                if (set.isVisible()) {
                    drawLastCloseLine(c, combinedChart);
                    return;
                }
            }
        }
        LineData lineData = combinedChart.getLineData();
        if (lineData == null) {
            return;
        }
        List<ILineDataSet> dataSets = lineData.getDataSets();
        for (ILineDataSet lineDataSet : dataSets) {
            LineDataSet set = (LineDataSet) lineDataSet;
            if (set != null && set.isVisible()) {
                drawLastCloseLine(c, combinedChart);
                return;
            }
        }
    }

    private void drawLastCloseLine(Canvas c, CombinedChart combinedChart) {
        float lastClose = SwitchUtils.getLastClose();
        if (lastClose < 0) {
            return;
        }
        float[] shadowBuffers = new float[]{0, lastClose, 0, lastClose};
        ViewPortHandler viewPortHandler = combinedChart.getViewPortHandler();
        mPriceRenderPaint.setColor(ResourceUtils.getHighLineColor());
        Transformer trans = combinedChart.getTransformer(YAxis.AxisDependency.LEFT);
        trans.pointValuesToPixel(shadowBuffers);

        Path path = new Path();
        path.moveTo(viewPortHandler.contentLeft(), shadowBuffers[1]);
        path.lineTo(viewPortHandler.contentRight(), shadowBuffers[3]);
        mPriceRenderPaint.setPathEffect(new DashPathEffect(new float[]{6, 2, 6, 8}, 0));
        mPriceRenderPaint.setStyle(Paint.Style.STROKE);
        c.drawPath(path, mPriceRenderPaint);
    }

    private void drawPriceLine(Canvas c, CombinedChart combinedChart, float price, float[] shadowBuffers) {
        ViewPortHandler viewPortHandler = combinedChart.getViewPortHandler();
        float chartWidth = viewPortHandler.getChartWidth();
        mPriceRenderPaint.setColor(Color.rgb(97, 130, 255));
        mPriceRenderPaint.setTextSize(30);
        String text = price + "";
        Rect rect = new Rect();
        mPriceRenderPaint.getTextBounds(text, 0, text.length(), rect);

        Transformer trans = combinedChart.getTransformer(YAxis.AxisDependency.LEFT);
        trans.pointValuesToPixel(shadowBuffers);
        if (shadowBuffers[2] - rect.width() - shadowBuffers[0]< 15) {
            shadowBuffers[0] = viewPortHandler.contentLeft();
        }

        Path path = new Path();
        path.moveTo(shadowBuffers[0], shadowBuffers[1]);
        path.lineTo(chartWidth - rect.width(), shadowBuffers[3]);
        mPriceRenderPaint.setPathEffect(new DashPathEffect(new float[]{6, 8}, 0));
        mPriceRenderPaint.setStyle(Paint.Style.STROKE);
        c.drawPath(path, mPriceRenderPaint);

        mPriceRenderPaint.setPathEffect(null);
        mPriceRenderPaint.setStyle(Paint.Style.FILL);
        c.drawText(text, chartWidth - rect.width() - 10, shadowBuffers[1] + rect.height() / 2, mPriceRenderPaint);
    }
}
