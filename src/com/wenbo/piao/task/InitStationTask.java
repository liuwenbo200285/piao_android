package com.wenbo.piao.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		progressDialog = new ProgressDialog(activity); 
		//设置进度条风格，风格为圆形，旋转的 
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
		//设置ProgressDialog 标题 
		progressDialog.setTitle("初始化车站数据"); 
		//设置ProgressDialog 提示信息 
		progressDialog.setMessage("正在初始化车站数据..."); 
		//设置ProgressDialog 标题图标 
		progressDialog.setIcon(android.R.drawable.ic_dialog_alert); 
		//设置ProgressDialog的最大进度 
		progressDialog.setMax(100); 
		//设置ProgressDialog 的一个Button 
		//设置ProgressDialog 是否可以按退回按键取消 
		progressDialog.setCancelable(false); 
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
//		progressDialog = ProgressDialog.show(activity,"初始化车站数据","正在初始化车站数据...",false,false);
		progressDialog.show();
		progressDialog.setProgress(0);
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
				progressDialog.setMax(infos.length);
				int n = 1;
				Station dbStation = null;
				for(String info:infos){
					String [] station = StringUtils.split(info,"\\|");
					if(station != null && station.length > 0){
						dbStation = new Station();
						dbStation.setSimpleCode(station[2]);
						dbStation.setZhCode(station[1]);
						dbStation.setStationCode(station[0]);
						dbStation.setPinyingCode(station[3]);
						dbStation.setSimplePinyingCode(station[4]);
						dbStation.setCode(station[5]);
						stationService.create(dbStation);
						dbStation = null;
						n++;
						progressDialog.setProgress(n);
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
