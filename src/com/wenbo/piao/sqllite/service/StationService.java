package com.wenbo.piao.sqllite.service;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.wenbo.piao.sqllite.domain.Station;

public class StationService {

	private Dao<Station,Integer> stationDao;
	
	public StationService(Dao<Station,Integer> stationDao){
		this.stationDao = stationDao;
	}
	
	/**
	 * 创建或者更新station
	 * @param account
	 */
	public void create(Station station){
		try {
			stationDao.create(station);
		} catch (Exception e) {
			Log.e("StationService","create Station error!",e);
		}
	}
	
	public List<Station> findAllStations(){
		try {
			return stationDao.queryForAll();
		} catch (Exception e) {
			Log.e("StationService","findAll Station error!",e);
		}
		return null;
	}
	
	public long countAllStation(){
		try {
			return stationDao.queryBuilder().countOf();
		} catch (Exception e) {
			Log.e("StationService","findAll Station error!",e);
		}
		return 0;
	}
	
	public void delAll(){
		try {
			stationDao.deleteBuilder().delete();
		} catch (Exception e) {
			Log.e("StationService","delAll Station error!",e);
		}
	}
	
	public Station findStationBySimpleCode(Station station){
		try {
			stationDao.queryForSameId(station);
		} catch (Exception e) {
			Log.e("StationService","findStationBySimpleCode Station error!",e);
		}
		return null;
	}
	
	/**
	 * 车站联想
	 * @param station
	 * @return
	 */
	public List<Station> findStationLike(String station){
		try {
			QueryBuilder<Station,Integer> queryBuilder = stationDao.queryBuilder();
			Where<Station,Integer> where = queryBuilder.where();
			where.like("simplePinyingCode",station+"%");
			where.or().like("zhCode", station+"%");
			return queryBuilder.query();
		} catch (Exception e) {
			Log.e("StationService","findStationLike Station error!",e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Station findStationByZHName(String name){
		try {
			QueryBuilder<Station,Integer> queryBuilder = stationDao.queryBuilder();
			Where<Station,Integer> where = queryBuilder.where();
			where.eq("zhCode",name);
			List<Station> stations = queryBuilder.query();
			if(!stations.isEmpty()){
				return stations.get(0);
			}
			return null;
		} catch (Exception e) {
			Log.e("StationService","findStationLike Station error!",e);
		}
		return null;
	}
}
