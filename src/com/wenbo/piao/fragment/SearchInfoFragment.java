package com.wenbo.piao.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.sqllite.domain.SearchInfo;
import com.wenbo.piao.sqllite.service.SearchInfoService;
import com.wenbo.piao.sqllite.util.SqlLiteUtil;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 订票查询信息
 * @author wenbo
 *
 */
public class SearchInfoFragment extends Fragment implements OnTouchListener{
	private UserActivity activity;
	
	private TextView stationText;
	
	private TextView searchInfoText;
	
	private Button skipButton;
	
	private SearchInfoService searchInfoService;
	
	private ListView listView;
	
	private List<SearchInfo> searchInfos;
	
	private AlertDialog alertDialog;
	
	private FragmentManager fm;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.searchinfo, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		activity = (UserActivity)getActivity();
		searchInfoService = SqlLiteUtil.getSearchInfoService(activity);
		fm = activity.getFragmentManager();
		View view = getActivity().getActionBar().getCustomView();
		Button skipButton = (Button)view.findViewById(R.id.actionBarSkipButton);
		skipButton.setVisibility(View.VISIBLE);
		listView = (ListView)activity.findViewById(R.id.searchInfoview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SearchInfo searchInfo = searchInfos.get(arg2);
				HttpClientUtil.setSelectSearchInfo(searchInfo);
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = fm.findFragmentByTag("tab1");
				if(fragment == null){
					fragment = new RobitOrderFragment();
					Bundle bundle = new Bundle();
					fragment.setArguments(bundle);
				}
				Bundle bundle = fragment.getArguments();
                bundle.putSerializable("searchInfo",searchInfo);
				activity.setCurrentFragment(fragment);
				ft.replace(R.id.details,fragment,"tab1");
				ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
				ft.addToBackStack(null);
				ft.commit();
			}
			
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setIcon(android.R.drawable.btn_dropdown)
				.setTitle("删除查询记录")
				.setMessage("您确认要删除该查询记录吗？")
				.setNegativeButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SearchInfo searchInfo = searchInfos.get(arg2);
						searchInfoService.delSearchInfo(searchInfo.getId());
						searchInfos.remove(arg2);
						showView();
					}
				})
				.setPositiveButton("取消",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.hide();
					}
				});
				alertDialog = builder.create();
				alertDialog.show();
				return true;
			}
		});
		showView();
		super.onActivityCreated(savedInstanceState);
	}
	
	public void showView(){
		searchInfos = HttpClientUtil.getSearchInfos();
		SearchInfoAdapter searchInfoAdapter = new SearchInfoAdapter(activity,0,searchInfos);
		listView.setAdapter(searchInfoAdapter);
	}
	
	
	private class SearchInfoAdapter extends ArrayAdapter<SearchInfo> {

		private List<SearchInfo> items;

		public SearchInfoAdapter(Context context, int textViewResourceId,
				List<SearchInfo> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.searchinfolistview, null);
			}
			SearchInfo searchInfo = items.get(position);
			stationText = (TextView)view.findViewById(R.id.stationText);
			stationText.setText(searchInfo.getFromStation()+"→"+searchInfo.getToStation());
			searchInfoText = (TextView)view.findViewById(R.id.searchInfoText);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("坐   席："+searchInfo.getOrderSeat()+"\n")
						 .append("乘   客："+searchInfo.getOrderPerson()+"\n")
						 .append("出发时间："+searchInfo.getOrderTime());
			searchInfoText.setText(stringBuilder.toString());
			return view;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		Log.i("onTouch", event.toString());
		return false;
	}
}
