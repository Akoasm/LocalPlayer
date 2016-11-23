package com.example.admin.huaweisdk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huawei.esdk.ivs.bean.ResponseInfo;
import com.huawei.esdk.ivs.service.agent.NSSServiceAgent;

public class LoginActivity extends BaseActivity implements OnClickListener
{
	private static final String TAG = LoginActivity.class.getSimpleName();
	
	public static boolean isPassInternet = false;
	
	private static int rootBottom = Integer.MIN_VALUE;
	
	private RelativeLayout				rootContainer;
	private LinearLayout				loginContainer;
	private LinearLayout				checkContainer;
	private EditText					etServerIp;
	private EditText					etServerPort;
	private EditText					etUsername;
	private EditText					etPassword;
	private Button						btnLogin;
	private CheckBox					keepPassword;
	private CheckBox					passInternet;
	private String						serverIp;
	private int							serverPort;
	private String						username;
	private String						password;
	private ProgressDialog				pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		loginContainer.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
		
		int offlineType = getIntent().getIntExtra("offline", -1);
        if (-1 != offlineType)
        {
            switch (offlineType)
            {
//                	//0���˺ű�����Ա����
//                case 0:
//                    Toast.makeText(this, R.string.offline_type_zero, 500).show();
//                    //1���˺ű�����Աɾ��
//                case 1:
//                    Toast.makeText(this, R.string.offline_type_one, 500).show();
//                    //2����¼�Ự������Աǩ��
//                case 2:
//                    Toast.makeText(this, R.string.offline_type_two, 500).show();
            }
        }
        
