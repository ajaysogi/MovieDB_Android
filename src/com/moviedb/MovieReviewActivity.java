package com.moviedb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.moviedb.services.AbstractService;
import com.moviedb.services.MovieReviewService;
import com.moviedb.services.ServiceListener;


public class MovieReviewActivity extends Activity implements ServiceListener {

	private Thread thread;
	private JSONArray results;
	
	private TextView display_title;
	private TextView byline;
	private TextView publiction_date;
	private TextView headline;
	private TextView summary_short;
	
	private Button reviews_btn;
	
	private ProgressBar pb;
	
	private String reviewURL;
	private String displayTitle, author, pubDate, headLine, summaryShort;
	
	//**********************
	// DatabaseHelper dbHelper;
	//**********************
	
	// Create instance of the class MovieDetailsActivity
	MovieDetailsActivity m = new MovieDetailsActivity();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);
		
		// Create Button
		reviews_btn = (Button) findViewById(R.id.reviewURL_btn);
		
		// Progress bar
		pb = (ProgressBar) findViewById(R.id.reviewsProgressbar);
		pb.setVisibility(View.VISIBLE);
		
		// Create TextViews 
		display_title = (TextView) findViewById(R.id.display_title);
		publiction_date = (TextView) findViewById(R.id.publication_date);
		byline = (TextView) findViewById(R.id.byline);
		headline = (TextView) findViewById(R.id.headline);
		summary_short = (TextView) findViewById(R.id.summary_short);
		
		// Get the intent passed by the onClick movieTitle view from the MovieDetailsActivity class
		String title = getIntent().getStringExtra("movieTitle"); 
		
		// Request service and pass through the "title" through the constructor
		// Start Thread
		MovieReviewService service = new MovieReviewService(title);
		service.addListener(this);
		thread = new Thread(service);
		thread.start();
		
	}
	//*************************************
	// ORM LITE CODE
	//*************************************
	//private void saveReview(){
	//	dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
	//	RuntimeExceptionDao<Review, Integer> reviewDao = dbHelper.getReviewRuntimeExceptionDao();
		// Review(String displayTitle, String author, String pubDate, String headLine, String summaryShort)
	//	reviewDao.create(new Review(getDisplayTitle(), getAuthor(), getPubDate(), getHeadLine(), getSummaryShort()));
	//	Log.i("demo","Review Saved");
	//	OpenHelperManager.releaseHelper();
	//}
	//*************************************
	@Override
	public void ServiceComplete(AbstractService service) {
		if(!service.hasError()){
			// Get the Array results & the movie title
			results = ((MovieReviewService)service).getResults();
			String check = ((MovieReviewService)service).returnMovieTitle();
			
			
			try {
				
				
				for(int i = 0; i < results.length(); i++){
					
					JSONObject match = results.getJSONObject(i);
					String strDisplayTitle = match.getString("display_title");
					
					// Check if the movie title in the requested array matches the queried title
					if(check.equals(strDisplayTitle)){
						
						
						//Execute layout code here
						setDisplayTitle(match.getString("display_title") + " Review");
						display_title.setText(getDisplayTitle());
						
						setAuthor(match.getString("byline"));
						byline.setText(getAuthor());
						
						setPubDate(match.getString("publication_date"));
						publiction_date.setText(getPubDate());
						
						// Present error msg to user if summary is not available
						if(match.getString("summary_short").equals("")){
							setSummaryShort("Opps... Summary is currently unavailable");
							summary_short.setText(getSummaryShort());
						} else{
							setSummaryShort(match.getString("summary_short"));
							summary_short.setText(getSummaryShort());
						}
		
						setHeadLine(match.getString("headline"));
						headline.setText(getHeadLine());
						
						// Step through JSONObject that holds more review details
						JSONObject link = new JSONObject();
						link = match.getJSONObject("link");
						setReviewURL(link.getString("url"));
						reviews_btn.setText(link.getString("suggested_link_text"));
						
						
						break;
						
					} else{
						// Notification message alerting user of no results
						
					}
					
				} // End for loop
				
			} catch (JSONException ex) {
				Log.d("JSON", ex.getLocalizedMessage());
			}
			
			pb.setVisibility(View.INVISIBLE);
		} // End if(!service.hasError())	
		
	}
	//=================================================================================================
	// Website Redirect Button
	public void reviewWeb(View v) {
		Log.i("URL", "review web btnPressed");
		Toast.makeText(MovieReviewActivity.this, "Redirecting To Website", Toast.LENGTH_LONG).show();

		Intent intent = new Intent(MovieReviewActivity.this, MovieWebActivity.class);
		intent.putExtra("link", getReviewURL());
		startActivity(intent);
	}
	//=================================================================================================
	// Save Review Button
	//public void saveButton(View v) {
	//	Log.i("demo", "save btnPressed");
	//	Toast.makeText(MovieReviewActivity.this, "Review Svaed", Toast.LENGTH_LONG).show();

		//****************************************
	//	saveReview();
		//****************************************
	//}
	//=================================================================================================
	// Getters & Setters
	public String getReviewURL(){
		return this.reviewURL;
	}
	public void setReviewURL(String reviewURL){
		this.reviewURL = reviewURL;
	}
	public String getDisplayTitle() {
		return displayTitle;
	}
	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getHeadLine() {
		return headLine;
	}
	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}
	public String getSummaryShort() {
		return summaryShort;
	}
	public void setSummaryShort(String summaryShort) {
		this.summaryShort = summaryShort;
	}
	
	//=================================================================================================
}
