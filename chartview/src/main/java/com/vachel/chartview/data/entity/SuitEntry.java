package com.vachel.chartview.data.entity;

/**
 * Created by jianglixuan on 2019/7/8
 * 便于参数指标计算是自动获取合适的字段, CandleEntry获取Close字段， Entry获取Price字段即y值
 */
public interface SuitEntry {
    float getValue();

    float getX();

}
