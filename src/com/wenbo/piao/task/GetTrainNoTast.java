package com.wenbo.piao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;

public class GetTrainNoTast extends AsyncTask<String,Integer,String[]> {
	
	private Activity activity;
	
	private ConfigInfo configInfo;
	
	private HttpClient httpClient;
	
	private ProgressDialog progressDialog;
	
	private Map<String,String> trainCodeMap = null;
	
	public GetTrainNoTast(Activity activity,ConfigInfo configInfo){
		this.activity = activity;
		this.configInfo = configInfo;
		this.httpClient = HttpClientUtil.getHttpClient();
	}

	@Override
	protected String[] doInBackground(String... arg0) {
		String info = getTrainNo();
		if(StringUtils.isBlank(info)){
			return null;
		}
		JSONArray arry = JSONArray.parseArray(info);
		String[] infos = new String[arry.size()];
		trainCodeMap = new HashMap<String, String>();
		for(int i = 0; i < arry.size(); i++){
			JSONObject object = arry.getJSONObject(i);
			String str=object.getString("value")+"("+object.getString("start_station_name")+object.getString("start_time")
					+"→"+object.getString("end_station_name")+object.getString("end_time")+")";
			trainCodeMap.put(object.getString("value"),object.getString("id"));
			infos[i] = str;
		}
		return infos;
	}

	@Override
	protected void onPostExecute(final String[] result) {
		progressDialog.dismiss();
		if(result == null){
			LoginDialog.newInstance("没有找到可以乘坐的车次！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		final EditText editText = (EditText)activity.findViewById(R.id.startTrainNo);
		final EditText trainCode = (EditText)activity.findViewById(R.id.trainCode);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
		.setSingleChoiceItems(result, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						String info = result[which];
						String trainNo = StringUtils.split(info,"(")[0];
						editText.setText(info);
						String code = trainCodeMap.get(trainNo.trim());
						trainCode.setText(code);
						InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
						if (imm.isActive()) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
						}
						dialog.dismiss();
					}
				}).setIcon(android.R.drawable.arrow_down_float);
		builder.setTitle("选择车次");
		AlertDialog dialog = builder.create();
		dialog.show();
//		Window window = dialog.getWindow();
//        WindowManager.LayoutParams wl = window.getAttributes();
//        wl.x = -100;
//        wl.y = -300;
//        window.setAttributes(wl);
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        window.setLayout(0,500);
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
