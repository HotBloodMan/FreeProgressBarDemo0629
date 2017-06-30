package com.example.administrator.freeprogressbardemo0629;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/6/29/029.
 */

public class NumberProgressBar extends View {


    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_reached_color = Color.rgb(66, 145, 241);
    private final int default_unreached_color = Color.rgb(204, 204, 204);
    private int mMaxProgress = 100;
    private int mCurrentProgress = 0;
    private final float default_progress_text_offset;
    private final float default_text_size;
    private final float default_reached_bar_height;
    private final float default_unreached_bar_height;
    private int mReachedBarColor;
    private int mUnreachedBarColor;
    private int mTextColor;
    private float mTextSize;
    private float mReachedBarHeight;
    private float mUnreachedBarHeight;
    private float mOffset;

    /**
     * Determine if need to draw unreached area.
     */
    private boolean mIfDrawText=true;
    private boolean mDrawReachedBar = true;
    private boolean mDrawUnreachedBar = true;

    private static final int PROGRESS_TEXT_VISIBLE = 0;
    private Paint mReachedBarPaint;
    private Paint mUnreachedBarPaint;
    private Paint mTextPaint;
    private String mCurrentDrawText;

    //prefix
    private String mPrefix="";

    //The suffix of the number
    private String mSuffix="%";
    private float mDrawTextWidth;
    private int mDrawReachedBar1;

    private RectF mReachedRectF=new RectF(0,0,0,0);

    /**
     * Unreached bar area to draw rect.
     */
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    private float mDrawTextStart;
    private int mDrawTextEnd;

    private OnProgressBarListener mListener;

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_reached_bar_height=dp2px(1.5f);
        default_unreached_bar_height=dp2px(1.0f);
        default_progress_text_offset=dp2px(3.0f);
        default_text_size=sp2px(10);
        Log.d("TAG","111 NumberProgressBar初始化参数=default_reached_bar_height"+default_reached_bar_height+
                "default_unreached_bar_height"+default_unreached_bar_height+
                "default_progress_text_offset"+default_progress_text_offset+
                "default_text_size"+default_text_size);

