package com.wenbo.piao.fragment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wenbo.piao.R;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.util.HttpClientUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CompletedOrderListFragment extends Fragment {
	
	private Activity activity;
	
	private ListView listView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nocompletedorder, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = getActivity();
		listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		List<Order> orders = HttpClientUtil.getMyOrders();
		OrderAdapter adapter = new OrderAdapter(activity,0,orders);
		listView.setAdapter(adapter);
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
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
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
					orderInfo.setText(order.getOrderDate().trim()+"      "+order.getOrderNum()+"\n订  单  号： "+order.getOrderNo());
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
