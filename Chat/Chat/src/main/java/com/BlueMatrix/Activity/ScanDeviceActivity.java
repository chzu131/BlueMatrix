package com.BlueMatrix.Activity;

import java.util.ArrayList;
import java.util.List;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.BlueMatrix.tools.Memory;


public class ScanDeviceActivity extends Activity {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		DeviceName = getResources().getString(R.string.deviceName);

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

		Memory memory = new Memory(this);
		PreviewMacAdress = memory.GetLastMacAddress();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mDevices != null)
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
							//过滤掉其它设备
							if ( (device.getName()!=null) &&
									(device.getName().compareTo(DeviceName) == 0)) {
								mDevices.add(device);
								//如果找到上次连接过的设备，直接连接
								if (PreviewMacAdress != null) {
									if (PreviewMacAdress.compareTo(device.getAddress()) == 0) {
										mTimer.cancel();
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

}
