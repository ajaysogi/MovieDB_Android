package com.moviedb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		TextView title = (TextView) findViewById(R.id.home_title);
		title.toString();
		
		TextView subTitle = (TextView) findViewById(R.id.home_subtitle);
		subTitle.toString();
		
		Button b = (Button) findViewById(R.id.home_startSearch_btn);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, MainActivity.class));
				
			}
		});
/*		
		Button reviewBtn = (Button) findViewById(R.id.view_review_btn);
		reviewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, ViewReviewsActivity.class));
				
			}
		});
*/
	}
	


	

}
