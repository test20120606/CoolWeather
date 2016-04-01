package com.coolweather.app.http;

public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
