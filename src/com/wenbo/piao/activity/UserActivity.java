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

public class UserActivity extends Activity {
	
	public static FragmentManager fm;
	
	private Fragment currentFragment = null;

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
		currentFragment = fm.findFragmentById(R.id.fragment1);
		FragmentTransaction fTransaction = fm.beginTransaction();
		fTransaction.hide(fm.findFragmentById(R.id.fragment2));
		fTransaction.hide(fm.findFragmentById(R.id.fragment3));
		fTransaction.hide(fm.findFragmentById(R.id.fragment4));
		fTransaction.commit();
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
			hideFragment = fm.findFragmentById(R.id.fragment1);
			break;
		case R.id.tab2:
			hideFragment = fm.findFragmentById(R.id.fragment2);
			break;
		case R.id.tab3:
			hideFragment = fm.findFragmentById(R.id.fragment3);
			break;
		case R.id.tab4:
			hideFragment = fm.findFragmentById(R.id.fragment4);
			break;
		case R.id.action_settings:
//			ft.add(new ContactFragment(),"tab5");
			break;
		default:
			break;
		}
		if(currentFragment != hideFragment){
			ft.hide(currentFragment);
			ft.show(hideFragment);
		}
		ft.addToBackStack(null);
		ft.commit();
		currentFragment = hideFragment;
		return super.onOptionsItemSelected(item);
	}

}
