package com.vachel.chartview.data.entity;

import com.github.mikephil.charting.data.CandleEntry;

/**
 * Created by jianglixuan on 2019/7/8
 *
 */
public class MyCandleEntry extends CandleEntry implements SuitEntry {
    public MyCandleEntry(float x, float shadowH, float shadowL, float open, float close, Object data) {
        super(x, shadowH, shadowL, open, close, data);
    }

    @Override
    public float getValue() {
        return getClose();
    }
}
