package com.vachel.chartview.data;

import android.text.TextUtils;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.vachel.chartview.bean.KLineData;
import com.vachel.chartview.util.ResourceUtils;

import java.util.List;

/**
 * 副图
 */

public class MyBarDataSet extends BarDataSet {
    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
        initDataSet();
    }

    private void initDataSet() {
        setHighLightAlpha(80);
        updateRiseFallColor();
    }

    public void updateRiseFallColor() {
        setColors(ResourceUtils.getBarColorRise(), ResourceUtils.getBarColorFall(), ResourceUtils.getBarColorRise());
    }

    /**
     * 获取k线图barChart柱状图中的颜色
     * @param index
     * @return
     */
    @Override
    public int getColor(int index) {
        int colorInt = 0;
        BarEntry barEntry = getEntryForIndex(index);
        if (barEntry != null) {
            KLineData data = (KLineData) barEntry.getData();
            String price = data.getPrice();
            if (TextUtils.isEmpty(price)) {
                colorInt = data.getOpen().equals(data.getClose()) ? 2 :
                        Float.valueOf(data.getClose()) > Float.valueOf(data.getOpen()) ? 0 : 1;
            } else {
                String lastPrice = index > 0 ? ((KLineData) getEntryForIndex(index - 1).getData()).getPrice() : data.getClose();
                colorInt = price.equals(lastPrice) ? 2 : Float.valueOf(price) > Float.valueOf(lastPrice) ? 0 : 1;
            }
        }
        if (mColors.size() >= 3) {
            // colors 0涨， 1跌， 2不变
            return mColors.get(colorInt);
        } else {
            return super.getColor(index);
        }
    }
}
