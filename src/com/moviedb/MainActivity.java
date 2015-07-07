package com.moviedb;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	// declare new array list
	private ArrayList<JSONObject> movies;
	
	// declare broadcast receiver, responsible for intercepting the broadcasts and triggering 
	// activity to handle the selected search result
	private BroadcastReceiver bcr;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// initialise new array list  which will be used to store and display broadcast results
		movies = loadMovies();
		
		// set title
		super.setTitle("Bookmarked Movies");
		setContentView(R.layout.activity_main);
		
		updateListView(movies);
		
		// listen for broadcasts sent by the SearchableActivity class
		IntentFilter iF = new IntentFilter();
		iF.addAction(SearchableActivity.SEARCH_BROADCAST);
		bcr = new BroadcastReceiver(){
			
			// call the addMovie method and pass in search results selected
			@Override
			public void onReceive(Context context, Intent intent) {
				
				// pull strings out from the selected item in the broadcast
				String jsonString = intent.getExtras().getString("result");
				try {
					// pass through strings to the JSON object constructor to create a new JSONObject
					JSONObject object = new JSONObject(jsonString);
					
					// call method and pass through the new JSONObject 
					addMovie(object);
				} catch (JSONException ex) {
				}
				
			}
		};
		// register receiver with the intent filter
		registerReceiver(bcr, iF);
		
		//=========================================
		// remove film that has been long pressed on
		this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView arg0, View arg1, int pos, long id) {
				JSONObject movie = movies.get(pos);
				removeMovie(movie);
				return true;
			}
		});
	}
	
	// This method is called when activity is disposed of and removed from the view stack
	// this method is used to unregister the user
	@Override
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(bcr);
	}

	//===========================================================================================
	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//===========================================================================================
	// Select option from menu.
	// method is called when a user an option from the menu, as you can see we test the option
	// selected Id and if it matches the itemSearch button the we request the search bar with 'onSearchRequested();'
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){

		case R.id.itmSearch:
			onSearchRequested();
			return true;
			
		case R.id.clearHistory:
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
					MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
			suggestions.clearHistory();
			return true;
			
		case R.id.search_icon:
			onSearchRequested();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	//===========================================================================================
	// Detect when user presses and releases a button on device,
	// Code check if its search key, and triggers method to display search bar
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_SEARCH){
			onSearchRequested();
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_BACK){
			/*
			 * This detects the back button has been pressed and finishes the activity by call a 
			 * preset method and returns user back to the home screen. 
			 */
			finish();
			return true;
		}
		return false;
	}
	
	//===========================================================================================
	// add search results to the list. provides a way of rebuilding the string array and
	// updating it after a new search result is clicked and a new broadcast message has been 
	// received
	private void addMovie(JSONObject movie){
		movies.add(movie);
		saveMovies(movies);
		
		// update list
		updateListView(movies);
		
		
	}
	// Data presistence - Storing data into a JSONObject
	public void saveMovies(ArrayList<JSONObject> movieList){
		try {
			JSONObject listWrapper = new JSONObject();
			JSONArray list = new JSONArray(movies);
			listWrapper.put("movies", list);
			
			String strList = listWrapper.toString();
			
			FileOutputStream outputStream;
			
			try {
				outputStream = openFileOutput(MOVIE_FILENAME,  Context.MODE_PRIVATE);
				outputStream.write(strList.getBytes());
				outputStream.close();
			} catch (Exception e) {}
			
		} catch (JSONException ex) {}
	}
	
	public ArrayList<JSONObject> loadMovies(){
		ArrayList<JSONObject> movieList = new ArrayList<JSONObject>();
		try {
			StringBuilder strList = new StringBuilder();
			FileInputStream inputStream;
			
			try {
				inputStream = openFileInput(MOVIE_FILENAME);
				byte[] buffer = new byte[1024];
				while (inputStream.read(buffer) != -1){
					strList.append(new String(buffer));
				}
				inputStream.close();
			} catch (Exception e) {}
			
			JSONObject listWrapper = new JSONObject(strList.toString());
			JSONArray list = listWrapper.getJSONArray("movies");
			
			for(int i=0; i<list.length(); i++){
				movieList.add(list.getJSONObject(i));
			}
			
		} catch (JSONException ex) {}
		return movieList;
	}
	
	public void removeMovie(final JSONObject movie){
		try {
			// Create Box
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			// Set Title
			builder.setMessage("Remove " + movie.getString("title") + " from your list").setTitle(R.string.remove_alert_title);
			
			// Positive Button
			builder.setPositiveButton(R.string.remove_alert_posotive, new DialogInterface.OnClickListener(){
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					movies.remove(movie);
					saveMovies(movies);
					updateListView(movies);
				}
			});
			
			// Negative Button
			builder.setNegativeButton(R.string.remove_alert_negative, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					updateListView(movies);
				}
			});
			
			// Create The box
			AlertDialog dialog = builder.create();
			dialog.show();
			
		} catch (JSONException ex) {
			Log.d("JSON", "remove film error:" + ex.getLocalizedMessage());
		}
	}
	
	public static final String MOVIE_FILENAME = "mymovie.json";
	
	//===========================================================================================
	private void updateListView(ArrayList<JSONObject> movie){
		// update list
		String[] CELLS = new String[movies.size()];
		for(int i=0; i<movies.size(); i++){
			try {
				CELLS[i] = movies.get(i).getString("title");
			} catch (JSONException ex) { }
		}

		setListAdapter(new ArrayAdapter<String>(this, R.layout.movie_list, R.id.text, CELLS));
	}
	//===========================================================================================
	// 5 This method is called when user presses on a film stored in bookmark list.
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		if(position < movies.size()){
			JSONObject movie = movies.get(position);
			
			Intent intent = new Intent(this, MovieDetailsActivity.class);
			Bundle extras = new Bundle();
			extras.putString("movie", movie.toString());
			intent.putExtras(extras);
			startActivity(intent);
		}
	}
}
