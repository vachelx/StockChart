package com.vachel.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.support.v7.widget.AppCompatTextView;

/**
 * 文字和drawableRight一起居中的TextView
 *
 */
public class RightDrawableCenterTextView extends AppCompatTextView {
    public RightDrawableCenterTextView(Context context) {
        this(context, null);
    }

    public RightDrawableCenterTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightDrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER | Gravity.RIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawable = drawables[2];
        if (drawable != null) {
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawable.getMinimumWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding + getPaddingLeft() + getPaddingRight();
            canvas.translate((bodyWidth - getWidth()) / 2, 0);
        }
        super.onDraw(canvas);
    }
}
