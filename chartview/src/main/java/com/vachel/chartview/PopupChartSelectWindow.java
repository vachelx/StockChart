package com.vachel.chartview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianglixuan on 2018/11/20
 */
public class PopupChartSelectWindow extends PopupWindow implements View.OnClickListener {
    private boolean mIsClickItem;
    private ChartPopupListener mChartPopupListener;
    private int[] mViewIds = new int[]{R.id.selection_5_minute, R.id.selection_15_minute, R.id.selection_30_minute, R.id.selection_60_minute};
    List<TextView> mViews = new ArrayList<>();

    public PopupChartSelectWindow(Context context) {
        this(context, null);
    }

    public PopupChartSelectWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupChartSelectWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_chart_selection, null);
        view.setBackgroundResource(ResourceUtils.getTheme().equals(Constant.THEME_WHITE) ? R.drawable.white_shape_popup_chart_pull_bg : R.drawable.black_shape_popup_chart_pull_bg);
        for (int id: mViewIds) {
            TextView textView = (TextView) view.findViewById(id);
            mViews.add(textView);
            textView.setOnClickListener(this);
        }
        setContentView(view);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(context.getResources().getDimensionPixelSize(R.dimen.popup_window_width));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mChartPopupListener != null) {
                    mChartPopupListener.onDismiss();
                    if (!mIsClickItem) {
                        mChartPopupListener.onCancel();
                        return;
                    }
                    mIsClickItem = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (mChartPopupListener != null) {
            int type = getTypeByViewId(view.getId());
            CharSequence text = ((TextView) view).getText();
            if (text != null) {
                mIsClickItem = true;
                mChartPopupListener.onItemClick(type, text.toString());
            }
        }
        dismiss();
    }

    private int getTypeByViewId(int id) {
        int type = 0;
        if (id == R.id.selection_5_minute) {
            type = Constant.TYPE_K_LINE_M5;
        } else if (id == R.id.selection_15_minute) {
            type = Constant.TYPE_K_LINE_M15;
        } else if (id == R.id.selection_30_minute) {
            type = Constant.TYPE_K_LINE_M30;
        } else if (id == R.id.selection_60_minute) {
            type = Constant.TYPE_K_LINE_M60;
        }
        return type;
    }

    public void setChartPopupListener(ChartPopupListener listener) {
        mChartPopupListener = listener;
    }

    public void setSelectItem(int tmpM5Type) {
        //mViews的第一个textview对应索引为2
        if (tmpM5Type - 2 > mViews.size() && tmpM5Type - 2 < 0) {
            return;
        }
        for (int i = 0; i < mViews.size(); i++) {
            mViews.get(i).setSelected(tmpM5Type - 2 == i);
        }
    }

    public interface ChartPopupListener {
        void onItemClick(int type, String text);

        void onCancel();

        void onDismiss();
    }

}
