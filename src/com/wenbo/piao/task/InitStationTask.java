package com.wenbo.piao.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wenbo.piao.sqllite.SqlliteHelper;
import com.wenbo.piao.sqllite.domain.Station;
import com.wenbo.piao.sqllite.service.StationService;

public class InitStationTask extends AsyncTask<String,Integer,Integer> {
	
	private ProgressDialog progressDialog;
	
	private Activity activity;
	
	public InitStationTask(Activity activity){
		this.activity = activity;
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		return initStationData();
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onCancelled(Integer result) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPostExecute(Integer result) {
		progressDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(activity,"初始化车站数据","正在初始化车站数据...",true,false);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		
	}
	
	/**
	 * 获取登录码
	 */
	private int initStationData() {
		InputStream inputStream = null;
		try {
			SqlliteHelper sqlliteHelper = new SqlliteHelper(activity);
			StationService stationService = sqlliteHelper.getStationService();
			inputStream = activity.getAssets().open("station.txt");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String str = bufferedReader.readLine();
			String[] infos = StringUtils.split(str,"@");
			if(infos != null && infos.length > 0){
				for(String info:infos){
					String [] station = StringUtils.split(info,"\\|");
					if(station != null && station.length > 0){
						Station dbStation = new Station();
						dbStation.setSimpleCode(station[2]);
						dbStation.setZhCode(station[1]);
						dbStation.setStationCode(station[0]);
						dbStation.setPinyingCode(station[3]);
						dbStation.setSimplePinyingCode(station[4]);
						dbStation.setCode(station[5]);
						stationService.create(dbStation);
					}
				}
			}
		} catch (Exception e) {
			Log.e("init station fail","init station fail",e);
		}finally{
			IOUtils.closeQuietly(inputStream);
		}
		return 0;
	}

}
