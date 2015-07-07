package com.moviedb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashScreenActivity extends Activity {
	
	// progress bar variable 
		private ProgressBar mProgress;
		private ImageView splashTitle;
	   
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			
			// Set the layout for the activity
			setContentView(R.layout.activity_splash);
			
			// Create progress bar
			mProgress = (ProgressBar) findViewById(R.id.progress_bar);
			// Create Image 
			splashTitle = (ImageView) findViewById(R.id.splashTitle);
			// Create instance of animation
			Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.splashanimie);
			// Run Animation
			splashTitle.startAnimation(scaleUp);
			// Create new thread for splash screen to run on
			Thread logoTimer = new Thread() {
	            public void run(){
	                try{
	                	// Screen display time and progress bar variables
	                    int logoTimer = 0;
	                    int prog = 0;
	                    // Loops through 20 times updating the splash screen and the
	                    // Progress bar
	                    while(logoTimer < 2000 && prog <= 100){
	                    	// Pause screen time
	                        sleep(100);
	                        // Add 100 to timer
	                        logoTimer = logoTimer +100;
	                        // Add to progress bar value
	                        prog = prog + 5;
	                        // Display update progress bar
	                        mProgress.setProgress(prog);
	                    }; // End While loop
	                    // Load the main activity and clear the splash screen so user
	                    // can no longer go back to it
	                    startActivity(new Intent("com.moviedb.CLEARSCREEN"));
	                } catch (InterruptedException e) {
	                    Log.e("UI", e.getLocalizedMessage());
	                } finally{
	                    finish();
	                }
	            }
	        }; // Close Thread()
	        // Start thread
	        logoTimer.start(); 
		} // End onCreate()
}
