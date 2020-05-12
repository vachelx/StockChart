package com.vachel.chartview.renderer;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.vachel.chartview.custom.BarAXisValueFormatter;
import com.vachel.chartview.util.ResourceUtils;

/**
 * 每次绘制操作判断绘制范围，从而根据时间跨度来决定横轴时间显示格式
 * <p>
 */
public class BarXAxisRenderer extends XAxisRenderer {

    private Paint mMarkLabelPaint;
    private IXAxisRendererCallback mCallback;
    private MPPointF mPointF;

    public BarXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    public BarXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, IXAxisRendererCallback callback) {
        super(viewPortHandler, xAxis, trans);
        mCallback = callback;
    }

    public void drawMarkLabels(Canvas c) {
        if (mCallback != null) {
            Highlight highlighted = mCallback.getHighlightDef();
            if (highlighted == null) {
                return;
            }
            String text = mCallback.getDateForHighlight(highlighted);
            if (TextUtils.isEmpty(text)) {
                return;
            }
            float drawX = highlighted.getDrawX();
            float labelY = mViewPortHandler.contentBottom();
            Paint markPaint = getMarkLabelPaint();
            markPaint.setColor(ResourceUtils.getThemeColorReverse());
            float width = markPaint.measureText(text);
            Paint paint = new Paint();
            paint.setColor(ResourceUtils.getThemeColor());
            paint.setStyle(Paint.Style.FILL);
            c.drawRect(drawX - width / 2, labelY + 1, drawX + width / 2, mCallback.getHeight(), paint);
            MPPointF pointF = getMPPointF();
            Utils.drawXAxisValue(c, text, drawX, labelY + mAxis.getYOffset(), markPaint, pointF, mXAxis.getLabelRotationAngle());
        }
    }

    private MPPointF getMPPointF() {
        if (mPointF == null) {
            mPointF = MPPointF.getInstance(0, 0);
            mPointF.x = 0.5f;
            mPointF.y = 0.0f;
        }
        return mPointF;
    }

    private Paint getMarkLabelPaint() {
        if (mMarkLabelPaint == null) {
            mMarkLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mMarkLabelPaint.setTextSize(mAxisLabelPaint.getTextSize());
            mMarkLabelPaint.setTextAlign(Paint.Align.CENTER);
        }
        return mMarkLabelPaint;
    }

    @Override
    public void renderAxisLabels(Canvas c) {
        ValueFormatter valueFormatter = mXAxis.getValueFormatter();
        if (valueFormatter instanceof BarAXisValueFormatter) {
            ((BarAXisValueFormatter) valueFormatter).needUpdateValueRange();
        }
        super.renderAxisLabels(c);
        drawMarkLabels(c);
    }

    public interface IXAxisRendererCallback {
        Highlight getHighlightDef();

        int getHeight();

        String getDateForHighlight(Highlight highlight);
    }
}
