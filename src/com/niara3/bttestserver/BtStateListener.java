package com.niara3.bttestserver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ToggleButton;

public class BtStateListener {
	private Activity mActivity;
	private BroadcastReceiver mReceiver;

	public void start(Activity activity) {
		if (activity == null) {
			return;
		}

		Intent intent = null;
		synchronized (this) {
			if (mActivity != null) {
				return;
			}

			mActivity = activity;
			mReceiver = new BtStateBCReceiver(this);
			IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			intent = mActivity.registerReceiver(mReceiver, filter);
		}

		updateToggleButton(intent);
	}

	public void updateToggleButton(Intent intent) {
		int btState = -1;
		if (intent != null) {
			btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
			System.out.println("btState : " + btState);
		}
		if (btState == -1) {
			BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
			if (bta != null) {
				btState = bta.getState();
			}
		}
		if (btState == -1) {
			return;
		}
		ToggleButton tbtnBluetooth = (ToggleButton) mActivity.findViewById(R.id.tbtnBluetooth);
		if (tbtnBluetooth == null) {
			return;
		}
		switch (btState) {
		case BluetoothAdapter.STATE_ON:				// 12
			tbtnBluetooth.setChecked(true);
			tbtnBluetooth.setEnabled(true);
			break;
		case BluetoothAdapter.STATE_OFF:			// 10
			tbtnBluetooth.setChecked(false);
			tbtnBluetooth.setEnabled(true);
			break;
		case BluetoothAdapter.STATE_TURNING_ON:		// 11
			tbtnBluetooth.setChecked(true);
			tbtnBluetooth.setEnabled(false);
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:	// 13
			tbtnBluetooth.setChecked(false);
			tbtnBluetooth.setEnabled(false);
			break;
		}
	}

	public void stop() {
		synchronized (this) {
			if (mActivity == null) {
				return;
			}

			mActivity.unregisterReceiver(mReceiver);
			mActivity = null;
			mReceiver = null;
		}
	}
}
