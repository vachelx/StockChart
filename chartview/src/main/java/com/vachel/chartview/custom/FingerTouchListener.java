package com.vachel.chartview.custom;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.highlight.Highlight;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;


public class FingerTouchListener implements View.OnTouchListener {
    private BarLineChartBase mChart;
    private GestureDetector mDetector;
    private TouchCallback mListener;
    private boolean mIsLongPress = false;

    public FingerTouchListener(BarLineChartBase chart, TouchCallback listener) {
        mChart = chart;
        mListener = listener;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                if (mListener != null) {
                    mListener.enableHighlight();
                }
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h != null && h.getDataIndex() >= 0) {
                    h.setDraw(e.getX(), e.getY());
                    mChart.highlightValue(h, true);
                    mChart.disableScroll();
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mListener != null) {
                    long timeMillis = System.currentTimeMillis();
                    if (timeMillis - ResourceUtils.getDoubleTapTime() > Constant.DOUBLE_TAP_MIN_DURATION) {
                        mListener.onDoubleTap();
                    }
                    ResourceUtils.setDoubleTapTime(timeMillis);
                }
                return super.onDoubleTap(e);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mIsLongPress = false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mIsLongPress = false;
            mChart.highlightValue(null, true);
            if (mListener != null) {
                mListener.disableHighlight();
            }
            mChart.enableScroll();
        }
        if (mIsLongPress && event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mListener != null) {
                mListener.enableHighlight();
            }
            Highlight h = mChart.getHighlightByTouchPoint(event.getX(), event.getY());
            if (h != null && h.getDataIndex() >= 0) {
                h.setDraw(event.getX(), event.getY());
                mChart.highlightValue(h, true);
                mChart.disableScroll();
            }
            return true;
        }
        return false;
    }

    public interface TouchCallback {
        void enableHighlight();

        void disableHighlight();

        void onDoubleTap();
    }
}
