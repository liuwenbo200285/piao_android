package com.wenbo.piao.Fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wenbo.piao.R;

public class OrderInfoFragment extends Fragment implements TabListener  {
	private Activity activity;
	
	private Tab noCompletedTab;
	
	private Tab completedTab;
	
	private Fragment currentFragment;
	
	private CompletedOrderFragment completedOrderFragment;
	
	private NoCompletedOrderFragment noCompletedOrderFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info3,null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("OrderInfoFragment","onActivityCreated");
		activity = getActivity();
		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		currentFragment = this;
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
		Log.i("OrderInfoFragment","onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("OrderInfoFragment","onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("OrderInfoFragment","onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("OrderInfoFragment","onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("OrderInfoFragment","onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("OrderInfoFragment","onStop");
		super.onStop();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabReselected:"+tab.getText());
	}

	@Override
	public void onTabSelected(Tab tab,FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabSelected:"+tab.getText());
		if(tab == noCompletedTab){
			if(noCompletedOrderFragment == null){
				noCompletedOrderFragment = new NoCompletedOrderFragment();
			}
			ft.replace(R.id.details,noCompletedOrderFragment);
		}else if(tab == completedTab){
			if(currentFragment == this){
				if(completedOrderFragment == null){
					completedOrderFragment = new CompletedOrderFragment();
				}
				ft.replace(R.id.details,completedOrderFragment);
			}
		}
	}
	

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabUnselected:"+tab.getText());
	}
    
    public void showView(){
//    	progressDialog.dismiss();
//    	if(noCompletedOrders.isEmpty()){
//    		noCompletedOrders = null;
//			LoginDialog.newInstance( "没有未付款订单！").show(activity.getFragmentManager(),"dialog"); 
//			return;
//		}
//		OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
//		listView.setAdapter(adapter);
//		ft.replace(R.id.details,this);
    }
	
	
}
