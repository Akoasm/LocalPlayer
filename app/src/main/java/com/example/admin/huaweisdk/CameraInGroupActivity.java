package com.example.admin.huaweisdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.ca.eivs.util.XMLUtil;
import com.huawei.esdk.ivs.bean.OrgInfo;
import com.huawei.esdk.ivs.bean.request.GeneralQueryRequest;

/**
 * 分组之中的相机列表视图
 * 
 * **/
public class CameraInGroupActivity extends Activity
{
	private static final String TAG = CameraInGroupActivity.class.getSimpleName();
	
	private OrgInfo orgInfo;
	
	private ProgressDialog pDialog;
	
	private static final String TO_INDEX = "30";
    
    private static final String FROM_INDEX = "1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_camera_group);
		
		
		
		convertOrgInfo();
		initUI();
	}
	
	/**
	 * 获取OrgInfo对象
	 * */
	private void convertOrgInfo()
	{
		Log.i(TAG, "convertOrgInfo exec ");
		Intent intent = getIntent();
		if(orgInfo==null)
		{
			orgInfo = new OrgInfo();
		}
		orgInfo.setDomainName(intent.getStringExtra("domainName"));
		orgInfo.setOrgCode(intent.getStringExtra("orgCode"));
		orgInfo.setOrgDesc(intent.getStringExtra("orgDesc"));
		orgInfo.setOrgName(intent.getStringExtra("orgName"));
		orgInfo.setOrgType(intent.getStringExtra("orgType"));
		orgInfo.setParentOrgCode(intent.getStringExtra("parentOrgCode"));
		orgInfo.setPopID(intent.getStringExtra("popId"));
		orgInfo.setPopName(intent.getStringExtra("popName"));
		
		GeneralQueryRequest queryRequest = new GeneralQueryRequest();
		String scId = XMLUtil.getInstance().getOnlineUserinfo().getScId();
		String orgCode = XMLUtil.getInstance().getOnlineUserinfo().getOrgCode();
        queryRequest.setLoginId(scId);
        if (null == orgCode)
        {
            queryRequest.setOrgCode("0");
        }
        else
        {
            queryRequest.setOrgCode(orgCode);
        }
        queryRequest.setFromIndex(FROM_INDEX);
        queryRequest.setToIndex(TO_INDEX);
		
	}
	
	
	private void initUI()
	{
		
		
		
	}
	
	/**
	 * 初始化进度对话框
	 */
	private void initProgressDialog()
	{
		pDialog = new ProgressDialog(this);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setMessage("获取组织设备列表...");
	}

	private void showProgressDialog()
	{
		if (pDialog != null && !pDialog.isShowing())
		{
			pDialog.show();
		}
	}

	private void closeProgressDialog()
	{
		if (pDialog != null && pDialog.isShowing())
		{
			pDialog.dismiss();
		}
	}
	
	
	/**
	 * 获取UI数据
	 * 
	 */
	private void getData()
	{
		
		getOrgData();

		getCameraData();

	}
	
	private void getCameraData()
	{}

	private void getOrgData()
	{}
	
	
}
