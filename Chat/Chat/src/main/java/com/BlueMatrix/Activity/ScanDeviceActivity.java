package com.BlueMatrix.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.ble.RBLService;

public class ScanDeviceActivity extends Activity {
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 3000;
	private Dialog mDialog;
	public static List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
	public static ScanDeviceActivity instance = null;
	private String PreviewMacAdress = null;	//存储上一次连接的蓝牙MAC地址
	Timer mTimer;

	//private RBLService mBluetoothLeService;
	public final static String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";
	public final static String EXTRA_DEVICE_NAME = "EXTRA_DEVICE_NAME";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		Button btn = (Button)findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scanLeDevice();

				showRoundProcessDialog(ScanDeviceActivity.this, R.layout.loading_process_dialog_anim);

				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						Intent deviceListIntent = new Intent(getApplicationContext(),
								DeviceActivity.class);
						startActivity(deviceListIntent);
						mDialog.dismiss();
					}
				}, SCAN_PERIOD);
			}
		});

		//Intent gattServiceIntent = new Intent(this, RBLService.class);
		//bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


		showRoundProcessDialog(ScanDeviceActivity.this, R.layout.loading_process_dialog_anim);


		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Intent deviceListIntent = new Intent(getApplicationContext(),
						DeviceActivity.class);
				startActivity(deviceListIntent);
				mDialog.dismiss();
			}
		}, SCAN_PERIOD);

		scanLeDevice();

		instance = this;

		//步骤1：创建一个SharedPreferences接口对象
		SharedPreferences read = getSharedPreferences("lock", MODE_WORLD_READABLE);
		//步骤2：获取文件中的值
		String value = read.getString("code", "");
		if(value != "")
		{
			PreviewMacAdress = value;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDevices.clear();

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

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);
		mDialog.show();
		mDialog.setContentView(layout);
	}

	private void scanLeDevice() {
		new Thread() {

			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);

				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

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
						if (mDevices.indexOf(device) == -1) {
							mDevices.add(device);
							//如果找到上次连接过的设备，直接连接
							if(PreviewMacAdress.compareTo(device.getAddress()) == 0)
							{
								mTimer.cancel();
								Intent intent = new Intent(ScanDeviceActivity.this, MainMenuActivity.class);
								intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
								intent.putExtra(EXTRA_DEVICE_NAME, device.getName());
								startActivity(intent);
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

}
