package com.wenbo.piao.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 登录task
 * @author wenbo
 *
 */
public class LoginTask extends AsyncTask<String,Integer,Integer> {
	
	private HttpClient httpClient;
	
	private Activity activity;
	
	public LoginTask(Activity activity){
		this.httpClient = HttpClientUtil.getHttpClient();
		this.activity = activity;
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		getLoginRand();
		return 0;
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
		Log.i("login","获取登录随机码成功!");
	}

	@Override
	protected void onPreExecute() {
		Log.i("login","开始准备登录...");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.i("login","正在登录...");
	}
	
	/**
	 * 获取登录码
	 */
	private void getLoginRand() {
		HttpResponse response = null;
		try {
			HttpGet httpget = HttpClientUtil.getHttpGet(UrlEnum.LOGIN_INIT_URL);
			httpget.getParams().setParameter("method","loginAysnSuggest");
			response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
				String str = EntityUtils.toString(response.getEntity());
				JSONObject object = JSONObject.parseObject(str);
				Log.i("Login",object.getString("loginRand")+":"+object.getString("randError"));
				login(object.getString("loginRand"),
						object.getString("randError"));
			}else if(response.getStatusLine().getStatusCode() == 404){
				
			}
		} catch (Exception e) {
			Log.e("Login","获取登录随机码失败!",e);
		} finally {

		}
	}
	
	private void login(String loginRand, String randError) {
		HttpResponse response = null;
		// 获取验证码
		try {
			
		}catch (Exception e) {
			
		}
	}

}
