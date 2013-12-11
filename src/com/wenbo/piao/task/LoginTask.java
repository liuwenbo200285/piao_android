package com.wenbo.piao.task;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.service.AccountService;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 登录task
 * @author wenbo
 *
 */
public class LoginTask extends AsyncTask<String,Integer,String> {
	
	
	private Activity activity;
	
	private ProgressDialog progressDialog = null;
	
	private String username;
	
	private String password;
	
	private String randCode;
	
	private EditText rangCodeEditText;
	
	private AccountService accountService;
	
	public LoginTask(Activity activity,AccountService accountService){
		this.activity = activity;
		this.accountService = accountService;
	}

	@Override
	protected String doInBackground(String... arg0) {
		return login();
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPostExecute(String result) {
		progressDialog.dismiss();
		if(StringUtils.isBlank(result)){
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
		}else{
			rangCodeEditText.setText("");
			rangCodeEditText.requestFocus();
			GetRandCodeTask getRandCodeTask = new GetRandCodeTask(activity,null,1);
			getRandCodeTask.execute(UrlNewEnum.DO_MAIN.getPath()+UrlNewEnum.LOGIN_RANGCODE_URL.getPath());
			LoginDialog.newInstance(result).show(activity.getFragmentManager(),"dialog"); 
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
	
	private String login() {
		// 获取验证码
		try {
			EditText userNameEditText = (EditText)activity.findViewById(R.id.username);
			EditText passwordEditText = (EditText)activity.findViewById(R.id.password);
			rangCodeEditText = (EditText)activity.findViewById(R.id.rangcode);
			username = userNameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			randCode = rangCodeEditText.getText().toString();
			Map<String,String> paraMap = new HashMap<String, String>();
			paraMap.put("loginUserDTO.user_name",username);
			paraMap.put("userDTO.password",password);
			paraMap.put("randCode",randCode);
			String info = HttpClientUtil.doPost(UrlNewEnum.LONGIN_CONFIM, paraMap,0);
			if(StringUtils.isNotBlank(info)){
				JSONObject jsonObject = JSON.parseObject(info);
				if(jsonObject.containsKey("data")){
					if("Y".equals(jsonObject.getJSONObject("data").getString("loginCheck"))){
						return "";
					}
				}
				return jsonObject.getString("messages");
			}
		}catch (Exception e) {
			Log.e("Login","登录出错!",e);
		}
		return "登录异常！";
	}

}
