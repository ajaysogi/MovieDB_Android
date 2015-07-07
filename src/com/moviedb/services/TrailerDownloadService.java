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
public class TrailerDownloadService extends AbstractService {

	
	private  String trailerID;
	//private JSONObject trailer;
	private JSONObject trailer;
	
	private JSONArray youtubeResult;
	
	public TrailerDownloadService(String trailerID) {
		this.trailerID = trailerID;
	}
	
	
	public JSONObject getResult(){
		return trailer;
	} 
	
	public JSONArray getYoutubeResult(){
		return youtubeResult;
	}
	
	
	
	@Override
	public void run() {
		// Retrieve the movie id from the MovieDetailsActivity class and assign the
		// value to the new string "movieTrailerID"
		//MovieDetailsActivity mda = new MovieDetailsActivity();
		//movieTrailerID = mda.getMovieID();
		
		// URL example from documentation for returning trailer details
		// http://api.themoviedb.org/3/movie/155/trailers?api_key=c32733824837ebd1ddb2ac60b90feced
		String api_key = "c32733824837ebd1ddb2ac60b90feced";
		String url = "http://api.themoviedb.org/3/movie/"+ trailerID +"/trailers?api_key="+api_key;
		
		Log.i("URL", url);
		
		
		boolean error = false;
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpResponse data = httpclient.execute(new HttpGet(url));
			HttpEntity entity = data.getEntity();
			String result = EntityUtils.toString(entity, "UTF8");
			
			trailer = new JSONObject(result);
			if(trailer.getInt("id") != 0){
				youtubeResult = trailer.getJSONArray("youtube");
			} else{
				error = true;
			}
		
		} catch (Exception e) {
			
			trailer = null;
			error = true;
			
		} finally{
			httpclient.getConnectionManager().shutdown();
		}
		
		super.serviceComplete(error);	
		
	}

}
