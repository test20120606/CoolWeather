package com.coolweather.app.view.activity;


import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.coolweather.app.R;
import com.coolweather.app.http.HttpCallbackListener;
import com.coolweather.app.http.HttpUtil;
import com.coolweather.app.http.Utility;
import com.coolweather.app.view.service.AutoUpdateService;








import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {
	/*
	 * 切换城市和刷新按钮
	 */
	private Button switchCity;
	private Button refreshWeather;
	private Builder builder;
	/*
	 * 与存储相关的量
	 */
	private String[] temperatureArray,weatherArray,weather_idArray,WindArray,weekArray,dateArray;
	private String cityname,publishTime;
	public String countyName;

	/*
	 * 定位相关变量
	 */
	private LocationClient mLocationClient;
	private TextView LocationResult;
	private Button startLocation;
//	private LinearLayout weatherinfoLayout;
	/*
	 * 各种需显示的信息
	 */
//	private TextView cityNameText;
//	private TextView publishText;
//	private TextView weatherDespText;
//	private TextView temp1Text;
//	private TextView temp2Text;
//	private TextView currentDateText;
	/*
	 * 新界面显示需要
	 */
	public Intent intent;
	public static Handler handler;
	public static WeatherActivity context;
	private LinearLayout weatherBg;
	private LinearLayout titleBarLayout;
	private LinearLayout changeCity;
	private TextView cityText;
	private ImageView share;
	private ImageView about;
	private ImageView refresh;
	private ImageView location;
	private ProgressBar refreshing;
	private TextView updateTimeText;
	private ScrollView scrollView;
	private LinearLayout currentWeatherLayout;
	private ImageView weatherIcon;
	private TextView currentTemperatureText;
	private TextView currentWeatherText;
	private TextView temperatureText;
	private TextView windText;
	private TextView dateText;
	private ListView weatherForecastList;
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather);
		
		/*
		 * 定位
		 */
		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		//初始化各个控件
//		weatherinfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
//		cityNameText = (TextView) findViewById(R.id.city_name);
//		publishText = (TextView) findViewById(R.id.publish_text);
//		weatherDespText = (TextView) findViewById(R.id.weather_desp);
//		temp1Text = (TextView) findViewById(R.id.temp1);
//		temp1Text.setText("hhhh");
//		temp2Text = (TextView) findViewById(R.id.temp2);
//		currentDateText = (TextView) findViewById(R.id.current_date);
		//新界面初始化
		weatherBg = (LinearLayout) findViewById(R.id.weather_bg);
		titleBarLayout = (LinearLayout) findViewById(R.id.title_bar_layout);
		changeCity = (LinearLayout) findViewById(R.id.change_city_layout);
		cityText = (TextView) findViewById(R.id.city);
		share = (ImageView) findViewById(R.id.share);
		about = (ImageView) findViewById(R.id.about);
		refresh = (ImageView) findViewById(R.id.refresh);
		location = (ImageView) findViewById(R.id.location);
		refreshing = (ProgressBar) findViewById(R.id.refreshing);
		updateTimeText = (TextView) findViewById(R.id.update_time);
		scrollView = (ScrollView) findViewById(R.id.scroll_view);
		currentWeatherLayout = (LinearLayout) findViewById(R.id.current_weather_layout);
		weatherIcon = (ImageView) findViewById(R.id.weather_icon);
		currentTemperatureText = (TextView) findViewById(R.id.current_temperature);
		currentWeatherText = (TextView) findViewById(R.id.current_weather);
		temperatureText = (TextView) findViewById(R.id.temperature);
		windText = (TextView) findViewById(R.id.wind);
		dateText = (TextView) findViewById(R.id.date);
		weatherForecastList = (ListView) findViewById(R.id.weather_forecast_list);
		changeCity.setOnClickListener(new ButtonListener());
		share.setOnClickListener(new ButtonListener());
		about.setOnClickListener(new ButtonListener());
		refresh.setOnClickListener(new ButtonListener());
		location.setOnClickListener(new ButtonListener());
