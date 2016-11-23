package com.example.admin.huaweisdk;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		initView();
		initData();
		initOver();
	}
	
	public void initView(){};
	public void initData(){};
	public void initOver(){};
	
	
}
