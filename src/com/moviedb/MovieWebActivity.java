package com.moviedb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MovieWebActivity extends Activity{

	private WebView myWebView;

	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		
	
		
		// Return Intent 
		String link = getIntent().getStringExtra("link");
		
		
		// Enable JavaScript in the web view
		myWebView = (WebView)findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        // fix size based on screen size
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.loadUrl(link);
        //Load in the app and not android externally
        myWebView.setWebViewClient(new MyWebViewClient());
        
     
		
	}
	
	private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url)
        {
            webview.loadUrl(url);
          
            return true;
        }
    }
	
	@Override 
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    if((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack())
    {
     myWebView.goBack();
    return true;
    }
    return super.onKeyDown(keyCode, event);
    }
	
}
