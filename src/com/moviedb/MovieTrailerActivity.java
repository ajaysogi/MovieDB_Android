package com.moviedb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.moviedb.services.AbstractService;
import com.moviedb.services.ServiceListener;
import com.moviedb.services.TrailerDownloadService;

public class MovieTrailerActivity extends Activity implements ServiceListener {
	
	private Thread thread;
	private JSONObject trailer;
	private String strYouTrail = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		super.setTitle("Trailer");
		setContentView(R.layout.activity_trailer);
		
		// Return intent containng the "movieID" from the MovieDetailsActivity 
		// when "View Trailer" button has been clicked
		String id = getIntent().getStringExtra("movieTrailerId");
		
		// Parse the "movieID" value through the TrailerDownloadService() and 
		// start the thread.
		TrailerDownloadService service = new TrailerDownloadService(id);
		service.addListener(this);
		thread = new Thread(service);
		thread.start();
	}
	
	@Override
	public void ServiceComplete(AbstractService service) {
		
		if(!service.hasError()){
			trailer = ((TrailerDownloadService)service).getResult();
			
			try {
				// Create a localised JSONArray and allocate values from
				// the get request prompted by the TrailerDownloadService
				JSONArray youtube = trailer.getJSONArray("youtube");
				// Create an empty String variable
				//String strYouTrail = "";
				
				
				// Loop through the localised JSONArray and extract the first object
				// from the Array. If object is more than 1, break the loop.
				for(int i=0; i<youtube.length(); i++){
					if(i == 1){
						break;
					}
					JSONObject t = youtube.getJSONObject(i);
					
					// Create a new String variable and assign semi-youtube link value
					// String ytLink = "https://www.youtube.com/watch?v=";
					// Combine string to create a valid youtube url
					// strYouTrail += ytLink + t.getString("source");
					String yt = t.getString("source");
					setStrYouTrail(yt);
				}

			// Initiate intent where user can chose to load the link into a youtube app
			// if available or through mobile browser.
			// startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strYouTrail)));
				makeYoutubeRequest();
			
			} catch (JSONException ex) {
				Log.d("JSON","trailer activity service complete error");
			}
		} // End if(!service.hasError())	
	} // End ServiceComplete()
	
	//==============================================================================
	public void  makeYoutubeRequest(){
		String ytLink = "https://www.youtube.com/watch?v=" + getStrYouTrail();
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ytLink)));
	}
	
	
	//==============================================================================
	public String getStrYouTrail(){
		return this.strYouTrail;
	}
	public void setStrYouTrail(String strYouTrail){
		this.strYouTrail = strYouTrail;
	}
	//==============================================================================
	
} // End MovieTrailerActivity
