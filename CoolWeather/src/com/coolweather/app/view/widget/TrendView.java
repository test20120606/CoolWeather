package com.coolweather.app.view.widget;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.http.Constants;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
/**
 * ÃÏ∆¯«˜ ∆’€œﬂÕº
 * @author Dave
 *
 */
public class TrendView extends View {

	private Paint mPointPaint;
	private Paint mTextPaint;
	private Paint mLinePaint1;
	private Paint mLinePaint2;
	private Paint mbackLinePaint;

	private int x[] = new int[4];
	private float radius = 8;
	private int h;
	private List<Integer> topTem;
	private List<Integer> lowTem;
	private Bitmap[] topBmps;
	private Bitmap[] lowBmps;
	
	private Context c;

	public TrendView(Context context) {
		super(context);
		this.c = context;
		init();
	}
	public TrendView(Context context, AttributeSet attr) {
		super(context, attr);
		this.c = context;
		init();
	}
	private void init(){
		topBmps = new Bitmap[4];
		lowBmps = new Bitmap[4];
		
		topTem = new ArrayList<Integer>();
		lowTem = new ArrayList<Integer>();
		
		mbackLinePaint = new Paint();
		mbackLinePaint.setColor(Color.WHITE);

		mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(Color.WHITE);

		mLinePaint1 = new Paint();
		mLinePaint1.setColor(Color.YELLOW);
		mLinePaint1.setAntiAlias(true);
		mLinePaint1.setStrokeWidth(4);
		mLinePaint1.setStyle(Style.FILL);
		
		mLinePaint2 = new Paint();
		mLinePaint2.setColor(Color.BLUE);
		mLinePaint2.setAntiAlias(true);
		mLinePaint2.setStrokeWidth(4);
		mLinePaint2.setStyle(Style.FILL);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(25F);
		mTextPaint.setTextAlign(Align.CENTER); 
	}
	public void setPosition(int a, int b, int c, int d) {
		x[0] = a;
		x[1] = b;
		x[2] = c;
		x[3] = d;
	}

