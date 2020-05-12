package com.vachel.chartview.data;

import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;
import com.vachel.chartview.util.SwitchUtils;

import java.util.List;

/**
 * Created by jianglixuan on 2019/5/29
 *
 * 主图
 *
 */
public class MyCandleDataSet extends CandleDataSet {

    public MyCandleDataSet(List<CandleEntry> yVals, String label) {
        super(yVals, label);
        initDataSet();
    }

    private void initDataSet() {
        setDrawIcons(false);
        setDrawValues(false);
        setHighLightColor(ResourceUtils.getHighLineColor());
        setHighlightLineWidth(Constant.CHART_HIGH_LINE_WIDTH);
        enableDashedHighlightLine(5, 15, 0);
        setShadowColor(Color.DKGRAY);
        setShadowColorSameAsCandle(true);
        setShadowWidth(0.7f);
        setDecreasingPaintStyle(Paint.Style.FILL);
        setIncreasingPaintStyle(Paint.Style.FILL);
        updateRiseFallColor();
    }

    @Override
    protected void calcMinMaxY(CandleEntry e) {
        super.calcMinMaxY(e);
        if (SwitchUtils.isEnableDrawPrice()) {
            float price = SwitchUtils.getCurrentPrice();
            // 未设置价格时以股线图最后一个点的收盘价为准
            if (price < 0) {
                CandleEntry candleEntry = mValues.get(mValues.size() - 1);
                price = candleEntry.getClose();
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

    public void updateRiseFallColor() {
        setDecreasingColor(ResourceUtils.getColorFall());
        setIncreasingColor(ResourceUtils.getColorRise());
        setNeutralColor(ResourceUtils.getColorRise());
    }
}