        prepareAccount();
       
	}
	
	
	
	/**
	 * ��ʼ���˻���Ϣ,Demo������ɺ�ɾ��
	 * */
	private void prepareAccount()
	{
		if(etServerIp!=null)
		{
			etServerIp.setText("10.89.12.250");
		}
		if(etServerPort!=null)
		{
			etServerPort.setText("9900");
		}
		if(etUsername!=null)
		{
			etUsername.setText("Admin1");
		}
		if(etPassword!=null)
		{
			etPassword.setText("huawei-123");
		}
		if(keepPassword!=null)
		{
			keepPassword.setChecked(true);
		}
	}
	
	@Override
	public void initView() 
	{
		super.setContentView(R.layout.activity_login);
		this.rootContainer = (RelativeLayout) findViewById(R.id.rootContainer);
		this.loginContainer = (LinearLayout) findViewById(R.id.loginContainer);
		this.checkContainer = (LinearLayout) findViewById(R.id.checkContainer);
		
		this.etServerIp = (EditText) findViewById(R.id.et_serverip);
		this.etServerPort = (EditText) findViewById(R.id.et_servport);
		this.etUsername = (EditText) findViewById(R.id.et_username);
		this.etPassword = (EditText) findViewById(R.id.et_password);
		
		this.keepPassword = (CheckBox) findViewById(R.id.keepPassWord);
		this.passInternet = (CheckBox) findViewById(R.id.passInternet);
		this.btnLogin = (Button) findViewById(R.id.btnLogin);
		
		System.out.println("LoginActivity.initView().rootContainer->"+rootContainer);
		System.out.println("LoginActivity.initView().checkContainer->"+checkContainer);
		System.out.println("LoginActivity.initView().passInternet->"+passInternet);
	}

	@Override
	public void initData()
	{
		Watcher watcher = null;
		watcher = new Watcher(etServerIp);
		etServerIp.addTextChangedListener(watcher);
		watcher = new Watcher(etServerPort);
		etServerPort.addTextChangedListener(watcher);
		watcher = new Watcher(etUsername);
		etUsername.addTextChangedListener(watcher);
		watcher = new Watcher(etPassword);
		etPassword.addTextChangedListener(watcher);
		
		TouchListener touchListener = new TouchListener();
		etServerIp.setOnTouchListener(touchListener);
		etServerPort.setOnTouchListener(touchListener);
		etUsername.setOnTouchListener(touchListener);
		etPassword.setOnTouchListener(touchListener);
		btnLogin.setOnClickListener(this);
	}

	@Override
	public void initOver()
	{
		initProgressDialog();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		btnLogin.setEnabled(true);
		closeProgressDialog();
	}
	
	/**
	 * ��ʼ�����ȶԻ���
	 * */
	private void initProgressDialog()
	{
		pDialog = new ProgressDialog(this);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setMessage("���ڵ�¼...");
	}
	
	private void showProgressDialog()
	{
		if(pDialog!=null)
		{
			if(!pDialog.isShowing())
			{
				pDialog.show();
			}
		}
	}
	
	private void closeProgressDialog()
	{
		if(pDialog!=null)
		{
			if(pDialog.isShowing())
			{
				pDialog.dismiss();
			}
		}
	}
	
	
	@Override
	public void onClick(View view) 
	{
		if(view.getId()==R.id.btnLogin)
		{
			btnLogin.setEnabled(false);	//�����ڵ�¼�������ٴε���
			login();
		}
	}
	
	/**
	 * ��������ռ䲢��½
	 * */
	private void login()
	{
		if(checkNameSpace() == 0)
		{
			if(NetworkUtil.isNetworkActive(this))		//��������
			{
				loginIVS();
			}
		}
	}
	
	/**
	 * ��������Ƿ���ȷ
	 * */
	private int checkNameSpace()
	{
		serverIp = etServerIp.getEditableText().toString().trim();
		String port = etServerPort.getEditableText().toString().trim();
		if(port!=null && !"".equals(port))
			serverPort = Integer.valueOf(port);
		else 
			serverPort = -1;
		username = etUsername.getEditableText().toString().trim();
		password = etPassword.getEditableText().toString().trim();
		if(serverIp==null||"".equals(serverIp))
		{
			Toast.makeText(this, "serverIp invalid", Toast.LENGTH_LONG).show();
			return -1;
		}
		if(serverPort < 0)
		{
			Toast.makeText(this, "serverPort invalid", Toast.LENGTH_LONG).show();
			return -1;
		}
		if(username==null||"".equals(username))
		{
			Toast.makeText(this, "username invalid", Toast.LENGTH_LONG).show();
			return -1;
		}
		if(password==null||"".equals(password))
		{
			Toast.makeText(this, "password invalid", Toast.LENGTH_LONG).show();
			return -1;
		}
		return 0;
	}
	
	/**
	 * ��¼IVS
	 * */
	private void loginIVS()
	{
		Log.i(TAG, "loginIVS exec ");
		new AsyncTask<Void, Integer, Boolean>()
		{
			protected void onPreExecute() 
			{
				Log.i(TAG, "onPreExecute exec ");
				showProgressDialog();
				btnLogin.setEnabled(false);
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			protected Boolean doInBackground(Void... arg0) 
			{
				Log.i(TAG, "doInBackground exec ");
				NSSServiceAgent nssServiceAgent = NSSServiceAgent.getInstance();
				ResponseInfo loginResponseInfo = null;
				int responseCode = 0;
				
				try 
				{
					loginResponseInfo = (ResponseInfo) nssServiceAgent.login(serverIp, serverPort, username, password);
					if(loginResponseInfo!=null)
					{
						responseCode = loginResponseInfo.getRspCode();
						Log.d(TAG, "responseCode->"+responseCode);
						
						if(responseCode == 0)
						{
							Log.d(TAG, "login success ");
							
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
						}
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return true;
			}
			
			protected void onPostExecute(Boolean result) 
			{
				Log.i(TAG, "onPostExecute exec ");
				closeProgressDialog();
				btnLogin.setEnabled(true);
			}
			
		}.execute();
	}
	
	class Watcher implements TextWatcher
	{
		View view;
		
		public Watcher(View view)
		{
			this.view = view;
		}
		
		@Override
		public void afterTextChanged(Editable arg0) 
		{
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) 
		{
			
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) 
		{
			if(!(view instanceof EditText))
			{
				return;
			}
//			if(s.length()>0)
//            {
//				((EditText)view).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_edittext_clear, 0);
//            }else{
//            	((EditText)view).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
//            }
		}
	}
	
	class TouchListener implements OnTouchListener
	{
		int etDrawableMargin = 40;
		
		@Override
		public boolean onTouch(View view, MotionEvent event) 
		{
			EditText temp = null;
			if(view instanceof EditText)
				temp = (EditText) view;
			if(event.getAction()==MotionEvent.ACTION_UP)
            {
                Drawable[] compounDrawables = temp.getCompoundDrawables();
                if(compounDrawables!=null)
                {
                    Drawable imageClear = compounDrawables[2];
                    if(imageClear!=null)
                    {
                        int leftMargin = temp.getRight()-temp.getPaddingRight()
                                -imageClear.getBounds().width()-etDrawableMargin;
                        if(event.getRawX()>=leftMargin)
                        {
                            if(temp!=null&&temp.getEditableText()!=null)
                            {
                                temp.getEditableText().clear();
                            }
                        }
                    }
                }
            }
			return false;
		}
		
	}
	
	private OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() 
		{
			Rect rect = new Rect();
			loginContainer.getGlobalVisibleRect(rect);
			if(rootBottom==Integer.MIN_VALUE)
			{
				rootBottom = rect.bottom;
				return;
			}
			// adjustResize������̵�����߶Ȼ��С
			if(rect.bottom<rootBottom)
			{
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) loginContainer.getLayoutParams();
				if(lp.getRules()[RelativeLayout.CENTER_IN_PARENT]!=0)
				{
					lp.addRule(RelativeLayout.CENTER_IN_PARENT, 0);	//ȡ������
					lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					lp.bottomMargin = 5;
					loginContainer.setLayoutParams(lp);
				}
			}
			else
			{
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) loginContainer.getLayoutParams();
				if(lp.getRules()[RelativeLayout.CENTER_IN_PARENT]==0)	//���û�о��и�����˵�������������
				{
					lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);	//ȡ���ӵ�
					lp.addRule(RelativeLayout.CENTER_IN_PARENT);
					loginContainer.setLayoutParams(lp);
				}
			}
		}
	};


	
}
