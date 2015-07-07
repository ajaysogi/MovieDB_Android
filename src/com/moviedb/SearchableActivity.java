package com.moviedb;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.moviedb.services.AbstractService;
import com.moviedb.services.MovieSearchService;
import com.moviedb.services.ServiceListener;

public class SearchableActivity extends ListActivity implements ServiceListener{
	
	// Create variable to store searched results
	private ArrayList<JSONObject> searchResults;
	
	private Thread thread;
	
	private ProgressBar pb;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		// Create Progressbar & make it invisible
		pb = (ProgressBar) findViewById(R.id.search_progress_bar);
		pb.setVisibility(View.INVISIBLE);
		
		// create a new instance of the array
		searchResults = new ArrayList<JSONObject>();
		
		// Get the intent, verify the action & get the query
		Intent intent = getIntent();
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
			//==============================
			// Query Saver
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
					MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
			//==============================
			doSearch(query);
		}
	}
	
	// notify other activities that the user has selected the search result
	// select search result and the corresponding search item is broadcasted for other items to use.
	@Override 
	protected void onListItemClick(ListView l, View v, int position, long id){
		
		if(position < searchResults.size()){
			Intent result = new Intent();
			result.setAction(SEARCH_BROADCAST);
			result.putExtra("result", searchResults.get(position).toString());
			this.sendBroadcast(result);
			// Close and return to my film list
			this.finish();
		}
	}
	
	public void doSearch(String query){
		String[] result = new String[] { };
		// Display progress bar when search is conducted
		pb.setVisibility(View.VISIBLE);
		
		MovieSearchService service = new MovieSearchService(query);
		service.addListener(this);
		thread = new Thread(service);
		thread.start();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.movie_list, R.id.text, result));
	}

	@Override
	public void ServiceComplete(AbstractService service) {
		
		pb.setVisibility(View.INVISIBLE);
		
		// Execute if service does not display error
		if(!service.hasError()){
			MovieSearchService movieService = (MovieSearchService)service;
			String[] result = new String[movieService.getResults().length()];
			
			searchResults.clear();
			
			for(int i=0; i<movieService.getResults().length(); i++)
			{
				try {
					// add results to the searchResults array by adding each JSONObject
					searchResults.add(movieService.getResults().getJSONObject(i));
					// Extract "title" from JSON array
					result[i] = movieService.getResults().getJSONObject(i).getString("title");
					
					
					
				} catch (JSONException ex) {
					result[i] = "Error";
					Log.d("JSON", ex.getLocalizedMessage());
				}
			}
			// add results to array adapter
			setListAdapter(new ArrayAdapter<String>(this, R.layout.movie_list, R.id.text, result));
		} else{
			String[] result = new String[] { "No Results" };
			
			setListAdapter(new ArrayAdapter<String>(this, R.layout.movie_list, R.id.text, result));
		}
	}
	
	// public string which will allow a way for different activities to communicate with each other
	public static final String SEARCH_BROADCAST = "search_result_selected";
	
	//========================================================================

}
