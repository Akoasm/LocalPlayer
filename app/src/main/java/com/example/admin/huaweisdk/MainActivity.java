package com.example.admin.huaweisdk;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.huawei.ca.eivs.util.XMLUtil;
import com.huawei.esdk.ivs.bean.DomainInfo;
import com.huawei.esdk.ivs.bean.ResponseInfo;
import com.huawei.esdk.ivs.service.agent.NSSServiceAgent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity implements OnItemClickListener
{
	private static final String TAG = MainActivity.class.getSimpleName();

	private List<DomainInfo> domainList = new ArrayList<DomainInfo>();
	private List<String> domainNameList = new ArrayList<String>();
	private ArrayAdapter<String> adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_main);

		Toast.makeText(this, TAG, Toast.LENGTH_SHORT).show();
		
		initUI();

		initData();
		Log.i("msg", "zoumei");
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		initUI();
	}
	
	private void initData()
	{
		AsyncTask<Void, Integer, Boolean> task = new AsyncTask<Void, Integer, Boolean>() {

			@SuppressWarnings("unchecked")
			@Override
			protected Boolean doInBackground(Void... paramVarArgs)
			{
				domainNameList.clear();
				
				try
				{
					ResponseInfo domainResponseInfo = (ResponseInfo) NSSServiceAgent.getInstance().getDomainRoute();
					
					if (domainResponseInfo != null)
					{
						domainList = (List<DomainInfo>) domainResponseInfo.getMsgData();
					}
					
					if (domainList != null && !domainList.isEmpty())
					{
						for(DomainInfo domainInfo: domainList)
						{
							domainNameList.add(domainInfo.getDomainName());
						}
						
					}
					// 检查数据

				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result)
			{
				if(adapter != null)
				{
					adapter.notifyDataSetChanged();
				}
			}
		};

		task.execute();

		
	}

	/**
	 * 初始化界面,首先要获取到所有的域列表
	 */
	private void initUI()
	{
		Log.i(TAG, "initUI exec ");

		Log.d(TAG, "domainNameList->"+domainNameList);
		adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
				domainNameList);
		ListView lv = getListView();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		adapter.notifyDataSetChanged();
	}

	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Log.d(TAG, "onItemClick position->" + position);
		if (position >= 0 && position < domainNameList.size())
		{
			IVSAppliacation.getInstance().setCurrentDomainInfo(domainList.get(position));
			XMLUtil.getInstance().getOnlineUserinfo().setDomainCode(
					domainList.get(position).getDomainCode());
			
			// 跳转到下页
			Intent intent = new Intent(MainActivity.this, CameraTreeActivity.class);

			startActivity(intent);
		}
	}

}
