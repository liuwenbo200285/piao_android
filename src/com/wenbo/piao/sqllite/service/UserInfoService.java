package com.wenbo.piao.sqllite.service;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.util.HttpClientUtil;

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
			Account account = HttpClientUtil.getAccount();
			QueryBuilder<UserInfo,Integer> builder = userinfoDao.queryBuilder();
			builder.where().eq("accountName",account.getName());
			builder.orderBy("index",true);
			return builder.query();
		} catch (Exception e) {
			Log.e("UserInfoService","create",e);
		}
		return null;
	}
	
	/**
	 * 根据用户删除订票人信息
	 * @param accountName
	 */
	public void delByAccountName(){
		try {
			Account account = HttpClientUtil.getAccount();
			DeleteBuilder<UserInfo,Integer> deleteBuilder = userinfoDao.deleteBuilder();
			deleteBuilder.where().eq("accountName", account.getName());
			deleteBuilder.delete();
		} catch (Exception e) {
			Log.e("UserInfoService","create",e);
		}
	}
	
	public void delAll(){
		try {
			userinfoDao.deleteBuilder().delete();
		} catch (Exception e) {
			Log.e("UserInfoService","create",e);
		}
	}

}
