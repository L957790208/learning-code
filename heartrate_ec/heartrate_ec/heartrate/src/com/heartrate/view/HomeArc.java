package com.heartrate.view;

import com.heartrate.activity.R;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * 圆弧计分
 * @author Administrator
 *
 */
public class HomeArc extends View { 

	private Paint paint_black, paint_white,paint_step,paint_step_num;
	private RectF rectf,rectf_i;
	private float tb;
	//private int blackColor = 0xFFC400;
	private int huangColor = 0xFF3DCC79; //huang
	private int blackColor = 0x35000000;  // 底黑色 
	private int whiteColor = 0xddffffff;  // 白色
	private int score;
	private float arc_y = 0f;
	private int score_text,step_num;

	public HomeArc(Context context, int score ,int step_num) {
		super(context);
		init(score,step_num);
	}

	public void init(int score,int step_num) {
		this.score = score;
		this.step_num = step_num;
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
		
		paint_step = new Paint();
		paint_step.setAntiAlias(true);
		paint_step.setColor(whiteColor);
		paint_step.setTextSize(tb*2.5f);
		paint_step.setStrokeWidth(tb * 0.1f);
		paint_step.setTextAlign(Align.CENTER);
		//paint_step.setStyle(Style.STROKE);//空心		
		paint_step_num = new Paint();
		paint_step_num.setAntiAlias(true);
		paint_step_num.setColor(whiteColor);
		paint_step_num.setTextSize(tb*4.5f);
		paint_step_num.setStrokeWidth(tb * 0.1f);
		paint_step_num.setTextAlign(Align.CENTER);
		
		rectf = new RectF();
		rectf.set(tb * 0.5f, tb * 0.5f, tb * 18.5f, tb * 18.5f);
		rectf_i = new RectF();
		//rectf_i.set(tb * 0.5f, tb * 0.5f, tb * 18.5f, tb * 18.5f);
		rectf_i.set(tb * 0.5f-11, tb * 0.5f-11, tb * 18.5f + 11, tb * 18.5f +11);
		
		setLayoutParams(new LayoutParams((int) (tb * 19.5f), (int) (tb * 19.5f)));

		this.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						new thread();
						getViewTreeObserver().removeOnPreDrawListener(this);
						return false;
					}
				});
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Bitmap bitmap;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.walk_n);
		canvas.drawBitmap(bitmap, tb * 8f, tb * 2.2f, null);
		
		canvas.drawArc(rectf, -90, 360, false, paint_black);
		canvas.drawArc(rectf_i, -90, arc_y, false, paint_white);
		canvas.drawText(step_num + "", tb * 9.7f, tb * 11.0f, paint_step_num);
		canvas.drawText(score_text + "%", tb * 9.7f, tb * 16.0f, paint_step);
		
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
						score_text++;
						count++;
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
				if (count >= score)
					break;
			}
		}
	}

}
