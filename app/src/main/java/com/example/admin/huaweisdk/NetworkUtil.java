package com.example.admin.huaweisdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil 
{


    public static boolean isNetworkActive(Context context)
    {
        ConnectivityManager cwjManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null)
        {
            return info.isAvailable();
        }
        else
        {
            return false;
        }
    }
	
	
	
}
