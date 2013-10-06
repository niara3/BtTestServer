package com.niara3.bttestserver;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements View.OnClickListener {

	private String TAG = MainActivity.this.toString().substring(MainService.PACKAGE_NAME_LENGTH);

	private BtStateListener mBtStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int[] ids = {
				R.id.tbtnService,
				R.id.tbtnBluetooth,
		};
		for (int id : ids) {
			View view = findViewById(id);
			if (view != null) {
				view.setOnClickListener(this);
			}
		}

		// R.id.tbtnServiceの初期状態反映ができてない
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		if (mBtStateListener == null) {
			mBtStateListener = new BtStateListener();
			mBtStateListener.start(this);
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();

		if (mBtStateListener != null) {
			mBtStateListener.stop();
			mBtStateListener = null;
		}
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick");
		if (v == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.tbtnService:
			toggleService();
			break;
		case R.id.tbtnBluetooth:
			toggleBluetooth();
			break;
		}
	}

	private void toggleService() {
		Log.d(TAG, "toggleService");
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.tbtnService);
		if (toggleButton != null) {
			Log.d(TAG, "ToggleButton = " + toggleButton.isChecked());
			//toggleButton.setEnabled(false);
			
			Intent serviceIntent = new Intent(this, MainService.class);
			if (toggleButton.isChecked()) {
				startService(serviceIntent);
			} else {
				stopService(serviceIntent);
			}
		}
	}

	private void toggleBluetooth() {
		Log.d(TAG, "toggleBluetooth");
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.tbtnBluetooth);
		if (toggleButton != null) {
			toggleButton.setEnabled(false);
		}
		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
		if (bta != null) {
			switch (bta.getState()) {
			case BluetoothAdapter.STATE_OFF:
				bta.enable();
				break;
			case BluetoothAdapter.STATE_ON:
				bta.disable();
				break;
			}
		}
	}

}
