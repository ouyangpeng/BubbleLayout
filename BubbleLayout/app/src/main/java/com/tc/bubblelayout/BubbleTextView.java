package com.tc.bubblelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * author：   tc
 * date：      2018/3/14 & 10:04
 * version    1.0
 * description 透明气泡view
 * modify by
 */
public class BubbleTextView extends AppCompatTextView {
    private static final String TAG = "BubbleTextView";
    private Path srcPath = new Path();
    private int mHeight;
    private int mWidth;
    private RectF mRoundRect;
    /**
     * 上弧线控制点和下弧线控制点
     */
    private PointF topControl, bottomControl;

    /**
     * 气泡图形右侧留空区域宽度
     */
    private int mWidthDiff;
    /**
     * 右上角圆角的半径
     */
    private int mRoundRadius;
    /**
     * 是否是右侧气泡
     */
    private boolean mIsRightPop;
    private int mLeftTextPadding;
    private int mRightTextPadding;

    /**
     * 加载时背景色
     */
    private int mLoadingBackColor;
    private int mDefaultPadding;
    private int mDefaultCornerPadding;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    public BubbleTextView(Context context) {
        this(context, null);
    }

    public BubbleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        topControl = new PointF(0, 0);
        bottomControl = new PointF(0, 0);
        mRoundRect = new RectF();
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.BubbleTextView);
        mLoadingBackColor = attr.getColor(R.styleable.BubbleTextView_BubbleTextView_backgroundColor, getResources()
                .getColor(R.color.color_ffffff));
        mLeftTextPadding = attr.getDimensionPixelOffset(R.styleable.BubbleTextView_BubbleTextView_leftTextPadding,
                DensityUtil.dip2px(context, 12));
        mRightTextPadding = attr.getDimensionPixelOffset(R.styleable.BubbleTextView_BubbleTextView_rightTextPadding,
                DensityUtil.dip2px(context, 12));
        attr.recycle();
        //左侧或右侧留出的空余区域
        mWidthDiff = DensityUtil.dip2px(getContext(), 7);
        //圆角的半径
        mRoundRadius = DensityUtil.dip2px(getContext(), 8);
        //默认一个字的时候的间隔
        mDefaultPadding = DensityUtil.dip2px(getContext(), 16);
        mDefaultCornerPadding = DensityUtil.dip2px(getContext(), 3);
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG);
        setTextPadding(mRightTextPadding, mLeftTextPadding);
    }


    private void initValues() {
        if (mIsRightPop) {
            //设置犄角的控制横坐标xy
            topControl.x = mWidth - DensityUtil.dip2px(getContext(), 2);
            topControl.y = mRoundRadius;
            bottomControl.x = mWidth - DensityUtil.dip2px(getContext(), 1);
            bottomControl.y = mRoundRadius + DensityUtil.dip2px(getContext(), 6);
        } else {
            //设置犄角的控制横坐标xy
            topControl.x = DensityUtil.dip2px(getContext(), 2);
            topControl.y = mRoundRadius;
            bottomControl.x = DensityUtil.dip2px(getContext(), 1);
            bottomControl.y = mRoundRadius + DensityUtil.dip2px(getContext(), 6);
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    public void judgePadding() {
        int length = getText().length();
        if (length == 1) {
            setTextPadding(mDefaultPadding, mDefaultPadding);
        } else {
            setTextPadding(mRightTextPadding, mLeftTextPadding);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
//        LogUtil.i(TAG, getText() + "  getPaddingLeft" + getPaddingLeft() + "  getPaddingRight" + getPaddingRight());
        initValues();
        srcPath.reset();
        if (mIsRightPop) {
            mRoundRect.set(0, 0, mWidth - mWidthDiff, mHeight);
            srcPath.addRoundRect(mRoundRect, mRoundRadius, mRoundRadius, Path.Direction.CW);
            //给path增加右侧的犄角，形成气泡效果
            srcPath.moveTo(mWidth - mWidthDiff, mRoundRadius);
            srcPath.quadTo(topControl.x, topControl.y, mWidth, mRoundRadius - mDefaultCornerPadding);
            srcPath.quadTo(bottomControl.x, bottomControl.y, mWidth - mWidthDiff,
                    mRoundRadius + mWidthDiff);
        } else {
            mRoundRect.set(mWidthDiff, 0, mWidth, mHeight);
            srcPath.addRoundRect(mRoundRect, mRoundRadius, mRoundRadius, Path.Direction.CW);
            //给path增加右侧的犄角，形成气泡效果
            srcPath.moveTo(mWidthDiff, mRoundRadius);
            srcPath.quadTo(topControl.x, topControl.y, 0, mRoundRadius - mDefaultCornerPadding);
            srcPath.quadTo(bottomControl.x, bottomControl.y, mWidthDiff, mRoundRadius + mWidthDiff);
        }
        canvas.clipPath(srcPath);
        if (mLoadingBackColor != 0) {
            canvas.drawColor(mLoadingBackColor);
        }
        super.onDraw(canvas);

    }

    private void setTextPadding(int rightTextPadding, int leftTextPadding) {
        if (mIsRightPop) {
            setPadding(leftTextPadding, getPaddingTop(), rightTextPadding + mWidthDiff, getPaddingBottom());
        } else {
            setPadding(leftTextPadding + mWidthDiff, getPaddingTop(), rightTextPadding, getPaddingBottom());
        }
    }

    public void setLoadingBackColor(int loadingBackColor) {
        mLoadingBackColor = getResources().getColor(loadingBackColor);
    }

    public void setLeftTextPadding(int leftTextPadding) {
        mLeftTextPadding = DensityUtil.dip2px(getContext(), leftTextPadding);
    }

    public void setRightTextPadding(int rightTextPadding) {
        mRightTextPadding = DensityUtil.dip2px(getContext(), rightTextPadding);
    }

    public void updateView() {
        judgePadding();
        invalidate();
    }

    /**
     * 设置圆角的半径
     *
     * @param roundRadius
     */
    public void setRoundRadius(int roundRadius) {
        mRoundRadius = DensityUtil.dip2px(getContext(), roundRadius);
    }


    /**
     * 是否是右侧气泡
     *
     * @param rightPop 是否是右侧气泡 false则为左侧气泡
     */
    public void setRightPop(boolean rightPop) {
        mIsRightPop = rightPop;
    }
}
