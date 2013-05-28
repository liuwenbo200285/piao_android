package com.wenbo.piao.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.sqllite.service.UserInfoService;
import com.wenbo.piao.sqllite.util.SqlLiteUtil;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.util.HttpClientUtil;

public class ContactFragment extends Fragment {
	
	private Activity activity;
	
	private UserInfoService userInfoService;
	
	private View view;
	
	private ListView listView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_info4, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setItemsCanFocus(true);
		return view;
	}
	
	public void showView(){
		userInfoService = SqlLiteUtil.getUserInfoService(activity);
		Collection<UserInfo> userInfos;
		if(HttpClientUtil.getUserInfoMap() != null
				&& !HttpClientUtil.getUserInfoMap().isEmpty()){
			userInfos = HttpClientUtil.getUserInfoMap().values();
		}else{
			userInfos = userInfoService.findAllInfos();
		}
		if(userInfos != null && !userInfos.isEmpty()){
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String,UserInfo> userinfoMap = new HashMap<String, UserInfo>();
			for(UserInfo userInfo:userInfos){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name",userInfo.getPassenger_name());
				map.put("info",userInfo.getSex_name()+" "+userInfo.getPassenger_id_type_name()
						+" "+userInfo.getPassenger_id_no());
				list.add(map);
				userinfoMap.put(userInfo.getPassenger_name(), userInfo);
			}
			SimpleAdapter adapter = new SimpleAdapter(activity,list,R.layout.listview,new String[]{"name","info"},
					new int[]{R.id.textView1,R.id.textView2});
			listView.setAdapter(adapter);
			HttpClientUtil.setUserInfoMap(userinfoMap);
		}else{
			LoginDialog.newInstance( "此账号还没有添加联系人！").show(activity.getFragmentManager(),"dialog"); 
		}
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = getActivity();
//		closeSoftInput();
		View view = getActivity().getActionBar().getCustomView();
		final Button skipButton = (Button)view.findViewById(R.id.actionBarSkipButton);
		skipButton.setText("同步");
		skipButton.setVisibility(View.VISIBLE);
		skipButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sync();
			}
		});
		showView();
		super.onActivityCreated(savedInstanceState);
	}
	
	public void sync(){
		Map<String,UserInfo> userinfoMap = new HashMap<String, UserInfo>();
		userInfoService.delByAccountName();
		GetPersonConstanct getPersonConstanct = new GetPersonConstanct(activity,userinfoMap,ContactFragment.this);
		getPersonConstanct.execute("");
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
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
	
}
