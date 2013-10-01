package com.niara3.bttestserver;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BtStateBCReceiver extends BroadcastReceiver {

	private WeakReference<BtStateListener> mWeak;

	public BtStateBCReceiver(BtStateListener listener) {
		mWeak = new WeakReference<BtStateListener>(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (mWeak == null) {
			return;
		}
		BtStateListener listener = mWeak.get();
		if (listener == null) {
			return;
		}
		listener.updateToggleButton(intent);
	}

}
