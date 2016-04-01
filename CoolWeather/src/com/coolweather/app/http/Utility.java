package com.coolweather.app.http;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.coolweather.app.bean.City;
import com.coolweather.app.bean.County;
import com.coolweather.app.bean.Province;
import com.coolweather.app.db.CoolWeatherDB;

public class Utility {
	/*
	 * 解析省级数据，存储到实体province中，再将每个存储到数据表中
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB 
			coolWeatherDB,String response)
	{
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null&&allProvinces.length>0)
			{	for(String p:allProvinces)
				{
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//存储到数据库
					coolWeatherDB.saveProvince(province);
					
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析市级数据，存储到实体city中，再存储到数据表中
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB 
			coolWeatherDB,String response,int provinceId)
	{
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null&&allCities.length>0)
			{	for(String p:allCities)
				{
					String[] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//存储到数据库
					coolWeatherDB.saveCity(city);
					
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析县区级数据，存储到实体county中，再存储到数据表中
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB 
			coolWeatherDB,String response,int cityId)
	{
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null&&allCounties.length>0)
			{	for(String p:allCounties)
				{
					String[] array = p.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//存储到数据库
					coolWeatherDB.saveCounty(county);
					
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析服务器返回的json数据。并将解析出的数据存储到本地。
	 */
	public static void handleWeatherResponse(Context context,String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String status = jsonObject.optString("reason");
			if(status.equals("查询成功")||status.equals("successed!"))
			{
			JSONObject result = jsonObject.getJSONObject("result");
			JSONObject sk = result.getJSONObject("sk");
			JSONObject today = result.getJSONObject("today");
			JSONArray future = result.getJSONArray("future");
			//JSONObject weatherInfo = future.getJSONObject(0);
			String cityName = today.optString("city");
			String publishTime = sk.optString("time");
			String[] temperatureArray= new String[4];
			String[] weatherArray = new String[4];
			String[] weather_idArray = new String[4];
			String[] windArray = new String[4];
			String[] weekArray = new String[4];
			String[] dateArray = new String[4];
			temperatureArray[0] = today.optString("temperature");
			weatherArray[0] = today.optString("weather");
			JSONObject tempid = today.getJSONObject("weather_id");
			weather_idArray[0] = tempid.optString("fa");
			windArray[0] = sk.optString("wind_direction")+sk.optString("wind_strength");
			weekArray[0] = today.optString("week");
			dateArray[0] = today.optString("date_y");
			for(int i=1;i<4;i++)//预留第一个放今天的
			{
				JSONObject weatherInfo = future.getJSONObject(i);
				temperatureArray[i] = weatherInfo.optString("temperature");
				weatherArray[i] = weatherInfo.optString("weather");
				tempid = weatherInfo.getJSONObject("weather_id");
				weather_idArray[i] = tempid.optString("fa");
				windArray[i] = weatherInfo.optString("wind");
				weekArray[i] = weatherInfo.optString("week");
				dateArray[i] = weatherInfo.optString("date");
			}
			
			saveWeatherInfo(context, cityName, publishTime, temperatureArray, weatherArray,
					weather_idArray, windArray, weekArray, dateArray);
			
			}
			
//			  "temperature": "28℃~36℃",
//              "weather": "晴转多云",
//              "weather_id": {
//                  "fa": "00",
//                  "fb": "01"
//              },
//              "wind": "南风3-4级",
//              "week": "星期一",
//              "date": "20140804"
//			JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
//			String cityName = weatherinfo.getString("city");
//			String weathercode = weatherinfo.getString("cityid");
//			String temp1 = weatherinfo.getString("temp1");
//			String temp2 = weatherinfo.getString("temp2");
//			//因为weather写错，导致下面就进入catch而没存储，小心呐，顺便学会了调试看value值，不错不错
//			String weatherDesp = weatherinfo.getString("weather");
//			String publishTime = weatherinfo.getString("ptime");
//			saveWeatherInfo(context, cityName, weathercode, temp1, temp2, weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	/*
		 * 将服务器返回的天气情况存储到sharedprefernences
		 * saveWeatherInfo(context, cityName, publishTime, temperatureArray, weatherArray,
					weather_idArray, windArray, weekArray, dateArray);
		 * 
		 */
		public static void saveWeatherInfo(Context context,String cityname,String publishTime,
				String[] temperature,String[] weather,String[] weather_id,String[] wind,
				String[] week,String[] date)
		{	
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putBoolean("city_select", true);
			editor.putString("city_name", cityname);
			editor.putString("publish_time", publishTime);
			editor.putString("test", "556677");
			saveArray(context, temperature, editor, "temperature");
			saveArray(context, weather, editor, "weather");
			saveArray(context, weather_id, editor, "weather_id");
			saveArray(context, wind, editor, "wind");
			saveArray(context, week, editor, "week");
			saveArray(context, date, editor, "date");
			editor.commit();
			
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
//			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//			editor.putBoolean("city_select", true);
//			editor.putString("city_name", cityname);
//			editor.putString("weather_code",weatherCode);
//			editor.putString("temp1", temp1);
//			editor.putString("temp2", temp2);
//			editor.putString("weather_desp", weatherDesp);
//			editor.putString("publish_time", publishTime);
//			editor.putString("current_date", sdf.format(new Date()));
//			editor.commit();
			
		}
		/*
		 * 由于sharedpreference不支持保存数据，所以转化为json再保存
		 */
		public static void saveArray(Context context,String[] StringArray,
				Editor editor,String keyname) {
		    
		    JSONArray jsonArray = new JSONArray();
		    for (String b : StringArray) {
		      jsonArray.put(b);
		    }
		    
		    editor.putString(keyname,jsonArray.toString());
		   
		  }
		
		/**
		 * 获取天气预报信息
		 * 
		 * @return 天气预报list
		 */
		public static void setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}
	
	
	
}
