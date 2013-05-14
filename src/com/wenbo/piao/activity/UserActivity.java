package com.wenbo.piao.activity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.fragment.AboutFargment;
import com.wenbo.piao.fragment.CompletedOrderListFragment;
import com.wenbo.piao.fragment.ContactFragment;
import com.wenbo.piao.fragment.OrderDetailFragment;
import com.wenbo.piao.fragment.OrderInfoFragment;
import com.wenbo.piao.fragment.RobitOrderFragment;
import com.wenbo.piao.fragment.SearchInfoFragment;
import com.wenbo.piao.sqllite.domain.SearchInfo;
import com.wenbo.piao.sqllite.service.SearchInfoService;
import com.wenbo.piao.sqllite.util.SqlLiteUtil;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.OperationUtil;

public class UserActivity extends Activity implements OnTouchListener, OnGestureListener{
	
	private static FragmentManager fm;
	
	private Fragment currentFragment;
	
	private ProgressDialog progressDialog;
	
	private SearchInfoService searchInfoService;
	
	private GestureDetector mGestureDetector;
	
	private int verticalMinDistance = 20;  
	private int minVelocity         = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		getActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getActionBar().setTitle(R.string.app_name);
		mGestureDetector = new GestureDetector((OnGestureListener) this);
		RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.useractivity);
		relativeLayout.setOnTouchListener(this);    
		relativeLayout.setLongClickable(true); 
		searchInfoService = SqlLiteUtil.getSearchInfoService(this);
		List<SearchInfo> searchInfos = searchInfoService.findAccountSearchInfos(HttpClientUtil.getAccount().getName());
		HttpClientUtil.setSearchInfos(searchInfos);
		fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if(searchInfos.isEmpty()){
			currentFragment = new RobitOrderFragment();
			Bundle bundle = new Bundle();
			currentFragment.setArguments(bundle);
			ft.replace(R.id.details,currentFragment,"tab1");
		}else{
			currentFragment = new SearchInfoFragment();
			ft.replace(R.id.details,currentFragment,"searchInfo");
		}
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
				Bundle bundle = new Bundle();
				hideFragment.setArguments(bundle);
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
			RobitOrderFragment robitOrderFragment = (RobitOrderFragment)fm.findFragmentByTag("tab1");
			if(robitOrderFragment != null){
				robitOrderFragment.unRegisterService();
			}
			finish();
			cleanInfo();
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
			 	if(currentFragment.getClass() == RobitOrderFragment.class){
			 		if(!HttpClientUtil.getSearchInfos().isEmpty()){
			 			currentFragment = fm.findFragmentByTag("searchInfo");
			 			if(currentFragment == null){
			 				currentFragment = new SearchInfoFragment();
			 			}
			 			FragmentTransaction ft = fm.beginTransaction();
			 			ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
			 			ft.replace(R.id.details,currentFragment);
						ft.addToBackStack(null);
						ft.commit();
			 		}else{
			 			dialog();
			 			return true;
			 		}
			 	}else if(currentFragment.getClass() != CompletedOrderListFragment.class
			    		&& currentFragment.getClass() != OrderDetailFragment.class){
			    	dialog(); 
		            return true;
			    }else{
			    	currentFragment = fm.findFragmentByTag("tab2");
			    	fm.popBackStack();
			    }
	        } 
	        return false; 
	}
	
	public void setCurrentFragment(Fragment fragment){
		this.currentFragment = fragment;
	}
	
	/**
	 * 登出清楚状态
	 */
	public void cleanInfo(){
		 HttpClientUtil.setAccount(null);
         HttpClientUtil.setConfigInfo(null);
         HttpClientUtil.setMyOrders(null);
         HttpClientUtil.setParams(null);
         HttpClientUtil.setSeatNum(null);
         HttpClientUtil.setTicketNo(null);
         HttpClientUtil.setToken(null);
         HttpClientUtil.setUserInfoMap(null);
         HttpClientUtil.setNoCompletedOrders(null);
         HttpClientUtil.setPayInfo(null);
         HttpClientUtil.setSelectOrder(null);
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
                        RobitOrderFragment robitOrderFragment = (RobitOrderFragment)fm.findFragmentByTag("tab1");
                        robitOrderFragment.unRegisterService();
                        UserActivity.this.finish(); 
                        cleanInfo();
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


	@Override
	protected void onDestroy() {
		Log.i("UserActivity","onDestroy");
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		Log.i("UserActivity","onPause");
		super.onPause();
	}


	@Override
	protected void onRestart() {
		Log.i("UserActivity","onRestart");
		checkLogin();
		super.onRestart();
	}
	
	private void checkLogin(){
		//检测是否已经在登录
				new AsyncTask<Integer,Integer,String>(){
					@Override
					protected String doInBackground(Integer... arg0) {
						return OperationUtil.checkLogin();
					}

					@Override
					protected void onPostExecute(String result) {
						progressDialog.dismiss();
						try {
							if(!StringUtils.contains(result,"waitTime")){
								LoginDialog.newInstance("登录已超时，请重新登录！").show(getFragmentManager(),"dialog"); 
								Intent intent = new Intent();
					            intent.setClass(UserActivity.this,MainActivity.class);
								startActivity(intent);
								RobitOrderFragment robitOrderFragment = (RobitOrderFragment)fm.findFragmentByTag("tab1");
					            robitOrderFragment.unRegisterService();
								finish();
								cleanInfo();
							}
						} catch (Exception e) {
							
						}
						super.onPostExecute(result);
					}

					@Override
					protected void onPreExecute() {
						progressDialog = ProgressDialog.show(UserActivity.this,"检测登录状态","正在检测登录状态...",true,false);
						super.onPreExecute();
					}
				
				}.execute(0);
	}


	@Override
	protected void onResume() {
		Log.i("UserActivity","onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i("UserActivity","onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i("UserActivity","onStop");
		super.onStop();
	}


	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event); 
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		  if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		        Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();  
		    } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		        Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
		    }  
		  	return false;
	}




	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	} 
	
	

}
