package com.wenbo.piao.sqllite.service;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.wenbo.piao.sqllite.domain.UserInfo;

public class UserInfoService {
	
	private Dao<UserInfo,Integer> userinfoDao;
	
	public UserInfoService(Dao<UserInfo,Integer> userinfoDao){
		this.userinfoDao = userinfoDao;
	}
	
	public void create(UserInfo userInfo){
		try {
			userinfoDao.createIfNotExists(userInfo);
		} catch (Exception e) {
			Log.e("UserInfoService","create",e);
		}
	}
	
	public List<UserInfo> findAllInfos(){
		try {
			return userinfoDao.queryBuilder().orderBy("index",true).query();
		} catch (Exception e) {
			Log.e("UserInfoService","create",e);
		}
		return null;
	}

}
