package com.wenbo.piao.sqllite.util;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;
import com.wenbo.piao.sqllite.SqlliteHelper;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.domain.SearchInfo;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.sqllite.service.AccountService;
import com.wenbo.piao.sqllite.service.SearchInfoService;
import com.wenbo.piao.sqllite.service.UserInfoService;

public class SqlLiteUtil {

	public static AccountService getAccountService(Activity activity){
		if(activity != null){
			SqlliteHelper sqlliteHelper = new SqlliteHelper(activity);
			Dao<Account,Integer> accountDao = sqlliteHelper.getAccountDataDao();
			return new AccountService(accountDao);
		}
		return null;
	}
	
	public static UserInfoService getUserInfoService(Activity activity){
		if(activity != null){
			SqlliteHelper sqlliteHelper = new SqlliteHelper(activity);
			Dao<UserInfo,Integer> userInfoDao = sqlliteHelper.getUserInfoDataDao();
			return new UserInfoService(userInfoDao);
		}
		return null;
	}
	
	public static SearchInfoService getSearchInfoService(Activity activity){
		if(activity != null){
			SqlliteHelper sqlliteHelper = new SqlliteHelper(activity);
			return sqlliteHelper.getSearchInfoService();
		}
		return null;
	}
}
