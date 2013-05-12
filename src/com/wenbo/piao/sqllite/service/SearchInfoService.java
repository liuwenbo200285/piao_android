package com.wenbo.piao.sqllite.service;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.sqllite.domain.SearchInfo;

public class SearchInfoService {
	private Dao<SearchInfo,Integer> searchInfoDao;
	
	public SearchInfoService(Dao<SearchInfo,Integer> searchInfoDao) {
		this.searchInfoDao = searchInfoDao;
	}
	
	/**
	 * 创建或者更新account
	 * @param account
	 */
	public void create(SearchInfo searchInfo){
		try {
			searchInfoDao.createOrUpdate(searchInfo);
		} catch (Exception e) {
			Log.e("SearchInfoService","create SearchInfo error!",e);
		}
	}
	
	
	/**
	 * 查询登录账号所有的查询记录
	 * @param accountName
	 * @return
	 */
	public List<SearchInfo> findAccountSearchInfos(String accountName){
		try {
			return searchInfoDao.queryBuilder().where().eq("accountName", accountName).query();
		} catch (Exception e) {
			Log.e("SearchInfoService","findAccountSearchInfos error!",e);
		}
		return null;
	}
	
	/**
	 * 创建或者更新account
	 * @param account
	 */
	public SearchInfo findSearchInfo(ConfigInfo configInfo){
		try {
			Where<SearchInfo,Integer> where = searchInfoDao.queryBuilder().where();
			List<SearchInfo> searchInfos = where.eq("fromStation", configInfo.getFromStation()).
					and().eq("toStation", configInfo.getToStation()).
					query();
			if(!searchInfos.isEmpty()){
				return searchInfos.get(0);
			}
		} catch (Exception e) {
			Log.e("SearchInfoService","findSearchInfo error!",e);
		}
		return null;
	}
	
	/**
	 * 删除记录
	 * @param account
	 */
	public SearchInfo delSearchInfo(Integer id){
		try {
			searchInfoDao.deleteById(id);
		} catch (Exception e) {
			Log.e("SearchInfoService","delSearchInfo error!",e);
		}
		return null;
	}
	
	

}
