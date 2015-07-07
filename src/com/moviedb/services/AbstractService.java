package com.moviedb.services;

import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


// Communicate with other classes using web service
@SuppressWarnings("serial")
public abstract class AbstractService implements Serializable, Runnable {
	
	// creates a list of listeners and methods to add and remove listeners from this list
	
	private ArrayList<ServiceListener> listeners;
	private boolean error;
	
	
	public AbstractService(){
		listeners = new ArrayList<ServiceListener>();
	}
	
	public void addListener(ServiceListener listener){
		listeners.add(listener);
	}
	public void removeListener(ServiceListener listener){
		listeners.remove(listener);
	}
	public boolean hasError(){
		return error;
	}
	
	public void serviceComplete(boolean error){
		this.error = error;
		
		Message m = _handler.obtainMessage();
		Bundle b = new Bundle();
		b.putSerializable("service", this);
		m.setData(b);
		_handler.sendMessage(m);
	}
	
	// used as service will be running on diffrent thread and listener events may require UI updates
	// need to make sure these events occur on the main thread. By dispatching a message to the handler
	// it will make sure the listeners are notified on the main or UI thread.
	@SuppressLint("HandlerLeak")
	final Handler _handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			AbstractService service = (AbstractService)msg.getData().getSerializable("service");
			
			for(ServiceListener listener : service.listeners){
				listener.ServiceComplete(service);
			}
		}
	}; // 

}
