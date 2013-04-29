package com.wenbo.piao.Fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wenbo.piao.R;

public class OrderInfoFragment extends Fragment implements TabListener  {
	private Activity activity;
	
	private ProgressDialog progressDialog;
	
	private Tab noCompletedTab;
	
	private Tab completedTab;
	
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
		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		if(actionBar.getTabCount() == 0){
			    if(noCompletedTab == null){
			    	noCompletedTab = actionBar.newTab()
				            .setText("未完成订单")
				            .setTabListener(this);
			    	completedTab = actionBar.newTab()
					        .setText("已完成订单")
					        .setTabListener(this);
			    }
			    actionBar.addTab(noCompletedTab);
			    actionBar.addTab(completedTab);
		}
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

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabReselected:"+tab.getText());
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabSelected:"+tab.getText());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabUnselected:"+tab.getText());
	}
	
	
}
