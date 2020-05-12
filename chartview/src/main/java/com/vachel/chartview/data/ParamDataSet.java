package com.vachel.chartview.data;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.vachel.chartview.util.Constant;

import java.util.List;

/**
 * Created by jianglixuan on 2019/7/8
 *
 * 指标参数曲线
 */
public class ParamDataSet extends LineDataSet {
    public ParamDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
        init();
    }

    private void init() {
        setLineWidth(Constant.CHART_HIGH_LINE_WIDTH);
        setDrawFilled(false);
        setMode(Mode.CUBIC_BEZIER);
        setDrawIcons(false);
        setDrawValues(false);
        setDrawCircles(false);
        setHighlightEnabled(false);
    }
}
