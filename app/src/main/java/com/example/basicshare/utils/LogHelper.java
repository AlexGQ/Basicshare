package com.example.basicshare.utils;

import android.util.Log;

public class LogHelper {

	private String mClassDebugName; 
	private boolean mTesting=false;
	private boolean mDebuging=true ;
	private String mDebugTag="MainActivity";
	
	
	public LogHelper(Object objeto, String tag){
		mClassDebugName=objeto.getClass().getSimpleName();
		mDebugTag = tag;
		mDebuging=true;
	}
	
	public LogHelper(String className, String tag){
		mClassDebugName=className; 
		mDebugTag = tag; 
	}
	
	public boolean isTesting() {
		return mTesting;
	}
	
	public void setTesting(boolean mTesting) {
		this.mTesting = mTesting;
	}
	
	public boolean isDebuging() {
		return mDebuging;
	}
	
	public void setDebug(boolean debug) {
		this.mDebuging = debug;
	}
	
	public String getDebugTag() {
		return mDebugTag;
	}
	
	public void debug(String str){
		if(mDebuging)
			Log.d(mDebugTag,mClassDebugName + " >_" + str);
	}
	
	public void error(String str){
		if(mDebuging)
			Log.e(mDebugTag,mClassDebugName + " >_" + str);
	}
	public void error(String str, Throwable tr){
		if(mDebuging)
			Log.e(mDebugTag,mClassDebugName + " >_" + str,tr);
	}
	
	public void Info(String str){
		if(mDebuging)
			Log.i(mDebugTag,mClassDebugName + " >_" + str);
	}

	
}
