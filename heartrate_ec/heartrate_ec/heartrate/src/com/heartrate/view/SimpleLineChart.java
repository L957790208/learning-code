package com.heartrate.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

import com.heartrate.activity.R;

/**
 * @author wing
 * @url http://blog.csdn.net/wingichoy
 * <p/>
 * 这是一个简约的折线图  适合展示一个趋势 而并非精确数据
 * <p/>
 * Created by Administrator on 2015/12/30.
 */
public class SimpleLineChart extends View {
    //View 的宽和高
    private int mWidth, mHeight;
    
    private int mWidthLine, mHeightLine;
    //Y轴字体的大小
    private float mYAxisFontSize = 24;

    //线的颜色
    private int mLineColor = Color.parseColor("#00BCD4");

    //线条的宽度
    private float mStrokeWidth = 6.0f;

    //点的集合
    private ArrayList<Integer> mPointArray;

    //点的半径
    private float mPointRadius = 3;

    //没有数据的时候的内容
    private String mNoDataMsg = "no data";

    //X轴的文字
    private String[] mXAxis = {"1","2","3","4","5"};

    //Y轴的文字
    private String[] mYAxis = {"180","170","160","150","140","130","120","110","100","90","80","70","60","50","40"};

    public SimpleLineChart(Context context) {
        this(context, null);
        System.out.println("........1..........");
    }

    public SimpleLineChart(Context context, AttributeSet attrs) {    	
        this(context, attrs, 0);
        System.out.println(".........2..........");
    }

    public SimpleLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        System.out.println(".........3..........");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){
    		if(!isInEditMode()){
    			//造成错误的代码段
    			   throw new IllegalArgumentException("width must be EXACTLY,you should set like android:width=\"200dp\"");
    		}         
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }else if(widthMeasureSpec == MeasureSpec.AT_MOST){
            throw new IllegalArgumentException("height must be EXACTLY,you should set like android:height=\"200dp\"");
        }
        setMeasuredDimension(mWidth, mHeight);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(mXAxis.length==0||mYAxis.length==0){
       		if(!isInEditMode()){
    			//造成错误的代码段
       		 throw new IllegalArgumentException("X or Y items is null");
    		}       	           
        }
        //画坐标线的轴
        Paint axisPaint = new Paint();
        axisPaint.setTextSize(mYAxisFontSize);
        axisPaint.setColor(Color.parseColor("#3F51B5"));
        if (mPointArray == null || mPointArray.size() == 0) {
            int textLength = (int) axisPaint.measureText(mNoDataMsg);
            canvas.drawText(mNoDataMsg, mWidth/2 - textLength/2, mHeight/2, axisPaint);
        } else {
            //画 Y 轴
            //存放每个Y轴的坐标
            //计算Y轴 每个刻度的间距
            mWidthLine = mWidth;
            mHeightLine = mHeight - 40;
            int yInterval = (int) (mHeightLine / 14);
            //测量Y轴文字的高度 用来画第一个数
            for (int i = 0; i < mYAxis.length; i++) {
                canvas.drawText(mYAxis[i], 0,  i * yInterval, axisPaint);
            }
            //画 X 轴
            //计算Y轴开始的原点坐标
            //X轴偏移量
            int xOffset = 100;
            //计算x轴 刻度间距
            int xInterval = (int) ((mWidth - xOffset) / (mXAxis.length));
            //获取X轴刻度Y坐标
            for (int i = 0; i < mXAxis.length; i++) {
                canvas.drawText(mXAxis[i], (i+1) * xInterval+50, mHeight, axisPaint);
            }
            //画点
            Paint pointPaint = new Paint();
            pointPaint.setColor(mLineColor);
            Paint linePaint = new Paint();
            linePaint.setColor(mLineColor);
            linePaint.setAntiAlias(true);
            //设置线条宽度
            //linePaint.setStrokeWidth(mStrokeWidth);
            linePaint.setStrokeWidth(1);
            //x y轴线         
            canvas.drawLine(50, 0, 50, mHeightLine, linePaint);
            canvas.drawLine(50, mHeightLine, mWidthLine,mHeightLine, linePaint);

            pointPaint.setStyle(Paint.Style.FILL);
            for (int i = 1; i < 15; i++) {
            	 canvas.drawLine(50, (mHeightLine / 14) * i, mWidthLine ,(mHeightLine / 14) * i, linePaint);
			}            
            for (int i = 0; i < mPointArray.size(); i++) {
                if (mPointArray.get(i) == null) {
                    throw new IllegalArgumentException("PointMap has incomplete data!");
                }
                //画点              
                canvas.drawCircle(i*3 + 50, mHeightLine -  ((mHeightLine / 140) * (mPointArray.get(i) - 40)), 
                		mPointRadius, pointPaint);
                if (i > 0) {
                    //canvas.drawLine((i-1)*10 + 50, mPointArray.get(i - 1), i*10 + 50, mPointArray.get(i), linePaint);
                }
            }
        }
    }

    /**
     * 设置map数据
     * @param data
     */
    public void setData(ArrayList<Integer> data){
    	mPointArray = data;
        invalidate();
    }

    /**
     * 设置Y轴文字
     * @param yItem
     */
    public void setYItem(String[] yItem){
        mYAxis = yItem;
    }

    /**
     * 设置X轴文字
     * @param xItem
     */
    public void setXItem(String[] xItem){
        mXAxis = xItem;
    }

    public void setLineColor(int color){
        mLineColor = color;
        invalidate();
    }
	public void updateData(int data){
    	mPointArray.add(data);
		postInvalidate();
	}    
    
}






