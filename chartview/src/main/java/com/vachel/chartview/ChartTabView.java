package com.vachel.chartview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;


/**
 * Created by jianglixuan on 2019/8/9
 */
public class ChartTabView extends TabView {

    private RightDrawableCenterTextView mGroupTextView;
    private String[] mGroupTexts;

    public ChartTabView(Context context) {
        this(context, null);
    }

    public ChartTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        Resources res = getResources();
        String[] items = new String[]{
                res.getString(R.string.market_string_chart_default_k),
                res.getString(R.string.market_string_chart_day_k),
                res.getString(R.string.market_string_chart_week_k),
                res.getString(R.string.market_string_chart_month_k)
        };
        mGroupTexts = new String[]{
                res.getString(R.string.market_string_5_minute),
                res.getString(R.string.market_string_15_minute),
                res.getString(R.string.market_string_30_minute),
                res.getString(R.string.market_string_60_minute)
        };
        setItems(items);
        mGroupTextView = new RightDrawableCenterTextView(getContext());
        onThemeChanged();
        mGroupTextView.setCompoundDrawablePadding(3);
        mGroupTextView.setText(res.getString(R.string.market_string_5_minute));
        addItemView(mGroupTextView, 1);
    }

    public String getTextByChartType(int type) {
        int[] groups = Constant.CHART_TYPES_MINIUTES;
        if (type < groups[0] || type > groups[groups.length - 1]) {
            return getResources().getString(R.string.market_string_chart_default_k);
        }
        return mGroupTexts[type - groups[0]];
    }

    public void setSelectChartType(int chartType) {
        setSelectItem(getBtnIndex(chartType));
        String groupText = getTextByChartType(chartType);
        mGroupTextView.setText(groupText);
    }

    public int getBtnIndex(int lastChartType) {
        int[] groups = Constant.CHART_TYPES_MINIUTES;
        if (lastChartType < groups[0]) {
            return lastChartType - 1;
        }
        if (lastChartType <= groups[groups.length - 1]) {
            return 1;
        }
        return lastChartType - groups[groups.length - 1] + 1;
    }

    public void onThemeChanged() {
        boolean isWhite = ResourceUtils.getTheme().equals(Constant.THEME_WHITE);
        Drawable drawable = getResources().getDrawable(isWhite ? R.drawable.white_selector_arrow_bg : R.drawable.black_selector_arrow_bg);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mGroupTextView.setCompoundDrawables(null, null, drawable, null);
        setBackgroundResource(isWhite ? R.drawable.white_shape_type_selection_bg : R.drawable.black_shape_type_selection_bg);
    }
}
