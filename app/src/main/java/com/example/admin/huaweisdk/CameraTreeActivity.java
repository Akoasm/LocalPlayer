package com.example.admin.huaweisdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.esdk.ivs.bean.CameraInfo;
import com.huawei.esdk.ivs.bean.DomainInfo;
import com.huawei.esdk.ivs.bean.OrgInfo;
import com.huawei.esdk.ivs.bean.ResponseInfo;
import com.huawei.esdk.ivs.bean.response.CameraQueryResponse;
import com.huawei.esdk.ivs.service.agent.NSSServiceAgent;

import java.util.ArrayList;
import java.util.List;

public class CameraTreeActivity extends Activity implements OnItemClickListener
{
	private static final String TAG = CameraTreeActivity.class.getSimpleName();

	private List<String> orgCodeList = new ArrayList<String>();
	
	private DomainInfo currentDomainInfo = null;

	private TextView tvHeaderTitle = null;
	
	private TextView tvHeaderBack = null;
	
	private View tvHeaderLine = null;

	// ��֯�б�
	private ListView lvOrgView = null;

	private List<OrgInfo> orgList = new ArrayList<OrgInfo>();

	// ������б�
	private ListView lvCamera = null;

	private List<CameraInfo> cameraList = new ArrayList<CameraInfo>();

	private ProgressDialog pDialog;

	private OrgAdapter orgAdapter = null;
	
	private CameraAdapter cameraAdapter = null;
	
	private boolean getOrgDatafinished = false;
	
