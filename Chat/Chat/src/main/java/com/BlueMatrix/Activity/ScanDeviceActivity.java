package com.BlueMatrix.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.BlueMatrix.tools.Memory;


public class ScanDeviceActivity extends Activity
		implements AdapterView.OnItemClickListener,OnClickListener{
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 2000;
	private Dialog mDialog;
	public static List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();

	private String PreviewMacAdress = null;	//存储上一次连接的蓝牙MAC地址
	Timer mTimer;
	String DeviceName;	//设备名

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
	boolean mFirstLauchApp = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main2);

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

		scanLeDevice();

		Memory memory = new Memory(this);
		PreviewMacAdress = memory.GetLastMacAddress();

		Looper looper = Looper.myLooper();
		messageHandler = new MessageHandler(looper);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.btnRefresh: {
				mDevices.clear();
				listItems.clear();
				adapter.notifyDataSetChanged();
				scanLeDevice();
				break;
			}
			case R.id.connect_button: {
				//mListViewIndex
				if(mListViewIndex != -1) {
					HashMap<String, String> hashMap = (HashMap<String, String>) listItems.get(mListViewIndex);
					String addr = hashMap.get(DEVICE_ADDRESS);
					String name = hashMap.get(DEVICE_NAME);

					Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
					intent.putExtra(EXTRA_DEVICE_ADDRESS, addr);
					intent.putExtra(EXTRA_DEVICE_NAME, name);
					startActivity(intent);
					//finish();
					break;
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if(mFirstLauchApp)
//		{
//			mFirstLauchApp = false;
//			Intent intent = new Intent();
//			intent.setClass(getApplicationContext(), WelcomeActivity.class);
//			startActivity(intent);
//		}
		if(mDevices != null) {
			mListViewIndex = -1;
			listItems.clear();
			mDevices.clear();
		}

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
										Intent intent = new Intent(ScanDeviceActivity.this, MainMenuActivity.class);
										intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
										intent.putExtra(EXTRA_DEVICE_NAME, device.getName());
										startActivity(intent);
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

		System.exit(0);
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
