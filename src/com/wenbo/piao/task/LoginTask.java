package com.wenbo.piao.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.service.AccountService;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

/**
 * 登录task
 * @author wenbo
 *
 */
public class LoginTask extends AsyncTask<String,Integer,Integer> {
	
	private HttpClient httpClient;
	
	private Activity activity;
	
	private ProgressDialog progressDialog = null;
	
	private String username;
	
	private String password;
	
	private String randCode;
	
	private AccountService accountService;
	
	public LoginTask(Activity activity,AccountService accountService){
		this.httpClient = HttpClientUtil.getHttpClient();
		this.activity = activity;
		this.accountService = accountService;
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		return login();
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
		switch (result) {
		case 0:
			Log.i("Login","登录成功!");
			Account account = new Account();
			account.setName(username);
			account.setPassword(password);
			account.setUpdateDate(new Date());
			accountService.create(account);
			HttpClientUtil.setAccount(account);
			Intent intent = new Intent();
            intent.setClass(activity,UserActivity.class);
			activity.startActivity(intent);
			activity.finish();
			break;
		case 1:
//			Toast.makeText(activity, "用户名不存在!",Toast.LENGTH_SHORT).show();
			LoginDialog.newInstance( "用户名不存在！").show(activity.getFragmentManager(),"dialog"); 
			break;
		case 2:
//			Toast.makeText(activity, "密码错误!",Toast.LENGTH_SHORT).show();
			LoginDialog.newInstance( "密码错误！").show(activity.getFragmentManager(),"dialog"); 
			break;
		case 3:
//			Toast.makeText(activity, "验证码错误!",Toast.LENGTH_SHORT).show();
			LoginDialog.newInstance("验证码错误！").show(activity.getFragmentManager(),"dialog"); 
			EditText editText = (EditText)activity.findViewById(R.id.rangcode);
			editText.setText("");
			editText.requestFocus();
			GetRandCodeTask getRandCodeTask = new GetRandCodeTask(activity,null,1);
			getRandCodeTask.execute(UrlEnum.DO_MAIN.getPath()+UrlEnum.LOGIN_RANGCODE_URL.getPath());
			break;
		case 4:
			LoginDialog.newInstance("系统维护中！").show(activity.getFragmentManager(),"dialog"); 
			break;
		default:
			LoginDialog.newInstance("请检测网络是否正常！").show(activity.getFragmentManager(),"dialog"); 
			break;
		}
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(activity,"登录","正在登录...",true,false);
		Log.i("Login","开始准备登录...");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.i("Login","正在登录...");
	}
	
	private int login() {
		// 获取验证码
		try {
			EditText userNameEditText = (EditText)activity.findViewById(R.id.username);
			EditText passwordEditText = (EditText)activity.findViewById(R.id.password);
			EditText rangCodeEditText = (EditText)activity.findViewById(R.id.rangcode);
			username = userNameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			randCode = rangCodeEditText.getText().toString();
			Map<String,String> paraMap = new HashMap<String, String>();
			paraMap.put("loginUserDTO.user_name",username);
			paraMap.put("userDTO.password",password);
			paraMap.put("randCode",randCode);
			String info = HttpClientUtil.doPost(UrlNewEnum.LONGIN_CONFIM, paraMap,0);
			if(StringUtils.isNotBlank(info)){
				
			}
		}catch (Exception e) {
			Log.e("Login","登录出错!",e);
			return 5;
		}
		return 4;
	}

}
