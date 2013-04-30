package com.wenbo.piao.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.wenbo.piao.R;
import com.wenbo.piao.Fragment.ContactFragment;
import com.wenbo.piao.Fragment.OrderInfoFragment;
import com.wenbo.piao.Fragment.RobitOrderFragment;

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
		ft.add(R.id.details,currentFragment,"tab1");
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
		ActionBar actionBar = getActionBar();
		if(actionBar.getTabCount() > 0 && item.getItemId() != R.id.tab2){
			actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setDisplayShowTitleEnabled(true);
		}
		switch (item.getItemId()) {
		case R.id.tab1:
			hideFragment = fm.findFragmentByTag("tab1");
			if(hideFragment == null){
				hideFragment = new RobitOrderFragment();
				ft.add(hideFragment,"tab1");
			}
			break;
		case R.id.tab2:
			hideFragment = fm.findFragmentByTag("tab2");
			if(hideFragment == null){
				hideFragment = new OrderInfoFragment();
				ft.add(hideFragment,"tab2");
			}
			break;
		case R.id.tab3:
			hideFragment = fm.findFragmentByTag("tab3");
			if(hideFragment == null){
				hideFragment = new ContactFragment();
				ft.add(hideFragment,"tab3");
			}
			break;
		case R.id.action_settings:
//			ft.add(new ContactFragment(),"tab5");
			break;
		default:
			break;
		}
		if(currentFragment != hideFragment){
			ft.replace(R.id.details,hideFragment);
			ft.addToBackStack(null);
			ft.commit();
			ft.hide(currentFragment);
			ft.show(hideFragment);
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
