package com.moviedb.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("serial")
public class MovieDetailsService extends AbstractService{

	private  String movieId;
	private JSONObject movie;
	
	public MovieDetailsService(String movieId){
		this.movieId = movieId;
	}
	
	public JSONObject getResult(){
		return movie;
	}
	@Override
	public void run() {
		String api_key = "c32733824837ebd1ddb2ac60b90feced";
		String url = "http://api.themoviedb.org/3/movie/"+ movieId +"?api_key="+api_key;
		
		Log.i("URL", url);
		
		boolean error = false;
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpResponse data = httpclient.execute(new HttpGet(url));
			HttpEntity entity = data.getEntity();
			String result = EntityUtils.toString(entity, "UTF8");
			
			movie = new JSONObject(result);
			
		} catch (Exception e) {
			
			movie = null;
			error = true;
			
		} finally{
			httpclient.getConnectionManager().shutdown();
		}
		
		super.serviceComplete(error);
	}

}
