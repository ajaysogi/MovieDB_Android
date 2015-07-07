/*
package com.moviedb;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "reviews.db";
	private static final int DATABASE_VERSION = 1;
	
	private Dao<Review, Integer> reviewDao = null;
	private RuntimeExceptionDao<Review, Integer> reviewRuntimeDao = null;
	
	
	public DatabaseHelper(Context context){
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		
		try {
			TableUtils.createTable(connectionSource, Review.class);
		} catch (SQLException e) {
			Log.d("demo", e.getLocalizedMessage());
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Review.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			Log.d("demo", e.getLocalizedMessage());
		}
		
	}
	
	public Dao<Review, Integer> getReviewDao() throws SQLException{
		if(reviewDao == null){
			reviewDao = getDao(Review.class);
		}
		return reviewDao;
	}
	
	public RuntimeExceptionDao<Review, Integer> getReviewRuntimeExceptionDao(){
		if(reviewRuntimeDao == null){
			reviewRuntimeDao = getRuntimeExceptionDao(Review.class);
		}
		return reviewRuntimeDao;
	}

	
	

}
*/