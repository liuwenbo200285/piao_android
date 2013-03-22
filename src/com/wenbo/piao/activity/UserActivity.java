package com.wenbo.piao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.wenbo.androidpiao.R;

public class UserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}

}
