package com.wenbo.piao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.wenbo.androidpiao.R;
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
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar.setIndeterminate(false);
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
		GetRandCodeTask getRandCodeTask = new GetRandCodeTask(this);
		getRandCodeTask.execute(UrlEnum.DO_MAIN.getPath()+UrlEnum.LOGIN_RANGCODE_URL.getPath());
	}

}
