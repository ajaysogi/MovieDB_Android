package com.moviedb.services;


import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class MovieSearchService extends AbstractService {

	private String query;
	private JSONArray results;
	
	
	@SuppressWarnings("deprecation")
	public MovieSearchService(String query){
		this.query = URLEncoder.encode(query);
	}
	
	public JSONArray getResults(){
		return results;
	}
	
	@Override
	// (KEY METHOD) This method will run on a seperate Thread and will access the service and the data it
	// returns.
	public void run() {
		String api_key = "c32733824837ebd1ddb2ac60b90feced";
		String url = "http://api.themoviedb.org/3/search/movie?api_key="+api_key+"&search_type=ngram&query="+query+"&include_adult=false";
		
		boolean error = false;
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpResponse data = httpclient.execute(new HttpGet(url));
			HttpEntity entity = data.getEntity();
			String result = EntityUtils.toString(entity, "UTF8");
			
			JSONObject json = new JSONObject(result);
			if(json.getInt("page")> 0){
				results = json.getJSONArray("results");
				//results.getJSONObject(0);
			}else{
				error = true;
			}
		} catch (Exception e) {
			results = null;
			error = true;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		super.serviceComplete(error);
	}

}
