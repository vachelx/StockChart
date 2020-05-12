package com.vachel.chartview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.vachel.chartview.util.Utils;

import java.util.ArrayList;

/**
 * Created by jianglixuan on 2019/8/9
 */
public class TabView extends ConstraintLayout implements View.OnClickListener {
    private View mBgAnimView;
    private int mLastSelectIndex = -1;
    private int mCurrSelectIndex = -1;
    private ArrayList<TextView> mViews = new ArrayList<>();
    private ValueAnimator mValueAnimator;
    private boolean mIsAnimCancel;
    private OnItemClickListener mListener;
    private int mStartColor;
    private int mEndColor;
    private int mPerWidth;
    private int mDefaultPadding;
    private OnTabAnimListener mAnimListener;
    private int mCanRepeatClickIndex;
    private float mItemTextSize;
    private ColorStateList mColorList;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = mViews.size();
        int height = 0;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (size > 0) {
            float perWidth = width * 1.0f / size;
            if (mPerWidth != perWidth) {
                mPerWidth = (int) perWidth;
                for (TextView view : mViews) {
                    view.setWidth(mPerWidth);
                    if (view.getMeasuredHeight() > height) {
                        height = view.getMeasuredHeight();
                    }
                }
            }
        }
        super.onMeasure(widthMeasureSpec, height == 0 ? heightMeasureSpec : View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutSize = mViews.size();
        if (layoutSize < 1) {
            return;
        }
        int left = 0;
        int right = left;
        for (int i = 0; i < layoutSize; i++) {
            View child = mViews.get(i);
            if (i == layoutSize - 1) {
                right = r - l;
                left = r - l - mPerWidth;
            } else {
                right += mPerWidth;
            }
            child.layout(left, 0, right, b - t);
            left = right;
        }
        updateBgAnimViewIfNeed(t, b);
    }

    private void updateBgAnimViewIfNeed(int t, int b) {
        if (mBgAnimView.getHeight() != b - t) {
            mBgAnimView.setLayoutParams(new LayoutParams(mPerWidth, b - t));
            mBgAnimView.layout(0, 0, mPerWidth, b - t);
            updateBgAnimLocation();
        }
    }

    private void updateBgAnimLocation() {
        if (mCurrSelectIndex >= 0 && mCurrSelectIndex < mViews.size()) {
            TextView view = mViews.get(mCurrSelectIndex);
            for (int i = 0; i < mViews.size(); i++) {
                if (i != mCurrSelectIndex && mViews.get(i).isSelected()) {
                    mViews.get(i).setSelected(false);
                }
            }
            view.setSelected(true);
            mBgAnimView.setX(view.getX());
            mBgAnimView.setVisibility(VISIBLE);
        }
    }

    private void init() {
        setBackgroundResource(R.drawable.white_shape_type_selection_bg);
        Context context = getContext();
        mStartColor = getResources().getColor(R.color.tab_layout_text_default_color);
        mEndColor = Color.WHITE;
        mColorList = getResources().getColorStateList(R.color.selector_tab_text_color);
        mBgAnimView = new View(context);
        mBgAnimView.setBackgroundResource(R.drawable.shape_type_selection_lite_bg);
        addView(mBgAnimView, 0);
        mBgAnimView.setVisibility(GONE);
        mDefaultPadding = getResources().getDimensionPixelSize(R.dimen.tab_layout_default_padding);
        mItemTextSize = getResources().getDimensionPixelSize(R.dimen.tab_layout_default_text_size);
    }