	private boolean getCameraDatafinished = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_camera_tree);

		// ���ݵ�ǰ�������
		currentDomainInfo = IVSAppliacation.getInstance().getCurrentDomainInfo();
		Log.d(TAG, "currentDomainInfo->" + currentDomainInfo);
		
		orgCodeList.add("0");
		initUI();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "CameraTreeActivity onResume 1->");
		if(orgCodeList!=null&&!orgCodeList.isEmpty())
		{
			getData(orgCodeList.get(orgCodeList.size()-1));
		}
		Log.d(TAG, "CameraTreeActivity onResume 2->");
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		Log.d(TAG, "onPostCreate->");
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		Log.d(TAG, "onRestart->");
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
		Log.d(TAG, "onPostResume->");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause->");
		closeProgressDialog();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy->");
		closeProgressDialog();
	}

	@Override
	public void onBackPressed()
	{
		if(orgCodeList!=null && orgCodeList.size()>=2)
		{
			String orgCode = orgCodeList.get(orgCodeList.size()-1);
			orgCodeList.remove(orgCode);
			Log.e(TAG, "remove orgCode->"+orgCode);
			orgCode = orgCodeList.get(orgCodeList.size()-1);
			getData(orgCode);
		}
		else
		{
			super.onBackPressed();
		}
		
	}
	
	/**
	 * ��ʼ��UI����
	 */
	private void initUI()
	{
		Log.i(TAG, "initUI exec ");
		tvHeaderTitle = (TextView) findViewById(R.id.header_title);
		tvHeaderBack = (TextView) findViewById(R.id.header_back);
		tvHeaderLine = findViewById(R.id.cutline);
		lvOrgView = (ListView) findViewById(R.id.camera_org_list);
		lvCamera = (ListView) findViewById(R.id.camera_tree_list);
		tvHeaderTitle.setText(TAG);
		
		initProgressDialog();
	}

	/**
	 * ��ʾ��ȡ����֯����
	 * */
	private void displayOrgData()
	{
		Log.i(TAG, "displayOrgData exec ");
		//��֯������ʾ
		if(orgList==null || orgList.isEmpty())
		{
			Log.e(TAG, "orgList isEmpty ");
			orgList = new ArrayList<OrgInfo>();
			OrgInfo orgInfo = new OrgInfo();
			orgInfo.setOrgName("__NO_ORG__");
			orgList.add(orgInfo);
		}
		orgAdapter = new OrgAdapter();
		if(lvOrgView!=null)
		{
			lvOrgView.setAdapter(orgAdapter);
			lvOrgView.setOnItemClickListener(CameraTreeActivity.this);
			View header = LayoutInflater.from(CameraTreeActivity.this).inflate(R.layout.layout_header, null);
			if(lvOrgView.getHeaderViewsCount()==0)
			{
				TextView tvDesc = (TextView) header.findViewById(R.id.tvDesc);
				tvDesc.setText("��֯�б�");
				lvOrgView.addHeaderView(header);
			}
		}
		lvOrgView.setOnItemClickListener(this);
		orgAdapter.notifyDataSetChanged();
		setListViewHeightBasedOnChildren(lvOrgView);
	}
	
	/**
	 * ��ʾ��ȡ���豸����
	 * */
	private void displayCameraData()
	{
		Log.i(TAG, "displayCameraData exec ");
		//�豸������ʾ
		if(cameraList==null || cameraList.isEmpty())
		{
			cameraList = new ArrayList<CameraInfo>();
			CameraInfo cameraInfo = new CameraInfo();
			cameraInfo.setCameraName("__NO_CAMERA__");
			cameraList.add(cameraInfo);
		}
		cameraAdapter = new CameraAdapter();
		if(lvCamera!=null)
		{
			lvCamera.setAdapter(cameraAdapter);
			lvCamera.setOnItemClickListener(CameraTreeActivity.this);
			View headerCamera = LayoutInflater.from(CameraTreeActivity.this).inflate(R.layout.layout_header, null);
			if(lvCamera.getHeaderViewsCount()==0)
			{
				TextView tvDescCamera = (TextView) headerCamera.findViewById(R.id.tvDesc);
				tvDescCamera.setText("�豸�б�");
				lvCamera.addHeaderView(headerCamera);
			}
		}
		lvCamera.setOnItemClickListener(this);
		cameraAdapter.notifyDataSetChanged();
		setListViewHeightBasedOnChildren(lvCamera);
	}
	
	
	/**
	 * ��ʼ�����ȶԻ���
	 */
	private void initProgressDialog()
	{
		pDialog = new ProgressDialog(this);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setMessage("��ȡ��֯�豸�б�...");
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
	 * ��ȡUI����
	 * 
	 */
	private void getData(String orgCode)
	{
		Log.i(TAG, "getData exec ");
		Log.d(TAG, "orgCode->"+orgCode);
		if(!orgCodeList.contains(orgCode))
		{
			Log.d(TAG, "add orgCode->"+orgCode);
			orgCodeList.add(orgCode);
		}
		getOrgDatafinished = false;
		getCameraDatafinished = false;
		
		getOrgData(orgCode);
		getCameraData(orgCode);
	}

	/**
	 * ��ȡ��֯����
	 * */
	private void getOrgData(final String orgCode)
	{
		Log.i(TAG, "getOrgData exec ");
		new AsyncTask<Void, Integer, List<OrgInfo>>()
		{
			@Override
			protected void onPreExecute() 
			{
				showProgressDialog();
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected List<OrgInfo> doInBackground(Void... arg0)
			{
				List<OrgInfo> orgInfoResult = new ArrayList<OrgInfo>();
				
				if (currentDomainInfo != null)
				{
					String domainCode = currentDomainInfo.getDomainCode();
					Log.d(TAG, "domainCode->" + domainCode);
					// �Ƿ�Ϊ����0-��1-��
					String isExDomain = "0";
					ResponseInfo orgResponseInfo = null;
					// ��ȡ��֯�б�����
					try
					{
						/*
						 * ���ѡ��������½��豸��1
						 * ���ѡ������T28181,VCN3000-52,T28181-17,T2818184,HIK8_9,
						 * HIK106
						 */
						orgResponseInfo = (ResponseInfo) NSSServiceAgent.getInstance().getOrgTree(domainCode, orgCode,
								isExDomain);
						Log.d(TAG, "orgResponseInfo->"+orgResponseInfo);
						if (orgResponseInfo != null)
						{
							List<OrgInfo> orgListResponse = (List<OrgInfo>) orgResponseInfo.getMsgData();
							testOrgListData(orgListResponse);
							
							if (orgListResponse != null && !orgListResponse.isEmpty())
							{
								orgInfoResult.addAll(orgListResponse);
								Log.e(TAG, "orgInfoResult add");
							}
						}
						
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return null;
					}
				}

				Log.d(TAG, "++++++++++++++++ getOrgDatafinished");

				return orgInfoResult;
			}

			@Override
			protected void onPostExecute(List<OrgInfo> result)
			{
				super.onPostExecute(result);
				getOrgDatafinished = true;
				
				orgList.clear();
				
				if(result != null && !result.isEmpty())
				{
					orgList.addAll(result);
					Log.d(TAG, "orgList addAll ");
				}
				
				displayOrgData();
				Log.d(TAG, "++++++++++++++++ getOrgData  onPostExecute�� getOrgDatafinished:" + getOrgDatafinished + ", getCameraDatafinished:" + getCameraDatafinished);
				
				if(getOrgDatafinished && getCameraDatafinished)
				{
					closeProgressDialog();
				}
				return;
			}
			
		}.execute();
	}

	/**
	 * ��ȡ�豸����
	 * */
	private void getCameraData(final String orgCode)
	{
		Log.i(TAG, "getCameraData exec ");
		new AsyncTask<Void, Integer, List<CameraInfo>>()
		{
			@Override
			protected void onPreExecute() 
			{
				showProgressDialog();
			}

			@Override
			protected List<CameraInfo> doInBackground(Void... arg0)
			{
				
				List<CameraInfo> cameraResult = new ArrayList<CameraInfo>();
				
				if (currentDomainInfo != null)
				{
					String domainCode = currentDomainInfo.getDomainCode();
					Log.d(TAG, "domainCode->" + domainCode);


					ResponseInfo cameraResponseInfo = null;
					// ��ȡ���������
					try
					{
						
						cameraResponseInfo = (ResponseInfo) NSSServiceAgent.getInstance().getDeviceListInGroup(currentDomainInfo.getDomainCode(), orgCode, "1", "30");
						Log.d(TAG, "cameraResponseInfo->"+cameraResponseInfo);
						if (cameraResponseInfo != null)
						{
							CameraQueryResponse cameraQueryResponse = null;
							cameraQueryResponse = (CameraQueryResponse) cameraResponseInfo.getMsgData();
							Log.d(TAG, "cameraQueryResponse->" + cameraQueryResponse);
							if (cameraQueryResponse != null)
							{
								List<CameraInfo> cameraListResponse = cameraQueryResponse.getCameraInfos();
								testCameraData(cameraListResponse);

								Log.d(TAG, "cameraList->" + cameraListResponse);
								if (cameraListResponse != null && !cameraListResponse.isEmpty())
								{
									cameraResult.addAll(cameraListResponse);
								}
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return null;
					}
				}
				
				Log.d(TAG, "++++++++++++++++ getCameraDatafinished");

				return cameraResult;
			}

			@Override
			protected void onPostExecute(List<CameraInfo> result)
			{
				super.onPostExecute(result);
				getCameraDatafinished = true;
				
				cameraList.clear();
				
				if(result != null && !result.isEmpty())
				{
					cameraList.addAll(result);
				}
				
				displayCameraData();
				Log.d(TAG, "++++++++++++++++ getCameraData  onPostExecute�� getOrgDatafinished:" + getOrgDatafinished + ", getCameraDatafinished:" + getCameraDatafinished);
				
				if(getOrgDatafinished && getCameraDatafinished)
				{
					closeProgressDialog();
				}
				
				return;
			}

			

		}.execute();
	}


	private void setListViewHeightBasedOnChildren(ListView listView)
	{
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
		{
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * ������֯��������
	 * 
	 */
	private void testOrgListData(List<OrgInfo> orgList)
	{
		Log.i(TAG, "testOrgListData exec ");
		if (orgList != null && !orgList.isEmpty())
		{
			Log.d(TAG, "++++++++++++++++ testOrgListData +++++++++++++++");
			for (OrgInfo orgInfo : orgList)
			{
				Log.e(TAG, orgInfo.getDomainName() + "->" + orgInfo.getOrgName());
			}
			Log.d(TAG, "++++++++++++++++ testOrgListData +++++++++++++++");
		}
	}

	/**
	 * ���Ի�ȡ������������豸�б�����
	 * 
	 */
	private void testCameraData(List<CameraInfo> cameraList)
	{
		Log.i(TAG, "testCameraData exec ");
		if (cameraList != null && !cameraList.isEmpty())
		{
			Log.d(TAG, "+++++++++++++ testCameraData +++++++++++++");
			for (CameraInfo cameraInfo : cameraList)
			{
				Log.e(TAG, cameraInfo.getCameraType() + "<>" + cameraInfo.getCameraName() + "<>"
						+ cameraInfo.getCameraCode() + "<>" + cameraInfo.getIsOnline());
			}
			Log.d(TAG, "+++++++++++++ testCameraData +++++++++++++");
		}
	}

	/**
	 * ����Ҫ���ŵ����������Ϣ
	 * */
	private void testCameraInfo(CameraInfo cameraInfo)
	{
		Log.i(TAG, "testCameraInfo exec ");
		if(cameraInfo==null)
		{
			Log.e(TAG, "cameraInfo NULL");
			return;
		}
		String status = cameraInfo.getIsOnline();
		String type = cameraInfo.getCameraType();
		String code = cameraInfo.getCameraCode();
        String name = cameraInfo.getCameraName();
        String nvrCode = cameraInfo.getNVRCode();
        String domainCode = cameraInfo.getDomainCode();
        String isOutScc = cameraInfo.getIsOutScc();
        StringBuffer buffer = new StringBuffer();
        buffer.append("status ").append(status).append("\n");
        buffer.append("type ").append(type).append("\n");
        buffer.append("code ").append(code).append("\n");
        buffer.append("name ").append(name).append("\n");
        buffer.append("nvrCode ").append(nvrCode).append("\n");
        buffer.append("domainCode ").append(domainCode).append("\n");
        buffer.append("isOutScc ").append(isOutScc).append("\n");
        Log.d(TAG, "++++++++++++++++++ cameraInfo +++++++++++++++++");
        Log.e(TAG, buffer.toString());
        Log.d(TAG, "++++++++++++++++++ cameraInfo +++++++++++++++++");
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Log.d(TAG, "AdapterView->" + parent + " position->" + position);

		position = position -1;
		if(parent == lvOrgView)
		{
			Log.d(TAG, "onItemClick lvOrgView ");
			OrgInfo orgInfo = orgList.get(position);
			String orgName = orgInfo.getOrgName();
			String orgCode = orgInfo.getOrgCode();
			if("__NO_ORG__".equals(orgName))
			{
				Toast.makeText(CameraTreeActivity.this, "no org exist", Toast.LENGTH_LONG).show();
				return;
			}
			getData(orgCode);
		}
		
		if(parent == lvCamera)
		{
			Log.d(TAG, "onItemClick lvCamera ");
			CameraInfo cameraInfo = cameraList.get(position);
			String status = cameraInfo.getIsOnline();
			String type = cameraInfo.getCameraType();
			String code = cameraInfo.getCameraCode();
            String name = cameraInfo.getCameraName();
            String nvrCode = cameraInfo.getNVRCode();
            String domainCode = cameraInfo.getDomainCode();
            String isOutScc = cameraInfo.getIsOutScc();
            testCameraInfo(cameraInfo);
            if("__NO_CAMERA__".equals(name))
            {
            	Toast.makeText(CameraTreeActivity.this, "no camera exist", Toast.LENGTH_LONG).show();
            	return;
            }
            if(status.equals("OFF"))
            {
            	Toast.makeText(CameraTreeActivity.this, name+" is offline", Toast.LENGTH_LONG).show();
            }
            else
            {
            	//��ת�����Բ�����Ƶ������
            	Intent intent = new Intent();
            	intent.setClass(CameraTreeActivity.this, CameraPlayerActivity.class);
            	intent.putExtra("ptzType", type);
                intent.putExtra("cameraId", code);
                intent.putExtra("cameraName", name);
                intent.putExtra("status", status);
                intent.putExtra("nvrCode", nvrCode);
                intent.putExtra("domainCode", domainCode);
            	//�������������
                intent.putExtra("isOutScc", isOutScc);
                startActivity(intent);
            }
		}
	}

	/**
	 * ��֯�б��������
	 */
	private class OrgAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return orgList != null ? orgList.size() : 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				LayoutInflater inflater = LayoutInflater.from(CameraTreeActivity.this);
				convertView = inflater.inflate(R.layout.layout_item, null);
			}
			ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			ivPhoto.setImageResource(R.drawable.small_organise);
			if (position >= 0 && position < orgList.size())
			{
				OrgInfo orgInfo = orgList.get(position);
				tvName.setText(orgInfo.getOrgName());
			}
			return convertView;
		}
	}

	/**
	 * ������б�������
	 * 
	 */
	private class CameraAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return cameraList != null ? cameraList.size() : 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				LayoutInflater inflater = LayoutInflater.from(CameraTreeActivity.this);
				convertView = inflater.inflate(R.layout.layout_item, null);
			}
			ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			CameraInfo cameraInfo = cameraList.get(position);
			String cameraType = cameraInfo.getCameraType();
			String cameraName = cameraInfo.getCameraName();
			String cameraStatus = cameraInfo.getIsOnline();
			if("ON".equals(cameraStatus))
			{
				if("BULLET".equals(cameraType))
				{
					ivPhoto.setImageResource(R.drawable.ptz_bolt_online);
				}
				else if("DOME".equals(cameraType))
				{
					ivPhoto.setImageResource(R.drawable.ball_machine_online);
				}
				else if("FIX".equals(cameraType))
				{
					ivPhoto.setImageResource(R.drawable.fixe_bolt_online);
				}
			}
			else
			{
				if("BULLET".equals(cameraType))
				{
					ivPhoto.setImageResource(R.drawable.ptz_bolt_offline);
				}
				else if("DOME".equals(cameraType))
				{
					ivPhoto.setImageResource(R.drawable.ball_machine_offline);
				}
				else if("FIX".equals(cameraType))
				{
					ivPhoto.setImageResource(R.drawable.fixe_bolt_offline);
				}
			}
			tvName.setText(cameraName);
			return convertView;
		}

	}

}
