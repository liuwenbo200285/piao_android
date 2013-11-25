package com.wenbo.piao.task;

import java.net.SocketTimeoutException;
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
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
	
	private AlertDialog alertDialog;
	
	private Map<String,String> trainCodeMap = null;
	
	public GetTrainNoTast(Activity activity,ConfigInfo configInfo){
		this.activity = activity;
		this.configInfo = configInfo;
		this.httpClient = HttpClientUtil.getHttpClient();
	}

	@Override
	protected String[] doInBackground(String... arg0) {
		String info = getTrainNo();
		if(info == null || StringUtils.isBlank(info)){
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
		final EditText editText = (EditText)activity.findViewById(R.id.startTrainNo);
		editText.setInputType(InputType.TYPE_NULL);
		if(result == null){
			LoginDialog.newInstance("没有找到可以乘坐的车次！").show(
					activity.getFragmentManager(), "dialog");
			editText.clearFocus();
			return;
		}
		final EditText trainCode = (EditText)activity.findViewById(R.id.trainCode);
		View showView = LayoutInflater.from(activity).inflate(R.layout.activity_info3,null);
		ListView listView = (ListView) showView.findViewById(R.id.noCompleteOrderView);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, result);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String info = result[arg2];
				String trainNo = StringUtils.split(info,"(")[0];
				editText.setText(info);
				String code = trainCodeMap.get(trainNo.trim());
				trainCode.setText(code);
//				editText.clearFocus();
				if(alertDialog != null){
					alertDialog.dismiss();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("选择车次").setView(showView);
		alertDialog = builder.create();
		alertDialog.show();
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
		}catch(SocketTimeoutException e){
			getTrainNo();
		}catch (Exception e) {
			Log.e("getTrainNo","search trainNo error!", e);
		}
		return null;
	}
	

}
