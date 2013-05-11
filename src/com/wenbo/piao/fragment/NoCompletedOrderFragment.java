package com.wenbo.piao.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.PayInfo;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;
import com.wenbo.piao.util.OperationUtil;

public class NoCompletedOrderFragment extends Fragment {
	private UserActivity activity;
	
	private List<Order> noCompletedOrders;
	
	private ProgressDialog progressDialog;
	
	private ListView listView;
	
	private AlertDialog cancelDialog;
	
	private FragmentManager fm;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nocompletedorder, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		activity = (UserActivity)getActivity();
		fm = activity.getFragmentManager();
		listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HttpClientUtil.setSelectOrder(noCompletedOrders.get(arg2));
				FragmentTransaction ft = fm.beginTransaction();
		    	Fragment listFragment = null;
		    	listFragment = fm.findFragmentByTag("orderDetail");
		    	if(listFragment == null){
		    		listFragment = new OrderDetailFragment();
		    	}
				ft.replace(R.id.details,listFragment,"orderDetail");
				ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
				ft.addToBackStack(null);
				ft.commit();
				activity.setCurrentFragment(listFragment);
			}
		});
		noCompletedOrders = HttpClientUtil.getNoCompletedOrders();
		getOrder();
		super.onActivityCreated(savedInstanceState);
	}
	
	private void getOrder(){
		if(noCompletedOrders == null || noCompletedOrders.isEmpty()){
			new AsyncTask<Integer,Integer,Integer>() {
				@Override
				protected Integer doInBackground(Integer... params) {
					HttpResponse response = null;
					try {
						HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.NO_NOTCOMPLETE);
						response = HttpClientUtil.getHttpClient().execute(httpGet);
						if (response.getStatusLine().getStatusCode() == 200) {
							noCompletedOrders = JsoupUtil.getNoCompleteOrders(response.getEntity().getContent());
//							noCompletedOrders = JsoupUtil.getNoCompleteOrders(activity.getAssets().open("Noname5.txt"));
							HttpClientUtil.setNoCompletedOrders(noCompletedOrders);
						}
					} catch (Exception e) {
						Log.e("GetNoCompletedOrder","onTabSelected", e);
					} finally {
						
					}
					return null;
				}

				@Override
				protected void onPostExecute(Integer result) {
					progressDialog.dismiss();
					showView();
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					progressDialog = ProgressDialog.show(activity,"获取未付款订单","正在获取未付款订单...",true,false);
					super.onPreExecute();
				}
			}.execute(0);
		}else{
			showView();
		}
	}
	
	private void showView(){
		if(noCompletedOrders == null || noCompletedOrders.isEmpty()){
    		noCompletedOrders = null;
			LoginDialog.newInstance( "没有未付款订单！").show(activity.getFragmentManager(),"dialog"); 
			return;
		}
		OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
		listView.setAdapter(adapter);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private class OrderAdapter extends ArrayAdapter<Order> {

		private List<Order> items;

		public OrderAdapter(Context context, int textViewResourceId,
				List<Order> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.nocompeletedorderdetail, null);
			}
			final Order order = items.get(position);
			if (order != null) {
				if(StringUtils.isNotBlank(order.getOrderDate())){
					TextView orderInfo = (TextView) view.findViewById(R.id.orderTextView);
					StringBuilder sbBuilder = new StringBuilder();
					sbBuilder.append(order.getOrderDate());
					Button refundButton = (Button)view.findViewById(R.id.refund);
					Button payButton = (Button)view.findViewById(R.id.pay);
					Button lastTimeButton = (Button)view.findViewById(R.id.lastTimeButton);
					if(StringUtils.isEmpty(order.getOrderNo())){
						refundButton.setVisibility(View.INVISIBLE);
						payButton.setVisibility(View.INVISIBLE);
						lastTimeButton.setVisibility(View.INVISIBLE);
					}else{
						sbBuilder.append("\n订  单  号： "+order.getOrderNo());
						refundButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(activity)
								.setIcon(android.R.drawable.btn_dropdown)
								.setTitle("订单号："+order.getOrderNo())
								.setMessage("您确认要取消该订单吗？")
								.setPositiveButton("确定",new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										new AsyncTask<String,Integer,String>(){
											private ProgressDialog progressDialog;
											@Override
											protected String doInBackground(
													String... params) {
												return OperationUtil.canelOrder(order.getOrderNo(),order.getToken());
											}

											@Override
											protected void onPostExecute(
													String result) {
												progressDialog.dismiss();
												if(result.equals(OperationUtil.OPERATION_SUCCESS)){
													LoginDialog.newInstance( "取消订单成功！").show(activity.getFragmentManager(),"dialog");
													HttpClientUtil.setNoCompletedOrders(null);
													noCompletedOrders.clear();
													OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
													listView.setAdapter(adapter);
												}else{
													LoginDialog.newInstance( "取消订单失败！").show(activity.getFragmentManager(),"dialog"); 
												}
												super.onPostExecute(result);
											}

											@Override
											protected void onPreExecute() {
												progressDialog = ProgressDialog.show(activity,"取消订单","正在取消订单...",true,false);
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
												cancelDialog.hide();
											}
										});
								cancelDialog = builder.create();
								cancelDialog.show();
							}
						});
						payButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(activity)
								.setIcon(android.R.drawable.btn_dropdown)
								.setTitle("订单号："+order.getOrderNo())
								.setMessage("您确认要付款吗？")
								.setPositiveButton("确定",new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										new AsyncTask<String,Integer,String>(){
											private ProgressDialog progressDialog;
											@Override
											protected String doInBackground(
													String... params) {
												 PayInfo payInfo = OperationUtil.toPayinit(order.getOrderNo(),order.getToken(),
														order.getOrderInfos().get(0).getTicketNo());
												if(payInfo == null){
													return null;
												}
												InputStream inputStream = OperationUtil.toPaySubmit(payInfo);
												if(inputStream == null){
													return null;
												}
												FileOutputStream fileOutputStream = null;
												String code = RandomStringUtils.randomNumeric(5);
												try {
													fileOutputStream = activity.openFileOutput("pay"+code+".html",Context.MODE_WORLD_READABLE);
													BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
													String str = bufferedReader.readLine();
													while(str != null){
														if(StringUtils.contains(str,"pages/web/mb.html")){
															str = StringUtils.replace(str,"pages/web/mb.html","https://epay.12306.cn/pay/pages/web/mb.html");
														}else if(StringUtils.contains(str,"pages/web/css/bank.css")){
															str = StringUtils.replace(str,"pages/web/css/bank.css","https://epay.12306.cn/pay/pages/web/css/bank.css");
														}else if(StringUtils.contains(str,"pages/web/mb.html")){
															str = StringUtils.replace(str,"pages/web/css/bank.css","https://epay.12306.cn/pay/pages/web/mb.html");
														}
														else if(StringUtils.contains(str,"/pay/webBusiness")){
															str = StringUtils.replace(str,"/pay/webBusiness","https://epay.12306.cn/pay/webBusiness");
														}else if(StringUtils.contains(str,"pages/web/images/bank_gsyh2.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_gsyh2.gif","https://epay.12306.cn/pay/pages/web/images/bank_gsyh2.gif");
														}else if(StringUtils.contains(str,"pages/web/images/bank_nyyh2.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_nyyh2.gif","https://epay.12306.cn/pay/pages/web/images/bank_nyyh2.gif");
														}else if(StringUtils.contains(str,"pages/web/images/bank_zgyh2.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_zgyh2.gif","https://epay.12306.cn/pay/pages/web/images/bank_zgyh2.gif");
														}else if(StringUtils.contains(str,"pages/web/images/bank_zsyh2.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_zsyh2.gif","https://epay.12306.cn/pay/pages/web/images/bank_zsyh2.gif");
														}else if(StringUtils.contains(str,"pages/web/images/bank_zgyl.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_zgyl.gif","https://epay.12306.cn/pay/pages/web/images/bank_zgyl.gif");
														}else if(StringUtils.contains(str,"pages/web/images/bank_ztytk.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_ztytk.gif","https://epay.12306.cn/pay/pages/web/images/bank_ztytk.gif");
														}else if(StringUtils.contains(str,"pages/web/images/bank_jsyh2.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_jsyh2.gif","https://epay.12306.cn/pay/pages/web/images/bank_jsyh2.gif");
														}
														str = str+"\n";
														fileOutputStream.write(str.getBytes());
														str = bufferedReader.readLine();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}finally{
													IOUtils.closeQuietly(fileOutputStream);
												}
												File file = new File(activity.getFilesDir().getPath()+"/pay"+code+".html");
												return file.getPath();
											}

											@Override
											protected void onPostExecute(
													String result) {
												progressDialog.dismiss();
												if(result == null){
													LoginDialog.newInstance("该订单已经被取消").show(activity.getFragmentManager(),"dialog");
													noCompletedOrders.clear();
													OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
													listView.setAdapter(adapter);
													return;
												}
												Intent intent=new Intent(); 
												intent.setAction("android.intent.action.VIEW"); 
												Uri CONTENT_URI_BROWSERS = Uri.parse("file://"+result); 
												intent.setData(CONTENT_URI_BROWSERS); 
												intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity"); 
												activity.startActivity(intent); 
												super.onPostExecute(result);
											}

											@Override
											protected void onPreExecute() {
												progressDialog = ProgressDialog.show(activity,"付款信息","正在获取付款信息...",true,false);
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
												cancelDialog.hide();
											}
										});
								cancelDialog = builder.create();
								cancelDialog.show();
							}
						});
						lastTimeButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								new AsyncTask<String,Integer,String>(){
									private ProgressDialog progressDialog;
									@Override
									protected String doInBackground(
											String... params) {
										return OperationUtil.getLastTime(order.getOrderNo(),order.getToken(),
												order.getOrderInfos().get(0).getTicketNo());
									}

									@Override
									protected void onPostExecute(
											String result) {
										progressDialog.dismiss();
										if(result == null){
											LoginDialog.newInstance("该订单已经被取消").show(activity.getFragmentManager(),"dialog");
											noCompletedOrders.clear();
											OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
											listView.setAdapter(adapter);
										}else{
											LoginDialog.newInstance( "剩余付款时间为："+result+"分钟！").show(activity.getFragmentManager(),"dialog");
										}
										super.onPostExecute(result);
									}

									@Override
									protected void onPreExecute() {
										progressDialog = ProgressDialog.show(activity,"剩余付款时间","正在查询剩余付款时间...",true,false);
										super.onPreExecute();
									}
								}.execute("");
							}
						});
					}
					sbBuilder.append("\n车次信息： "+order.getTrainInfo()
							+"\n总  张  数： "+order.getOrderNum()+"张\n总  价  格： "+order.getAllMoney()+"元\n订单状态： "+order.getOrderStatus());
					orderInfo.setText(sbBuilder.toString());
				}
			}
			return view;
		}
	}
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
}
