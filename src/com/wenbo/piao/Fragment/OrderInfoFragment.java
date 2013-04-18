package com.wenbo.piao.Fragment;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.task.GetPersonConstanct;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class OrderInfoFragment extends Fragment {
	private Activity activity;
	
	private ProgressDialog progressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info3,null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("onActivityCreated","onActivityCreated");
		activity = getActivity();
		Button button = (Button)activity.findViewById(R.id.test);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				new AsyncTask<Integer,Integer,Integer>(){
//
//					@Override
//					protected Integer doInBackground(Integer... params) {
//						try {
//							Thread.sleep(1000*5);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						return null;
//					}
//
//					@Override
//					protected void onPostExecute(Integer result) {
//						// TODO Auto-generated method stub
//						progressDialog.dismiss();
//						super.onPostExecute(result);
//					}
//
//					@Override
//					protected void onPreExecute() {
//						progressDialog = ProgressDialog.show(activity,"获取联系人","正在获取联系人...",true,false);
//						super.onPreExecute();
//					}
//
//					@Override
//					protected void onProgressUpdate(Integer... values) {
//						// TODO Auto-generated method stub
//						super.onProgressUpdate(values);
//					}
//					
//				}.execute(0);
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onCreate","onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("onDestroy","onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("onPause","onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("onResume","onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("onStart","onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("onStop","onStop");
		super.onStop();
	}
	
	
}
