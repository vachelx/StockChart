package com.vachel.chartview.custom;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.vachel.chartview.data.MacdDataSet;
import com.vachel.chartview.data.MyBarDataSet;
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
public class SecondaryArithmeticControl {

    public static void showParamLine(int type, List candleEntries, LineData lineData, List barEntries, BarData barData) {
        if (candleEntries == null || candleEntries.isEmpty() || barEntries == null || barEntries.isEmpty()) {
            return;
        }
        switch (type){
            case Arguments.SECONDARY_TYPE_MACD:
                List[] macd = Arithmetic.getMACD(candleEntries, Arguments.KINDS_SECONDARY_MACD[0], Arguments.KINDS_SECONDARY_MACD[1], Arguments.KINDS_SECONDARY_MACD[2]);
                for (int i = 0; i< macd.length - 1; i++) {
                    List<Entry> list = macd[i];
                    LineDataSet dataSet = new ParamDataSet(list, i == 0 ? "DIF" : i == 1 ? "DEA" : "MACD");
                    dataSet.setColor(ResourceUtils.getParameterLineColors()[i]);
                    lineData.addDataSet(dataSet);
                }
                List<BarEntry> list = macd[2];
                MacdDataSet macdSet = new MacdDataSet(list, "MACD");
                barData.addDataSet(macdSet);
                barData.setBarWidth(0.2f);
                break;
            case Arguments.SECONDARY_TYPE_VOL:
                barData.clearValues();
                MyBarDataSet barDataSet = new MyBarDataSet(barEntries, "VOL");
                barData.addDataSet(barDataSet);
                barData.setBarWidth(0.8f);
                barData.setDrawValues(false);
                break;
            case Arguments.SECONDARY_TYPE_KDJ:
                List[] kdj = Arithmetic.getKDJ(candleEntries, Arguments.KINDS_SECONDARY_KDJ[0], Arguments.KINDS_SECONDARY_KDJ[1], Arguments.KINDS_SECONDARY_KDJ[2], Arguments.KINDS_SECONDARY_KDJ_WEIGHT);
                for (int i = 0; i< kdj.length; i++) {
                    List<Entry> values = kdj[i];
                    LineDataSet dataSet = new ParamDataSet(values, i == 0 ? "K" : i == 1 ? "D" : "J");
                    dataSet.setColor(ResourceUtils.getParameterLineColors()[i]);
                    lineData.addDataSet(dataSet);
                }
                break;
            case Arguments.SECONDARY_TYPE_RSI:
                int[] rsiArgus = Arguments.KINDS_SECONDARY_RSI;
                for (int i = 0; i< rsiArgus.length; i++) {
                    List<Entry> rsiList = Arithmetic.getRSI(candleEntries, rsiArgus[i], Arguments.KINDS_SECONDARY_RSI_WEIGHT);
                    LineDataSet dataSet = new ParamDataSet(rsiList, "RSI"+rsiArgus[i]);
                    dataSet.setColor(ResourceUtils.getParameterLineColors()[i]);
                    lineData.addDataSet(dataSet);
                }
                break;
        }
    }

}
