package com.coolweather.app.view.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.bean.City;
import com.coolweather.app.bean.County;
import com.coolweather.app.bean.Province;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.http.HttpCallbackListener;
import com.coolweather.app.http.HttpUtil;
import com.coolweather.app.http.Utility;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private boolean isFromWeatherActivity;
	
	private ProgressDialog progressdialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		//判断是否是从天气信息页面转会主页，为后面按返回键返回那一层提供判断标准
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return ;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);//别导错包导致要导入全包名
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long arg3) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}
				else if(currentLevel == LEVEL_CITY)
				{
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY) {
					String countyname = countyList.get(index).getCountyName();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					//改
					intent.putExtra("county_name", countyname);
					startActivity(intent);
					finish();
				}
			}
			

		});
		queryProvinces();//打开软件呈现最上层的省级
		
	
	}

	/*
	 * 查询数据库里的省级表列并呈现在列表中
	 */

	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size()>0) {
			dataList.clear();
			for(Province province:provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
			
		} else {
			queryFromSever(null,"province");//不存在数据就从API获取（此函数还包含获取后再加载）
		}
		
	}


    /*
     * 查询数据库里的市级表列并呈现在列表中
     */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for(City city :cityList)
			{
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
			
		} else {
			queryFromSever(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	/*
	 * 查询数据库里的县级表列并呈现在列表中
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size()>0) {
			dataList.clear();
			for(County county : countyList)
			{
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
			
		} else {
			queryFromSever(selectedCity.getCityCode(),"county");
		}
		
	}
	
	/*
	 * 
	 */
	private void queryFromSever(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";//查找市或县数据地址
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";	//查找省级数据地址		
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type))
				{
					result = Utility.handleProvincesResponse(coolWeatherDB, response);
				}
				if("city".equals(type))
				{
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}
				if("county".equals(type))
				{
					result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				//根据是否返回正确数据再进行界面呈现
			if(result) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						if("province".equals(type))
							queryProvinces();
						if("city".equals(type))
							queryCities();
						if("county".equals(type))
							queryCounties();
					}
				});
			}
			
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_LONG).show();
					}

				});
				
			}
		});
		
	}
	
	
	/*
	 * 显示进度条
	 */
	private void showProgressDialog() {
		if(progressdialog == null)
		{
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("正在加载。。。");
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	
	/*
	 *关闭进度条 
	 */
	private void closeProgressDialog() {
		if(progressdialog != null){
			progressdialog.dismiss();
		}
	}
	
	/*
	 * 捕获Back按键
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY) {
			queryCities();
		}else if(currentLevel == LEVEL_CITY)
		{
			queryProvinces();
		}else {
			if(isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
		}
		finish();
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