    public void setItems(String[] items, int selectIndex) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        boolean isExactly = false;
        if (layoutParams != null && layoutParams.height > mDefaultPadding) {
            isExactly = true;
        }
        if (items != null && items.length != 0) {
            for (int i = 0; i < items.length; i++) {
                TextView textView = new TextView(getContext());
                initItemView(textView, isExactly);
                textView.setText(items[i]);
                textView.setGravity(Gravity.CENTER);
                mViews.add(i, textView);
                if (isExactly) {
                    ConstraintLayout.LayoutParams params = new LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, layoutParams.height);
                    textView.setLayoutParams(params);
                }
                textView.setOnClickListener(this);
                addView(textView);
            }
        }
        if (selectIndex >= 0 && selectIndex < mViews.size()) {
            mCurrSelectIndex = selectIndex;
            mLastSelectIndex = selectIndex;
        }
    }

    private void initItemView(TextView textView, boolean isExactly) {
        textView.setTextColor(mColorList);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mItemTextSize);
        if (!isExactly) {
            textView.setPadding(0, mDefaultPadding, 0, mDefaultPadding);
        }
    }

    public void setItems(String[] items) {
        setItems(items, -1);
    }

    public void setSelectItem(int selectIndex) {
        if (selectIndex >= 0 && selectIndex < mViews.size() && mCurrSelectIndex != selectIndex) {
            mCurrSelectIndex = selectIndex;
            mLastSelectIndex = selectIndex;
            updateBgAnimLocation();
        }
    }

    public int getSelectIndex() {
        return mCurrSelectIndex;
    }

    public void addItemView(TextView view, int index) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        boolean isExactly = false;
        if (layoutParams != null && layoutParams.height > mDefaultPadding) {
            isExactly = true;
        }
        initItemView(view, isExactly);
        if (isExactly) {
            ConstraintLayout.LayoutParams params = new LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, layoutParams.height);
            view.setLayoutParams(params);
        }
        mViews.add(index, view);
        view.setOnClickListener(this);
        addView(view, index + 1);
        mCanRepeatClickIndex = index;
    }

    public void setItemPaddingTopBottom(int padding) {
        mDefaultPadding = padding;
        if (mViews == null || mViews.isEmpty()) {
            return;
        }
        for (View view : mViews) {
            view.setPadding(0, padding, 0, padding);
        }
    }

    public void setItemTextSize(float size) {
        mItemTextSize = size;
        if (mViews.isEmpty()) {
            return;
        }
        for (TextView view : mViews) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    @Override
    public void onClick(View v) {
        int currIndex = -1;
        TextView toView = null;
        for (int i = 0; i < mViews.size(); i++) {
            TextView view = mViews.get(i);
            if (view == v) {
                currIndex = i;
                toView = (TextView) v;
                break;
            }
        }
        if (currIndex != -1) {
            if (currIndex != mCurrSelectIndex) {
                mCurrSelectIndex = currIndex;
                if (mLastSelectIndex != -1) {
                    startChangeSelectAnimation(mViews.get(mLastSelectIndex), toView, currIndex);
                    mLastSelectIndex = currIndex;
                } else {
                    mLastSelectIndex = currIndex;
                    updateBgAnimLocation();
                }
                if (mListener != null) {
                    mListener.onItemClick(toView, currIndex);
                }
            } else if (currIndex == mCanRepeatClickIndex && mListener != null) {
                mListener.onItemClick(toView, currIndex);
            }
        }
    }

    private void startChangeSelectAnimation(@NonNull final TextView fromView, @NonNull final TextView toView, final int index) {
        if (fromView == toView) {
            return;
        }
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        final float startX = mBgAnimView.getX();
        final float distance = toView.getX() - startX;
        mValueAnimator = ValueAnimator.ofFloat(0, 100);
        mValueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            float fraction = animatedValue / 100f;
            toView.setTextColor(Utils.evaluateColor(fraction, mStartColor, mEndColor));
            fromView.setTextColor(Utils.evaluateColor(1 - fraction, mStartColor, mEndColor));
            mBgAnimView.setX(fraction * distance + startX);
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimListener != null) {
                    mAnimListener.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.OnItemClickAnimEnd(toView, index, mIsAnimCancel);
                }
                if (!mIsAnimCancel) {
                    mBgAnimView.setX(toView.getX());
                }
                fromView.setTextColor(mColorList);
                toView.setTextColor(mColorList);
                toView.setSelected(true);
                fromView.setSelected(false);
                mIsAnimCancel = false;
                if (mAnimListener != null) {
                    mAnimListener.onEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsAnimCancel = true;
                if (mAnimListener != null) {
                    mAnimListener.onCancel();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.setDuration(200);
        mValueAnimator.start();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(TextView view, int index);

        void OnItemClickAnimEnd(TextView view, int index, boolean cancel);
    }

    public void setOnTabAnimListener(OnTabAnimListener listener) {
        mAnimListener = listener;
    }

    public interface OnTabAnimListener {
        void onStart();

        void onEnd();

        void onCancel();
    }
}
