package com.moviedb;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moviedb.services.AbstractService;
import com.moviedb.services.ImageDownloadService;
import com.moviedb.services.MovieDetailsService;
import com.moviedb.services.ServiceListener;

public class MovieDetailsActivity extends Activity implements ServiceListener{

	private Thread thread;
	private Thread imgThread;
	
	private JSONObject movie;
	
	private ImageView ivMovie;
	
	private TextView tvTitle;
	private TextView tvReleaseDate;
	private TextView tvVoteAverage;
	
	private TextView tvOverview;
	
	private String webUrl;
	private String movieID;
	
	// Debug code (1)
	private String movieTitle;
	//=======================================================================================
	// Allow use of the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detail_refresh, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){

		case R.id.itmRefresh:
			// Refresh info & poster image when refresh is selected
			try {
				String id = getMovieID();
				MovieDetailsService service = new MovieDetailsService(id);
				service.addListener(this);
				thread = new Thread(service);
				thread.start();

				String imgPath = this.getDir("MyMovieLib", Context.MODE_PRIVATE).getAbsolutePath()+"/MyMovieLib/"+getMovieID()+".png";
				String external = "http://image.tmdb.org/t/p/w500" + movie.getString("poster_path");
				ImageDownloadService downloadService = new ImageDownloadService(external, imgPath);
				downloadService.addListener(this);
				imgThread = new Thread(downloadService);
				imgThread.start();
				Log.i("OPTS", "refresh clicked");
			} catch (JSONException ex) {
				Log.e("JSON", ex.getLocalizedMessage());
			}
	
			
			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	//=======================================================================================
	// onCreate() called when activity launches
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set title
		super.setTitle("Movie Details");
		
		// Set layout view
		setContentView(R.layout.activity_moviedetails);
		
		// Initialise instances and allocate relevant id
		ivMovie = (ImageView) findViewById(R.id.movie_picture);
		tvTitle = (TextView) findViewById(R.id.movieTitle);
		tvReleaseDate = (TextView) findViewById(R.id.movieReleaseDate);
		tvVoteAverage = (TextView) findViewById(R.id.movieVoteAverage);
		tvOverview = (TextView) findViewById(R.id.movieOverview);
		
		// Return intent value and assign to strMovie
		// Retrieve movie that has been selected on "mainActivity"
		String strMovie = this.getIntent().getExtras().getString("movie");
		
		
		if(strMovie != null){
			try {
				JSONObject movie = new JSONObject(strMovie);
				super.setTitle(movie.getString("title"));
				
				tvTitle.setText(movie.getString("title"));
				tvReleaseDate.setText(movie.getString("release_date"));
				tvVoteAverage.setText(movie.getString("vote_average"));
				
				setMovieTitle(movie.getString("title"));
				// Call service 
				setMovieID(movie.getString("id"));
				MovieDetailsService service = new MovieDetailsService(getMovieID());
				service.addListener(this);
				thread = new Thread(service);
				thread.start();
				
				//===========================================================================
				// MOVIE POSTER
                // Create a path to the local image & the creates a file object from that path
                String imgPath = this.getDir("MyMovieLib", Context.MODE_PRIVATE).getAbsolutePath()+"/MyMovieLib/"+getMovieID()+".png";
                
                // Declare image path as file
                File f = new File(imgPath);
                
                /*
                 * Test if file exists, if not, create a new ImageDownloadService & combine it with the imageThread
                 * 
                 * @param external
                 * Image request string as stated in documentation:
                 * Possible Poster Sizes: w92 / w154 / w185 / w343 / w500 / w700 / original
                 * 
                 */
                String external = "http://image.tmdb.org/t/p/w92" + movie.getString("poster_path");
                if(f.exists()){
                    ivMovie.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                    Log.i("JSON", "Image Set");
                } else {
                	
                    ImageDownloadService downloadService = new ImageDownloadService(external, imgPath);
                    downloadService.addListener(this);
                    imgThread = new Thread(downloadService);
                    imgThread.start();
                    Log.i("JSON", "Thread Started");
                    
          
                }
              //=============================================================================
			} catch (JSONException ex) {
				Log.e("JSON", ex.getLocalizedMessage());
			}
		} // End if(strMovie != null) statement	
	} // End onCreate() method
	//=======================================================================================
	// This class retrives the JSON data from the ENDPOINT
	@Override
	public void ServiceComplete(AbstractService service) {
		if(!service.hasError()){
			
			if(service instanceof MovieDetailsService){
				movie = ((MovieDetailsService)service).getResult();
				
				try {
					tvTitle.setText(movie.getString("title"));
					tvReleaseDate.setText(movie.getString("release_date"));
					tvVoteAverage.setText(movie.getString("vote_average"));
					
					tvOverview.setText(movie.getString("overview"));
					
					setWebUrl(movie.getString("homepage"));
					setMovieTitle(movie.getString("title"));
					
				} catch (JSONException ex) {
					Log.d("JSON", ex.getLocalizedMessage());
				}	
			} else if (service instanceof ImageDownloadService){
				File f = new File(((ImageDownloadService)service).getLocal());
				if(f.exists()){
					ivMovie.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
					Log.i("JSON", "Image retrived");
				}
			} 
		} // End if(!service.hasError())
	} // End ServiceComplete()
	
	//====================================================================================================
	// Method called when button is pressed via the "onClick" attribute in the activity_moviedetails.xml
	// Logs a message to log cat that displays button has been clicked
	// Displays a toast message to user to alert them of request
	// Create a new intent & assign "url" value to the key "link" & initiate the intent
	public void movieWeb(View v) {
		Log.i("URL", "movie web btnPressed");
		Toast.makeText(MovieDetailsActivity.this, "Redirecting To Website", Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent(MovieDetailsActivity.this, MovieWebActivity.class);
		intent.putExtra("link", getWebUrl());
		startActivity(intent);
	}

	//====================================================================================================
	// Movie Trailer Button
	public void movieTrailer(View v){
		Log.i("URL","Movie Trailer btnPressed");
		Toast.makeText(MovieDetailsActivity.this, "Redirecting To Trailer", Toast.LENGTH_LONG).show();
		// Add movieID to the intent and take the stored data in the string to the MovieTrailerActivityClass
		Intent intent = new Intent (MovieDetailsActivity.this, MovieTrailerActivity.class);
		intent.putExtra("movieTrailerId", getMovieID());
		startActivity(intent);
	}
	//====================================================================================================
	// Search NYTimes API Button
	public void movieReview(View v){
		Log.i("URL","Movie Review btnPressed");
		Toast.makeText(MovieDetailsActivity.this, "Redirecting To Reviews", Toast.LENGTH_LONG).show();
		// Add movieID to the intent and take the stored data in the string to the MovieTrailerActivityClass
		Intent intent = new Intent (MovieDetailsActivity.this, MovieReviewActivity.class);
		intent.putExtra("movieTitle", getMovieTitle());
		startActivity(intent);
	}
	//====================================================================================================
	// GETTER & SETTER METHODS
	public String getMovieID(){
		return this.movieID;
	}
	public void setMovieID(String movieID){
		this.movieID = movieID;
	}
	
	public String getWebUrl() {
		return this.webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	// Debug code (1)
	public String getMovieTitle(){
		return this.movieTitle;
	}
	public void setMovieTitle(String movieTitle){
		this.movieTitle = movieTitle;
	}
	//=====================================================================================================
}
