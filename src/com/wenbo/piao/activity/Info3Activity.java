package com.wenbo.piao.activity;

import com.wenbo.androidpiao.R;
import com.wenbo.androidpiao.R.layout;
import com.wenbo.androidpiao.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Info3Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info3, menu);
		return true;
	}

}
