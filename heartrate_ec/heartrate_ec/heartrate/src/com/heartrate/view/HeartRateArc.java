package com.heartrate.view;

import com.heartrate.activity.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * 心率血氧圆环
 * @author Administrator
 *
 */
public class HeartRateArc extends View { 

	private Paint paint_black, paint_white,paint_heartRate,paint_spo2h;
	private RectF rectf,rectf_i;
	private float tb;
	//private int blackColor = 0xFFC400;
	private int huangColor = 0xFF3DCC79; //huang
	private int blackColor = 0x35000000;  // 底黑色 
	private int whiteColor = 0xddffffff;  // 白色
	private int heartRate,spo2h;
	private float arc_y = 0f;
	private int step_num;

	public HeartRateArc(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public HeartRateArc(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setData(0);		
	}
	public HeartRateArc(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}	
	public void setData(int heartRate){
		init(heartRate);
	}
	public void updateData(int heartRate){
		init(heartRate);
		//invalidate();
		postInvalidate();
	}
	
	
	public void init(int heartRate) {
		this.heartRate = heartRate;
	//	this.spo2h = spo2h;
		Resources res = getResources();
		tb = res.getDimension(R.dimen.historyscore_tb);

		paint_black = new Paint();
		paint_black.setAntiAlias(true);
		paint_black.setColor(blackColor);
		paint_black.setStrokeWidth(tb * 1.0f);
		paint_black.setStyle(Style.STROKE);

		paint_white = new Paint();
		paint_white.setAntiAlias(true);
		paint_white.setColor(huangColor);
		paint_white.setStrokeWidth(5);
		paint_white.setTextAlign(Align.CENTER);
		paint_white.setStyle(Style.STROKE);
		
		paint_heartRate = new Paint();
		paint_heartRate.setAntiAlias(true);
		paint_heartRate.setColor(whiteColor);
		paint_heartRate.setTextSize(tb*1.8f);
		paint_heartRate.setStrokeWidth(tb * 0.1f);
		paint_heartRate.setTextAlign(Align.CENTER);
		//paint_step.setStyle(Style.STROKE);//空心		
		paint_spo2h = new Paint();
		paint_spo2h.setAntiAlias(true);
		paint_spo2h.setColor(whiteColor);
		paint_spo2h.setTextSize(tb*4.5f);
		paint_spo2h.setStrokeWidth(tb * 0.1f);
		paint_spo2h.setTextAlign(Align.CENTER);
		
		rectf = new RectF();
		rectf.set(tb * 0.5f, tb * 0.5f, tb * 18.5f, tb * 18.5f);
		rectf_i = new RectF();
		rectf_i.set(tb * 0.5f-11, tb * 0.5f-11, tb * 18.5f + 11, tb * 18.5f +11);

/*		this.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						new thread();
						getViewTreeObserver().removeOnPreDrawListener(this);
						return false;
					}
				});*/
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Bitmap bitmap;
		//bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.walk_p);
		//canvas.drawBitmap(bitmap, tb * 8f, tb * 2.2f, null);
		
		canvas.drawArc(rectf, -90, 360, false, paint_black);
		canvas.drawArc(rectf_i, -90, arc_y, false, paint_white);
		if (heartRate == 0) {
			canvas.drawText("--", tb * 8.0f, tb * 8.2f, paint_heartRate);
		}else {
			canvas.drawText(""+heartRate, tb * 8.0f, tb * 8.2f, paint_heartRate);
		}
		canvas.drawText("BPM", tb * 12.5f, tb * 8.2f, paint_heartRate);
	}

	class thread implements Runnable {
		private Thread thread;
		private int statek;
		int count;

		public thread() {
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			arc_y = 0;
			while (true) {
				
				switch (statek) {
				case 0:
					try {
						Thread.sleep(200);
						statek = 1;
					} catch (InterruptedException e) {
					}
					break;
				case 1:
					try {
						Thread.sleep(15);
						arc_y += 3.6f;
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

}
