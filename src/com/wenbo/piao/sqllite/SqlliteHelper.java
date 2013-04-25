package com.wenbo.piao.sqllite;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.domain.Station;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.sqllite.service.StationService;

public class SqlliteHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "android_piao";

	private static final int Version = 6;

	private static Dao<Account, Integer> accountDao = null;

	private static Dao<UserInfo, Integer> userInfoDao = null;
	
	private static Dao<Station, Integer> stationDao = null;
	
	private static StationService stationService = null;

	public SqlliteHelper(Context context) {
		super(context, DATABASE_NAME, null, Version);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Account.class);
			TableUtils.createTableIfNotExists(connectionSource, UserInfo.class);
			TableUtils.createTableIfNotExists(connectionSource, Station.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int arg2, int arg3) {
		try {
			TableUtils.dropTable(connectionSource, Account.class, true);
			TableUtils.dropTable(connectionSource, UserInfo.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Dao<Account, Integer> getAccountDataDao() {
		if (accountDao == null) {
			try {
				accountDao = getDao(Account.class);
			} catch (Exception e) {
				Log.e("getAccountDataDao", "获取accountDao失败！");
			}
		}
		return accountDao;
	}
	
	public Dao<UserInfo, Integer> getUserInfoDataDao() {
		if (userInfoDao == null) {
			try {
				userInfoDao = getDao(UserInfo.class);
			} catch (Exception e) {
				Log.e("getUserInfoDataDao", "获取userInfoDao失败！");
			}
		}
		return userInfoDao;
	}
	
	public Dao<Station, Integer> getStationDao() {
		if (stationDao == null) {
			try {
				stationDao = getDao(Station.class);
			} catch (Exception e) {
				Log.e("getStationDao", "获取stationDao失败！");
			}
		}
		return stationDao;
	}
	
	public StationService getStationService() {
		if (stationService == null) {
			try {
				stationService = new StationService(getStationDao());
			} catch (Exception e) {
				Log.e("getStationDao", "获取stationDao失败！");
			}
		}
		return stationService;
	}

}
