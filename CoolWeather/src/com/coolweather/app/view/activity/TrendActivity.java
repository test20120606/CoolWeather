package com.coolweather.app.view.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.view.widget.TrendView;


import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class TrendActivity extends Activity {
	private String[] temperatureArray,weatherArray,weather_idArray,WindArray,weekArray,dateArray;
	private String cityname;
	private TrendView trendview;
	private List<Integer> topTem;
	private List<Integer> lowTem;
	private TextView day1;
	private TextView day2;
	private TextView day3;
	private TextView day4;
	private TextView weather1;
	private TextView weather2;
	private TextView weather3;
	private TextView weather4;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.trend);
		day1 = (TextView) findViewById(R.id.day1);
		day2 = (TextView) findViewById(R.id.day2);
		day3 = (TextView) findViewById(R.id.day3);
		day4 = (TextView) findViewById(R.id.day4);
		weather1 = (TextView) findViewById(R.id.weather1);
		weather2 = (TextView) findViewById(R.id.weather2);
		weather3 = (TextView) findViewById(R.id.weather3);
		weather4 = (TextView) findViewById(R.id.weather4);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
//		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();		// 屏幕宽（像素，如：480px）
//	    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		trendview = (TrendView)findViewById(R.id.trendView);
	    trendview.setWidthHeight(width, height);
		Intent intent = getIntent();
		temperatureArray = new String[4];
		weatherArray = new String[4];
		weather_idArray = new String[4];
		weekArray = new String[4];
		cityname = intent.getStringExtra("cityname");
		temperatureArray = intent.getStringArrayExtra("temperature");
		weatherArray = intent.getStringArrayExtra("weather");
		weather_idArray = intent.getStringArrayExtra("weatherid");
		weekArray = intent.getStringArrayExtra("week");
		topTem = new ArrayList<Integer>();
		lowTem = new ArrayList<Integer>();
		Log.d("传送成功", "haha");
		for(String a :temperatureArray)
		{	
			int firstCIndex=a.indexOf('℃');
			int lastCIndex=a.lastIndexOf('℃');
			int index=a.indexOf('~');
			
			String c = a.substring(0,firstCIndex);
			int b = Integer.valueOf(c);
			lowTem.add(b);
			c = a.substring(index+1,lastCIndex);
			b = Integer.valueOf(c);
			topTem.add(b);
		}
		
		trendview.setTemperature(topTem, lowTem);
		trendview.setBitmap(weatherArray);
		day1.setText(weekArray[0]);
		day2.setText(weekArray[1]);
		day3.setText(weekArray[2]);
		day4.setText(weekArray[3]);
		weather1.setText(weatherArray[0]);
		weather2.setText(weatherArray[1]);
		weather3.setText(weatherArray[2]);
		weather4.setText(weatherArray[3]);
		
		
		
	}
}
