package com.wenbo.piao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.wenbo.piao.R;

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
