package com.vachel.chartview.data.entity;

import com.github.mikephil.charting.data.Entry;
import com.vachel.chartview.bean.KLineData;

/**
 * Created by jianglixuan on 2019/7/8
 * 便于参数指标计算是自动获取合适的字段
 *
 */
public class MyLineEntry extends Entry implements SuitEntry {
    public MyLineEntry(int i, Float aFloat, KLineData data) {
        super(i, aFloat, data);
    }

    @Override
    public float getValue() {
        return getY();
    }
}
