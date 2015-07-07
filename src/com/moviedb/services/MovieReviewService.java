package com.moviedb.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("serial")
public class MovieReviewService extends AbstractService {

	private  String movieTitle;

	private JSONObject reviewResults;
	
	private JSONArray results;
	
	// Constructor which receives movie title
	public MovieReviewService(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	
	// 
	public JSONObject getReviewResult(){
		return reviewResults;
	} 
	
	public JSONArray getResults(){
		return results;
	}
	
	public String returnMovieTitle(){
		return movieTitle;
	}
	
	@Override
	public void run() {
		// Create a string that replaces any white spaces with the + sign in order to make a correct api request 
		String newMovieTitle = movieTitle.replaceAll(" ", "+");
		String api_key = "cbce549b4f88e097bc20ff5efe87fae7:8:70277571";
		String url = "http://api.nytimes.com/svc/movies/v2/reviews/search.json?query="+ newMovieTitle +"&api-key="+api_key;
		
		Log.i("URL", url);
		
		
		boolean error = false;
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpResponse data = httpclient.execute(new HttpGet(url));
			HttpEntity entity = data.getEntity();
			String result = EntityUtils.toString(entity, "UTF8");
			
			reviewResults = new JSONObject(result);
			if(reviewResults.getInt("num_results") != 0){
				results = reviewResults.getJSONArray("results");
			} else{
				error = true;
			}
			
		
		} catch (Exception e) {
			
			reviewResults = null;
			error = true;
			
		} finally{
			httpclient.getConnectionManager().shutdown();
		}
		
		super.serviceComplete(error);	
		
	}

}
