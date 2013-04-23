package com.wenbo.piao.activity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.service.AccountService;
import com.wenbo.piao.sqllite.util.SqlLiteUtil;
import com.wenbo.piao.task.GetRandCodeTask;
import com.wenbo.piao.task.LoginTask;

public class MainActivity extends Activity {
	
	private AccountService accountService;
	
	private AlertDialog dialog;
	
	private List<Account> accounts;
	
	private String[] contacts;
	
	private boolean [] checkedItems;
	
	private EditText userNameText;
	
	private EditText userPassText;
	
	private EditText rangCodeText;
	
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
	}
	
	@Override
	protected void onStart() {
		Log.i("onStart","onStart...");
		userNameText = (EditText)findViewById(R.id.username);
		userPassText = (EditText)findViewById(R.id.password);
		rangCodeText = (EditText)findViewById(R.id.rangcode);
		imageView = (ImageView)findViewById(R.id.rangCodeImg);
		accountService = SqlLiteUtil.getAccountService(this);
		Account account = accountService.queryLastLoginAccount();
		if(account != null){
			userNameText.setText(account.getName());
			userPassText.setText(account.getPassword());
			rangCodeText.requestFocus();
			accounts = accountService.findAllAccounts();
		}
		ImageView imageView = (ImageView)findViewById(R.id.rangCodeImg);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getLoginRangeCode();
			}
		});
		Button button = (Button)findViewById(R.id.loginButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(StringUtils.isBlank(userNameText.getText().toString().trim())){
					LoginDialog.newInstance( "请输入用户名！").show(getFragmentManager(),"dialog"); 
					userNameText.requestFocus();
					return;
				}
				if(StringUtils.isBlank(userPassText.getText().toString().trim())){
					LoginDialog.newInstance( "请输入密码！").show(getFragmentManager(),"dialog"); 
					userPassText.requestFocus();
					return;
				}
				if(StringUtils.isBlank(rangCodeText.getText().toString().trim())){
					LoginDialog.newInstance( "请输入验证码！").show(getFragmentManager(),"dialog"); 
					rangCodeText.requestFocus();
					return;
				}
				LoginTask loginTask = new LoginTask(MainActivity.this,accountService);
				loginTask.execute("");
			}
		});
		Button selectAccountButton = (Button)findViewById(R.id.selectAccountbutton);
		selectAccountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});
		getLoginRangeCode();
		super.onStart();
	}
	
	public void showDialog(){
		if(dialog == null){
	        try {
	        	if(accounts == null || accounts.isEmpty()){
	    			new AlertDialog.Builder(this).setTitle("选择账号")
	    			.setMessage("没有可以选择的账号!").setNegativeButton("确定",null).show();
	    			return;
	    		}
	        	contacts = new String[accounts.size()];
	        	if(checkedItems == null){
					checkedItems = new boolean[contacts.length];
				}
	        	int i = 0;
	        	int select = 0;
	        	for(Account dbAccount:accounts){
	        		contacts[i] = dbAccount.getName();
	        		if(userNameText.getText().toString().equals(dbAccount.getName())){
	        			select = i;
	        		}
	        		i++;
	        	}
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setSingleChoiceItems(contacts,select,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = contacts[which];
						for(Account findAccount:accounts){
							if(name.equals(findAccount.getName())){
								userNameText.setText(findAccount.getName());
								userPassText.setText(findAccount.getPassword());
								rangCodeText.requestFocus();
								dialog.dismiss();
								break;
							}
						}
					}
				})
				.setIcon(android.R.drawable.btn_dropdown);
				builder.setTitle("选择登录账号");
				dialog = builder.create();
		        dialog.show();
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			dialog.show();
		}
	}
	
	@Override
	protected void onResume() {
		Log.i("onResume","onResume...");
		super.onResume();
	}

	protected void onPause() {
		Log.i("onPause","onPause...");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.i("onStop","onStop...");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		Log.i("onRestart","onRestart...");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		Log.i("onDestroy","onDestroy...");
		super.onDestroy();
	}
	
	/**
	 * 获取登录验证码
	 */
	private void getLoginRangeCode(){
		GetRandCodeTask getRandCodeTask = new GetRandCodeTask(imageView,1);
		getRandCodeTask.execute(UrlEnum.DO_MAIN.getPath()+UrlEnum.LOGIN_RANGCODE_URL.getPath());
	}

}
