package com.vachel.stockchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vachel.chartview.StockChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private StockChartView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChartView = (StockChartView)findViewById(R.id.chart_view);
        findViewById(R.id.change_theme).setOnClickListener(this);
        findViewById(R.id.change_rise_type).setOnClickListener(this);
        findViewById(R.id.change_grad_color).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_theme:
                mChartView.changeTheme();
                break;
            case R.id.change_rise_type:
                mChartView.changeRiseType();
                break;
            case R.id.change_grad_color:
                mChartView.changeGradColor();
                break;
            default:
                break;
        }
    }
}
