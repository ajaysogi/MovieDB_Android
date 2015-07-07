/*
package com.moviedb;

import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewReviewsActivity extends Activity {

	DatabaseHelper dbHelper;
	private ListView reviewListView;
	private ArrayAdapter<String> listAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_reviews);
		
		reviewListView = (ListView) findViewById(R.id.reviewListView);
		
		loadReviews();
		
		String[] reviews = new String[]{};
		
		
		
		
	}
	
	private void loadReviews(){
		dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		RuntimeExceptionDao<Review, Integer> reviewDao = dbHelper.getReviewRuntimeExceptionDao();
		
		// query
		List<Review> reviews = reviewDao.queryForAll();
		Log.i("demo", reviews.toString());
		
		OpenHelperManager.releaseHelper();
	}
}
*/