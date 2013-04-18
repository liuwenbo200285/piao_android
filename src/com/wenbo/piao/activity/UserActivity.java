package com.wenbo.piao.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.Fragment.ContactFragment;
import com.wenbo.piao.Fragment.OrderInfoFragment;
import com.wenbo.piao.Fragment.RobitOrderFragment;

public class UserActivity extends Activity {
	
	public static FragmentManager fm;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		getActionBar().setTitle(R.string.app_name);
		fm = getFragmentManager();
		fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				
			}
		});
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.details,new RobitOrderFragment(),"tab1");
		ft.commit();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentTransaction ft = fm.beginTransaction();
		Fragment hideFragment = null;
		switch (item.getItemId()) {
		case R.id.tab1:
			hideFragment = fm.findFragmentByTag("tab2");
			if(hideFragment == null){
				hideFragment = new RobitOrderFragment();
				ft.add(hideFragment,"tab2");
			}
			break;
		case R.id.tab2:
			hideFragment = fm.findFragmentByTag("tab3");
			if(hideFragment == null){
				hideFragment = new OrderInfoFragment();
				ft.add(hideFragment,"tab3");
			}
			break;
		case R.id.tab3:
			hideFragment = fm.findFragmentByTag("tab4");
			if(hideFragment == null){
				hideFragment = new ContactFragment();
				ft.add(hideFragment,"tab4");
			}
			break;
		case R.id.action_settings:
//			ft.add(new ContactFragment(),"tab5");
			break;
		default:
			break;
		}
		ft.addToBackStack(null);
		ft.replace(R.id.details,hideFragment);
		ft.commit();
		return super.onOptionsItemSelected(item);
	}

}
