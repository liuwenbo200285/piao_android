package com.wenbo.piao.Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

public class OrderInfoFragment extends Fragment implements TabListener  {
	private Activity activity;
	
	private ProgressDialog progressDialog;
	
	private Tab noCompletedTab;
	
	private Tab completedTab;
	
	private ListView listView;
	
	private List<Order> noCompletedOrders;
	
	private FragmentTransaction ft;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info3,null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("onActivityCreated","onActivityCreated");
		activity = getActivity();
		listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
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
		Log.i("onCreate","onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("onDestroy","onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("onPause","onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("onResume","onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("onStart","onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("onStop","onStop");
		super.onStop();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabReselected:"+tab.getText());
	}

	@Override
	public void onTabSelected(Tab tab,FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabSelected:"+tab.getText());
		this.ft = ft;
		if(tab == noCompletedTab){
			new AsyncTask<Integer,Integer,Integer>() {
				@Override
				protected Integer doInBackground(Integer... params) {
					HttpResponse response = null;
					try {
						HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.NO_NOTCOMPLETE);
						response = HttpClientUtil.getHttpClient().execute(httpGet);
						if (response.getStatusLine().getStatusCode() == 200) {
							noCompletedOrders = JsoupUtil.getNoCompleteOrders(response.getEntity().getContent());
							if(!noCompletedOrders.isEmpty()){
								Iterator<Order> iterator = noCompletedOrders.iterator();
						    	List<Order> turnOrders = new ArrayList<Order>();
						    	while(iterator.hasNext()){
						    		Order order = iterator.next();
						    		int n = 0;
						    		if(order.getOrderInfos() != null && !order.getOrderInfos().isEmpty()){
						    			for(OrderInfo orderInfo:order.getOrderInfos()){
						    				if(n == 0){
						    					order.setOrderInfo(orderInfo);
						    					turnOrders.add(order);
						    				}else{
						    					Order order2 = new Order();
						    					order2.setOrderInfo(orderInfo);
						    					turnOrders.add(order2);
						    				}
						    				n++;
						    			}
						    		}
						    	}
						    	noCompletedOrders = null;
						    	noCompletedOrders = turnOrders;
							}
						}
//						noCompletedOrders = JsoupUtil.getNoCompleteOrders(activity.getAssets().open("Noname5.txt"));
					} catch (Exception e) {
						Log.e("GetNoCompletedOrder","onTabSelected", e);
					} finally {
						
					}
					return null;
				}

				@Override
				protected void onPostExecute(Integer result) {
					showView();
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					progressDialog = ProgressDialog.show(activity,"获取未付款订单","正在获取未付款订单...",true,false);
					super.onPreExecute();
				}
			}.execute(0);
		}else if(tab == completedTab){
			
		}
	}
	

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.i("OrderInfoFragment","onTabUnselected:"+tab.getText());
	}
    
    public void showView(){
    	progressDialog.dismiss();
    	if(noCompletedOrders.isEmpty()){
    		noCompletedOrders = null;
			LoginDialog.newInstance( "没有未付款订单！").show(activity.getFragmentManager(),"dialog"); 
			return;
		}
    	listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
		listView.setAdapter(adapter);
		ft.add(R.id.details,OrderInfoFragment.this,null);
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
				view = inflater.inflate(R.layout.nocompeletedorderview, null);
			}
			Order order = items.get(position);
			if (order != null) {
				if(StringUtils.isNotBlank(order.getOrderDate())){
					TextView orderInfo = (TextView) view
							.findViewById(R.id.orderTextView);
					orderInfo.setText(order.getOrderDate()+"      "+order.getOrderNum()+"\n订  单  号："+order.getOrderNo());
				}
				OrderInfo info = order.getOrderInfo();
				if(info != null){
					TextView trainInfo = (TextView) view
							.findViewById(R.id.trainInfoTextView);
					trainInfo.setText(info.getTrainInfo());
					TextView seatInfo = (TextView) view
							.findViewById(R.id.seatInfoTextView);
					seatInfo.setText(info.getSeatInfo());
					TextView passengersInfo = (TextView) view
							.findViewById(R.id.passengersInfoTextView);
					passengersInfo.setText(info.getPassengersInfo());
					TextView statusInfo = (TextView) view
							.findViewById(R.id.statusInfoTextView);
					statusInfo.setText(info.getStatusInfo());
				}
			}
			return view;
		}
	}
	
	
}
