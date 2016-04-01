package com.coolweather.app.view.activity;

import com.coolweather.app.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class WelcomeAcivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);

		Runnable run = new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(WelcomeAcivity.this, ChooseAreaActivity.class);
				WelcomeAcivity.this.startActivity(intent);
				WelcomeAcivity.this.finish();
			}
		};
		Handler handler = new Handler();
		handler.postDelayed(run, 1 * 1000);
	}

}
