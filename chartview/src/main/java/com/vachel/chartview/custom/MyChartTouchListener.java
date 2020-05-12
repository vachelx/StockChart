package com.vachel.chartview.custom;

import android.graphics.Matrix;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * getTrans(float x, float y)的xy值即为缩放中心点；
 * TRANS_EDGE_LEFT模式下缩放将会相对于0点（即左侧坐标轴）
 * TRANS_EDGE_RIGHT模式下缩放将会相对于右侧坐标轴
 * <p>
 * Created by jianglixuan on 2018/12/14
 */
public class MyChartTouchListener extends BarLineChartTouchListener {
    public static final int TRANS_EDGE_LEFT = 1;
    public static final int TRANS_EDGE_RIGHT = 2;
    public static final int TRANS_EDGE_FREE = 0;
    private ITransChangedListener mListener;

    public MyChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart, touchMatrix, dragTriggerDistance);
    }

    public MyChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, ITransChangedListener listener) {
        this(chart, chart.getViewPortHandler().getMatrixTouch(), 3f);
        mListener = listener;
    }

    @Override
    public MPPointF getTrans(float x, float y) {
        if (mListener != null) {
            int transEdge = mListener.getTransEdge();
            if (transEdge == TRANS_EDGE_LEFT) {
                x = 0;
            } else if (transEdge == TRANS_EDGE_RIGHT) {
                x = mChart.getWidth();
            }
        }
        return super.getTrans(x, y);
    }

    public interface ITransChangedListener {
        int getTransEdge();
    }
}