        /*
        * 加载样式属性
        * */
        Log.d("TAG","222------- 加载开始 begin");
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberProgressBar
                ,defStyleAttr, 0);
        mReachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_reached_color, default_reached_color);
        mUnreachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_unreached_color, default_unreached_color);
        mTextColor = attributes.getColor(R.styleable.NumberProgressBar_progress_text_color, default_text_color);
        mTextSize = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_size, default_text_size);

        mReachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_reached_bar_height, default_reached_bar_height);
        mUnreachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_unreached_bar_height, default_unreached_bar_height);

        mOffset = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_offset, default_progress_text_offset);

        int textVisible=attributes.getInt(R.styleable.NumberProgressBar_progress_text_visibility,PROGRESS_TEXT_VISIBLE);

        Log.d("TAG","222 3333*******加载 textVisible= "+textVisible);
        if(textVisible!=PROGRESS_TEXT_VISIBLE){
            mIfDrawText=false;
        }
        setProgress(attributes.getInt(R.styleable.NumberProgressBar_progress_current,0));
        setMax(attributes.getInt(R.styleable.NumberProgressBar_progress_max,100));

        attributes.recycle();
        initializePainters();
        Log.d("TAG","222 *******加载结束 over");

    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int)mTextSize,Math.max((int)mReachedBarHeight,(int)mUnreachedBarHeight));
    }
    @Override
    protected int getSuggestedMinimumWidth() {
        return (int)mTextSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec,true),measure(heightMeasureSpec,false));
    }
    private int measure(int measureSpec,boolean isWidth){
        int result;
        int mode=MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding=isWidth ? getPaddingLeft()+getPaddingRight() : getPaddingTop() +getPaddingBottom();
        if(mode==MeasureSpec.EXACTLY){
            result=size;
        }else{
            result=isWidth?getSuggestedMinimumWidth():getSuggestedMinimumHeight();
            result += padding;
            if(mode==MeasureSpec.AT_MOST){
                result=Math.max(result,size);
            }else{
                result=Math.min(result,size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
       /*
       * 判断是否绘制进度数字
       * */
        if(mIfDrawText){
            Log.d("TAG","aaa--11111->>>calculateDrawRectF ");
            calculateDrawRectF();
        }else{
            Log.d("TAG","aaa--2222->>>calculateDrawRectFWithoutProgressText ");
            calculateDrawRectFWithoutProgressText();
        }
        /*
        *
        * */
        if(mDrawReachedBar){
            Log.d("TAG","aaa--33333->>>mDrawReachedBar=true ");
            canvas.drawRect(mReachedRectF,mReachedBarPaint);
        }

        if(mDrawUnreachedBar){
            Log.d("TAG","aaa--44444->>>mDrawUnreachedBar=true ");
            canvas.drawRect(mUnreachedRectF,mUnreachedBarPaint);
        }
        if(mIfDrawText){
            Log.d("TAG","aaa-555555-->>>mIfDrawText=true mDrawTextStart="+mDrawTextStart+
                    " mDrawTextEnd="+mDrawTextEnd
            +" mCurrentDrawText"+mCurrentDrawText);

            canvas.drawText(mCurrentDrawText,mDrawTextStart, mDrawTextEnd, mTextPaint);
        }
    }

    private void calculateDrawRectF() {
        mCurrentDrawText = String.format("%d", getProgress() * 100 / getMax());
        mCurrentDrawText=mPrefix +mCurrentDrawText +mSuffix;
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText);

        if(getProgress()==0){
            mDrawReachedBar=false;
            mDrawReachedBar1 = getPaddingLeft();
        }else{
            mDrawReachedBar=true;
            mReachedRectF.left=getPaddingLeft();
            mReachedRectF.top=getHeight()/2.0f -mReachedBarHeight/2.0f;
            mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() - mOffset + getPaddingLeft();
            mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
            mDrawTextStart = mReachedRectF.right + mOffset;
        }
        mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        if((mDrawTextStart+mDrawTextWidth)>=getWidth()-getPaddingRight()){
          mDrawTextStart=getWidth()-getPaddingRight()-mDrawTextWidth;
            mReachedRectF.right=mDrawTextStart-mOffset;
        }
        float unreachedBarStart = mDrawTextStart + mDrawTextWidth + mOffset;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) {
            mDrawUnreachedBar = false;
        } else {
            mDrawUnreachedBar = true;
            mUnreachedRectF.left = unreachedBarStart;
            mUnreachedRectF.right = getWidth() - getPaddingRight();
            mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
            mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
        }

    }

    private void calculateDrawRectFWithoutProgressText() {
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;

        mUnreachedRectF.left = mReachedRectF.right;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
        mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    private void setMax(int maxProgress) {
        if(maxProgress>0){
            this.mMaxProgress=maxProgress;
            invalidate();
        }
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setPrefix(String prefix) {
        if(prefix==null)
            mPrefix="";
        else{
            mPrefix=prefix;
        }
    }
    public void incrementProgressBy(int by){
        if(by>0){
            setProgress(getProgress()+by);
        }
        if(mListener!=null){
            mListener.onProgressChange(getProgress(),getMax());
        }
    }

    public int getProgress(){
        return mCurrentProgress;
    }
    public void setProgress(int progress) {
        if(progress<=getMax() && progress >=0){
            this.mCurrentProgress=progress;
            invalidate();
        }
    }
    public int getMax(){
        return mMaxProgress;
    }

    private float sp2px(float i) {
        final float scale=getResources().getDisplayMetrics().scaledDensity;
        return i*scale;
    }

    public float dp2px(float dp){
        final float scale=getResources().getDisplayMetrics().density;

        return dp*scale+0.5f;
    }
    public void setOnProgressBarListener(OnProgressBarListener listener){
        mListener=listener;
    }

}
