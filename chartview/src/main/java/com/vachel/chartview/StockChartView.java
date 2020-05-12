package com.vachel.chartview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.jobs.MoveViewJob;
import com.vachel.chartview.bean.KLineData;
import com.vachel.chartview.custom.BarAXisValueFormatter;
import com.vachel.chartview.custom.ChartViewPresenter;
import com.vachel.chartview.custom.MasterArithmeticControl;
import com.vachel.chartview.custom.MyChartTouchListener;
import com.vachel.chartview.custom.SecondaryArithmeticControl;
import com.vachel.chartview.data.MyBarDataSet;
import com.vachel.chartview.data.MyCandleDataSet;
import com.vachel.chartview.data.MyLineDataSet;
import com.vachel.chartview.util.Arguments;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.FormatUtils;
import com.vachel.chartview.util.ResourceUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockChartView extends LinearLayout implements ChartViewPresenter.IStockChartView, View.OnClickListener, BarAXisValueFormatter.IValueFormatterCallback {
    private Context mContext;
    MyBarCombineChart mBarChart;
    private int mChartType;
    private ChartViewPresenter mChartPresenter;
    private boolean mIsCandleMode = true;
    private NativeChartCallback mNativeChartCallback;
    private MyCombineChart mCombineChart;
    private MyLineDataSet mLineDataSet;
    private MyCandleDataSet mCandleDataSet;
    private String mNoDataTips;
    // 默认为false 红涨绿跌， true为红跌绿涨
    private boolean mRiseColorType;
    private LottieAnimationView mWaitingView;
    private boolean mIsMulti;
    private MyBarDataSet mBarDataSet;
    //
    private String mTheme = Constant.THEME_WHITE;
    private SelectPopup mMasterPopup;
    // 主图指标显示类型： 0 MA, 1 EMA, 2 BOLL, 3 不显示
    private int mMasterShowType = 0;
    // 副图指标显示类型： 0 成交量, 1：MACD， 2 KDJ, 3 RSI
    private int mSecondaryShowType = 0;
    private SelectPopup mSecondaryPopup;
    private View mMasterContainers;
    private View mSecondaryContainers;
    private TextView mMasterParametersView;
    private TextView mSecParametersView;
    private List<Entry> mMarkLastEntry;
    private String mUpStatus = Constant.TYPE_UP_UP;
    private boolean mParamsViewVisible;
    private TextView mSecChangedOption;
    private TextView mMasterChangedOption;
    private ImageButton mModeBtn;
    private String mSymbol;
    private boolean mIsUsdt;
    private int mMinuteType = Constant.TYPE_K_LINE_M5;
    private PopupChartSelectWindow mPopupChartSelectWindow;
    private ChartTabView mTabLayout;

    public StockChartView(Context context) {
        this(context, null);
    }

    public StockChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StockChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        ResourceUtils.initResource(getContext(), mTheme, mRiseColorType);
        LayoutInflater.from(mContext).inflate(R.layout.net_stock_chart, this, true);
        mCombineChart = (MyCombineChart) findViewById(R.id.combine_chart);
        mBarChart = (MyBarCombineChart) findViewById(R.id.bar_chart);
        mWaitingView = (LottieAnimationView) findViewById(R.id.chart_waiting);

        mMasterContainers = findViewById(R.id.mark_contains);
        mSecondaryContainers = findViewById(R.id.auxiliary_contains);
        mMasterChangedOption = (TextView)findViewById(R.id.change_option);
        mSecChangedOption = (TextView)findViewById(R.id.auxiliary_change_option);
        mMasterParametersView = (TextView)findViewById(R.id.parameters);
        mSecParametersView = (TextView)findViewById(R.id.auxiliary_parameters);
        mModeBtn = (ImageButton) findViewById(R.id.chart_mode_btn);
        mTabLayout = (ChartTabView) findViewById(R.id.chart_tab_layout);
        mTabLayout.setOnItemClickListener(new TabView.OnItemClickListener() {
            @Override
            public void onItemClick(TextView view, int index) {
                onClickBtn(view, index);
            }

            @Override
            public void OnItemClickAnimEnd(TextView view, int index, boolean cancel) {

            }
        });
        mModeBtn.setOnClickListener(v -> changedChartMode(!mIsCandleMode));
        mChartPresenter = new ChartViewPresenter(this);
        mChartType = mChartPresenter.getLastChartType(mContext);
        mTabLayout.setSelectChartType(mChartType);
        mChartPresenter.setSecondaryText(mSecondaryShowType, mSecChangedOption);
        mChartPresenter.setSelectedText(mMasterShowType, mMasterChangedOption);
        mSecChangedOption.setOnClickListener(this);
        mMasterChangedOption.setOnClickListener(this);
        updateBtnStyle();

        mCombineChart.setOnTextChangedListener(text -> {
            if (text!= null && !text.equals(mMasterParametersView.getText())) {
                mMasterParametersView.setText(text);
            }
        });

        mBarChart.setOnTextChangedListener(text -> {
            if (text!= null && !text.equals(mSecParametersView.getText())) {
                mSecParametersView.setText(text);
            }
        });

        // 初始化时防止初始涨跌颜色未确定导致颜色跳变
//        updateViewsUpStatus(false);
        setNativeChartCallback(null);
        mChartPresenter.refresh(mChartType);
    }

    private void onClickBtn(TextView view, int index) {
        syncModeState(index);
        if (index == 1) {
            if (mChartType >= Constant.CHART_TYPES_MINIUTES[0] && mChartType <= Constant.CHART_TYPES_MINIUTES[Constant.CHART_TYPES_MINIUTES.length - 1]) {
                showPopupSelectWindow(view);
            } else {
                clearChart();
                mChartType = mMinuteType;
                mChartPresenter.refresh(mMinuteType);
            }
            return;
        }
        int tmpType = mChartPresenter.getTypeByItemIndex(index);
        if (mChartType == tmpType || tmpType == 0) {
            return;
        }
        clearChart();
        mChartType = tmpType;
        mChartPresenter.refresh(tmpType);
    }

    private void syncModeState(int index) {
        mModeBtn.setEnabled(index != 0);
        if (index + 1 == Constant.TYPE_TIME_SHARING_M1) {
            showParamsViews(false);
        }
        boolean showCandle = mIsCandleMode && index != 0;
        if (mLineDataSet != null) {
            mLineDataSet.setVisible(!showCandle);
        }
        if (mCandleDataSet != null) {
            mCandleDataSet.setVisible(showCandle);
        }
    }

    private void showPopupSelectWindow(final View view) {
        if (mPopupChartSelectWindow == null) {
            mPopupChartSelectWindow = new PopupChartSelectWindow(mContext);
            mPopupChartSelectWindow.setChartPopupListener(new PopupChartSelectWindow.ChartPopupListener() {
                @Override
                public void onItemClick(int type, String text) {
                    mMinuteType = type;
                    if (mChartType == type) {
                        return;
                    }
                    mChartType = type;
                    clearChart();
                    mChartPresenter.refresh(type);
                    ((TextView) view).setText(text);
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onDismiss() {
                }
            });
        }
        mPopupChartSelectWindow.setSelectItem(mMinuteType);
        mPopupChartSelectWindow.showAsDropDown(view, (view.getWidth() - mPopupChartSelectWindow.getWidth()) / 2, 30);
    }

    private void updateBtnStyle() {
        boolean isBlack = mTheme.equals(Constant.THEME_BLACK);
        mMasterChangedOption.setTextColor(ResourceUtils.getThemeColorReverse());
        mMasterChangedOption.setBackgroundResource(isBlack ? R.drawable.black_parameter_bg_shape : R.drawable.white_parameter_bg_shape);
        Drawable drawable = getResources().getDrawable(isBlack?R.drawable.black_selector_arrow_bg: R.drawable.white_selector_arrow_bg);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mMasterChangedOption.setCompoundDrawables(null, null, drawable, null);

        mSecChangedOption.setTextColor(ResourceUtils.getThemeColorReverse());
        mSecChangedOption.setBackgroundResource(isBlack ? R.drawable.black_parameter_bg_shape : R.drawable.white_parameter_bg_shape);
        Drawable drawableSec = getResources().getDrawable(isBlack?R.drawable.black_selector_arrow_bg: R.drawable.white_selector_arrow_bg);
        drawableSec.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mSecChangedOption.setCompoundDrawables(null, null, drawableSec, null);
    }

    private void onSecondaryTypeChanged(int type) {
        if (mSecondaryShowType == type) return;
        mSecondaryShowType = type;
        mBarChart.onArithmeticChanged(type);
    }

    private void onMasterTypeChanged(int type) {
        if (mMasterShowType == type) return;
        mMasterShowType = type;
        mCombineChart.onArithmeticChanged(type);
    }

    public void changedChartMode(boolean candleMode) {
        if (mIsCandleMode == candleMode) {
            return;
        }
        mIsCandleMode = candleMode;
        if (mCandleDataSet != null) {
            mCandleDataSet.setVisible(mIsCandleMode);
        }
        if (mLineDataSet != null) {
            mLineDataSet.setVisible(!mIsCandleMode);
        }
        // todo 还没有涨跌色基准
//        if (!mUpStatus.equals(Constant.TYPE_UP_DEFAULT)) {
        mCombineChart.invalidate();
        mModeBtn.setSelected(mIsCandleMode);
//        }
    }

    private void initChartDataSet() {
        mBarDataSet = new MyBarDataSet(new ArrayList<BarEntry>(), "VOL");
        mBarChart.setData(new CombinedData());

        mCombineChart.setSyncChart(mBarChart, mNativeChartCallback);
        mLineDataSet = new MyLineDataSet(new ArrayList<Entry>(), "");
        mLineDataSet.setColor(ResourceUtils.getLinePathColor(mUpStatus.equals(Constant.TYPE_UP_UP)));
        mLineDataSet.setFillDrawable(getFillDrawable());

        mCandleDataSet = new MyCandleDataSet(new ArrayList<CandleEntry>(), "");
        mCombineChart.setData(new CombinedData());
    }

    @Override
    public void refreshContent(List<Entry> lineEntries, List<BarEntry> barEntries, List<CandleEntry> candleEntries, int type) {
//        boolean keepStatus = mChartType == type; // 同一type数据才需要
//        if (keepStatus && type == Constant.TYPE_TIME_SHARING_M1 && !mIsUsdt) {
//            keepStatus = false;
//        }
        updateSelectType(type);
        initDataSet(lineEntries, barEntries, candleEntries, false);
    }

    private void updateSelectType(int type) {
        mChartType = type;
        boolean enableScale = type != Constant.TYPE_TIME_SHARING_M1 || mIsUsdt;
        if (!enableScale) {
            mIsCandleMode = false;
        }
        mBarChart.setScaleXEnabled(enableScale);
        mCombineChart.setScaleXEnabled(enableScale);
    }

    private void initDataSet(final List<Entry> lineEntries, final List<BarEntry> barEntries, final List<CandleEntry> candleEntries, boolean keepStatus) {
        mWaitingView.setVisibility(GONE);
        if (barEntries == null || barEntries.isEmpty()) {
            mBarChart.clear();
            mCombineChart.clear();
            clearParametersViewText();
            return;
        }

        // 记录上次数据状态， 更新数据后根据该数据确定缩放比及移动位置
        float[] statusParam = getKeepStatusParam(lineEntries, keepStatus);
        float lastShowCount = statusParam[0];
        float lowestVisibleX = statusParam[1];
        if (lastShowCount <= 0) {
            keepStatus = false;
        }
        mMarkLastEntry = lineEntries;

        float showSize = fillingChartDataSet(lineEntries, barEntries, candleEntries);

        boolean isTimeSharing = mChartType == Constant.TYPE_TIME_SHARING_M1;
        if (!isTimeSharing) {
            showParamsViews(true);
        } else {
            // 数字货币的close字段不对 先不显示昨收基准线
            mCombineChart.setDrawLastCloseEnable(true, false);
        }

        // 默认移动到最后一个点显示默认条数，而股线分时图移动到起始点无缩放，数据更新时移动至上次记录位置和缩放大小
        float scaleRatio = isTimeSharing && !mIsUsdt ? 1.0f : keepStatus ? showSize / lastShowCount :
                showSize / Constant.DEFAULT_CHART_LINE_COUNT;
        float moveToX = isTimeSharing && !mIsUsdt ? -0.5f : keepStatus ? lowestVisibleX : showSize;
        mBarChart.updateAxisMin(mSecondaryShowType);
        mBarChart.notifyDataSetChanged();
        mCombineChart.notifyDataSetChanged();
        Matrix m = new Matrix();
        m.preScale(scaleRatio, 1f);
        mCombineChart.getViewPortHandler().refresh(m, mCombineChart, false);
        mBarChart.getViewPortHandler().refresh(m, mBarChart, false);
        mBarChart.moveViewToX(moveToX);
        mCombineChart.moveViewToX(moveToX);
    }

    /**
     * 根据前后数据时间对比确认是否需要保持上次缩放状态
     *
     * @param lineEntries
     * @return
     */
    private float[] getKeepStatusParam(List<Entry> lineEntries, boolean keepStatus) {
        float lowestVisibleX = mCombineChart.getLowestVisibleX();
        float highestVisibleX = mCombineChart.getHighestVisibleX();
        float lastShowCount = highestVisibleX - lowestVisibleX;
        if (!keepStatus || lastShowCount <= 0) {
            return new float[]{0, 0};
        }
        try {
            XAxis combineXAxis = mCombineChart.getXAxis();
            // case上次股线图在最右边 保持在右侧但缩放保持
            if (combineXAxis.getAxisMaximum() - highestVisibleX < 1) {
                lowestVisibleX = lineEntries.size();
            } else if (lowestVisibleX - combineXAxis.getAxisMinimum() < 1) {
                // case上次股线图在最左边切存在缩放 判断上次数据左边和本次一致则保持最左边
                Date lastDisplayTime = ((KLineData) mMarkLastEntry.get(0).getData()).getDate();
                Date startTime = ((KLineData) lineEntries.get(0).getData()).getDate();
                Date secTime = ((KLineData) lineEntries.get(1).getData()).getDate();
                // 主要针对目前数字货币每次更新数据会有一两个count的错位
                long diffCount = Math.abs(startTime.getTime() - lastDisplayTime.getTime()) / Math.abs(secTime.getTime() - startTime.getTime());
                if (diffCount > lastShowCount / 2) {
                    lastShowCount = 0;
                }
            } else {
                // case
                Date lastFirstTime = ((KLineData) mMarkLastEntry.get(0).getData()).getDate();
                Date startTime = ((KLineData) lineEntries.get(0).getData()).getDate();
                if (!lastFirstTime.equals(startTime)) {
                    Date secTime = ((KLineData) lineEntries.get(1).getData()).getDate();
                    int diffCount = (int) (Math.abs(startTime.getTime() - lastFirstTime.getTime()) / Math.abs(secTime.getTime() - startTime.getTime()));
                    if (diffCount > lastShowCount / 2) {
                        lastShowCount = 0;
                    } else {
                        Date endTime = ((KLineData) lineEntries.get(lineEntries.size() - 1).getData()).getDate();
                        Date lastStartTime = ((KLineData) mMarkLastEntry.get((int) lowestVisibleX).getData()).getDate();
                        Date lastEndTime = ((KLineData) mMarkLastEntry.get((int) lowestVisibleX).getData()).getDate();
                        // 上次可视区域和本次数据不重叠
                        if (lastStartTime.before(startTime) || lastEndTime.after(endTime)) {
                            lastShowCount = 0;
                        } else {
                            lowestVisibleX -= diffCount;
                        }
                    }
                }
            }
        } catch (Exception e) {
            lastShowCount = 0;
            lowestVisibleX = 0;
        }

        return new float[]{lastShowCount, lowestVisibleX};
    }

    // 更新数据并重置chart
    private float fillingChartDataSet(List<Entry> lineEntries, final List<BarEntry> barEntries, List<CandleEntry> candleEntries) {
        int listSize = barEntries.size();
        // extraLength用于最右边空间添加额外空白区域 （现在暂时为0， 没有发挥作用）
        float extraLength = mChartType == Constant.TYPE_TIME_SHARING_M1 ? listSize * Constant.CHART_LINE_EXTRA_RATIO
                : Constant.DEFAULT_CHART_LINE_COUNT * Constant.CHART_LINE_EXTRA_RATIO;
        float showSize = listSize + extraLength;

        XAxis barXAxis = mBarChart.getXAxis();
        XAxis combineXAxis = mCombineChart.getXAxis();
        boolean isTimeSharing = mChartType == Constant.TYPE_TIME_SHARING_M1;

        if (showSize > Constant.DEFAULT_CHART_LINE_COUNT) {
            barXAxis.setAxisMaximum(showSize - 0.5f);
            combineXAxis.setAxisMaximum(showSize - 0.5f);
            mCombineChart.setTransEdge(MyChartTouchListener.TRANS_EDGE_FREE);
        } else {
            barXAxis.setAxisMaximum(Constant.DEFAULT_CHART_LINE_COUNT - 0.5f);
            combineXAxis.setAxisMaximum(Constant.DEFAULT_CHART_LINE_COUNT - 0.5f);
            mCombineChart.setTransEdge(MyChartTouchListener.TRANS_EDGE_LEFT);
            showSize = Constant.DEFAULT_CHART_LINE_COUNT;
        }
        boolean showCandle = mIsCandleMode && !isTimeSharing;

        mLineDataSet.clear();
        mLineDataSet.setValues(lineEntries);
        mLineDataSet.setVisible(!showCandle);
        LineData lineData = new LineData();
        lineData.addDataSet(mLineDataSet);

        //初始化BarChart数据
        mBarDataSet.clear();
        mBarDataSet.setValues(barEntries);
        BarData barData = new BarData();
        if (mSecondaryShowType == Arguments.SECONDARY_TYPE_VOL || isTimeSharing) {
            barData.addDataSet(mBarDataSet);
        }
        barData.setDrawValues(false);
        barData.setBarWidth(0.8f);
        LineData barLineData = new LineData();

        CombinedData combinedData = new CombinedData();
        if (candleEntries != null && !candleEntries.isEmpty()) {
            mCandleDataSet.clear();
            mCandleDataSet.setValues(candleEntries);
            CandleData mCandleData = new CandleData(mCandleDataSet);
            mCandleDataSet.setVisible(showCandle);
            combinedData.setData(mCandleData);
            mCombineChart.setSourceEntries(candleEntries);
            mBarChart.setSourceEntries(candleEntries, barEntries);
            MasterArithmeticControl.showParamLine(mMasterShowType, candleEntries, lineData);
            SecondaryArithmeticControl.showParamLine(mSecondaryShowType, candleEntries, barLineData, barEntries, barData);
        } else {
            mCombineChart.setSourceEntries(null);
            mBarChart.setSourceEntries(null, null);
        }
        combinedData.setData(lineData);

        barXAxis.setValueFormatter(new BarAXisValueFormatter(barEntries, this));

        //重置放大倍数，避免下一次刷新时被前一次放大倍数限制
        mCombineChart.getViewPortHandler().setMaximumScaleX(0.f);
        mBarChart.getViewPortHandler().setMaximumScaleX(0.f);

        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(barData);
        barCombinedData.setData(barLineData);
        mBarChart.setData(barCombinedData);
        mCombineChart.setData(combinedData);
        mBarChart.setVisibleXRangeMinimum(Constant.MIN_CHART_LINE_COUNT);
        mCombineChart.setVisibleXRangeMinimum(Constant.MIN_CHART_LINE_COUNT);
        return showSize;
    }

    private void setLimitLine(LineDataSet lineDataSet) {
        //设置均线
        float yMin = lineDataSet.getYMin();
        float yMax = lineDataSet.getYMax();
        LimitLine limitLine = new LimitLine((yMax + yMin) / 2, FormatUtils.formatValue((yMax + yMin) / 2) + "");
        limitLine.setLineColor(getResources().getColor(R.color.market_color_chart_line_average));
        limitLine.setEnabled(true);
        limitLine.setTextColor(ResourceUtils.getLabelColor());
        limitLine.enableDashedLine(5, 3f, 0);
        YAxis axisLeft = mCombineChart.getAxisLeft();
        axisLeft.removeAllLimitLines();
        axisLeft.setDrawGridLinesBehindData(true);
        axisLeft.addLimitLine(limitLine);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MoveViewJob.getInstance(null, 0, 0, null, null);
        MoveViewJob.getInstance(null, 0, 0, null, null);
    }

    public void clearChart() {
        mBarChart.clear();
        mCombineChart.setData(new CombinedData());
        mWaitingView.setVisibility(VISIBLE);
        mCombineChart.setDrawLastCloseEnable(false);
        clearParametersViewText();
    }

    private void showParamsViews(boolean b) {
        mParamsViewVisible = b;
        mMasterContainers.setVisibility(b ? VISIBLE : INVISIBLE);
        mSecondaryContainers.setVisibility(b ? VISIBLE : INVISIBLE);
    }

    private void clearParametersViewText() {
        mMasterParametersView.setText("");
        mSecParametersView.setText("");
    }

    public void setNativeChartCallback(NativeChartCallback listener) {
        mNativeChartCallback = listener;
        initChartDataSet();
    }

    public void setNoDataTips(String tips) {
        mNoDataTips = tips;
        mCombineChart.setNoDataText(tips);
    }

    public void setRiseFallColorType(boolean type) {
        if (mRiseColorType == type) {
            return;
        }
        mRiseColorType = !mRiseColorType;
        ResourceUtils.setRiseColorType(mRiseColorType);

        if (mCandleDataSet != null) {
            mCandleDataSet.updateRiseFallColor();
            mCombineChart.invalidate();
        }
        mBarChart.updateBarDataRiseFallColor();
        setUpDownColors(mUpStatus);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mNativeChartCallback!= null && mIsMulti != ev.getPointerCount() > 1) {
            mIsMulti = ev.getPointerCount() > 1;
            mNativeChartCallback.onMultiTouch(mIsMulti);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setCurrentPrice(float price) {
        mCombineChart.setCurrentPrice(price);
    }

    public void setLastClose(float price) {
        mCombineChart.setLastClose(price);
    }

    public void setOrientationLandscape(boolean isLand) {
        //暂时 do nothing
    }

    public void setTheme(String theme) {
        if (theme != null && theme.equals(mTheme)) {
            return;
        }
        mTheme = theme;
        ResourceUtils.setTheme(theme);
        onThemeChanged();
    }

    private void onThemeChanged() {
        mLineDataSet.setFillDrawable(getFillDrawable());
        mLineDataSet.setHighLightColor(ResourceUtils.getHighLineColor());
        mCandleDataSet.setHighLightColor(ResourceUtils.getHighLineColor());
        mBarChart.onThemeChanged();
        mCombineChart.onThemeChanged();
        updateBtnStyle();
        mTabLayout.onThemeChanged();
        mMasterPopup = null;
        mSecondaryPopup = null;
        mPopupChartSelectWindow = null;
    }

    private Drawable getFillDrawable() {
        boolean isRed = mUpStatus.equals(Constant.TYPE_UP_UP) ^ mRiseColorType;
        return getResources().getDrawable(mTheme.equals(Constant.THEME_WHITE) ? (isRed ? R.drawable.line_gradient_color_white_rise : R.drawable.line_gradient_color_white_down) :
                isRed ? R.drawable.line_gradient_color_black_rise : R.drawable.line_gradient_color_black_down);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.auxiliary_change_option) {
            if (mSecondaryPopup == null) {
                mSecondaryPopup = new SelectPopup(mContext, Arguments.SECONDARY_ARGUMENTS);
                mSecondaryPopup.setChartPopupListener(new SelectPopup.ChartPopupListener() {
                    @Override
                    public void onItemClick(int type) {
                        if (mSecondaryShowType == type || mChartType == Constant.TYPE_TIME_SHARING_M1)
                            return;
                        mChartPresenter.setSecondaryText(type, (TextView) v);
                        onSecondaryTypeChanged(type);
                    }

                    @Override
                    public void onDismiss() {
                        v.setSelected(false);
                    }
                });
            }
            mSecondaryPopup.setSelectItem(mSecondaryShowType, mChartType == Constant.TYPE_TIME_SHARING_M1);
            mSecondaryPopup.showAsDropDown(v, v.getWidth() * 2 / 3, Constant.POPUP_OFFSET_Y);
        } else if (v.getId() == R.id.change_option) {
            if (mMasterPopup == null) {
                mMasterPopup = new SelectPopup(mContext, Arguments.MASTER_ARGUMENTS);
                mMasterPopup.setChartPopupListener(new SelectPopup.ChartPopupListener() {
                    @Override
                    public void onItemClick(int type) {
                        if (mMasterShowType == type) return;
                        mChartPresenter.setSelectedText(type, (TextView) v);
                        onMasterTypeChanged(type);
                    }

                    @Override
                    public void onDismiss() {
                        v.setSelected(false);
                    }
                });
            }
            mMasterPopup.setSelectItem(mMasterShowType, mChartType == Constant.TYPE_TIME_SHARING_M1);
            mMasterPopup.showAsDropDown(v, v.getWidth() * 2 / 3, Constant.POPUP_OFFSET_Y);
        }
    }

    public void setUpDownColors(String isUp) {
        if (mUpStatus.equals(isUp)) {
            return;
        }
        mUpStatus = isUp;
        mLineDataSet.setFillDrawable(getFillDrawable());
        mLineDataSet.setColor(ResourceUtils.getLinePathColor(mUpStatus.equals(Constant.TYPE_UP_UP)));
        if (mCombineChart.getVisibility()== INVISIBLE) {
            updateViewsUpStatus(true);
        }
        if (mLineDataSet.isVisible()) {
            mCombineChart.invalidate();
        }
    }

    private void updateViewsUpStatus(boolean b) {
        int vis = b ? VISIBLE : INVISIBLE;
        mCombineChart.setVisibility(vis);
        mBarChart.setVisibility(vis);
        if (b) {
            showParamsViews(mParamsViewVisible);
        } else {
            showParamsViews(false);
        }
    }

    public void changeTheme() {
        setTheme(mTheme.equals(Constant.THEME_BLACK) ? Constant.THEME_WHITE : Constant.THEME_BLACK);
        setBackgroundColor(ResourceUtils.getThemeColor());
    }

    public void changeRiseType() {
        setRiseFallColorType(!mRiseColorType);
    }

    public void changeGradColor() {
        setUpDownColors(mUpStatus.equals(Constant.TYPE_UP_UP) ? Constant.TYPE_UP_DOWN : Constant.TYPE_UP_UP);
    }

    @Override
    public float getHighestVisibleX() {
        return mBarChart.getHighestVisibleX();
    }

    @Override
    public float getLowestVisibleX() {
        return mBarChart.getLowestVisibleX();
    }
}
