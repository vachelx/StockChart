package com.vachel.chartview.custom;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.vachel.chartview.data.ParamDataSet;
import com.vachel.chartview.util.Arguments;
import com.vachel.chartview.util.Arithmetic;
import com.vachel.chartview.util.ResourceUtils;

import java.util.List;

/**
 * Created by jianglixuan on 2019/7/30
 *
 * 主图指标切换管理类
 *
 */
public class MasterArithmeticControl {

    public static void showParamLine(int type, List candleEntries, LineData lineData) {
        if (candleEntries == null || candleEntries.isEmpty()) {
            return;
        }
        switch (type){
            case Arguments.MASTER_TYPE_MA:
                int[] maArgus = Arguments.KINDS_MASTER_MA;
                for (int i = 0; i < maArgus.length; i++) {
                    List<Entry> maList = Arithmetic.getMA(candleEntries, maArgus[i]);
                    LineDataSet ma = new ParamDataSet(maList, "MA" + maArgus[i]);
                    ma.setColor(ResourceUtils.getParameterLineColors()[i]);
                    lineData.addDataSet(ma);
                }
                break;
            case Arguments.MASTER_TYPE_EMA:
                int[] emaArgus = Arguments.KINDS_MASTER_EMA;
                for (int i = 0; i < emaArgus.length; i++) {
                    List emaList = Arithmetic.getEMA(candleEntries, emaArgus[i]);
                    LineDataSet emaData = new ParamDataSet(emaList, "EMA"+emaArgus[i]);
                    emaData.setColor(ResourceUtils.getParameterLineColors()[i]);
                    lineData.addDataSet(emaData);
                }
                break;
            case Arguments.MASTER_TYPE_BOLL:
                List[] mb = Arithmetic.getMB(candleEntries, Arguments.KINDS_MASTER_BOLL[0], Arguments.KINDS_MASTER_BOLL[1]);
                for (int i = 0; i< mb.length; i++) {
                    List<Entry> list = mb[i];
                    LineDataSet dataSet = new ParamDataSet(list, i == 0 ? "BOLL" : i == 1 ? "UB" : "LB");
                    dataSet.setColor(ResourceUtils.getParameterLineColors()[i]);
                    lineData.addDataSet(dataSet);
                }
                break;
        }
    }
}