	public void setWidthHeight(int w, int h){
		x[0] = w/8;
		x[1] = w*3/8;
		x[2] = w*5/8;
		x[3] = w*7/8;
		
		this.h = h;
	}
	public void setTemperature(List<Integer> top, List<Integer> low){
		this.topTem = top;
		this.lowTem = low;
		
		postInvalidate();
	}
	public void setBitmap(String[] weather){
		/** Õ®π˝Õº∆¨idªÒµ√Bitmap  **/
		
		topBmps[0] = BitmapFactory.decodeResource(getResources(), getWeatherImg(weather[0]));
		topBmps[1] = BitmapFactory.decodeResource(getResources(), getWeatherImg(weather[1]));
		topBmps[2] = BitmapFactory.decodeResource(getResources(), getWeatherImg(weather[2]));
		topBmps[3] = BitmapFactory.decodeResource(getResources(), getWeatherImg(weather[3]));
//		topBmps[0] = WeatherPic.getSmallPic(c, topList.get(0), 0);
//		topBmps[1] = WeatherPic.getSmallPic(c, topList.get(1), 0);
//		topBmps[2] = WeatherPic.getSmallPic(c, topList.get(2), 0);
//		topBmps[3] = WeatherPic.getSmallPic(c, topList.get(3), 0);
//		
		lowBmps[0] = topBmps[0];
		lowBmps[1] = topBmps[1];
		lowBmps[2] = topBmps[2];
		lowBmps[3] = topBmps[3];
	}
	private int getWeatherImg(String weather) {
		int img = 0;
		if (weather.contains("◊™")) {
			weather = weather.substring(0, weather.indexOf("◊™"));
		}
		if (weather.contains("«Á")) {
			img = R.drawable.qing00;
		} else if (weather.contains("∂‡‘∆")) {
			img = R.drawable.duoyun01;
		} else if (weather.contains("“ı")) {
			img = R.drawable.yin02;
		} else if (weather.contains("’Û”Í")) {
			img = R.drawable.zhenyu03;
		} else if (weather.contains("¿◊’Û”Í")) {
			img = R.drawable.leizhenyu04;
		} else if (weather.contains("¿◊’Û”Í∞È”–±˘±¢")) {
			img = R.drawable.leibing05;
		} else if (weather.contains("”Íº–—©")) {
			img = R.drawable.yujiaxue06;
		} else if (weather.contains("–°”Í")) {
			img = R.drawable.xiaoyu07;
		} else if (weather.contains("÷–”Í")) {
			img = R.drawable.zhongyu08;
		} else if (weather.contains("¥Û”Í")) {
			img = R.drawable.dayu09;
		} else if (weather.contains("±©”Í")) {
			img = R.drawable.baoyu10;
		} else if (weather.contains("¥Û±©”Í")) {
			img = R.drawable.dabaoyu11;
		} else if (weather.contains("Ãÿ¥Û±©”Í")) {
			img = R.drawable.tedabao12;
		} else if (weather.contains("’Û—©")) {
			img = R.drawable.zhenxue13;
		} else if (weather.contains("–°—©")) {
			img = R.drawable.xiaoxue14;
		} else if (weather.contains("÷–—©")) {
			img = R.drawable.zhongxue15;
		} else if (weather.contains("¥Û—©")) {
			img = R.drawable.daxue16;
		} else if (weather.contains("±©—©")) {
			img = R.drawable.baoxue17;
		} else if (weather.contains("ŒÌ")) {
			img = R.drawable.wu18;
		} else if (weather.contains("∂≥”Í")) {
			img = R.drawable.dongyu19;
		} else if (weather.contains("…≥≥æ±©")) {
			img = R.drawable.shachengbao20;
		} else if (weather.contains("∏°≥æ")) {
			img = R.drawable.fuchen29;
		} else if (weather.contains("—Ô…≥")) {
			img = R.drawable.yangsha30;
		} else if (weather.contains("«ø…≥≥æ±©")) {
			img = R.drawable.qiangshachen31;
		} else if (weather.contains("ˆ≤")) {
			img = R.drawable.qiangshachen31;
		} 
		
		else {
			img = R.drawable.qing00;
		}
		return img;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float space = 0f;
		float space1 = 0f;
		int temspace = 8;
		
		FontMetrics fontMetrics = mTextPaint.getFontMetrics();  
		// º∆À„Œƒ◊÷∏ﬂ∂»  
		float fontHeight = fontMetrics.bottom - fontMetrics.top;  
		
		int h = this.h/2;
		int h2 = (int) (h - fontHeight/2);
		int h3 = (int) (h - fontHeight - Constants.picSize);
		 
		int h4 = (int) (h + fontHeight);
		int h5 = (int) (h + fontHeight);
		
		for (int i = 0; i < topTem.size(); i++) {
			space = ( - topTem.get(i)) * temspace;
			if(topTem.get(i) != 100){
				if (i != topTem.size() - 1) {
					space1 = ( - topTem.get(i+1)) * temspace;
					canvas.drawLine(x[i], h + space, x[i+1], h + space1, mLinePaint1);
				}
				canvas.drawText(topTem.get(i) + "°„", x[i], h2 + space, mTextPaint);
				canvas.drawCircle(x[i], h + space, radius, mPointPaint);
				//canvas.drawBitmap(topBmps[i], x[i]-topBmps[i].getWidth()/2, h3 + space, null);
			}
		}

		for (int i = 0; i < lowTem.size(); i++) {
			space = (-lowTem.get(i)) * temspace;
			if (i != lowTem.size() - 1) {
				space1 = ( - lowTem.get(i+1)) * temspace;
				canvas.drawLine(x[i], h + space, x[i+1], h + space1, mLinePaint2);
			} 
			canvas.drawText(lowTem.get(i) + "°„", x[i], h4 + space, mTextPaint);
			canvas.drawCircle(x[i], h + space, radius, mPointPaint);
			canvas.drawBitmap(lowBmps[i], x[i]-lowBmps[i].getWidth()/2, h5 + space, null);
		}
	}

}
