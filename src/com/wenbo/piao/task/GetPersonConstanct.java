package com.wenbo.piao.task;

import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.fragment.ContactFragment;
import com.wenbo.piao.fragment.RobitOrderFragment;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.sqllite.service.UserInfoService;
import com.wenbo.piao.sqllite.util.SqlLiteUtil;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 获取账号联系人
 * @author wenbo
 *
 */
public class GetPersonConstanct extends AsyncTask<String,Integer,String>{
	
	private Activity activity;
	
	private ProgressDialog progressDialog;
	
	private Map<String,UserInfo> userInfoMap;	
	
	private UserInfoService userInfoService;
	
	private Fragment fragment;
	
	public GetPersonConstanct(Activity activity,Map<String,UserInfo> userInfoMap
			,Fragment fragment){
		this.activity = activity;
		this.userInfoMap = userInfoMap;
		this.fragment = fragment;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		if(userInfoMap.isEmpty()){
			if(userInfoService == null){
				userInfoService = SqlLiteUtil.getUserInfoService(activity);
			}
			List<UserInfo> userInfos = userInfoService.findAllInfos();
			boolean isInit = false;
			if(userInfos == null || userInfos.isEmpty()){
				String info = getOrderPerson();
				if(info == null){
					LoginDialog.newInstance("获取联系人超时，请在联系人菜单选择同步！").show(activity.getFragmentManager(),"dialog");
					return null;
				}
		    	JSONObject jsonObject = JSON.parseObject(info);
		    	userInfos = JSONArray.parseArray(
						jsonObject.getString("rows"), UserInfo.class);
		    	isInit = true;
			}
			if (userInfos != null && !userInfos.isEmpty()) {
				UserInfo userInfo = null;
				Account account = HttpClientUtil.getAccount();
				for (int i = 0; i < userInfos.size(); i++) {
					userInfo = userInfos.get(i);
					if (userInfo != null) {
						userInfo.setIndex(i);
						userInfoMap.put(userInfo.getPassenger_name(), userInfo);
						if(isInit){
							userInfo.setAccountName(account.getName());
							userInfoService.create(userInfo);
						}
					}
				}
				HttpClientUtil.setUserInfoMap(userInfoMap);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		HttpClientUtil.setUserInfoMap(userInfoMap);
		if(fragment != null){
			progressDialog.dismiss();
			if(fragment.getClass() == RobitOrderFragment.class){
				RobitOrderFragment robitOrderFragment = (RobitOrderFragment)fragment;
				robitOrderFragment.showDialog();
			}else if(fragment.getClass() == ContactFragment.class){
				ContactFragment contactFragment = (ContactFragment)fragment;
				contactFragment.showView();
			}
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		if(fragment != null){
			progressDialog = ProgressDialog.show(activity,"获取联系人","正在获取联系人...",true,false);
		}
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	
	/**
	 * 获取登录账号用户信息
	 * 
	 * @throws URISyntaxException
	 */
	private String getOrderPerson() {
		HttpResponse response = null;
		try {
			HttpClient httpClient = HttpClientUtil.getHttpClient();
//			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.GET_ORDER_PERSON.getPath());
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.GET_ORDER_PERSON);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "getPagePassengerAll"));
			parameters.add(new BasicNameValuePair("pageIndex",
					"0"));
			parameters.add(new BasicNameValuePair("pageSize",
					"100"));
			parameters.add(new BasicNameValuePair("passenger_name",
					"请输入汉字或拼音首字母"));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (SocketTimeoutException e) {
			Log.e("GetPersonConstanct","getOrderPerson data timeout!",e);
		} catch (Exception e) {
			Log.e("GetPersonConstanct","getOrderPerson error!",e);
		} finally {
			Log.i("GetPersonConstanct","close getOrderPerson");
		}
		return null;
	}

}
