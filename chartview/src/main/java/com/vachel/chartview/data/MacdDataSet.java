package com.vachel.chartview.data;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.vachel.chartview.util.ResourceUtils;

import java.util.List;

/**
 * Created by jianglixuan on 2019/7/30
 *
 * MACD指标参数曲线
 */
public class MacdDataSet extends BarDataSet {
    public MacdDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
        init();
    }

    private void init() {
        setDrawIcons(false);
        setDrawValues(false);
        setHighlightEnabled(false);
        setHighLightAlpha(80);
        updateRiseFallColor();
    }

    @Override
    public int getColor(int index) {
        BarEntry barEntry = getEntryForIndex(index);
        return mColors.get(barEntry.getY() >= 0 ? 0 : 1);
    }

    public void updateRiseFallColor() {
        setColors(ResourceUtils.getBarColorRise(), ResourceUtils.getBarColorFall());
    }
}
