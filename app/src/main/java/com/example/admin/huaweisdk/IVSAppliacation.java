package com.example.admin.huaweisdk;

import com.huawei.esdk.ivs.bean.DomainInfo;

import android.app.Application;

public class IVSAppliacation extends Application
{

	private DomainInfo currentDomainInfo;

	private static IVSAppliacation instance = null;


	public static IVSAppliacation getInstance()
	{
		return instance;
	}


	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;
	}

	public DomainInfo getCurrentDomainInfo()
	{
		return currentDomainInfo;
	}



	public void setCurrentDomainInfo(DomainInfo currentDomainInfo)
	{
		this.currentDomainInfo = currentDomainInfo;
	}



}
