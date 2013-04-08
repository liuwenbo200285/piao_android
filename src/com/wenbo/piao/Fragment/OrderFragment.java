package com.wenbo.piao.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.wenbo.androidpiao.R;

public class OrderFragment extends Fragment {
	
	private static Activity activity = null;
	
	private Spinner spinner = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info1,null);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		Log.i("method","onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
//		Log.i("method","onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
//		Log.i("method","onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
//		Log.i("method","onStart");
		activity = getActivity();
		Button button = (Button)activity.findViewById(R.id.button1);
		spinner = (Spinner)activity.findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
		        R.array.planets_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(getActivity()).setTitle("登录失败!")
				.setMessage("密码错误!").show();
			}
		});
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
//		Log.i("method","onStop");
		super.onStop();
	}
	
	

	
}
