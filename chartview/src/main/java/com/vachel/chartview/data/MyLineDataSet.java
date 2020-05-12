package com.vachel.chartview.data;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;
import com.vachel.chartview.util.SwitchUtils;

import java.util.List;

/**
 * Created by jianglixuan on 2019/5/30
 *
 * 分时线
 *
 */
public class MyLineDataSet extends LineDataSet {
    public MyLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
        init();
    }

    private void init() {
        setMode(Mode.HORIZONTAL_BEZIER);
        setDrawIcons(false);
        setDrawValues(false);
        setDrawCircles(false);
        setHighlightEnabled(true);
        setLineWidth(Constant.CHART_LINE_WIDTH);
        setDrawFilled(true);
        setHighLightColor(ResourceUtils.getHighLineColor());
        setHighlightLineWidth(Constant.CHART_HIGH_LINE_WIDTH);
        enableDashedHighlightLine(5, 15, 0);
    }

    @Override
    protected void calcMinMaxY(Entry e) {
        super.calcMinMaxY(e);
        // 处理视图中不可见价格线情况
        if (SwitchUtils.isEnableDrawPrice()) {
            float price = SwitchUtils.getCurrentPrice();
            // 未设置价格时以股线图最后一个点的收盘价为准
            if (price < 0) {
                Entry entry = mValues.get(mValues.size() - 1);
                price = entry.getY();
            }
            if (price < mYMin)
                mYMin = price;

            if (price > mYMax)
                mYMax = price;
        }

        if (SwitchUtils.isEnableDrawLastClose()) {
            float close = SwitchUtils.getLastClose();
            if (close < 0) return;
            if (close < mYMin)
                mYMin = close;

            if (close > mYMax)
                mYMax = close;
        }
    }
}
