package com.wenbo.piao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.util.HttpClientUtil;

public class GetTrainNoTast extends AsyncTask<String,Integer,String[]> {
	
	private Activity activity;
	
	private ConfigInfo configInfo;
	
	private HttpClient httpClient;
	
	private ProgressDialog progressDialog;
	
	private AlertDialog alertDialog;
	
	private Map<String,String> trainCodeMap = null;
	
	private int count = 0;
	
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
		JSONObject jsonObject = JSON.parseObject(info);
		if(!jsonObject.getBooleanValue("status")){
			return null;
		}
		if(!jsonObject.containsKey("data")){
			return null;
		}
		JSONArray arry = jsonObject.getJSONArray("data");
		if(arry == null){
			return null;
		}
		List<String> trains = new ArrayList<String>();
		trainCodeMap = new HashMap<String, String>();
		boolean isALl = false;
		boolean isAllTime = false;
		if(configInfo.getTrainClass().length == 1
				&& "QB".equals(configInfo.getTrainClass()[0])){
			isALl = true;
		}
		if(("00:00--24:00").equals(configInfo.getOrderTime())){
			isAllTime = true;
		}
		for(int i = 0; i < arry.size(); i++){
			JSONObject object = arry.getJSONObject(i).getJSONObject("queryLeftNewDTO");
			boolean isHave = false;
			if(!isALl){
				for(String type:configInfo.getTrainClass()){
					if(StringUtils.contains(object.getString("station_train_code"),type)){
						isHave = true;
					}
				}
				if(!isHave){
					continue;
				}
			}
			if(!isAllTime){
				String [] times = StringUtils.split(configInfo.getOrderTime(),"--");
				int beginTime = Integer.parseInt(StringUtils.split(times[0],":")[0]);
				int endTime = Integer.parseInt(StringUtils.split(times[1],":")[0]);
				int trainStartTime = Integer.parseInt(StringUtils.split(object.getString("start_time"),":")[0]);
				if(trainStartTime < beginTime
						|| trainStartTime > endTime){
					continue;
				}
			}
			String str=object.getString("station_train_code")+"("+object.getString("start_station_name")+object.getString("start_time")
					+"→"+object.getString("to_station_name")+object.getString("arrive_time")+")";
			trainCodeMap.put(object.getString("station_train_code"),object.getString("train_no"));
			trains.add(str);
		}
		if(trains.size() > 0){
			trains.add(0,"不选择车次");
		}
		String[] infos = new String[trains.size()];
		trains.toArray(infos);
		return infos;
	}

	@Override
	protected void onPostExecute(final String[] result) {
		progressDialog.dismiss();
		final EditText editText = (EditText)activity.findViewById(R.id.startTrainNo);
		editText.setInputType(InputType.TYPE_NULL);
		if(result == null
				|| result.length == 0){
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
				if(arg2 != 0){
					String info = result[arg2];
					if(info == null){
						return;
					}
					String trainNo = StringUtils.split(info,"(")[0];
					editText.setText(info);
					String code = trainCodeMap.get(trainNo.trim());
					trainCode.setText(code);
				}else{
					editText.setText("");
					trainCode.setText("");
				}
				editText.clearFocus();
				if(alertDialog != null){
					alertDialog.dismiss();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("选择车次").setView(showView).setCancelable(false);
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
		try {
			Map<String, String> paraMap = new LinkedHashMap<String, String>();
			paraMap.put("leftTicketDTO.train_date",configInfo.getOrderDate());
			paraMap.put("leftTicketDTO.from_station",configInfo.getFromStation());
			paraMap.put("leftTicketDTO.to_station",configInfo.getToStation());
			paraMap.put("purpose_codes","ADULT");
			return HttpClientUtil.doGet(UrlNewEnum.SEARCH_TICKET, paraMap,0);
		}catch (Exception e) {
			Log.e("getTrainNo","search trainNo error!", e);
		}finally{
			
		}
		return null;
	}
	

}