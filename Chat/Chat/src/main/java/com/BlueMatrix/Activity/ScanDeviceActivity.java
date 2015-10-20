package com.BlueMatrix.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.ble.RBLService;
import com.BlueMatrix.tools.DirectionService;
import com.BlueMatrix.tools.Memory;
import com.BlueMatrix.tools.MyDialog;


public class ScanDeviceActivity extends Activity
		implements AdapterView.OnItemClickListener,OnClickListener{
	private final static String TAG = ScanDeviceActivity.class.getSimpleName();

	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 3000;
	private Dialog mDialog;
	public static List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
	private Memory memory;

	private MyDialog mMyDialog = null;

	private String PreviewMacAdress = null;	//存储上一次连接的蓝牙MAC地址
	Timer mTimer;
	String DeviceName;	//设备名

	private static BlueAction blueAction;  //提供蓝牙操作

	public final static String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";
	public final static String EXTRA_DEVICE_NAME = "EXTRA_DEVICE_NAME";

	private ArrayList<BluetoothDevice> devices;
	private List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
	private SimpleAdapter adapter;
	private Map<String, String> map = null;
	private ListView listView;
	private String DEVICE_NAME = "name";
	private String DEVICE_ADDRESS = "address";
	public static final int RESULT_CODE = 31;
	private int mListViewIndex = -1;

	private TextView TextSearing;
	private TextView TextSearingEnd;
	private Handler messageHandler;

	Button btnRefresh;
	Button Connect_button;

	private String mDeviceName;
	private String mDeviceAddress;

	private RBLService mBluetoothLeService;
	Intent gattServiceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.scan_device_activity);

		DeviceName = getResources().getString(R.string.deviceName);
		TextSearing = (TextView)findViewById(R.id.textSeaching);
		TextSearingEnd = (TextView)findViewById(R.id.textSearchEnd);

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		initBtDeviceListView();
		//listView.setSelector(R.drawable.list_select);