//		Typeface face = Typeface.createFromAsset(getAssets(),
//				"fonts/HelveticaNeueLTPro-Lt.ttf");
//		currentTemperatureText.setTypeface(face);
		//setCurrentWeatherLayoutHight();
		//捕获查询的县级代码(改)
		countyName = getIntent().getStringExtra("county_name");
		
		
		if(!TextUtils.isEmpty(countyName))
		{
//			publishText.setText("同步中");
//			//数据还没获取，先不进行显示，免得显示之前的数据造成错误
//			weatherinfoLayout.setVisibility(View.INVISIBLE);
//			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyName);
		}
		
		else {
//			publishText.setText("同步失败。。。。");
			//没有县级代号就直接呈现之前的天气信息
			showWeather();
		}
		
		/*
		 * 初始化城市及刷新按钮，绑定监听器。
		 */
//		switchCity =(Button) findViewById(R.id.switch_city);
//		refreshWeather = (Button) findViewById(R.id.refresh_weather);
//		switchCity.setOnClickListener(this);
//		refreshWeather.setOnClickListener(this);
		
	}
	//老程序的监听
//	@Override
//	public void onClick(View v) {
//		switch(v.getId()){
//		//返回主界面按钮
//		case R.id.switch_city:
//			Intent intent  = new Intent(this,ChooseAreaActivity.class);
//			intent.putExtra("from_weather_activity", true);//标志
//			startActivity(intent);
//			finish();
//			break;
//		case R.id.refresh_weather:
//		//	publishText.setText("同步中");
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//			String weatherCode = prefs.getString("weather_code", "");
//			if(!TextUtils.isEmpty(weatherCode)){
//				queryWeatherInfo(weatherCode);
//			}
//			break;
//		default : break;
//			
//			
//			
//		}
//		
//	}
	
	/*
	 * 根据城市名转换格式并瓶装地址，查询城市天气
	 */

	private void queryWeatherCode(String city) {
		//String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		
		try {
//			Log.i("info","---------------->>>>>>>>");
			String str = new String(city.getBytes(), "UTF-8");  
			city = URLEncoder.encode(str, "UTF-8");  
			
		} catch (Exception e1) {
			
			e1.printStackTrace();
		} 
		
		final String address = "http://v.juhe.cn/weather/index?format=2&cityname="+city+
				"&key=8a2dfd4e7e7a27593e42f540e251a464";
		queryFromServer(address,"weatherCode");
		
	}
	
	/*
	 * 查询天气代号对应的天气信息
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo//"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}
	
	/*
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(final String address, final String type) {
  		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
//				if("countyCode".equals(type)){
//					if(!TextUtils.isEmpty(type)){
//						//从服务器返回数据中解析出天气代号
//						String[] array = response.split("\\|");
//						if(array != null && array.length == 2){
//							String weatherCode = array[1];
//							queryWeatherInfo(weatherCode);
//						}
//					}
//				}
				
				 if("weatherCode".equals(type)) {
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread( new Runnable() {
					public void run() {
						//publishText.setText("同步失败");
					}
				});
				
			}
		});
	}

	/*
	 * 从sharedpreferences文件中读取存成的天气信息，并显示到界面上
	 */
	
	private void showWeather() {
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		initdataFromshared();
		
		updateWeatherImage();
		updateWeatherInfo();
		
//		weatherinfoLayout.setVisibility(View.VISIBLE);
//		cityNameText.setVisibility(View.VISIBLE);
		/*
		 * 后台自动更新
		 */
//		Intent i = new Intent(this,AutoUpdateService.class);
//		startService(i);
		
	}
	/*
	 * 天气信息各组数据的解析及存储
	 */
	private void initdataFromshared()
	{
		temperatureArray = new String[4];
		weatherArray = new String[4];
		weather_idArray = new String[4];
		WindArray = new String[4];
		dateArray = new String[4];
		weekArray = new String[4];
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityname = prefs.getString("city_name", "");
		publishTime = prefs.getString("publish_time", "");
		JSONArray temp;
		
		try {
			temp = new JSONArray(prefs.getString("temperature", ""));
			for(int i=0;i<temp.length();i++)
			{
				temperatureArray[i] = temp.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			temp = new JSONArray(prefs.getString("weather", ""));
			for(int i=0;i<temp.length();i++)
			{
				weatherArray[i] = temp.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			temp = new JSONArray(prefs.getString("weather_id", ""));
			for(int i=0;i<temp.length();i++)
			{
				weather_idArray[i] = temp.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			temp = new JSONArray(prefs.getString("wind", ""));
			for(int i=0;i<temp.length();i++)
			{
				WindArray[i] = temp.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			temp = new JSONArray(prefs.getString("week", ""));
			for(int i=0;i<temp.length();i++)
			{
				weekArray[i] = temp.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			temp = new JSONArray(prefs.getString("date", ""));
			for(int i=0;i<temp.length();i++)
			{
				dateArray[i] = temp.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		
//			saveArray(context, temperature, editor, "temperature");
//				saveArray(context, weather, editor, "weather");
//				saveArray(context, weather_id, editor, "weather_id");
//				saveArray(context, wind, editor, "wind");
//				saveArray(context, week, editor, "week");
//				saveArray(context, date, editor, "date");
//		private String[] temperatureArray,weatherArray,weather_idArray,WindArray,weekArray,dateArray;
//		private String cityname,publishTime;

	}
	
	private void updateWeatherImage() {
		//scrollView.setVisibility(View.VISIBLE);
		String currentWeather = weatherArray[0];
		if (null != currentWeather && currentWeather.contains("转")) {
			currentWeather = currentWeather.substring(0,
					currentWeather.indexOf("转"));
		}
		Time time = new Time();
		time.setToNow();
		weatherIcon.setImageResource(getWeatherImg(currentWeather));
		
	}
	
	
	/**
	 * 根据天气信息设置天气图片
	 * 
	 * @param weather
	 *            天气信息
	 * @return 对应的天气图片id
	 */
	private int getWeatherImg(String weather) {
		int img = 0;
		if (weather.contains("转")) {
			weather = weather.substring(0, weather.indexOf("转"));
		}
		if (weather.contains("晴")) {
			img = R.drawable.qing00;
		} else if (weather.contains("多云")) {
			img = R.drawable.duoyun01;
		} else if (weather.contains("阴")) {
			img = R.drawable.yin02;
		} else if (weather.contains("阵雨")) {
			img = R.drawable.zhenyu03;
		} else if (weather.contains("雷阵雨")) {
			img = R.drawable.leizhenyu04;
		} else if (weather.contains("雷阵雨伴有冰雹")) {
			img = R.drawable.leibing05;
		} else if (weather.contains("雨夹雪")) {
			img = R.drawable.yujiaxue06;
		} else if (weather.contains("小雨")) {
			img = R.drawable.xiaoyu07;
		} else if (weather.contains("中雨")) {
			img = R.drawable.zhongyu08;
		} else if (weather.contains("大雨")) {
			img = R.drawable.dayu09;
		} else if (weather.contains("暴雨")) {
			img = R.drawable.baoyu10;
		} else if (weather.contains("大暴雨")) {
			img = R.drawable.dabaoyu11;
		} else if (weather.contains("特大暴雨")) {
			img = R.drawable.tedabao12;
		} else if (weather.contains("阵雪")) {
			img = R.drawable.zhenxue13;
		} else if (weather.contains("小雪")) {
			img = R.drawable.xiaoxue14;
		} else if (weather.contains("中雪")) {
			img = R.drawable.zhongxue15;
		} else if (weather.contains("大雪")) {
			img = R.drawable.daxue16;
		} else if (weather.contains("暴雪")) {
			img = R.drawable.baoxue17;
		} else if (weather.contains("雾")) {
			img = R.drawable.wu18;
		} else if (weather.contains("冻雨")) {
			img = R.drawable.dongyu19;
		} else if (weather.contains("沙尘暴")) {
			img = R.drawable.shachengbao20;
		} else if (weather.contains("浮尘")) {
			img = R.drawable.fuchen29;
		} else if (weather.contains("扬沙")) {
			img = R.drawable.yangsha30;
		} else if (weather.contains("强沙尘暴")) {
			img = R.drawable.qiangshachen31;
		} else if (weather.contains("霾")) {
			img = R.drawable.qiangshachen31;
		} 
		
		else {
			img = R.drawable.qing00;
		}
		return img;
	}
	
	
class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.change_city_layout://更换城市
				intent = new Intent();
				intent.setClass(WeatherActivity.this, ChooseAreaActivity.class);
				WeatherActivity.this.startActivityForResult(intent, 100);
				break;
			case R.id.share://分享按钮
				intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友分享");
				intent.putExtra(Intent.EXTRA_TEXT,
						"我正在使用DQ天气，可以随时随地查看天气信息，是您出差、旅行的贴心助手！推荐你也试试~");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				WeatherActivity.this
						.startActivity(Intent.createChooser(intent, "好友分享"));
				break;
			case R.id.about://关于本软的开发信息
				LayoutInflater inflater = getLayoutInflater();
				View dialogLayout = inflater.inflate(R.layout.weather_dialog,
						(ViewGroup) findViewById(R.layout.weather_dialog));
				TextView version = (TextView) dialogLayout
						.findViewById(R.id.version);
				
				version.setText("实现功能如下：\n1.全国各省市的数据库动态加载\n2.获取聚合数据的天气预报接口并呈现\n" +
						"3.定位功能\n4.天气温度曲线图\n5.欢迎界面，分享及说明按钮\n欢迎使用，谢谢");
				builder = new Builder(WeatherActivity.this);
				builder.setTitle("关于");
				builder.setView(dialogLayout);
				builder.setPositiveButton("确定", null);
				builder.setCancelable(false);
				builder.show();
				break;
			case R.id.refresh://更新按钮
//				if (Utils.checkNetwork(Weather.this) == false) {
//					Toast.makeText(Weather.this, "网络异常,请检查网络设置",
//							Toast.LENGTH_SHORT).show();
//					return;
//				}
//				updateWeather();
				Intent intentTrend = new Intent(WeatherActivity.this, TrendActivity.class);
				intentTrend.putExtra("cityname", cityname);
				intentTrend.putExtra("temperature", temperatureArray);
				intentTrend.putExtra("weatherid", weather_idArray);
				intentTrend.putExtra("weather", weatherArray);
				intentTrend.putExtra("week", weekArray);
				startActivity(intentTrend);
				
				break;
			case R.id.location:
				dialog = new ProgressDialog(WeatherActivity.this);
				dialog.setMessage("正在定位...");
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				InitLocation();
				mLocationClient.start();
				break;
			default:
				break;
			}
		}

	}
	
	/**
	 * 更新界面（天气信息）
	 */
	private void updateWeatherInfo() {
		cityText.setText(cityname);
		currentTemperatureText.setText(temperatureArray[0]);
		currentWeatherText.setText(weatherArray[0]);
		temperatureText.setText(temperatureArray[0]);
		windText.setText(WindArray[0]);
		Time time = new Time();
		time.setToNow();
		String date = new SimpleDateFormat("MM/dd").format(new Date());
		dateText.setText(weekArray[0] + " " + date);
		String updateTime = publishTime;
		
		updateTimeText.setText(updateTime + " 更新");
		currentWeatherLayout.setVisibility(View.VISIBLE);
		weatherForecastList.setAdapter(new MyAdapter(WeatherActivity.this));
		scrollView.setVisibility(View.VISIBLE);
		
//		Utility.setListViewHeightBasedOnChildren(weatherForecastList);
	}
	
	
	class MyAdapter extends BaseAdapter {

		private Context mContext;

		private MyAdapter(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return getData().size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		//列表每一项横向的内容
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.weather_forecast_item, null);
				holder = new ViewHolder();
				holder.date = (TextView) convertView
						.findViewById(R.id.weather_forecast_date);
				holder.img = (ImageView) convertView
						.findViewById(R.id.weather_forecast_img);
				holder.weather = (TextView) convertView
						.findViewById(R.id.weather_forecast_weather);
				holder.temperature = (TextView) convertView
						.findViewById(R.id.weather_forecast_temperature);
				holder.wind = (TextView) convertView
						.findViewById(R.id.weather_forecast_wind);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
//			Typeface face = Typeface.createFromAsset(getAssets(),
//					"fonts/fangzhenglantingxianhe_GBK.ttf");
			holder.date.setText(getData().get(position).get("date").toString());
			holder.img.setImageResource((Integer) getData().get(position).get(
					"img"));
			holder.weather.setText(getData().get(position).get("weather")
					.toString());
			holder.temperature.setText(getData().get(position)
					.get("temperature").toString());
		//	holder.temperature.setTypeface(face);
			holder.wind.setText(getData().get(position).get("wind").toString());
			return convertView;
		}

	}

	class ViewHolder {
		TextView date;
		ImageView img;
		TextView weather;
		TextView temperature;
		TextView wind;
	}
	/**
	 * 获取天气预报信息
	 * 
	 * @return 天气预报list
	 */
	private ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 4; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (i == 0) {
				map.put("date", "今天    ");
			} else {
				map.put("date", weekArray[i]);
			}
			map.put("img", getWeatherImg(weatherArray[i]));
			map.put("weather", weatherArray[i]);
			map.put("temperature", temperatureArray[i]);
			map.put("wind", WindArray[i]);
			list.add(map);
		}
		return list;
	}

	
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		//option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		int span=1000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			dialog.cancel();//取消定位框
			mLocationClient.stop();
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getCity());
			countyName = location.getCity();
			if(!TextUtils.isEmpty(countyName))
			{	String tesss = countyName.replace("市", "");
//				publishText.setText("同步中");
//				//数据还没获取，先不进行显示，免得显示之前的数据造成错误
//				weatherinfoLayout.setVisibility(View.INVISIBLE);
//				cityNameText.setVisibility(View.INVISIBLE);
				queryWeatherCode(tesss);
			}
			
			else {
//				publishText.setText("同步失败。。。。");
				//没有县级代号就直接呈现之前的天气信息
				showWeather();
			}
			mLocationClient.stop();
//			sb.append("\nerror code : ");
//			sb.append(location.getLocType());
//			sb.append("\nlatitude : ");
//			sb.append(location.getLatitude());
//			sb.append("\nlontitude : ");
//			sb.append(location.getLongitude());
//			sb.append("\nradius : ");
//			sb.append(location.getRadius());
//			if (location.getLocType() == BDLocation.TypeGpsLocation){
//				sb.append("\nspeed : ");
//				sb.append(location.getSpeed());
//				sb.append("\nsatellite : ");
//				sb.append(location.getSatelliteNumber());
//				sb.append("\ndirection : ");
//				sb.append("\naddr : ");
//				sb.append(location.getAddrStr());
//				sb.append(location.getDirection());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//				sb.append("\naddr : ");
//				sb.append(location.getAddrStr());
//				//运营商信息
//				sb.append("\noperationers : ");
//				sb.append(location.getOperators());
//			}
			//LocationResult.setText(sb.toString());
			Log.i("定位到的城市", sb.toString());
		}


	}
	
}
