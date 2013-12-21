package com.wenbo.piao.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.OperationUtil;

public class NoCompletedOrderFragment extends Fragment {
	private UserActivity activity;
	
	private List<Order> noCompletedOrders;
	
	private ProgressDialog progressDialog;
	
	private ListView listView;
	
	private AlertDialog cancelDialog;
	
	private String initInfo;
	
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
		new AsyncTask<Integer,Integer,Integer>() {
			@Override
			protected Integer doInBackground(Integer... params) {
				try {
					String info = HttpClientUtil.doPost(UrlNewEnum.QUERYMYORDERNOCOMPLETE,new HashMap<String, String>(),0);
					if(StringUtils.isNotEmpty(info)){
						JSONObject jsonObject = JSONObject.parseObject(info);
						if(jsonObject.containsKey("status") && jsonObject.getBooleanValue("status")
								&& jsonObject.containsKey("data")){
							JSONArray array = jsonObject.getJSONObject("data").getJSONArray("orderDBList");
							noCompletedOrders = new ArrayList<Order>();
							Order order = null;
							JSONObject ordeObject = null;
							for(int i = 0; i < array.size(); i++){
								ordeObject = array.getJSONObject(i);
								order = new Order();
								order.setAllMoney(ordeObject.getDoubleValue("ticket_price_all")/100);
								order.setOrderDate(ordeObject.getString("order_date"));
								order.setOrderNo(ordeObject.getString("sequence_no"));
								order.setOrderNum(ordeObject.getString("ticket_totalnum"));
								order.setTrainInfo(ordeObject.getString("start_train_date_page")+"开 "+ordeObject.getString("train_code_page")
										+" "+ordeObject.getString("from_station_name_page")+"-"+ordeObject.getString("to_station_name_page"));
//								order.setOrderStatus(ordeObject.getJSONArray("tickets").getJSONObject(0).getString("ticket_status_name"));
								OrderInfo orderInfo = null;
								List<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
								JSONArray perpleArray = ordeObject.getJSONArray("tickets");
								for(int j = 0; j < perpleArray.size(); j++){
									JSONObject perpleObject = perpleArray.getJSONObject(j);
									order.setOrderStatus(perpleObject.getString("ticket_status_name"));
									orderInfo = new OrderInfo();
									orderInfo.setPassengersInfo(perpleObject.getJSONObject("passengerDTO").getString("passenger_name")
											+"\n"+perpleObject.getJSONObject("passengerDTO").getString("passenger_id_type_name"));
									orderInfo.setSeatInfo(perpleObject.getString("coach_name")+"\n"+perpleObject.getString("seat_name")+"\n"+
											perpleObject.getString("seat_type_name"));
									orderInfo.setStatusInfo(perpleObject.getString("ticket_status_name"));
									orderInfo.setTicketNo(perpleObject.getString("ticket_no"));
									orderInfo.setTrainInfo(perpleObject.getString("start_train_date_page")+" 开\n"+perpleObject.getJSONObject("stationTrainDTO").getString("station_train_code")
											+"\n"+perpleObject.getJSONObject("stationTrainDTO").getString("from_station_name")+"-"+perpleObject.getJSONObject("stationTrainDTO").getString("to_station_name"));
									orderInfos.add(orderInfo);
								}
								order.setOrderInfos(orderInfos);
								noCompletedOrders.add(order);
							}
						}
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
					sbBuilder.append("订单日期："+order.getOrderDate());
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
								.setIcon(android.R.drawable.btn_star)
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
												Map<String,String> paraMap = new LinkedHashMap<String, String>();
												paraMap.put("sequence_no",order.getOrderNo());
												paraMap.put("cancel_flag","cancel_order");
												return HttpClientUtil.doPost(UrlNewEnum.CANCELNOCOMPLETEMYORDER, paraMap,0);
											}

											@Override
											protected void onPostExecute(
													String result) {
												progressDialog.dismiss();
												JSONObject jsonObject = JSON.parseObject(result);
												if(jsonObject.containsKey("status")
														&& jsonObject.getBooleanValue("status")){
													LoginDialog.newInstance( "取消订单成功！").show(activity.getFragmentManager(),"dialog");
													HttpClientUtil.setNoCompletedOrders(null);
													initInfo = null;
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
								.setIcon(android.R.drawable.btn_star)
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
												if(initInfo == null){
													Map<String, String> paraMap = new LinkedHashMap<String, String>();
													paraMap.put("sequence_no",order.getOrderNo());
													paraMap.put("pay_flag","pay");
													String info = HttpClientUtil.doPost(UrlNewEnum.CONTINUE_PAY_NOCOMPLETEMYORDER, paraMap,0);
													JSONObject jsonObject = JSON.parseObject(info);
													if(jsonObject.containsKey("status")
															&& jsonObject.getBooleanValue("status")){
														info = HttpClientUtil.doPost(UrlNewEnum.PAYORDER_INIT,new LinkedHashMap<String, String>(),0);
														if(StringUtils.isNotBlank(info)){
															initInfo = info;
														}
													}
												}
												InputStream inputStream = OperationUtil.toPaySubmit(initInfo);
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
														}else if(StringUtils.contains(str,"pages/web/images/bank_zfb.gif")){
															str = StringUtils.replace(str,"pages/web/images/bank_zfb.gif","https://epay.12306.cn/pay/pages/web/images/bank_zfb.gif");
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
										Map<String, String> paraMap = new LinkedHashMap<String, String>();
										paraMap.put("sequence_no",order.getOrderNo());
										paraMap.put("pay_flag","pay");
										String info = HttpClientUtil.doPost(UrlNewEnum.CONTINUE_PAY_NOCOMPLETEMYORDER, paraMap,0);
										JSONObject jsonObject = JSON.parseObject(info);
										if(jsonObject.containsKey("status")
												&& jsonObject.getBooleanValue("status")){
											info = HttpClientUtil.doPost(UrlNewEnum.PAYORDER_INIT,new LinkedHashMap<String, String>(),0);
											if(StringUtils.isNotBlank(info)){
												initInfo = info;
												int n = StringUtils.indexOf(info,"loseTime");
												if(n != -1){
													info = StringUtils.substring(info,n+11,n+24);
													return info;
												}
											}
										}
										return null;
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
											long losetime = Long.parseLong(result);
											losetime = losetime-new Date().getTime();
											LoginDialog.newInstance( "剩余付款时间为："+(losetime/(1000*60))+"分钟！").show(activity.getFragmentManager(),"dialog");
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
					sbBuilder.append("\n车次信息： "+formatTrainInfo(order.getTrainInfo())
							+"\n总  张  数： "+order.getOrderNum()+"张\n总  价  格： "+order.getAllMoney()+"元\n订单状态： "+order.getOrderStatus());
					orderInfo.setText(sbBuilder.toString());
				}
			}
			return view;
		}
	}
	
	private String formatTrainInfo(String info){
		if(StringUtils.isNotEmpty(info) && StringUtils.isNotBlank(info)){
			info = StringUtils.replace(info,"\"","");
			info = StringUtils.replace(info,"[","");
			info = StringUtils.replace(info,"]","");
			return info;
		}
		return "";
	}
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
}