//		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//									long arg3) {
//				// TODO Auto-generated method stub
//				//adapter..setSelectedPosition(arg2);
//				listView.setSelection(arg2);
//				//adapter.notifyDataSetInvalidated();
//
//			}
//		});
		Connect_button = (Button)findViewById(R.id.connect_button);
		Connect_button.setOnClickListener(this);
		btnRefresh = (Button)findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(this);

		//showRoundProcessDialog(ScanDeviceActivity.this, R.layout.loading_process_dialog_anim);



		memory = new Memory(this);
		PreviewMacAdress = memory.GetLastMacAddress();

		Looper looper = Looper.myLooper();
		messageHandler = new MessageHandler(looper);

		mMyDialog = new MyDialog(this, "Connecting Bluetooth device...");
		mMyDialog.setmWaitTime(12000);

		gattServiceIntent = new Intent(this, RBLService.class);
		//bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
									   IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			if (!mBluetoothLeService.isConnected() && (mDeviceAddress != null)) {
				//设置按键状态
				//SetButtonStatus(false);
				//mMyDialog.showDialog();

				mBluetoothLeService.connect(mDeviceAddress);
				memory.SaveMacAddress(mDeviceAddress);
			}
			//初始化蓝牙操作类
			blueAction = new BlueAction(mBluetoothLeService);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.btnRefresh: {
				if(mDevices != null)	mDevices.clear();

				if(listItems != null)	listItems.clear();
				if(adapter != null)	adapter.notifyDataSetChanged();
				scanLeDevice();
				break;
			}
			case R.id.connect_button: {
				//mListViewIndex
				if(mListViewIndex != -1) {
					if(mListViewIndex < adapter.getCount()) {
						HashMap<String, String> hashMap = (HashMap<String, String>) listItems.get(mListViewIndex);
						if (hashMap != null) {
							String addr = hashMap.get(DEVICE_ADDRESS);
							String name = hashMap.get(DEVICE_NAME);
							mDeviceAddress = addr;

							bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
							mMyDialog.showDialog();
						}
					}
					break;
				}
			}
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		//第一次启动，显示开机界面
		if(memory.firstLogin == true)
		{
			memory.firstLogin = false;
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), WelcomeActivity.class);
			startActivity(intent);
			//finish();
			return;
		}
		if(mDevices != null) {
			mListViewIndex = -1;
			listItems.clear();
			mDevices.clear();
			scanLeDevice();
		}
		registerReceiver(mServiceUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver(mServiceUpdateReceiver);
		} catch (Exception e) {
		}
	}

	private final BroadcastReceiver mServiceUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			//蓝牙命令
			if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {

			} else if (RBLService.ACTION_GATT_CONNECTED.equals(action)) {

				//mMyDialog.hideDialog();

			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				getGattService(mBluetoothLeService.getSupportedGattService());
				mMyDialog.hideDialog();
				if(mDeviceAddress != null) {
					Intent intent2 = new Intent(getApplicationContext(), MainMenuActivity.class);
					startActivity(intent2);
				}
			} else if (RBLService.ACTION_DATA_WRITE_SUCCESS.equals(action)) {
				// Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_SHORT).show();
				//mMyDialog.hideDialog();
			} else if (RBLService.ACTION_DATA_WRITE_FAILURE.equals(action)) {
				//Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
				//mMyDialog.hideDialog();
			}
		}
	};

	private void getGattService(BluetoothGattService gattService) {
		if (gattService == null)
			return;

		blueAction.getGattService(gattService);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(RBLService.ACTION_DATA_WRITE_SUCCESS);
		intentFilter.addAction(RBLService.ACTION_DATA_WRITE_FAILURE);

		intentFilter.addAction(DirectionService.ACTION_DIRCTION_LEFT);
		intentFilter.addAction(DirectionService.ACTION_DIRCTION_RIGHT);

		return intentFilter;
	}

	public void showRoundProcessDialog(Context mContext, int layout) {
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		};

		mDialog = new AlertDialog.Builder(mContext, R.style.FullScreenDialog).create();
		mDialog.setOnKeyListener(keyListener);
		//mDialog.setCancelable(false);
		mDialog.show();
		mDialog.setContentView(layout);
		View loadingView = mDialog.findViewById(R.id.loading_view);
		int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.loading_view_size);
		RotateAnimation rotateAnimation = new RotateAnimation(360, 0, dimensionPixelSize / 2, dimensionPixelSize / 2);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setRepeatMode(Animation.RESTART);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		loadingView.startAnimation(rotateAnimation);
	}

	private void scanLeDevice() {
		mListViewIndex = -1;
		mDeviceAddress = null;
		TextSearing.setVisibility(View.VISIBLE);
		TextSearingEnd.setVisibility(View.GONE);
		btnRefresh.setEnabled(false);
		new Thread() {

			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);

				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message message = Message.obtain();
				messageHandler.sendMessage(message);
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}.start();
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device != null) {
						if (mDevices.indexOf(device) == -1)
						{
							//过滤掉其它设备
							if ( (device.getName()!=null) &&
									(device.getName().compareTo(DeviceName) == 0)) {
								mDevices.add(device);
								map = new HashMap<String, String>();
								map.put(DEVICE_NAME, device.getName());
								map.put(DEVICE_ADDRESS, device.getAddress());
								listItems.add(map);
								adapter.notifyDataSetChanged();

								//如果找到上次连接过的设备，直接连接
								if (PreviewMacAdress != null) {
									if (PreviewMacAdress.compareTo(device.getAddress()) == 0) {
										//mTimer.cancel();
										mDeviceAddress = PreviewMacAdress;
										bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
										mMyDialog.showDialog();
										//startActivity(intent);
										//finish();
									}
								}

							}
						}
					}
				}
			});
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//System.exit(0);
	}

	void initBtDeviceListView()
	{
		listView = (ListView) findViewById(R.id.listView);
		listItems.clear();

		//devices = (ArrayList<BluetoothDevice>) mDevices;
//		for (BluetoothDevice device : devices) {
//			map = new HashMap<String, String>();
//			map.put(DEVICE_NAME, device.getName());
//			map.put(DEVICE_ADDRESS, device.getAddress());
//			listItems.add(map);
//		}

		adapter = new SimpleAdapter(getApplicationContext(), listItems,
				R.layout.list_item, new String[] { "name", "address" },
				new int[] { R.id.deviceName, R.id.deviceAddr});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
							int position, long id) {
		mListViewIndex = position;
		listView.setSelector(R.drawable.list_select);
	}


	class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			TextSearing.setVisibility(View.GONE);
			TextSearingEnd.setVisibility(View.VISIBLE);
			btnRefresh.setEnabled(true);
			//adapter.notifyDataSetChanged();
		}
	}

}
