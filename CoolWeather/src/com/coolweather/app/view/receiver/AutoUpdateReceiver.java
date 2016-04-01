package com.coolweather.app.view.receiver;

import com.coolweather.app.view.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//响应后台的自动更新
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
	}

}
