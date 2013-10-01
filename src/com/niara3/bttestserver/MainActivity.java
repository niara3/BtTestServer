package com.niara3.bttestserver;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements View.OnClickListener {

	private BtStateListener mBtStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mBtStateListener == null) {
			mBtStateListener = new BtStateListener();
			mBtStateListener.start(this);
		}

		ToggleButton tbtnBluetooth = (ToggleButton) findViewById(R.id.tbtnBluetooth);
		if (tbtnBluetooth != null) {
			tbtnBluetooth.setOnClickListener(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mBtStateListener != null) {
			mBtStateListener.stop();
			mBtStateListener = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.tbtnBluetooth:
			{
				ToggleButton tbtnBluetooth = (ToggleButton) findViewById(R.id.tbtnBluetooth);
				if (tbtnBluetooth != null) {
					tbtnBluetooth.setEnabled(false);
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
			break;
		}
	}

}
