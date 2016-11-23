package com.example.admin.huaweisdk;

/*
 * �����������Ƶ���Ž���
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.esdk.ivs.IVSService;
import com.huawei.esdk.ivs.bean.MediaRsp;
import com.huawei.esdk.ivs.bean.PTZControl;
import com.huawei.esdk.ivs.bean.ResponseInfo;
import com.huawei.esdk.ivs.bean.TypeCameraListInf;
import com.huawei.esdk.ivs.media.IVSPlayer;
import com.huawei.esdk.ivs.media.IVSPlayer.OnBeginListener;
import com.huawei.esdk.ivs.media.IVSPlayer.OnCompletionListener;
import com.huawei.esdk.ivs.media.IVSPlayer.OnErrorListener;
import com.huawei.esdk.ivs.media.IVSPlayer.OnVideoSizeChangedListener;
import com.huawei.esdk.ivs.service.agent.MinaClient;
import com.huawei.esdk.ivs.util.Base64Encrypt;
import com.huawei.esdk.ivs.util.SysUtil;
import com.huawei.esdk.ivs.util.constants.ErrorCode;
import com.huawei.esdk.ivs.util.constants.ResultCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CameraPlayerActivity extends Activity
		implements OnGestureListener, OnTouchListener, OnClickListener, OnSeekBarChangeListener
{
	private static final String TAG = CameraPlayerActivity.class.getSimpleName();

	public static final String FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			.getAbsolutePath() + "/";

	public static String currPath = null;

	private static final String DOMECAMERA = "DOME";

	private static final String PTZ = "PTZ";

	private static final int SUCCESS = 0;

	private static int INDEX = 2;

	// �����豸������
	private static final int DOMAIN_EXTERN_PTZ_UNUSED = 115020;

	// ���������豸������
	private static final int LOCK_DOMAIN_EXTERN_PTZ_UNUSED = 112066;

	// ����֧����̨��������
	private static final int PTZ_LOCKE_NOT_SUPPORT = 129116008;

	// ��̨������
	private static final int PTZ_IS_LOCKED = 129116001;

	// ��̨��ͬ���ȼ����߸����ȼ��û�����
	private static final int PTZ_CONTROLED_BY_OTHER_USER = 129116002;

	// ��̨��ͬ���ȼ����߸����ȼ��û�����
	private static final int PTZ_LOCKED_BY_OTHER_USER = 129116003;

	// ��̨δ������������ʧ��
	private static final int PTZ_DO_NOT_LOCKED = 129116005;

	// ��̨���澯�����У����������
	private static final int PTZ_LOCKED_BY_ALARM = 129116004;

	// private static final int PTZ_UNRECOGNIZED_MSG_TYPE =
	// 115006;//����ʶ�����̨������Ϣ����

	// private static final int PTZ_DEV_OFFLINE = 115011;//��̨�豸������

	// �˵�ID;�Ƿ���ʾ�ƾ�������
	private static final int ISSHOWTOOLS = 1;

	// �Ƿ���ʾץ�İ�ť
	private static final int ISSHOWSNAP = 2;

	// �ղغ�ȡ���ղ�
	private static final int COLLECTION = 3;

	private static final int SWITCHTOBIGSCREEN_DEMO_MENU = 4;

	private static final int HELP_MENU = 5;

	private static final int BACK_MENU = 6;

	private static final double XY_OFFSET = 22.5;

	// ����������������
	private static final int REPLAY_MAX_TIME = 3;

	// ��ȡ��ǰ������״̬,���ݵ�ǰ������״̬�ڵ������ʱ������ͬ��ͼƬ;
	Configuration newConfig = null;

	// ��ĻĬ�Ͽ��
	private int defaultDisplayWidth = 0;

	// ��ĻĬ�ϸ߶�
	private int defaultDisplayHeight = 0;

	// �Ƿ�Ϊ��һ��
	private int firstUsed = 1;

	// PTZ���Ʋ���
	private String param1 = "";

	private String param2 = "";

	// �����Ƿ�ȫ�����λ
	private boolean isFullScreen = true;

	private LinearLayout realTimePlay = null;

	// private TextView customTitleTV = null;

	private LinearLayout ptzControlWindowLL = null;

	// private LinearLayout ptzControlWindowLLRight = null;

	// ��Ȧ��С
	private ImageView apertureReduce = null;

	// ��Ȧ�Ŵ�
	private ImageView apertureBlowUp = null;

	// �۽�Զ
	private ImageView focusFarOff = null;

	// �۽���
	private ImageView focusNear = null;

	// ��Χ��������С
	private ImageView scopeLargen = null;

	// ��Χ��С��������
	private ImageView scopeReduce = null;

	// ץ��
	private ImageView snap = null;

	// �ƾ����Ƽ���
	private ImageView lock = null;

	// �л�����
	private ImageView switchScale = null;

	private TextView textCollection = null;

	private TextView textMainStream = null;

	private TextView textSubStream = null;

	private TextView textMtuStream = null;

	// ��̨����
	private ImageView ptzSetting = null;

	// ת��
	private ImageView streamSetting = null;

	// ControlCenter��������
	// private MGRControlCenter mcc = MGRControlCenter.getInstance();

	// �����ȡ�û�ID;
	private String userIdTemp = "";

	// �����ȡcameraName;
	private String cameraName = "";

	// �Ƿ�Ϊ�����豸 0�������豸 1�������豸

	private String isOutScc = null;

	// �����id
	private String cameraId = "";

	// ptz ����
	private String ptzType = "";

	private String nvrCode = "";

	// private MenuItem fullScreen; // ��¼ȫ���˵�itemֵ
	// ��ȡͼƬ����·��;
	private String picPath;

	// ������ý���url
	private String url = "";

	private IVSPlayer mmPlayer;

	@SuppressWarnings("deprecation")
	private GestureDetector detector = new GestureDetector(this);

	private ProgressBar progressBar;

	private Handler playHandler;

	private boolean isFixCamera = false;

	// ���ڽ�����̳���ʱ���������ƾ��������������
	private boolean keyUpDownMatch = true;

	private boolean onTouchDownUpMatch = true;

	private ImageView imageName = null;

	private TextView cameraTitleName = null;

	// ���������
	private TypeCameraListInf tcli;

	// SharedPreferences�������ڻ�ȡ�������������
	private SharedPreferences setting;

	// �Ƿ���ʾ�ƾ������������������н�������
	private boolean isShowPtzTool = false;

	// �Ƿ���ʾץ��
	private boolean isShowSnap = true;

	private SharedPreferences.Editor editor = null;

	private AlertDialog alertDialog = null;

	// �Ƿ��ղ�
	private boolean isCollection = true;

	// ��ͼ���״̬
	private Boolean isLocked = false;

	// �Ƿ���ʾ��̨����
	private boolean isShowPtzSetting = false;

	// �Ƿ���ʾת������
	private boolean isShowStreamSetting = false;

	// ��̨���ù�����
	private LinearLayout ptzSettingTools = null;

	// ת�����ù�����
	private LinearLayout streamSetTools = null;

	private LinearLayout ptzStepLen = null;

	// ץ�ĵ���ʾ������
	private LinearLayout snapShowHiden = null;

	// ����
	private SeekBar stepSeekBar = null;

	private TextView seekbarValue = null;

	private TextView textShowHidenTools = null;

	// ��̨��ʾ������
	private LinearLayout ptzToolsShowOrHiden = null;

	private LinearLayout mainStreamLayout = null;

	private LinearLayout subStreamLayout = null;

	private LinearLayout mtuStreamLayout = null;

	private TextView textShowHidenSnap = null;

	// �ղز���
	private LinearLayout addOrdeletCollection = null;

	// ��������
	private int connectCount = 0;

	private String domainCode = null;

	private boolean hasMTU = true;

	private boolean showSet = false;

	private int timerDelay = 3000;

	private Timer mTimer = null;

	private TimerTask mTimerTask = null;

	private Handler timerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			hidePTZTool();
		}
	};

	private void startTimer()
	{
		Log.d(TAG, "startTimer start ");
		if (mTimer == null)
		{
			mTimer = new Timer();
		}

		if (mTimerTask == null)
		{
			mTimerTask = new TimerTask() {
				@Override
				public void run()
				{
					timerHandler.sendEmptyMessage(0);
				}
			};
		}

		if (mTimer != null && mTimerTask != null)
			mTimer.schedule(mTimerTask, timerDelay);
		Log.d(TAG, "startTimer end ");
	}

	private void stopTimer()
	{
		Log.d(TAG, "stopTimer start ");
		if (mTimer != null)
		{
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null)
		{
			mTimerTask.cancel();
			mTimerTask = null;
		}
		Log.d(TAG, "stopTimer end ");
	}

	/**
	 * ע������
	 */
	private OnTouchListener surfaceViewTouchListener = new SurfaceViewTouchListener();

	/**
	 * ע������
	 */
	private OnErrorListener onErrorListener = new OnErrorListener() {
		@Override
		public boolean onError(IVSPlayer mp, int what)
		{
			Message msg = Message.obtain();
			msg.what = what;
			playHandler.sendMessage(msg);
			return false;
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg)
		{
			Log.i(TAG, "handler handleMessage ");
			handleServiceResult(msg.what, msg.obj);
		}

	};

	private Handler lockHandler = new Handler() {

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// ��̨��ͬ���ȼ����߸����ȼ��û�����
				case PTZ_CONTROLED_BY_OTHER_USER:
					Toast.makeText(CameraPlayerActivity.this, R.string.higth_level_user_control, Toast.LENGTH_SHORT)
							.show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
					isLocked = false;
					break;
				// ��̨������
				case PTZ_IS_LOCKED:
					Toast.makeText(CameraPlayerActivity.this, R.string.ptz_lock, Toast.LENGTH_SHORT).show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
					isLocked = true;
					break;
				// ��̨��ͬ���ȼ����߸����ȼ��û�����
				case PTZ_LOCKED_BY_OTHER_USER:
					Toast.makeText(CameraPlayerActivity.this, R.string.higth_level_user_lock, Toast.LENGTH_SHORT)
							.show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
					isLocked = true;
					break;
				// ��̨δ������������ʧ��
				case PTZ_DO_NOT_LOCKED:
					Toast.makeText(CameraPlayerActivity.this, R.string.ptz_unlock_faile, Toast.LENGTH_SHORT).show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
					isLocked = false;
					break;
				// ����֧����̨��������
				case PTZ_LOCKE_NOT_SUPPORT:
					Toast.makeText(CameraPlayerActivity.this, R.string.ptz_not_support, Toast.LENGTH_SHORT).show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
					isLocked = false;
					break;
				case LOCK_DOMAIN_EXTERN_PTZ_UNUSED:
					Toast.makeText(CameraPlayerActivity.this, R.string.DOMAIN_EXTERN_PTZ_UNUSED, Toast.LENGTH_SHORT)
							.show();
					break;
				case SUCCESS:
					isLocked = true;
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
					break;

				default:
					break;
			}
		}

	};

	private Handler unLockHandler = new Handler() {

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// ��̨��ͬ���ȼ����߸����ȼ��û�����
				case PTZ_LOCKED_BY_OTHER_USER:
					Toast.makeText(CameraPlayerActivity.this, R.string.higth_level_user_lock, Toast.LENGTH_SHORT)
							.show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
					isLocked = true;
					break;
				// ��̨��ͬ���ȼ����߸����ȼ��û�����
				case PTZ_CONTROLED_BY_OTHER_USER:
					Toast.makeText(CameraPlayerActivity.this, R.string.higth_level_user_control, Toast.LENGTH_SHORT)
							.show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
					isLocked = false;
					break;
				// ��̨������
				case PTZ_IS_LOCKED:
					Toast.makeText(CameraPlayerActivity.this, R.string.ptz_lock, Toast.LENGTH_SHORT).show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
					isLocked = true;
					break;
				// ��̨δ������������ʧ��
				case PTZ_DO_NOT_LOCKED:
					Toast.makeText(CameraPlayerActivity.this, R.string.ptz_unlock_faile, Toast.LENGTH_SHORT).show();
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
					isLocked = false;
					break;
				case LOCK_DOMAIN_EXTERN_PTZ_UNUSED:
					Toast.makeText(CameraPlayerActivity.this, R.string.DOMAIN_EXTERN_PTZ_UNUSED, Toast.LENGTH_SHORT)
							.show();
					break;
				case SUCCESS:
					isLocked = false;
					lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
					break;
				default:
					break;
			}
		}

	};

	private OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(IVSPlayer mp)
		{
			Message msg = Message.obtain();
			msg.what = IVSPlayer.PlayerEvent.MEDIA_PLAYER_END;
			playHandler.sendMessage(msg);
		}
	};

	private OnBeginListener onBeginListener = new OnBeginListener() {
		@Override
		public void onBegin(IVSPlayer mp)
		{
			Message msg = Message.obtain();
			msg.what = IVSPlayer.PlayerEvent.MEDIA_PLAYER_BEGIN;
			playHandler.sendMessage(msg);
		}
	};

	private OnVideoSizeChangedListener myonVideoSizeChangedListener = new OnVideoSizeChangedListener() {
		@Override
		public void onVideoSizeChangedListener(IVSPlayer mp, int width, int height)
		{
			Message msg = new Message();
			msg.what = IVSPlayer.PlayerEvent.MEDIA_PLAYER_SIZE_CHANGED;
			playHandler.sendMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// ���ñ�ҳ���ʼ��ʱ�ޱ��⣬��������xml������;
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.realtime_scan);
		// ActivityService.getInstance().addActivity(this);
		setting = PreferenceManager.getDefaultSharedPreferences(this);
		// ����һ���ƾ����Ƽ�����Ӧ��Ϣ����
		// ptzQueryRsp = new PtzQueryRsp();
		setServiceParam();
		
		init();
		initPTZControl();
		playVideo();

		hidePTZTool();

		stopTimer();
		startTimer();

		Log.d(TAG, "FILE->" + FILE);
		Log.e(TAG, "domainCode->" + MinaClient.getInstance().getDomainCode());
		Log.d(TAG, "onCreate end ");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		INDEX = 2;
	}

	@Override
	protected void onRestart()
	{
		progressBar.setVisibility(View.VISIBLE);

		if (mmPlayer != null)
		{
			mmPlayer.setVisibility(View.GONE);
			realTimePlay.removeAllViews();
			mmPlayer.stop();
			mmPlayer = null;
		}
		playVideo();
		super.onRestart();
	}

	private void stopPlayer()
	{
		if (mmPlayer != null)
		{
			mmPlayer.onPause();
			mmPlayer.setVisibility(View.GONE);
			realTimePlay.removeAllViews();
			mmPlayer.stop();
			mmPlayer = null;
		}
	}

	/**
	 * ʵʱ�����ѡ��������
	 */
	private void playVideo()
	{
		Log.i(TAG, "playVideo start ");

		Thread getURLThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					Log.d(TAG, "getVideoURL ");
					ResponseInfo urlResponse = IVSService.getVideoURL(domainCode, cameraId, "2", false);
					if (urlResponse == null)
					{
						return;
					}
					int errorCode = urlResponse.getRspCode();
					Log.d(TAG, "playVideo getVideoURL errorCode->" + errorCode);

					String str = "";

					if ((SUCCESS != errorCode) && (connectCount < REPLAY_MAX_TIME))
					{
						new RePlayThread().start();
						connectCount++;
					}
					else
					{
						connectCount = 0;
						switch (errorCode)
						{
							case SUCCESS:
								MediaRsp mediaRsp = (MediaRsp) (urlResponse.getMsgData());

								if (mediaRsp.getMtuRtspIP() == null || mediaRsp.getMtuRtspIP().equals("")
										|| mediaRsp.getMtuRtspPort() == null || mediaRsp.getMtuRtspPort().equals(""))
								{
									hasMTU = false;
									mtuStreamLayout.setEnabled(false);
									textMtuStream.setTextColor(getResources().getColor(R.color.streamDisabled));
								}

								if (showSet)
								{

									url = "rtsp://" + mediaRsp.getMtuRtspIP() + ":" + mediaRsp.getMtuRtspPort()
											+ "/livex?l=rtsp&i=base64:" + Base64Encrypt.encodeBase64(mediaRsp.getUrl())
											+ "&streamtype=2?tcp";
									Log.i("msg","��ַ"+url);
								}
								else
								{
									url = mediaRsp.getUrl() + "?tcp";
								}

								System.out.println("playVideo -- URL  :  " + url);

								playVideo(url);
								break;
							case ErrorCode.CONN_TIME_OUT:
							case ErrorCode.OTHER_EXCEPTION:
							case ErrorCode.NETWORK_EXCEPTION: // �����޷�����
								str = getResources().getString(R.string.network_unreachbale);
								showErrorDialog(str);
								break;
							case ErrorCode.USER_DEACTIVATED:
							case ErrorCode.USER_NOT_EXIST:
							case ErrorCode.USER_NAME_OR_PWD_ERROR:
								// sysUtil = new
								// SysUtil(UIRealtimePlayActivity.this);
								// str =
								// getBaseContext().getResources().getString(R.string.user_pwd_error);
								// sysUtil.showsDialog(str);
								break;
							case ResultCode.USER_OFFLINE:
								// sysUtil = new
								// SysUtil(UIRealtimePlayActivity.this);
								// str =
								// getBaseContext().getResources().getString(R.string.system_error);
								// sysUtil.showsDialog(str);
								break;
							case ErrorCode.NO_BUSINESS_RIGHT:
								str = getResources().getString(R.string.no_business_right);
								showErrorDialog(str);
								break;
							case ErrorCode.DEV_IS_NOT_ONLINE:
								str = getResources().getString(R.string.dev_is_not_online);
								showErrorDialog(str);
								break;
							case ErrorCode.IS_EXCEPTION_DEVICE:
								str = getResources().getString(R.string.is_exception_device);
								showErrorDialog(str);
								break;
							case ErrorCode.MAIN_DEV_IS_NULL:
							case ErrorCode.IVS_SMU_DEVICE_NOTEXIST:
							case ErrorCode.IVS_SMU_DEV_NOEXIST:
								str = getResources().getString(R.string.main_dev_is_null);
								showErrorDialog(str);
								break;
							case ErrorCode.MDU_IS_NOT_ONLINE:
								str = getResources().getString(R.string.mdu_is_not_online);
								showErrorDialog(str);
								break;
							case ErrorCode.STREAM_NOT_FOUND:
								str = getResources().getString(R.string.stream_not_found);
								showErrorDialog(str);
								break;
							case ErrorCode.VEDIO_CHANNEL_IS_DISABLE:
								str = getResources().getString(R.string.vedio_channel_is_disable);
								showErrorDialog(str);
								break;
							case ErrorCode.IVS_SMU_ROUTE_NVR_FAIL:
								str = getBaseContext().getResources().getString(R.string.get_nvr_route_failed);
								showErrorDialog(str);
								break;
							case ErrorCode.IVS_NO_ENOUGH_RIGHT:
								str = getResources().getString(R.string.no_authority_err);
								showErrorDialog(str);
								break;
							case -1:
								str = getResources().getString(R.string.failToOPenStream);
								showErrorDialog(str);
								break;
							case ErrorCode.SERVICE_ERROR:
							default:
								str = getBaseContext().getResources().getString(R.string.scc_error);
								showErrorDialog(str);
								break;
						}
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		getURLThread.start();

		Log.i(TAG, "playVideo end ");
	}

	private void playVideoByStreamId(String id)
	{

	}

	/**
	 * <һ�仰���ܼ���>
	 * <������ϸ����>
	 * 
	 * @param str
	 * @see [�ࡢ��#��������#��Ա]
	 */
	private void showErrorDialog(final String str)
	{

		this.runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				if (CameraPlayerActivity.this != null && !CameraPlayerActivity.this.isFinishing())
				{
					alertDialog = new AlertDialog.Builder(CameraPlayerActivity.this).setTitle(R.string.information)
							.setMessage(str).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int whichButton)
						{
							finish();
						}
					}).setCancelable(false).show();
				}
			}
		});

	}

	/**
	 * <������Ƶ�������ƵView>
	 * <������ϸ����>
	 * 
	 * @param videoUrl
	 *            videoUrl
	 * @author l00165774
	 * @see [�ࡢ��#��������#��Ա]
	 */
	private void playVideo(final String videoUrl)
	{
		Log.d(TAG, "playVideo start ");
		Log.e(TAG, "playVideo videoUrl->" + videoUrl);
		this.runOnUiThread(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				if (mmPlayer == null)
				{
					mmPlayer = new IVSPlayer(CameraPlayerActivity.this);
					mmPlayer.setScreenSize(getWindowManager().getDefaultDisplay().getWidth(),
							getWindowManager().getDefaultDisplay().getHeight());
					mmPlayer.setDataSource(videoUrl);
					realTimePlay.addView(mmPlayer);
					mmPlayer.setOnTouchListener(surfaceViewTouchListener);
					mmPlayer.setOnErrorListener(onErrorListener);
					mmPlayer.setOnCompletionListener(onCompletionListener);
					mmPlayer.setOnBeginListener(onBeginListener);
					mmPlayer.setOnVideoSizeChangedListener(myonVideoSizeChangedListener);
					mmPlayer.setLongClickable(true);
					// Ϊ��ֹ��������������ǰ4֡ͼ��
					mmPlayer.setDropNum(4);

					setFullScreen();
					switchScale.setBackgroundDrawable(
							CameraPlayerActivity.this.getResources().getDrawable(R.drawable.switch_scale));
				}
			}
		});

		Log.d(TAG, "playVideo end ");
	}

	/**
	 * ��ʼ���������
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void init()
	{
		Log.d(TAG, "init start ");
		Intent in = this.getIntent();
		ptzType = in.getStringExtra("ptzType");
		imageName = (ImageView) findViewById(R.id.camera_image);
//		 status = in.getStringExtra("status");
		domainCode = in.getStringExtra("domainCode");
//		 nvrCode = in.getStringExtra("nvrCode");
		if (DOMECAMERA.equals(ptzType))
		{
			isFixCamera = false;
			imageName.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.live_ball_machine_online));
		}
		else
		{
			isFixCamera = true;
			imageName.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.live_ptz_machine_online));
		}
		cameraId = in.getStringExtra("cameraId");
		cameraName = in.getStringExtra("cameraName");
		isOutScc = in.getStringExtra("isOutScc");
		if (cameraId.length() > 20)
		{
			isOutScc = "1";
		}

		realTimePlay = (LinearLayout) findViewById(R.id.realTimePlay);

		realTimePlay.setOnTouchListener(surfaceViewTouchListener);

		cameraTitleName = (TextView) findViewById(R.id.camera_name);
		cameraTitleName.setText(cameraName);

		detector.setIsLongpressEnabled(true);
		progressBar = (ProgressBar) findViewById(R.id.player_prepairing);
		playHandler = new RealTimePlayHandler();

		Log.d(TAG, "init end ");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Activity#onRestoreInstanceState(Bundle)
	 */

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * ��ʼ���ƾ����ƴ���
	 *
	 */
	private void initPTZControl()
	{
		Log.d(TAG, "initPTZControl start ");

		newConfig = getResources().getConfiguration();

		ptzControlWindowLL = (LinearLayout) this.findViewById(R.id.ptzControl_view2);

		param2 = String.valueOf(setting.getInt("ptzstep", 5));

		addOrdeletCollection = (LinearLayout) this.findViewById(R.id.collection);
		textShowHidenSnap = (TextView) this.findViewById(R.id.text_show_hiden_snap);
		ptzToolsShowOrHiden = (LinearLayout) this.findViewById(R.id.show_hiden_tools);
		textShowHidenTools = (TextView) this.findViewById(R.id.text_show_hiden_tools);
		seekbarValue = (TextView) this.findViewById(R.id.seekbar_value);
		stepSeekBar = (SeekBar) this.findViewById(R.id.step_seekbar);
		snapShowHiden = (LinearLayout) this.findViewById(R.id.show_hiden_snap);
		ptzStepLen = (LinearLayout) this.findViewById(R.id.ptz_step_len);
		ptzSettingTools = (LinearLayout) this.findViewById(R.id.ptzsettingtools);
		ptzSetting = (ImageView) this.findViewById(R.id.setting);
		streamSetTools = (LinearLayout) this.findViewById(R.id.streamSettings);
		streamSetting = (ImageView) this.findViewById(R.id.streamSet);

		textMainStream = (TextView) this.findViewById(R.id.text_mainStream);
		textSubStream = (TextView) this.findViewById(R.id.text_subStream);
		textMtuStream = (TextView) this.findViewById(R.id.text_mtuStream);

		textCollection = (TextView) this.findViewById(R.id.text_collection);

		// ��̨���ƹ������
		// ��Ȧ��С
		apertureReduce = (ImageView) this.findViewById(R.id.aperture_reduce);
		// ��Ȧ�Ŵ�
		apertureBlowUp = (ImageView) this.findViewById(R.id.aperture_blowup);
		// ����Զ
		focusFarOff = (ImageView) this.findViewById(R.id.focus_far_off);
		// �����
		focusNear = (ImageView) this.findViewById(R.id.focus_near);
		// ��Χ��������С
		scopeLargen = (ImageView) this.findViewById(R.id.scope_largen);
		// ��Χ��С��������
		scopeReduce = (ImageView) this.findViewById(R.id.scope_reduce);

		mainStreamLayout = (LinearLayout) this.findViewById(R.id.mainStream);
		subStreamLayout = (LinearLayout) this.findViewById(R.id.subStream);
		mtuStreamLayout = (LinearLayout) this.findViewById(R.id.mtuStream);

		// ��
		lock = (ImageView) this.findViewById(R.id.lock);
		switchScale = (ImageView) this.findViewById(R.id.switchScale);

		// ץ��
		snap = (ImageView) this.findViewById(R.id.airgraph);

		addOrdeletCollection.setOnTouchListener(this);
		addOrdeletCollection.setOnClickListener(this);
		ptzToolsShowOrHiden.setOnTouchListener(this);
		ptzToolsShowOrHiden.setOnClickListener(this);
		stepSeekBar.setOnSeekBarChangeListener(this);
		snapShowHiden.setOnTouchListener(this);
		snapShowHiden.setOnClickListener(this);
		ptzStepLen.setOnClickListener(this);
		ptzStepLen.setOnTouchListener(this);
		ptzSettingTools.setOnTouchListener(this);
		ptzSetting.setOnClickListener(this);
		streamSetting.setOnClickListener(this);
		apertureReduce.setOnTouchListener(this);
		apertureBlowUp.setOnTouchListener(this);
		focusFarOff.setOnTouchListener(this);
		focusNear.setOnTouchListener(this);
		scopeLargen.setOnTouchListener(this);
		scopeReduce.setOnTouchListener(this);
		lock.setOnTouchListener(this);
		switchScale.setOnTouchListener(this);

		mainStreamLayout.setOnClickListener(this);
		subStreamLayout.setOnClickListener(this);
		mtuStreamLayout.setOnClickListener(this);

		snap.setOnClickListener(this);
		apertureReduce.setOnClickListener(this);
		apertureBlowUp.setOnClickListener(this);
		focusFarOff.setOnClickListener(this);
		focusNear.setOnClickListener(this);
		scopeLargen.setOnClickListener(this);
		scopeReduce.setOnClickListener(this);
		lock.setOnClickListener(this);
		switchScale.setOnClickListener(this);

//		// ��ʼ��param1,����һ����ҳ��ִ��PTZ����ʱparam1û��ֵ;
//		if (param1.length() == 0)
//		{
//			param1 = PTZControl.SERIES;
//			// param2 = (ptzSb.getProgress() + 1) + "";
//			param2 = setting.getInt("ptzstep", 5) + "";
//		}

		// �����ǹ������̨�ٶȲ��ɵ��
		if (!(ptzType.equals(DOMECAMERA) || ptzType.equals(PTZ)))
		{
			stepSeekBar.setEnabled(false);
		}
		else
		{
			stepSeekBar.setEnabled(true);
		}

		Log.d(TAG, "initPTZControl end ");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Activity#onCreateOptionsMenu(Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		/**
		 * �Ѿ��޸ĵ�ʵ��ƵĻ��
		 **/
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressWarnings("deprecation")
	private void setFullScreen()
	{
		/**
		 * ��̬����ץ�ĺ��ղذ�ť��λ��
		 */

		this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		isFullScreen = true;

		mmPlayer.setAspectRatio(IVSPlayer.SURFACE_FILL);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();
		mmPlayer.changeSurfaceSize(width, height);
	}

	@SuppressWarnings("deprecation")
	private void exitFullScreen()
	{

		/**
		 * ��̬����ץ�ĺ��ղذ�ť��λ��
		 */
		this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		isFullScreen = false;

		mmPlayer.setAspectRatio(IVSPlayer.SURFACE_NONE);
		int height = getWindowManager().getDefaultDisplay().getHeight();
		mmPlayer.changeSurfaceSize(mmPlayer.getWidth(), height);

	}

	/** {@inheritDoc} */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * ����˵����¼�
	 *
	 * @param item
	 *            ϵͳ���ݲ˵������
	 * @return boolean
	 * @exception null
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.i(TAG, "onOptionsItemSelected exec ");
		Log.d(TAG, "ItemId->" + item.getItemId());
		switch (item.getItemId())
		{
			case ISSHOWTOOLS: // 1
				// snatchImage();
				// ��ȡ�������Ƿ���ʾ�ƾ���������״̬
				isShowPtzTool = setting.getBoolean("ptztool", true);
				Log.e(TAG, "ptzType->" + ptzType + " DOMECAMERA->" + DOMECAMERA + " PTZ->" + PTZ);
				if (ptzType.equals(DOMECAMERA) || ptzType.equals(PTZ))
				{
					if (!isShowPtzTool)
					{
						// ��ʾ������
						// ptzControlWindowLL.setVisibility(View.VISIBLE);
						item.setTitle(R.string.hide_tools);
						showPTZTool();
					}
					else
					{
						item.setTitle(R.string.show_tools);
						hidePTZTool();
					}
				}
				else
				{
					Log.d(TAG, "isShowPtzTool->" + isShowPtzTool);
					if (!isShowPtzTool)
					{
						// ��ʾ������
						ptzControlWindowLL.setVisibility(View.VISIBLE);

						item.setTitle(R.string.hide_tools);
						apertureReduce.setEnabled(false);
						apertureBlowUp.setEnabled(false);
						focusNear.setEnabled(false);
						focusFarOff.setEnabled(false);
						scopeLargen.setEnabled(false);
						scopeReduce.setEnabled(false);
						apertureReduce.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
								.getDrawable(R.drawable.aperture_small_unchecked));
						apertureBlowUp.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.aperture_big_uncheck));
						focusNear.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_samll_uncheck));
						focusFarOff.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_big_unchecked));
						scopeLargen.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.shorten_uncheck));
						scopeReduce.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.blowup_uncheck));
						lock.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock_uncheck));
						editor = setting.edit();
						editor.putBoolean("ptztool", true);
						editor.commit();
					}
					else
					{
						// ���ع�����
						ptzControlWindowLL.setVisibility(View.GONE);

						item.setTitle(R.string.show_tools);

						editor = setting.edit();
						editor.putBoolean("ptztool", false);
						editor.commit();
					}
				}

				break;
			case ISSHOWSNAP: // 2
				// ptzControlWindowLL.setVisibility(View.VISIBLE);
				isShowSnap = setting.getBoolean("snapBtn", true);
				if (!isShowSnap)
				{
					snap.setVisibility(View.VISIBLE);
					// isShowSnap = true;
					editor = setting.edit();
					editor.putBoolean("snapBtn", true);
					editor.commit();
					item.setTitle(R.string.hide_snap);
				}
				else
				{
					snap.setVisibility(View.GONE);
//					 isShowSnap = false;
					editor = setting.edit();
					editor.putBoolean("snapBtn", false);
					editor.commit();
					item.setTitle(R.string.show_snap);
				}
				break;
			case COLLECTION: // 3
				if (!isCollection)
				{
					// ȡ���ղ�
					removeCameraToFavorite(cameraId, domainCode, CameraPlayerActivity.this);
					isCollection = true;
					item.setTitle(R.string.collection);
				}
				else
				{
					// ����ղ�
					tcli = new TypeCameraListInf();
					tcli.setCameraId(cameraId);
					tcli.setCameraName(cameraName);
					tcli.setStatus("ON");
					tcli.setPtzType(ptzType);
					/**
					 * add by miaobinbin
					 */
					tcli.setDomainCode(domainCode);
					tcli.setIsOutScc(isOutScc);
					addCameraToFavorite(tcli);
					isCollection = false;
					item.setTitle(R.string.cancel_collection);
				}
				break;
			case SWITCHTOBIGSCREEN_DEMO_MENU: // 4
				break;
			case HELP_MENU: // 5
				if (null != mmPlayer)
				{
					mmPlayer.stop();
				}
				break;
			case BACK_MENU: // 6
				if (null != mmPlayer)
				{
					mmPlayer.stop();
					mmPlayer = null;
				}
				finish();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showPTZTool()
	{
		ptzControlWindowLL.setVisibility(View.VISIBLE);

	}

	private void hidePTZTool()
	{
		Log.d(TAG, "hidePTZTool start ");
		ptzControlWindowLL.setVisibility(View.GONE);

		Log.d(TAG, "hidePTZTool end ");
	}



	/**
	 * ץȡͼƬ
	 */
	private void snatchImage()
	{
		// ��ȡͼƬ����·��;

		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
		picPath = pre.getString("imagepath", FILE + "eIVS/images");
		picPath = picPath + "/";
		File file = new File(picPath);
		if (!file.exists())
		{
			try
			{
				file.mkdirs();
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error", e);
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");//

		final String picPathWhole = picPath + sdf.format(new Date()) + "_" + cameraName + ".jpg";

		currPath = picPathWhole;

		int i = mmPlayer.takeSnapshot(picPathWhole, 0, 0);

		if (i == 1)
		{
			updateGallery(picPathWhole);

			/* End 2014/4/10 ץ��ͼƬ��ʾ�������� */
			Toast.makeText(this, R.string.snatch, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, R.string.snatch_fail, Toast.LENGTH_SHORT).show();
		}
	}

	private void updateGallery(String filename)
	{
		MediaScannerConnection.scanFile(this, new String[] { filename }, null,
				new MediaScannerConnection.OnScanCompletedListener()
		{
					public void onScanCompleted(String path, Uri uri)
					{
						// Toast.makeText(UIRealtimePlayActivity.this, "ʵ�� --
						// ˢ��ý������", Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * <ʵʱ���ʱץȡ���һ֡ͼƬ����ʾ��������б�ʱ>
	 * <������ϸ����>
	 *
	 * @see [�ࡢ��#��������#��Ա]
	 */
	private void snatchLastFrame()
	{
		String lastFramePath = FILE + "eIVS/lastFrame/";
		File file = new File(lastFramePath);
		if (!file.exists())
		{

			try
			{
				file.mkdirs();
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error", e);
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS ");//
		final String pictureName = lastFramePath + sdf.format(new Date()) + "_" + cameraName + ".jpg";
		int i = mmPlayer.takeSnapshot(pictureName, 0, 0);
		if (i == 1)
		{
			// this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
			// Uri.parse("file://"
			// + Environment.getExternalStorageDirectory())));

			updateGallery(pictureName);

			// ����Bitmap�������ѡ��
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			bitmapOptions.inSampleSize = SysUtil.computeSampleSize(bitmapOptions, -1, 128 * 128);
			bitmapOptions.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(pictureName);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (bitmap != null)
			{
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			}

			TypeCameraListInf tclinf = new TypeCameraListInf();
			tclinf.setCameraId(cameraId);
			tclinf.setCameraName(cameraName);
			tclinf.setCameraImage(baos.toByteArray());

			// MGRControlCenter.getInstance().updateCameraImageToFavorite(tclinf,
			// new CallbackUI()
			// {
			// @Override
			// public void execute(int errorCode, Object obj)
			// {
			// if (errorCode == 0)
			// {
			// File file = new File(pictureName);
			// try
			// {
			// file.delete();
			// }
			// catch (Exception e)
			// {
			// Log.e(TAG, "Error", e);
			// }
			//
			// }
			//
			// }
			// });

			// ����bitmap����
			if (null != bitmap && !bitmap.isRecycled())
			{
				bitmap.recycle();
			}
		}

	}

	private void doPTZControl(final PTZControl ptzAction)
	{
		Thread ptzThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					if(ptzAction.getOpCode().equals(PTZControl.PTZ_STOP))
					{
						Thread.sleep(1000);
					}

					Log.i(TAG, "ptzAction->" + ptzAction);
					ResponseInfo ptzResponseInfo = (ResponseInfo) IVSService.ptzControl(domainCode, cameraId,ptzAction);

					Log.d(TAG, "leftActionResponseInfo->" + ptzResponseInfo);
					if (ptzResponseInfo != null)
					{
						int errorCode = ptzResponseInfo.getRspCode();
						Log.e(TAG, "leftAction errorCode->" + errorCode);
						handler.obtainMessage(errorCode, ptzResponseInfo).sendToTarget();
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		ptzThread.start();



	}




	/**
	 * ��̨�������
	 */
	private void leftAction()
	{
		Log.i(TAG, "leftAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LEFT, param1, param2));

	}


	/**
	 * ��̨���Ҳ���
	 */
	private void rightAction()
	{
		Log.i(TAG, "rightAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_RIGHT, param1, param2));
	}

	/**
	 * ��̨���ϲ���
	 */
	private void upAction()
	{
		Log.i(TAG, "upAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_UP, param1, param2));

	}

	/**
	 * ��̨���²���
	 */
	private void downAction()
	{
		Log.i(TAG, "downAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_DOWN, param1, param2));

	}

	/**
	 * ��̨���ϲ���
	 */
	private void leftUpAction()
	{
		Log.i(TAG, "leftUpAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_UP_LEFT, param1, param2));

	}

	/**
	 * ��̨���²���
	 */
	private void leftDownAction()
	{
		Log.i(TAG, "leftDownAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_DOWN_LEFT, param1, param2));

	}

	/**
	 * ��̨���ϲ���
	 */
	private void rightUpAction()
	{
		Log.i(TAG, "rightUpAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_UP_RIGHT, param1, param2));

	}

	/**
	 * ��̨���²���
	 */
	private void rightDownAction()
	{
		Log.i(TAG, "rightDownAction exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_DOWN_RIGHT, param1, param2));
	}

	/**
	 * ��Ȧ�Ŵ�
	 */
	private void ptzApertureOpen()
	{
		Log.i(TAG, "ptzApertureOpen exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LENS_APERTURE_OPEN, param1, param2));
	}

	/**
	 * ��Ȧ��С
	 */
	private void ptzApertureClose()
	{
		Log.i(TAG, "ptzApertureClose exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LENS_APERTURE_CLOSE, param1, param2));
	}

	/**
	 * �۽���
	 */
	private void ptzfocusIn()
	{
		Log.i(TAG, "ptzfocusIn exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LENS_FOCAL_NEAT, param1, param2));
	}

	/**
	 * �۽�Զ
	 */
	private void ptzfocusOut()
	{
		Log.i(TAG, "ptzfocusOut exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LENS_FOCAL_FAR, param1, param2));

	}

	private void ptzZoomIn()
	{
		Log.i(TAG, "ptzZoomIn exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LENS_ZOOM_IN, param1, param2));

	}

	private void ptzZoomOut()
	{
		Log.i(TAG, "ptzZoomOut exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LENS_ZOOM_OUT, param1, param2));

	}

	/**
	 * �ƾ�ֹͣ����
	 */
	private void ptzStop()
	{
		Log.i(TAG, "ptzStop exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_STOP, "", ""));


	}



	/**
	 * ��̨����
	 */
	private void ptzLock()
	{
		Log.i(TAG, "ptzLock exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_LOCK, null, null));

	}

	/**
	 * ��̨����
	 */
	private void ptzUnLock()
	{
		Log.i(TAG, "ptzUnLock exec ");
		doPTZControl(new PTZControl(PTZControl.PTZ_UNLOCK, null, null));

	}

	/**
	 * ��ѯ����״̬
	 */
	private void ptzQueryLockState(String camearID, final Boolean lockStatu)
	{
		if (!isLocked)
		{
			ptzLock();
		}
		else
		{
			ptzUnLock();
		}
		// if (queryLockedStatus != 10)
		// {
		// queryLockedStatus = 10;
		// mcc.ptzQueryLockStateControl(camearID, new CallbackUI()
		// {
		// @Override
		// public void execute(int errorCode, Object obj)
		// {
		// queryLockedStatus = errorCode;
		// ptzQueryRsp = (PtzQueryRsp)obj;
		// lockStatus = ptzQueryRsp.getLockStatus();
		// if (lockStatus.equals("1"))
		// {
		// if (!lockStatu)
		// {
		// lock.setBackgroundDrawable(UIRealtimePlayActivity.this.getResources()
		// .getDrawable(R.drawable.lock));
		// isLocked = true;
		// }
		//
		// if (lockStatu && isLocked)
		// {
		// ptzUnLock();
		// }
		//
		// if (lockStatu && !isLocked)
		// {
		// ptzLock();
		// }
		// }
		// if (lockStatus.equals("0"))
		// {
		// if (!lockStatu)
		// {
		// lock.setBackgroundDrawable(UIRealtimePlayActivity.this.getResources()
		// .getDrawable(R.drawable.unlock));
		// isLocked = false;
		// }
		// if (lockStatu && !isLocked)
		// {
		// ptzLock();
		// }
		// if (lockStatu && isLocked)
		// {
		// ptzUnLock();
		// }
		// }
		// }
		// });
		// }

	}


	/**
	 * <һ�仰���ܼ���>
	 * <������ϸ����>
	 *
	 * @param v
	 *            v
	 * @param event
	 *            event
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public void onTouchLandSeries(View v, MotionEvent event)
	{
		Log.i(TAG, "onTouchLandSeries exec ");
		Log.d(TAG, "touch view->" + v);
		switch (v.getId())
		{
			case R.id.ptz_up_btn:

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						upAction();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// upBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_up_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						// ����PTZֹͣ����;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.ptz_down_btn:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						downAction();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// downBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_down_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.ptz_left_btn:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						leftAction();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// leftBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_left_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.ptz_right_btn:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						rightAction();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// rightBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_right_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.bright_out_ib:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						ptzApertureClose();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// brightZoomoutIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_out_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.bright_in_ib:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						ptzApertureOpen();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// brightZoominIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_in_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.focus_out_ib:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						// ptzfocusOut();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// focusZoomoutIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_out_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.focus_in_ib:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						ptzfocusIn();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// focusZoominIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_in_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.record_out_ib:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						ptzZoomOut();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// recordZoomoutIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_out_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.record_in_ib:
				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						ptzZoomIn();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// recordZoominIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_in_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						ptzStop();
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * <һ�仰���ܼ���>
	 * <������ϸ����>
	 *
	 * @param v
	 *            v
	 * @param event
	 *            event
	 * @see [�ࡢ��#��������#��Ա]
	 */
	@SuppressWarnings("deprecation")
	public void onTouchLandPoint(View v, MotionEvent event)
	{
		Log.i(TAG, "onTouchLandPoint exec ");

		switch (v.getId())
		{
			case R.id.aperture_reduce:// ���ڵ㶯ģʽ��ѡ�����ȣ����࣬��С��������ģʽ�Զ�ѡ��Ϊ����ģʽ,�ҽ�����ǰģʽ�ı�ֵҲ���Ÿı�
				Log.d(TAG, "touch aperture reduce ");
				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					Log.d(TAG, "ptzSettingTools GONE ");
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// param1 = SERIES;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						apertureReduce.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.aperture_small_press));
						// ��ʾ��̨�������ȼ��û�����
						ptzApertureClose();

						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// brightZoomoutIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_out_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						apertureReduce.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.aperture_small));
						ptzStop();
						break;
					default:
						break;
				}
				break;
			// ���ڵ㶯ģʽ��ѡ�����ȣ����࣬��С��������ģʽ�Զ�ѡ��Ϊ����ģʽ
			case R.id.aperture_blowup:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// param1 = SERIES;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						apertureBlowUp.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.aperture_big_press));
						// ��ʾ��̨�������ȼ��û�����
						ptzApertureOpen();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// brightZoominIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_in_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						apertureBlowUp.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.aperture_big));
						ptzStop();
						break;
					default:
						break;
				}
				break;
			// ���ڵ㶯ģʽ��ѡ�����ȣ����࣬��С��������ģʽ�Զ�ѡ��Ϊ����ģʽ
			case R.id.focus_far_off:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// param1 = SERIES;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						focusFarOff.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_big_press));
						ptzfocusOut();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// focusZoomoutIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_out_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						focusFarOff.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_big));
						ptzStop();
						break;
					default:
						break;
				}
				break;
			// ���ڵ㶯ģʽ��ѡ�����ȣ����࣬��С��������ģʽ�Զ�ѡ��Ϊ����ģʽ
			case R.id.focus_near:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// param1 = SERIES;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						focusNear.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_samll_press));
						ptzfocusIn();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// focusZoominIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_in_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						focusNear.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_samll));
						ptzStop();
						break;
					default:
						break;
				}
				break;
			// ���ڵ㶯ģʽ��ѡ�����ȣ����࣬��С��������ģʽ�Զ�ѡ��Ϊ����ģʽ
			case R.id.scope_largen:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// param1 = SERIES;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						scopeLargen.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.shorten_press));
						ptzZoomOut();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// recordZoomoutIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_out_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						scopeLargen.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.shorten));
						ptzStop();
						break;
					default:
						break;
				}
				break;
			// ���ڵ㶯ģʽ��ѡ�����ȣ����࣬��С��������ģʽ�Զ�ѡ��Ϊ����ģʽ
			case R.id.scope_reduce:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					// ��Ļ����
					case MotionEvent.ACTION_DOWN:
						// param1 = SERIES;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);
						scopeReduce.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.blowup_press));
						ptzZoomIn();
						break;
					// ����̧��
					case MotionEvent.ACTION_UP:
						// recordZoominIb.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_in_blur_land));//
						// ��������ʱ������ʾ����ͼ,�ڵ��̧��ʱ���û�;
						scopeReduce.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.blowup));
						ptzStop();
						break;
					default:
						break;
				}
				break;
			case R.id.lock:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						if (!isLocked)
						{
							lock.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock_press));
						}
						else
						{
							lock.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock_press));
						}
						// ��ȡ�ƾ����Ƽ�������������״̬
						ptzQueryLockState(cameraId, true);
						break;
					case MotionEvent.ACTION_UP:
						break;
					default:
						break;
				}

				break;
			case R.id.switchScale:

				if (ptzSettingTools.getVisibility() == View.GONE)
				{
					stopTimer();
					startTimer();
				}

				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						if (!isFullScreen)
						{
							setFullScreen();
							switchScale.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.switch_scale));
						}
						else
						{

							exitFullScreen();
							switchScale.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
									.getDrawable(R.drawable.switch_scale_fullscreen_press));
						}
						break;
					case MotionEvent.ACTION_UP:

						if (!isFullScreen)
						{

							switchScale.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
									.getDrawable(R.drawable.switch_scale_press));
						}
						else
						{
							switchScale.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.switch_scale_up));
						}
						break;
					default:
						break;
				}

				break;
			default:
				break;
		}
	}

	/**
	 * <һ�仰���ܼ���>
	 * <������ϸ����>
	 *
	 * @param v
	 *            v
	 * @param event
	 *            event
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public void onTouchLand(View v, MotionEvent event)
	{
		Log.i(TAG, "onTouchLand exec ");
		onTouchLandPoint(v, event);
	}

	/** {@inheritDoc} ����PTZ��������(�ֻ�֧�ִ���) */
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		Log.i(TAG, "onTouch View->" + v);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			Log.e(TAG, "orientation landscape ");
			// ����
			// ������״̬��ִ��PTZ�κβ����ڰ���̧���ֹͣPTZ����
			onTouchLand(v, event);
		}

		return false;
	}

	/** {@inheritDoc} ������̲���PTZ���������¶���(���ֻ���֧�ִ���ʱ) */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyUpDownMatch)
		{
			keyUpDownMatch = false;

			if (param1.equals(PTZControl.SERIES))
			{
				switch (event.getKeyCode())
				{
					// W��
					case KeyEvent.KEYCODE_W:
					case KeyEvent.KEYCODE_DPAD_UP:
						// ��up����Ϊ������������Ϊ����
						// up_btn.setBackgroundDrawable(this.getResources().getDrawable(
						// R.drawable.ptz_up_focus));

						// ���Ͷ�ӦPTZ��������;
						upAction();
						break;
					// S��
					case KeyEvent.KEYCODE_S:
					case KeyEvent.KEYCODE_DPAD_DOWN:
						// down_btn.setBackgroundDrawable(this.getResources().getDrawable(
						// R.drawable.ptz_down_focus));

						// ���Ͷ�ӦPTZ��������;
						downAction();
						break;
					// A��
					case KeyEvent.KEYCODE_A:
					case KeyEvent.KEYCODE_DPAD_LEFT:
						// left_btn.setBackgroundDrawable(this.getResources().getDrawable(
						// R.drawable.ptz_left_focus));

						// ���Ͷ�ӦPTZ��������;
						leftAction();
						break;
					// D��
					case KeyEvent.KEYCODE_D:
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						// right_btn.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_right_focus));

						// ���Ͷ�ӦPTZ��������;
						rightAction();
						break;
					// U:���ȵ�
					case KeyEvent.KEYCODE_U:
						// bright_zoomout.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_out_focus));

						// ���Ͷ�ӦPTZ��������;
						ptzApertureClose();
						break;
					// I:���ȸ�
					case KeyEvent.KEYCODE_I:
						// bright_zoomin.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_in_focus));

						// ���Ͷ�ӦPTZ��������;
						ptzApertureOpen();
						break;
					// J:����Զ
					case KeyEvent.KEYCODE_J:
						// focus_zoomout.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_out_focus));

						// ���Ͷ�ӦPTZ��������;
						ptzfocusOut();
						break;
					// K:�����
					case KeyEvent.KEYCODE_K:
						// focus_zoomin.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_in_focus));

						// ���Ͷ�ӦPTZ��������;
						ptzfocusIn();
						break;
					// N:��С
					case KeyEvent.KEYCODE_N:
						// record_zoomout.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_out_focus));

						// ���Ͷ�ӦPTZ��������;
						ptzZoomOut();
						break;
					// M:�Ŵ�
					case KeyEvent.KEYCODE_M:
						// record_zoomin.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_in_focus));

						// ���Ͷ�ӦPTZ��������;
						ptzZoomIn();
						break;
					/*
					 * case KeyEvent.KEYCODE_BACK:
					 * if (ptzControlWindowLL.isShown())
					 * {
					 * ptzControlWindowLL.setVisibility(View.GONE);
					 * return true;
					 * }
					 * else
					 * {
					 * return super.onKeyDown(keyCode, event);
					 * }
					 */
					default:
						break;
				}
			}
			else if (param1.equals(PTZControl.POINT))
			{
				// �㶯ģʽ�£��������ȡ����ࡢ��Сʱģʽ�Զ�����Ϊ����ģʽ;
				switch (event.getKeyCode())
				{
					// W��
					case KeyEvent.KEYCODE_W:
					case KeyEvent.KEYCODE_DPAD_UP:
						// ��up����Ϊ������������Ϊ����
						// up_btn.setBackgroundDrawable(this.getResources().getDrawable(
						// R.drawable.ptz_up_focus));

						// ���Ͷ�ӦPTZ��������;
						upAction();
						break;
					// S��
					case KeyEvent.KEYCODE_S:
					case KeyEvent.KEYCODE_DPAD_DOWN:
						// down_btn.setBackgroundDrawable(this.getResources().getDrawable(
						// R.drawable.ptz_down_focus));

						// ���Ͷ�ӦPTZ��������;
						downAction();
						break;
					// A��
					case KeyEvent.KEYCODE_A:
					case KeyEvent.KEYCODE_DPAD_LEFT:
						// left_btn.setBackgroundDrawable(this.getResources().getDrawable(
						// R.drawable.ptz_left_focus));

						// ���Ͷ�ӦPTZ��������;
						leftAction();
						break;
					// D��
					case KeyEvent.KEYCODE_D:
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						// right_btn.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_right_focus));

						// ���Ͷ�ӦPTZ��������;
						rightAction();
						break;
					// U:���ȵ�
					case KeyEvent.KEYCODE_U:
						// bright_zoomout.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_out_focus));

						// param1 = SERIES;// ģʽ�Զ�����Ϊ����ģʽ;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);

						// ���Ͷ�ӦPTZ��������;
						ptzApertureClose();
						break;
					// I:���ȸ�
					case KeyEvent.KEYCODE_I:
						// bright_zoomin.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_bright_in_focus));

						// param1 = SERIES;// ģʽ�Զ�����Ϊ����ģʽ;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);

						// ���Ͷ�ӦPTZ��������;
						ptzApertureOpen();
						break;
					// J:����Զ
					case KeyEvent.KEYCODE_J:
						// focus_zoomout.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_out_focus));

						// param1 = SERIES;// ģʽ�Զ�����Ϊ����ģʽ;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);

						// ���Ͷ�ӦPTZ��������;
						ptzfocusOut();
						break;
					// K:�����
					case KeyEvent.KEYCODE_K:
						// focus_zoomin.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_focus_in_focus));

						// param1 = SERIES;// ģʽ�Զ�����Ϊ����ģʽ;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);

						// ���Ͷ�ӦPTZ��������;
						ptzfocusIn();
						break;
					// N:��С
					case KeyEvent.KEYCODE_N:
						// record_zoomout.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_out_focus));

						// param1 = SERIES;// ģʽ�Զ�����Ϊ����ģʽ;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);

						// ���Ͷ�ӦPTZ��������;
						ptzZoomOut();
						break;
					// M:�Ŵ�
					case KeyEvent.KEYCODE_M:
						// record_zoomin.setBackgroundDrawable(this.getResources()
						// .getDrawable(R.drawable.ptz_zoom_in_focus));

						// param1 = SERIES;// ģʽ�Զ�����Ϊ����ģʽ;
						// seriesRb.setChecked(true);
						// stepSpeedTv.setText(R.string.ptz_speed);

						// ���Ͷ�ӦPTZ��������;
						ptzZoomIn();
						break;
					/*
					 * case KeyEvent.KEYCODE_BACK:
					 *
					 * if (ptzControlWindowLL.isShown())
					 * {
					 * // ptzControlWindowLL.setVisibility(View.GONE);
					 * return true;
					 * }
					 * else
					 * {
					 * return super.onKeyDown(keyCode, event);
					 * }
					 * return super.onKeyDown(keyCode, event);
					 */
					default:
						break;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ������̲���PTZ������̧����(���ֻ���֧�ִ���ʱ) ���ֵ㶯������ģʽ�� �㶯ģʽ��ʱ��������������ģʽ��ʱ����̧���Ҫִ��PTZֹͣ����;
	 */
	/** {@inheritDoc} */
	// @Override
	// public boolean onKeyUp(int keyCode, KeyEvent event)
	// {
	// keyUpDownMatch = true;
	//
	// // ��ȡ��ǰ������״̬,���ݵ�ǰ������״̬�ڵ������ʱ������ͬ��ͼƬ;
	// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
	// {// ����
	// // ������״̬��ִ��PTZ�κβ����ڰ���̧���ֹͣPTZ����
	// if (param1.equals(SERIES))
	// {
	// switch (event.getKeyCode())
	// {
	// case KeyEvent.KEYCODE_W:// W��
	// case KeyEvent.KEYCODE_DPAD_UP:
	// upBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_up_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_S:// S��
	// case KeyEvent.KEYCODE_DPAD_DOWN:
	// downBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_down_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_A:// A��
	// case KeyEvent.KEYCODE_DPAD_LEFT:
	// leftBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_left_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_D:// D��
	// case KeyEvent.KEYCODE_DPAD_RIGHT:
	// rightBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_right_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_U:// U:���ȵ�
	// // brightZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_out_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_I:// I:���ȸ�
	// // brightZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_in_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_J:// J:����Զ
	// // focusZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_out_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_K:// K:�����
	// // focusZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_in_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_N:// N:��С
	// // recordZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_out_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_M:// M:�Ŵ�
	// // recordZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_in_blur_land));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// default:
	// break;
	// }
	// }
	// else if (param1.equals(POINT))
	// {
	// // ��ѡ��㶯ģʽ��ʱ������Ҫ���̧��ʱִ��PTZֹͣ����
	// switch (event.getKeyCode())
	// {
	// case KeyEvent.KEYCODE_W:// W��
	// case KeyEvent.KEYCODE_DPAD_UP:
	// upBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_up_blur_land));
	//
	// break;
	// case KeyEvent.KEYCODE_S:// S��
	// case KeyEvent.KEYCODE_DPAD_DOWN:
	// downBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_down_blur_land));
	// break;
	// case KeyEvent.KEYCODE_A:// A��
	// case KeyEvent.KEYCODE_DPAD_LEFT:
	// leftBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_left_blur_land));
	// break;
	// case KeyEvent.KEYCODE_D:// D��
	// case KeyEvent.KEYCODE_DPAD_RIGHT:
	// rightBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_right_blur_land));
	// break;
	// case KeyEvent.KEYCODE_U:// U:���ȵ�
	// // brightZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_out_blur_land));
	// break;
	// case KeyEvent.KEYCODE_I:// I:���ȸ�
	// // brightZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_in_blur_land));
	// break;
	// case KeyEvent.KEYCODE_J:// J:����Զ
	// // focusZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_out_blur_land));
	// break;
	// case KeyEvent.KEYCODE_K:// K:�����
	// // focusZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_in_blur_land));
	// break;
	// case KeyEvent.KEYCODE_N:// N:��С
	// // recordZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_out_blur_land));
	// break;
	// case KeyEvent.KEYCODE_M:// M:�Ŵ�
	// // recordZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_in_blur_land));
	// break;
	// default:
	// break;
	// }
	// }
	// }
	// else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
	// {// ����
	// if (param1.equals(SERIES))
	// {
	// switch (event.getKeyCode())
	// {
	// case KeyEvent.KEYCODE_W:// W��
	// case KeyEvent.KEYCODE_DPAD_UP:
	// upBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_up_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_S:// S��
	// case KeyEvent.KEYCODE_DPAD_DOWN:
	// downBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_down_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_A:// A��
	// case KeyEvent.KEYCODE_DPAD_LEFT:
	// leftBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_left_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_D:// D��
	// case KeyEvent.KEYCODE_DPAD_RIGHT:
	// rightBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_right_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_U:// U:���ȵ�
	// // brightZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_out_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_I:// I:���ȸ�
	// // brightZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_in_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_J:// J:����Զ
	// // focusZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_out_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_K:// K:�����
	// // focusZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_in_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_N:// N:��С
	// // recordZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_out_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// case KeyEvent.KEYCODE_M:// M:�Ŵ�
	// // recordZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_in_blur));
	//
	// // ����̧��ʱ����PTZֹͣ����;
	// ptzStop();
	// break;
	// default:
	// break;
	// }
	// }
	// else if (param1.equals(POINT))
	// {// ��ѡ��㶯ģʽ��ʱ������Ҫ���̧��ʱִ��PTZֹͣ����
	// switch (event.getKeyCode())
	// {
	// case KeyEvent.KEYCODE_W:// W��
	// case KeyEvent.KEYCODE_DPAD_UP:
	// upBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_up_blur));
	//
	// break;
	// case KeyEvent.KEYCODE_S:// S��
	// case KeyEvent.KEYCODE_DPAD_DOWN:
	// downBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_down_blur));
	// break;
	// case KeyEvent.KEYCODE_A:// A��
	// case KeyEvent.KEYCODE_DPAD_LEFT:
	// leftBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_left_blur));
	// break;
	// case KeyEvent.KEYCODE_D:// D��
	// case KeyEvent.KEYCODE_DPAD_RIGHT:
	// rightBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ptz_right_blur));
	// break;
	// case KeyEvent.KEYCODE_U:// U:���ȵ�
	// // brightZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_out_blur));
	// break;
	// case KeyEvent.KEYCODE_I:// I:���ȸ�
	// // brightZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_bright_in_blur));
	// break;
	// case KeyEvent.KEYCODE_J:// J:����Զ
	// // focusZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_out_blur));
	// break;
	// case KeyEvent.KEYCODE_K:// K:�����
	// // focusZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_focus_in_blur));
	// break;
	// case KeyEvent.KEYCODE_N:// N:��С
	// // recordZoomoutIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_out_blur));
	// break;
	// case KeyEvent.KEYCODE_M:// M:�Ŵ�
	// // recordZoominIb.setBackgroundDrawable(this.getResources()
	// // .getDrawable(R.drawable.ptz_zoom_in_blur));
	// break;
	// default:
	// break;
	// }
	// }
	// }
	//
	// return super.onKeyUp(keyCode, event);
	// }

	/** {@inheritDoc} */

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		// �̶�ǹ�������ܽ����ƾ��ɿ���
		if (isFixCamera)
		{
			return true;
		}

		if (onTouchDownUpMatch)
		{
			onTouchDownUpMatch = false;

			double x = e2.getX() - e1.getX();
			double y = e2.getY() - e1.getY();
			double jiaodu = Math.atan(Math.abs(y) / Math.abs(x)) / (Math.PI) * 180;
			if (e1.getX() > e2.getX())
			{
				if (e1.getY() > e2.getY())
				{
					if (jiaodu <= XY_OFFSET)
					{
						rightAction();
					}
					else if (jiaodu <= (90 - XY_OFFSET))
					{
						rightDownAction();
					}
					else if (jiaodu <= 90)
					{
						downAction();
					}
				}
				if (e1.getY() < e2.getY())
				{

					if (jiaodu <= XY_OFFSET)
					{
						rightAction();
					}
					else if (jiaodu <= (90 - XY_OFFSET))
					{
						rightUpAction();
					}
					else if (jiaodu <= 90)
					{
						upAction();
					}
				}
			}
			else if (e1.getX() < e2.getX())
			{
				if (e1.getY() > e2.getY())
				{
					if (jiaodu <= XY_OFFSET)
					{
						leftAction();
					}
					else if (jiaodu <= (90 - XY_OFFSET))
					{
						leftDownAction();
					}
					else if (jiaodu <= 90)
					{
						downAction();
					}
				}
				if (e1.getY() < e2.getY())
				{

					if (jiaodu <= XY_OFFSET)
					{
						leftAction();
					}
					else if (jiaodu <= (90 - XY_OFFSET))
					{
						leftUpAction();
					}
					else if (jiaodu <= 90)
					{
						upAction();
					}
				}
			}

		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onDown(MotionEvent e)
	{
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void onLongPress(MotionEvent e)
	{
	}

	/** {@inheritDoc} */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		return false;

	}

	/** {@inheritDoc} */
	@Override
	public void onShowPress(MotionEvent e)
	{

	}

	/** {@inheritDoc} */
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		// if (ptzControlWindowLL.getVisibility() == View.VISIBLE)
		// {
		// ptzControlWindowLL.setVisibility(View.GONE);
		// }
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void onBackPressed()
	{
		/*
		 * if (isFullScreen)
		 * {
		 * fullScreen.setTitle(R.string.full_screen);
		 * exitFullScreen(fullScreen);
		 * }
		 * else
		 */

		if (mmPlayer != null && mmPlayer.isPlaying())
		{
			// natchLastFrame();
			mmPlayer.onPause();
			mmPlayer.stop();
			mmPlayer = null;
			finish();
		}
		else
		{
			super.onBackPressed();
		}
	}

	/** {@inheritDoc} */

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v)
	{
		Log.i(TAG, "onClick exec ");
		Log.d(TAG, "view->" + v + " id->" + v.getId());

		switch (v.getId())
		{
			case R.id.airgraph:
				snatchImage();
				break;
			case R.id.collection:
				if (!isCollection)
				{
					// ȡ���ղ�
					removeCameraToFavorite(cameraId, domainCode, CameraPlayerActivity.this);
					isCollection = true;
					textCollection.setText(R.string.collection);
				}
				else
				{
					// ����ղ�
					tcli = new TypeCameraListInf();
					tcli.setCameraId(cameraId);
					tcli.setCameraName(cameraName);
					tcli.setStatus("ON");
					tcli.setPtzType(ptzType);
					/**
					 * add by miaobinbin
					 */
					tcli.setIsOutScc(isOutScc);
					tcli.setDomainCode(domainCode);
					addCameraToFavorite(tcli);
					isCollection = false;
					textCollection.setText(R.string.cancel_collection);
				}
				break;
			case R.id.setting:

				if (isShowStreamSetting)
				{
					streamSetTools.setVisibility(View.GONE);
					isShowStreamSetting = false;
				}
				if (!isShowPtzSetting)
				{
					ptzSettingTools.setVisibility(View.VISIBLE);

					stopTimer();

					stepSeekBar.setProgress(setting.getInt("ptzstep", 6) - 1);
					seekbarValue.setText(setting.getInt("ptzstep", 5) + "");
					isShowPtzSetting = true;
					/*
					 * ptzSetting.setBackgroundDrawable(UIRealtimePlayActivity.
					 * this.getResources()
					 * .getDrawable(R.drawable.setting_press));
					 */
					isShowPtzTool = setting.getBoolean("ptztool", true);
					if (!isShowPtzTool)
					{
						textShowHidenTools.setText(R.string.show_tools);
					}
					else
					{
						textShowHidenTools.setText(R.string.hide_tools);

						ptzControlWindowLL.setVisibility(View.VISIBLE);
					}

					if (!isCollection)
					{
						textCollection.setText(R.string.cancel_collection);
					}
					else
					{
						textCollection.setText(R.string.collection);
					}

					isShowSnap = setting.getBoolean("snapBtn", true);
					if (!isShowSnap)
					{
						textShowHidenSnap.setText(R.string.show_snap);

					}
					else
					{
						textShowHidenSnap.setText(R.string.hide_snap);
					}

				}
				else
				{
					ptzSettingTools.setVisibility(View.GONE);
					isShowPtzSetting = false;

					stopTimer();
					startTimer();
					/*
					 * ptzSetting.setBackgroundDrawable(UIRealtimePlayActivity.
					 * this.getResources()
					 * .getDrawable(R.drawable.setting));
					 */
				}
				break;
			case R.id.streamSet:
				if (isShowPtzSetting)
				{
					ptzSettingTools.setVisibility(View.GONE);
					isShowPtzSetting = false;

					hidePTZTool();
				}
				if (!isShowStreamSetting)
				{
					isShowStreamSetting = true;
					streamSetTools.setVisibility(View.VISIBLE);
				}
				else
				{
					isShowStreamSetting = false;
					streamSetTools.setVisibility(View.GONE);
				}
				break;
			case R.id.show_hiden_tools:
				isShowPtzTool = setting.getBoolean("ptztool", true);
				if (ptzType.equals(DOMECAMERA) || ptzType.equals(PTZ))
				{
					if (!isShowPtzTool)
					{
						textShowHidenTools.setText(R.string.hide_tools);

						// ��ʾ������
						ptzControlWindowLL.setVisibility(View.VISIBLE);

						editor = setting.edit();
						editor.putBoolean("ptztool", true);
						editor.commit();

						// stopTimer();
						// startTimer();

					}
					else
					{
						textShowHidenTools.setText(R.string.show_tools);

						// ���ع�����
						ptzControlWindowLL.setVisibility(View.GONE);

						editor = setting.edit();
						editor.putBoolean("ptztool", false);
						editor.commit();

						// stopTimer();
					}
				}
				else
				{
					Log.e(TAG, "isShowPtzTool->>" + isShowPtzTool);
					if (!isShowPtzTool)
					{
						textShowHidenTools.setText(R.string.hide_tools);
						// ��ʾ������
						ptzControlWindowLL.setVisibility(View.VISIBLE);

						apertureReduce.setEnabled(false);
						apertureBlowUp.setEnabled(false);
						focusNear.setEnabled(false);
						focusFarOff.setEnabled(false);
						scopeLargen.setEnabled(false);
						scopeReduce.setEnabled(false);
						lock.setEnabled(false);
						apertureReduce.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
								.getDrawable(R.drawable.aperture_small_unchecked));
						apertureBlowUp.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.aperture_big_uncheck));
						focusNear.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_samll_uncheck));
						focusFarOff.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.focus_big_unchecked));
						scopeLargen.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.shorten_uncheck));
						scopeReduce.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.blowup_uncheck));
						lock.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock_uncheck2));
						editor = setting.edit();
						editor.putBoolean("ptztool", true);
						editor.commit();
					}
					else
					{
						textShowHidenTools.setText(R.string.show_tools);
						// ���ع�����
						ptzControlWindowLL.setVisibility(View.GONE);
						editor = setting.edit();
						editor.putBoolean("ptztool", false);
						editor.commit();
					}
				}
				break;
			case R.id.show_hiden_snap:
				isShowSnap = setting.getBoolean("snapBtn", true);
				if (!isShowSnap)
				{
					snap.setVisibility(View.VISIBLE);
					// isShowSnap = true;
					editor = setting.edit();
					editor.putBoolean("snapBtn", true);
					editor.commit();

					textShowHidenSnap.setText(R.string.hide_snap);

				}
				else
				{
					snap.setVisibility(View.GONE);
					// isShowSnap = false;
					editor = setting.edit();
					editor.putBoolean("snapBtn", false);
					editor.commit();
					textShowHidenSnap.setText(R.string.show_snap);
				}
				break;
			case R.id.mainStream:
				if (INDEX != 1)
				{
					INDEX = 1;

					subStreamLayout.setEnabled(false);
					mtuStreamLayout.setEnabled(false);

					progressBar.setVisibility(View.VISIBLE);
					textMainStream.setTextColor(getResources().getColor(R.color.streamSelected));
					textSubStream.setTextColor(getResources().getColor(R.color.streamNormal));
					textMtuStream.setTextColor(getResources().getColor(R.color.streamNormal));
					showSet = false;
					stopPlayer();
					playVideoByStreamId("1");
				}

				break;
			case R.id.subStream:
				if (INDEX != 2)
				{
					INDEX = 2;

					mainStreamLayout.setEnabled(false);
					mtuStreamLayout.setEnabled(false);

					progressBar.setVisibility(View.VISIBLE);
					textSubStream.setTextColor(getResources().getColor(R.color.streamSelected));
					textMainStream.setTextColor(getResources().getColor(R.color.streamNormal));
					textMtuStream.setTextColor(getResources().getColor(R.color.streamNormal));
					showSet = false;
					stopPlayer();
					playVideoByStreamId("2");
				}

				break;
			case R.id.mtuStream:
				if (INDEX != 3)
				{
					INDEX = 3;

					subStreamLayout.setEnabled(false);
					mainStreamLayout.setEnabled(false);

					progressBar.setVisibility(View.VISIBLE);
					textMtuStream.setTextColor(getResources().getColor(R.color.streamSelected));
					textSubStream.setTextColor(getResources().getColor(R.color.streamNormal));
					textMainStream.setTextColor(getResources().getColor(R.color.streamNormal));

					showSet = true;
					stopPlayer();
					playVideoByStreamId("1");
				}

				break;
			default:
				break;
		}

	}

	/**
	 * ��ӵ��ղؼ�
	 *
	 * @param TypeCameraListInf
	 *            TypeCameraListInf
	 *            ���������
	 */
	private void addCameraToFavorite(TypeCameraListInf tclinf)
	{
	}

	private void removeCameraToFavorite(String cameraID, String code, final Context text)
	{
	}

	/**
	 * <���������>
	 * <������ϸ����>
	 *
	 * @param camName
	 *            camName
	 * @see [�ࡢ��#��������#��Ա]
	 */
	// private void filterCameraList(String camName)
	// {
	//
	// }

	/**
	 * �ж��ղؼ����Ƿ����ʵʱ����������
	 * ���ݲ�ѯ����Ƿ���ʾ�ղؼ�ͼ��
	 */
	private void getFavoriteCameraByCode()
	{
	}

	/** {@inheritDoc} */
	@Override
	protected void onDestroy()
	{
		if (alertDialog != null)
		{
			alertDialog.dismiss();
		}
		super.onDestroy();
	}

	/**
	 * <�����߳�>
	 * <��������>
	 *
	 * @author lKF77942
	 * @version [�汾��, 2012-10-25]
	 * @see [�����/����]
	 * @since [��Ʒ/ģ��汾]
	 */
	private class RePlayThread extends Thread
	{

		@Override
		public void run()
		{
			try
			{
				// �ȴ�10s
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				// �ж�
				Log.e(TAG, "replay sleep except.");
			}
			playVideo();
		}

	}

	/**
	 * <һ�仰���ܼ���>
	 * <������ϸ����>
	 *
	 * @author z00201993
	 * @version [�汾��, 2012-5-12]
	 * @see [�����/����]
	 * @since [��Ʒ/ģ��汾]
	 */
	private class SurfaceViewTouchListener implements OnTouchListener
	{

		float baseValue = 0, value = 0;

		boolean isMoving = false;

		/** {@inheritDoc} */

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			// �̶�ǹ�������ܽ����ƾ��ɿ���
			if (isFixCamera)
			{
				if (isShowPtzSetting && ptzSettingTools.getVisibility() == View.VISIBLE)
				{
					ptzSettingTools.setVisibility(View.GONE);
					isShowPtzSetting = false;

					stopTimer();
					startTimer();
					return true;
				}
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				x1 = event.getX();
				y1 = event.getY();

				if (isShowPtzSetting)
				{
					ptzSettingTools.setVisibility(View.GONE);
					isShowPtzSetting = false;

					stopTimer();
					startTimer();
				}

				baseValue = 0;
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if (event.getPointerCount() >= 2)
				{
					float x = event.getX(0) - event.getX(1);
					float y = event.getY(0) - event.getY(1);
					// ��������ľ���
					value = (float) Math.sqrt(x * x + y * y);
					if (baseValue == 0)
					{
						baseValue = value;
					}
					else
					{
						if (!isMoving && onTouchDownUpMatch)
						{
							onTouchDownUpMatch = false;
							if (value > baseValue)
							{
								ptzZoomIn();
							}
							else
							{
								ptzZoomOut();
							}
							isMoving = true;
						}
					}
					return true;
				}
			}
			detector.onTouchEvent(event);
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				if (!onTouchDownUpMatch)
				{
					onTouchDownUpMatch = true;
					ptzStop();
					isMoving = false;
				}

				isShowPtzTool = setting.getBoolean("ptztool", true);

				float xd = event.getX() - x1;
				float yd = event.getY() - y1;

				if (xd < 10 && yd < 10 && isShowPtzTool && ptzControlWindowLL.getVisibility() == View.GONE
						&& streamSetTools.getVisibility() == View.GONE)
				{
					ptzControlWindowLL.setVisibility(View.VISIBLE);
					stopTimer();
					startTimer();
				}

				if (isShowStreamSetting)
				{
					streamSetTools.setVisibility(View.GONE);
					isShowStreamSetting = false;
				}

				x1 = 0.0f;
				y1 = 0.0f;
			}
			return true;
		}

	}

	float x1 = 0.0f;

	float y1 = 0.0f;

	/**
	 * <һ�仰���ܼ���>
	 * <������ϸ����>
	 *
	 * @author z00201993
	 * @version [�汾��, 2012-5-14]
	 * @see [�����/����]
	 * @since [��Ʒ/ģ��汾]
	 */
	private class RealTimePlayHandler extends Handler
	{
		/** {@inheritDoc} */

		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg)
		{
			Log.i(TAG, "RealTimePlayHandler handleMessage->" + msg.what);

			String str = "";
			switch (msg.what)
			{
				case IVSPlayer.PlayerEvent.MEDIA_PLAYER_BEGIN: // 1001
					progressBar.setVisibility(View.GONE);

					mainStreamLayout.setEnabled(true);
					subStreamLayout.setEnabled(true);
					Log.d(TAG, "hasMTU->" + hasMTU);
					if (hasMTU)
					{
						mtuStreamLayout.setEnabled(true);
					}

					Log.e(TAG, "isFullScreen->" + isFullScreen);
					if (isFullScreen)
					{
						switchScale.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.switch_scale_up));
					}
					else
					{
						switchScale.setBackgroundDrawable(
								CameraPlayerActivity.this.getResources().getDrawable(R.drawable.switch_scale_press));
					}
					isShowPtzTool = setting.getBoolean("ptztool", true);
					Log.d(TAG, "ptzType->" + ptzType + " DOMECAMERA->" + DOMECAMERA + " PTZ->" + PTZ);
					if (ptzType.equals(DOMECAMERA) || ptzType.equals(PTZ))
					{
						/**
						 * ��ѯ��̨�Ƿ�����
						 */

						hidePTZTool();
					}
					else
					{
						Log.d(TAG, "isShowPtzTool->" + isShowPtzTool);
						if (isShowPtzTool)
						{
							ptzControlWindowLL.setVisibility(View.VISIBLE);
							// apertureReduce.setEnabled(false);
							// apertureBlowUp.setEnabled(false);
							// focusNear.setEnabled(false);
							// focusFarOff.setEnabled(false);
							// scopeLargen.setEnabled(false);
							// scopeReduce.setEnabled(false);
							// lock.setEnabled(false);
							/**
							 * @author sWX248302 ǿ����Ӱ�ť����״̬
							 */
							apertureReduce.setEnabled(true);
							apertureBlowUp.setEnabled(true);
							focusNear.setEnabled(true);
							focusFarOff.setEnabled(true);
							scopeLargen.setEnabled(true);
							scopeReduce.setEnabled(true);
							lock.setEnabled(true);

							apertureReduce.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
									.getDrawable(R.drawable.aperture_small_unchecked));
							apertureBlowUp.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
									.getDrawable(R.drawable.aperture_big_uncheck));
							focusNear.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
									.getDrawable(R.drawable.focus_samll_uncheck));
							focusFarOff.setBackgroundDrawable(CameraPlayerActivity.this.getResources()
									.getDrawable(R.drawable.focus_big_unchecked));
							scopeLargen.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.shorten_uncheck));
							scopeReduce.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.blowup_uncheck));
							lock.setBackgroundDrawable(
									CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock_uncheck));
						}
						else
						{
							ptzControlWindowLL.setVisibility(View.GONE);
						}
					}

					isShowSnap = setting.getBoolean("snapBtn", true);
					Log.d(TAG, "isShowSnap->" + isShowSnap);
					if (isShowSnap)
					{
						snap.setVisibility(View.VISIBLE);
					}
					else
					{
						snap.setVisibility(View.GONE);
					}

					// ��ʾ��̨����
					ptzSetting.setVisibility(View.VISIBLE);

					// �ж��Ƿ�����ղ������ղ�״̬
					getFavoriteCameraByCode();
					Log.e(TAG, "isCollection->" + isCollection);
					if (!isCollection)
					{
						textCollection.setText(R.string.collection);
					}
					else
					{
						textCollection.setText(R.string.cancel_collection);
					}
					break;

				case IVSPlayer.PlayerEvent.MEDIA_PLAYER_SIZE_CHANGED: // 1004
					if (mmPlayer != null)
					{
						int width = mmPlayer.getWidth();
						int height = mmPlayer.getHeight();
						if (isFullScreen)
						{
							mmPlayer.changeSurfaceSize(width, height);
						}
						else
						{
							if (firstUsed == 1)
							{
								firstUsed++;
								defaultDisplayWidth = width;
								defaultDisplayHeight = height;
							}
							mmPlayer.changeSurfaceSize(defaultDisplayWidth, defaultDisplayHeight);
						}
					}
					break;
				case IVSPlayer.PlayerEvent.ERROR_READING_PACKET: // 4
				case IVSPlayer.PlayerEvent.MEDIA_PLAYER_END:
					str = getResources().getString(R.string.stream_is_close);
					showErrorDialog(str);
					break;
				case IVSPlayer.PlayerEvent.ERROR_OPEN_CODEC:
				case IVSPlayer.PlayerEvent.ERROR_OPEN_FILE:
					str = getResources().getString(R.string.failToOPenStream);
					showErrorDialog(str);
					break;
				default:
					break;
			}
		};

	}

	/**
	 * ����ҵ�񷵻ؽ��
	 * <������ϸ����>
	 * 
	 * @param errorCode
	 *            �����
	 * @see [�ࡢ��#��������#��Ա]
	 */
	@SuppressWarnings("deprecation")
	private void handleServiceResult(int errorCode, Object obj)
	{
		Log.i(TAG, "handleServiceResult exec ");
		Log.d(TAG, "errorCode->" + errorCode);
		switch (errorCode)
		{
			// ��̨��ͬ���ȼ����߸������ȼ��û�����
			case PTZ_CONTROLED_BY_OTHER_USER:
				Toast.makeText(CameraPlayerActivity.this, R.string.higth_level_user_control, Toast.LENGTH_SHORT).show();
				lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.unlock));
				isLocked = false;
				break;
			// ��̨��ͬ���ȼ����߸����ȼ��û�����
			case PTZ_LOCKED_BY_OTHER_USER:
				// ��̨������higth_level_user_lock
				Toast.makeText(CameraPlayerActivity.this, R.string.higth_level_user_lock, Toast.LENGTH_SHORT).show();
				lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
				isLocked = true;
				break;
			case PTZ_IS_LOCKED:
				Toast.makeText(CameraPlayerActivity.this, R.string.ptz_locked_byother_unlock, Toast.LENGTH_SHORT)
						.show();
				lock.setBackgroundDrawable(CameraPlayerActivity.this.getResources().getDrawable(R.drawable.lock));
				isLocked = true;
				break;
			case PTZ_LOCKED_BY_ALARM:
				Toast.makeText(CameraPlayerActivity.this, R.string.ptz_locked_by_alarm, Toast.LENGTH_SHORT).show();
				isLocked = true;
				break;
			case DOMAIN_EXTERN_PTZ_UNUSED:
				Toast.makeText(CameraPlayerActivity.this, R.string.DOMAIN_EXTERN_PTZ_UNUSED, Toast.LENGTH_SHORT).show();
				break;
			case SUCCESS:
				Log.d(TAG, "handleServiceResult Enter SUCCESS ");

				break;
			default:
				break;
		}
	}

	/**
	 * ����ҵ����������
	 * <������ϸ����>
	 * 
	 * @see [�ࡢ��#��������#��Ա]
	 */
	private void setServiceParam()
	{
		param1 = PTZControl.SERIES;
		if ("1".equals(isOutScc))
		{
			param2 = "5";
		}
		else
		{
			param2 = String.valueOf(setting.getInt("ptzstep", 5));
		}
		Log.d(TAG, "param2->" + param2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		seekbarValue.setText((stepSeekBar.getProgress() + 1) + "");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		editor = setting.edit();
		editor.putInt("ptzstep", seekBar.getProgress() + 1);
		editor.commit();
	}

}
