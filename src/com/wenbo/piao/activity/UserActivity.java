package com.wenbo.piao.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.wenbo.piao.R;
import com.wenbo.piao.fragment.AboutFargment;
import com.wenbo.piao.fragment.ContactFragment;
import com.wenbo.piao.fragment.OrderInfoFragment;
import com.wenbo.piao.fragment.RobitOrderFragment;
import com.wenbo.piao.util.HttpClientUtil;

public class UserActivity extends Activity {
	
	public static FragmentManager fm;
	
	public ImageView rangcodeImageView;
	
	private Fragment currentFragment;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		rangcodeImageView = new ImageView(this);
		getActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getActionBar().setTitle(R.string.app_name);
		fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		currentFragment = new RobitOrderFragment();
		ft.replace(R.id.details,currentFragment,"tab1");
		ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
		ft.addToBackStack(null);
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
//		closeSoftInput();
		ActionBar actionBar = getActionBar();
		if(actionBar.getTabCount() > 0 && item.getItemId() != R.id.tab2){
			actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setDisplayShowTitleEnabled(true);
		}
		boolean isNew = false;
		switch (item.getItemId()) {
		case R.id.tab1:
			hideFragment = fm.findFragmentByTag("tab1");
			if(hideFragment == null){
				hideFragment = new RobitOrderFragment();
				ft.replace(R.id.details,hideFragment,"tab1");
				isNew = true;
			}
			break;
		case R.id.tab2:
			hideFragment = fm.findFragmentByTag("tab2");
			if(hideFragment == null){
				hideFragment = new OrderInfoFragment();
				ft.replace(R.id.details,hideFragment,"tab2");
				isNew = true;
			}
			break;
		case R.id.tab3:
			hideFragment = fm.findFragmentByTag("tab3");
			if(hideFragment == null){
				hideFragment = new ContactFragment();
				ft.replace(R.id.details,hideFragment,"tab3");
				isNew = true;
			}
			break;
		case R.id.tab4:
			Intent intent = new Intent();
            intent.setClass(this,MainActivity.class);
			startActivity(intent);
			finish();
			HttpClientUtil.setConfigInfo(null);
			HttpClientUtil.setParams(null);
			HttpClientUtil.setSeatNum(null);
			HttpClientUtil.setTicketNo(null);
			HttpClientUtil.setToken(null);
			HttpClientUtil.setUserInfoMap(null);
			break;
		case R.id.tab5:
			hideFragment = fm.findFragmentByTag("tab4");
			if(hideFragment == null){
				hideFragment = new AboutFargment();
				ft.replace(R.id.details,hideFragment,"tab4");
				isNew = true;
			}
			break;
		default:
			break;
		}
		if(currentFragment != hideFragment
				&& hideFragment != null){
			ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
			if(!isNew){
				ft.replace(R.id.details,hideFragment);
			}
			ft.addToBackStack(null);
			ft.commit();
			currentFragment = hideFragment;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
	            dialog(); 
	            return true; 
	        } 
	        return false; 
	}
	
	protected void dialog() { 
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this); 
        builder.setMessage("确定要退出吗?"); 
        builder.setTitle("提示"); 
        builder.setPositiveButton("确认", 
                new android.content.DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                        dialog.dismiss(); 
                        UserActivity.this.finish(); 
                    } 
                }); 
        builder.setNegativeButton("取消", 
                new android.content.DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                        dialog.dismiss(); 
                    } 
                }); 
        builder.create().show(); 
    } 

}
