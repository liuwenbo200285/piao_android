package com.wenbo.piao.sqllite.service;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.wenbo.piao.sqllite.domain.Account;

public class AccountService {
	
	private Dao<Account,Integer> accountDao;
	
	public AccountService(Dao<Account,Integer> accountDao) {
		this.accountDao = accountDao;
	}
	
	/**
	 * 创建或者更新account
	 * @param account
	 */
	public void create(Account account){
		try {
			accountDao.createOrUpdate(account);
		} catch (Exception e) {
			Log.e("AccountService","create Account error!",e);
		}
	}
	
	/**
	 * 查询最后一次登录的账户
	 * @return
	 */
	public Account queryLastLoginAccount(){
		try {
			return accountDao.queryBuilder().orderBy("updateDate",false).queryForFirst();
		} catch (Exception e) {
			Log.e("AccountService","queryLastLoginAccount error!",e);
		}
		return null;
	}
	
	/**
	 * 查询所有登录过的账户
	 * @return
	 */
	public List<Account> findAllAccounts(){
		try {
			return accountDao.queryForAll();
		} catch (Exception e) {
			Log.e("AccountService","findAllAccounts error!",e);
		}
		return null;
	}

}
