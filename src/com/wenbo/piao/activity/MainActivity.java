package com.wenbo.piao.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.task.GetRandCodeTask;
import com.wenbo.piao.task.LoginTask;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
	}
	
	@Override
	protected void onStart() {
		Log.i("onStart","onStart...");
		final EditText userNameText = (EditText)findViewById(R.id.editText1);
		final EditText userPassText = (EditText)findViewById(R.id.editText2);
		final EditText rangCodeText = (EditText)findViewById(R.id.editText3);
//		BufferedReader bufferedReader = null;
//		 try {
//	            bufferedReader = new BufferedReader(new InputStreamReader(this.openFileInput("pass.txt")));
//	            String username = bufferedReader.readLine();
//	            String password = bufferedReader.readLine();
//	            if(StringUtils.isNotBlank(username)){
//	            	userNameText.setText(username.trim());
//	            }
//	            if(StringUtils.isNotBlank(password)){
//	            	userPassText.setText(password.trim());
//	            }
//	     }catch (Exception e) {
//	            return;
//	     }finally{
//	    	 IOUtils.closeQuietly(bufferedReader);
//	     }
		ImageView imageView = (ImageView)findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getLoginRangeCode();
			}
		});
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(StringUtils.isBlank(userNameText.getText().toString().trim())){
					LoginDialog.newInstance( "请输入用户名！").show(getFragmentManager(),"dialog"); 
					userNameText.requestFocus();
					return;
				}
				if(StringUtils.isBlank(userPassText.getText().toString().trim())){
					LoginDialog.newInstance( "请输入密码！").show(getFragmentManager(),"dialog"); 
					userPassText.requestFocus();
					return;
				}
				if(StringUtils.isBlank(rangCodeText.getText().toString().trim())){
					LoginDialog.newInstance( "请输入验证码！").show(getFragmentManager(),"dialog"); 
					rangCodeText.requestFocus();
					return;
				}
				LoginTask loginTask = new LoginTask(MainActivity.this);
				loginTask.execute("");
			}
		});
		getLoginRangeCode();
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.i("onResume","onResume...");
		super.onResume();
	}

	protected void onPause() {
		Log.i("onPause","onPause...");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.i("onStop","onStop...");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		Log.i("onRestart","onRestart...");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		Log.i("onDestroy","onDestroy...");
		super.onDestroy();
	}
	
	/**
	 * 获取登录验证码
	 */
	private void getLoginRangeCode(){
		GetRandCodeTask getRandCodeTask = new GetRandCodeTask(this,1);
		getRandCodeTask.execute(UrlEnum.DO_MAIN.getPath()+UrlEnum.LOGIN_RANGCODE_URL.getPath());
	}

}
