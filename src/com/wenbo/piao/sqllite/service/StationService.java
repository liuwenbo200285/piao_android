package com.wenbo.piao.sqllite.service;

import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
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
			Log.e("findAllStations","findAll Station error!",e);
		}
		return null;
	}
}
