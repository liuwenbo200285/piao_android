package com.wenbo.piao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.dialog.LoginDialog;

public class Info1Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info1);
	}
	
	

	@Override
	protected void onDestroy() {
		Log.i("method","onDestroy");
		super.onDestroy();
	}



	@Override
	protected void onPause() {
		Log.i("method","onPause");
		super.onPause();
	}



	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.i("method","onRestart");
		super.onRestart();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i("method","onResume");
		super.onResume();
	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i("method","onStart");
		super.onStart();
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.i("method","onStop");
		super.onStop();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.info1, menu);
		return true;
	}

}
