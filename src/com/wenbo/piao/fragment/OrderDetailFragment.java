package com.wenbo.piao.fragment;

import java.util.List;

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

import com.wenbo.piao.R;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.util.HttpClientUtil;

public class OrderDetailFragment extends Fragment {
	
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
		OrderAdapter adapter = new OrderAdapter(activity,0,HttpClientUtil.getSelectOrder().getOrderInfos());
		listView.setAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private class OrderAdapter extends ArrayAdapter<OrderInfo> {

		private List<OrderInfo> items;

		public OrderAdapter(Context context, int textViewResourceId,
				List<OrderInfo> items) {
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
			OrderInfo orderInfo = items.get(position);
			if(orderInfo != null){
				TextView trainInfo = (TextView) view
						.findViewById(R.id.trainInfoTextView);
				trainInfo.setText(orderInfo.getTrainInfo());
				TextView seatInfo = (TextView) view
						.findViewById(R.id.seatInfoTextView);
				seatInfo.setText(orderInfo.getSeatInfo());
				TextView passengersInfo = (TextView) view
						.findViewById(R.id.passengersInfoTextView);
				passengersInfo.setText(orderInfo.getPassengersInfo());
				TextView statusInfo = (TextView) view
						.findViewById(R.id.statusInfoTextView);
				statusInfo.setText(orderInfo.getStatusInfo());
			}
			return view;
		}
	}

}
