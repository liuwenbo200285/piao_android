package com.wenbo.piao.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class GetTrainNoTast extends AsyncTask<String,Integer,String[]> {
	
	private Activity activity;
	
	private ConfigInfo configInfo;
	
	private HttpClient httpClient;
	
	private ProgressDialog progressDialog;
	
	public GetTrainNoTast(Activity activity,ConfigInfo configInfo){
		this.activity = activity;
		this.configInfo = configInfo;
		this.httpClient = HttpClientUtil.getHttpClient();
	}

	@Override
	protected String[] doInBackground(String... arg0) {
		String info = getTrainNo();
		JSONArray arry = JSONArray.parseArray(info);
		String[] infos = new String[arry.size()];
		for(int i = 0; i < arry.size(); i++){
			JSONObject object = arry.getJSONObject(i);
			String str=object.getString("value")+"("+object.getString("start_station_name")+object.getString("start_time")
					+"→"+object.getString("end_station_name")+object.getString("end_time")+")";
			infos[i] = str;
		}
		return infos;
	}

	@Override
	protected void onPostExecute(final String[] result) {
		progressDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
		.setSingleChoiceItems(result, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						System.out.println(result[which]);
					}
				}).setIcon(android.R.drawable.btn_star);
		builder.setTitle("选择车次类型");
		AlertDialog dialog = builder.create();
		dialog.show();
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(activity,"获取车次","正在获取车次...",true,false);
		super.onPreExecute();
	}
	
	public String getTrainNo(){
		HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","queryststrainall"));
			parameters.add(new BasicNameValuePair("date",configInfo.getOrderDate()));
			parameters.add(new BasicNameValuePair("fromstation",configInfo.getFromStation()));
			parameters.add(new BasicNameValuePair("tostation",configInfo.getToStation()));
			parameters.add(new BasicNameValuePair("starttime",configInfo.getOrderTime()));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.SEARCH_TRAINNO);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			Log.e("getTrainNo","search trainNo error!", e);
		}
		return null;
	}
	

}
