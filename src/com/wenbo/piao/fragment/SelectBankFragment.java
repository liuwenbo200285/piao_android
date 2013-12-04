package com.wenbo.piao.fragment;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.PayInfo;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.OperationUtil;

/**
 * 选择银行界面fragment
 * @author wenbo
 *
 */
public class SelectBankFragment extends Fragment implements OnTouchListener {
	
	private UserActivity activity;
	
	private TextView textView;
	
	private ImageView icbcImage;
	
	private ImageView ccbImage;
	
	private ImageView abcImage;
	
	private ImageView bocImage;
	
	private ImageView unionPayImage;
	
	private ImageView cmbcImage;
	
	private ImageView baikeImage;
	
	private PayInfo payInfo;
	
	private String selectBank;
	
	private AlertDialog tipsDialog;
	private FragmentManager fm;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.selectbank, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		activity = (UserActivity)getActivity();
		fm = activity.getFragmentManager();
		payInfo = HttpClientUtil.getPayInfo();
		Order order = HttpClientUtil.getNoCompletedOrders().get(0);
		textView = (TextView)activity.findViewById(R.id.payInfoText);
		textView.setText("车次信息："+order.getTrainInfo()+"\n应付金额："+payInfo.getPayMoney()+"\n\n请选择付款银行：");
		icbcImage = (ImageView)activity.findViewById(R.id.icbcImage);
		icbcImage.setOnTouchListener(this);
		ccbImage = (ImageView)activity.findViewById(R.id.ccbImage);
		ccbImage.setOnTouchListener(this);
		abcImage = (ImageView)activity.findViewById(R.id.abcImage);
		abcImage.setOnTouchListener(this);
		bocImage = (ImageView)activity.findViewById(R.id.bocImage);
		bocImage.setOnTouchListener(this);
		unionPayImage = (ImageView)activity.findViewById(R.id.unionPayImage);
		unionPayImage.setOnTouchListener(this);
		cmbcImage = (ImageView)activity.findViewById(R.id.cmbcImage);
		cmbcImage.setOnTouchListener(this);
		baikeImage = (ImageView)activity.findViewById(R.id.baikeImage);
		baikeImage.setOnTouchListener(this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.icbcImage:
			selectBank = "工商银行";
			break;
		case R.id.ccbImage:
			selectBank = "建设银行";
			break;
		case R.id.abcImage:
			selectBank = "农业银行";
			break;
		case R.id.bocImage:
			selectBank = "中国银行";
			break;
		case R.id.unionPayImage:
			selectBank = "中国银联";
			break;
		case R.id.cmbcImage:
			selectBank = "招商银行";
			break;
		case R.id.baikeImage:
			selectBank = "中铁银通卡";
			break;
		default:
			break;
		}
		payInfo.setBankId(HttpClientUtil.getBankMap().get(selectBank));
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
		.setIcon(android.R.drawable.btn_star)
		.setTitle(selectBank+"付款")
		.setMessage("您确认要选择"+selectBank+"付款吗？")
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new AsyncTask<String,Integer,String>(){
					private ProgressDialog progressDialog;
					@Override
					protected String doInBackground(
							String... params) {
						FileOutputStream fileOutputStream = null;
						String code = RandomStringUtils.randomNumeric(5);
						try {
							String info = OperationUtil.selectBank(payInfo);
							fileOutputStream = activity.openFileOutput("pay"+code+".html",Context.MODE_WORLD_READABLE);
							fileOutputStream.write(info.getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							IOUtils.closeQuietly(fileOutputStream);
						}
						File file = new File(activity.getFilesDir().getPath()+"/pay"+code+".html");
						if(file.exists()){
							return file.getPath();
						}
						return null;
					}

					@Override
					protected void onPostExecute(
							String result) {
						progressDialog.dismiss();
						if(result != null){
							Intent intent=new Intent(); 
							intent.setAction("android.intent.action.VIEW"); 
							Uri CONTENT_URI_BROWSERS = Uri.parse("file://"+result); 
							intent.setData(CONTENT_URI_BROWSERS); 
							intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity"); 
							activity.startActivity(intent); 
						}
						activity.setCurrentFragment(fm.findFragmentByTag("tab2"));
						fm.popBackStack();
						super.onPostExecute(result);
					}

					@Override
					protected void onPreExecute() {
						File file = new File(activity.getFilesDir().getPath());
						if(file.exists()){
							File [] files = file.listFiles();
							if(files != null){
								for(File dbFile:files){
									dbFile.deleteOnExit();
								}
							}
						}
						progressDialog = ProgressDialog.show(activity,"确认付款","正在付款...",true,false);
						super.onPreExecute();
					}
				}.execute("");
			}
		})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						tipsDialog.hide();
					}
				});
		tipsDialog = builder.create();
		tipsDialog.show();
		return false;
	}
}
