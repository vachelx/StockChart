package com.vachel.chartview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;
import com.vachel.chartview.util.Utils;

import java.util.ArrayList;

/**
 * 指标类型选择弹窗
 */
public class SelectPopup extends PopupWindow implements View.OnClickListener {
    private ChartPopupListener mChartPopupListener;
    private ArrayList<TextView> mViews = new ArrayList<>();
    private String[] mArguments;
    private LinearLayout mRootView;
    private double mMeasureHeight = 0;
    private View mAnchor;
    private int mXoff;
    private int mYoff;

    public SelectPopup(Context context) {
        super(context);
    }

    public SelectPopup(Context context, String[] arguments) {
        this(context);
        init(context, arguments);
    }

    private void init(Context context, String[] arguments) {
        mArguments = arguments;
        Resources resources = context.getResources();
        int offsetX = resources.getDimensionPixelSize(R.dimen.popup_select_horizontal);
        int offsetY = resources.getDimensionPixelSize(R.dimen.popup_select_vertical);

        mRootView = new LinearLayout(context);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.setGravity(Gravity.CENTER);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        boolean isBlackTheme = ResourceUtils.getTheme().equals(Constant.THEME_BLACK);
        mRootView.setBackground(resources.getDrawable(isBlackTheme ? R.drawable.black_popup_bg : R.drawable.white_popup_bg));
        mRootView.setPadding(0, offsetY, 0, offsetY);

        ColorStateList colorStateList = resources.getColorStateList(isBlackTheme ? R.color.black_pul_popup_text_color : R.color.white_pul_popup_text_color);

        for (int i = 0; i < arguments.length; i++) {
            String arg = arguments[i];
            TextView textView = new TextView(context);
            textView.setText(arg);
            textView.setPadding(offsetX, offsetY, offsetX ,offsetY);
            textView.setTextSize(Constant.POPUP_TEXT_SIZE);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(colorStateList);
            textView.setTag(arg);
            mRootView.addView(textView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mViews.add(textView);
            textView.setOnClickListener(this);
        }
        setClippingEnabled(false);
        setContentView(mRootView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable());
        setTouchable(true);
        setOutsideTouchable(true);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mChartPopupListener != null) {
                    mChartPopupListener.onDismiss();
                }
            }
        });

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // popupWindow 可能会超出屏幕， 先不显示rooView, 待测出popupWindow的高度后，重新show。
                if (mMeasureHeight == 0) {
                    mMeasureHeight = mRootView.getMeasuredHeight();
                    dismiss();
                    showAsDropDown(mAnchor, mXoff, mYoff);
                }
            }
        });
    }

    private int getTypeByViewTag(Object tag) {
        for (int i = 0; i < mArguments.length; i++) {
            if (tag.equals(mArguments[i])) {
                return i;
            }
        }
        return 0;
    }

    private TextView getViewByType(int type) {
        return mViews.get(type);
    }

    public void setChartPopupListener(ChartPopupListener listener) {
        mChartPopupListener = listener;
    }

    /**
     *
     * @param type
     * @param isM1 表示当前为分时线 不需要某些指标
     */
    public void setSelectItem(int type, boolean isM1) {
        TextView targetView = getViewByType(type);
        for (TextView view: mViews) {
            boolean equals = targetView.equals(view);
            view.setSelected(equals);
            view.setClickable(!isM1 || equals);
        }
    }

    @Override
    public void onClick(View v) {
        if (mChartPopupListener != null) {
            int type = getTypeByViewTag(v.getTag());
            mChartPopupListener.onItemClick(type);
        }
        dismiss();
    }

    public interface ChartPopupListener {
        void onItemClick(int type);

        void onDismiss();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (mMeasureHeight != 0) {
            // 根据popup高度调整应该显示的位置
            int[] outs = new int[2];
            anchor.getLocationOnScreen(outs);
            int height = anchor.getHeight();
            int screenHeight = Utils.getScreenHeight(mRootView.getContext());
            if (outs[1] + height + mMeasureHeight> screenHeight) {
                yoff = (int) (screenHeight - mMeasureHeight - outs[1] - height);
            }
            if (mRootView.getVisibility() == View.INVISIBLE) {
                mRootView.setVisibility(View.VISIBLE);
            }
        } else {
            mAnchor = anchor;
            mXoff = xoff;
            mYoff = yoff;
        }
        super.showAsDropDown(anchor, xoff, yoff);
        anchor.setSelected(true);
    }
}
