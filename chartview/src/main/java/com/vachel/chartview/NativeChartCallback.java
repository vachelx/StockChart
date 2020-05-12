package com.vachel.chartview;

import com.vachel.chartview.bean.KLineData;

/**
 * Created by jianglixuan on 2019/7/5
 */
public interface NativeChartCallback {
    void onHighLine(KLineData data);

    void onMultiTouch(boolean isMulti);

    void onChangeOrientation();
}
