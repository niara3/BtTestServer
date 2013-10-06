package com.niara3.bttestserver;

import java.io.IOException;
import java.util.UUID;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

	public static final int PACKAGE_NAME_LENGTH = MainService.class.getPackage().getName().length();

	private static final int ID_NOTOFICATION = 10;
	private static final int STATE_UNKNOWN = -1;

	public static final String SERVER_NAME = "BtTestServer";
	public static final String SERVER_UUID = "6bf7eae0-2e5e-11e3-aa6e-0800200c9a66";

	private String TAG = MainService.this.toString().substring(PACKAGE_NAME_LENGTH);

	private BroadcastReceiver mReceiver;
	private BluetoothAdapter mBta;
	private ServerTesk mServerTask;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		try {
			mBta = BluetoothAdapter.getDefaultAdapter();
			startForegroundNotification();
			startReceiver();
			startServer();
		} catch (Exception e) {
			e.printStackTrace();
			stopSelf();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)  {
		Log.d(TAG, "onStartCommand intent = " + intent + " / flags = " + flags + " / startId = " + startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		try {
			stopServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			stopReceiver();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			stopForegroundNotification();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mBta = null;
	}

	private void startForegroundNotification() {
		Log.d(TAG, "startForegroundNotification");
		Notification notification = new Notification();
		startForeground(ID_NOTOFICATION, notification);
	}

	private void stopForegroundNotification() {
		Log.d(TAG, "stopForegroundNotification");
		stopForeground(true);
	}

	private synchronized void startReceiver() {
		Log.d(TAG, "startReceiver");
		if (mReceiver != null) {
			return;
		}
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "onReceive");
				if (mReceiver != this) {
					return;
				}
				if (intent == null) {
					return;
				}
				String action = intent.getAction();
				if (action == null) {
					return;
				}
				if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, STATE_UNKNOWN);
					switch (btState) {
					case BluetoothAdapter.STATE_ON:
						startServer();
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						stopServer();
						break;
					default:
						break;
					}
					return;
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, filter);
	}

	private synchronized void stopReceiver() {
		Log.d(TAG, "stopReceiver");
		if (mReceiver == null) {
			return;
		}
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}


	private void startServer() {
		if (mServerTask != null) {
			return;
		}
		if (mBta.getState() != BluetoothAdapter.STATE_ON) {
			return;
		}
		mServerTask = new ServerTesk();
		mServerTask.execute();
	}

	private class ServerTesk extends AsyncTask<Object, Integer, Integer> {
		private BluetoothServerSocket mBtServer;
		private BluetoothSocket mBtSocket;
		private volatile boolean mStop;

		public boolean cancel() {
			mStop = true;
			close();
			return cancel(false);
		}

		@Override
		protected Integer doInBackground(Object... params) {
			BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
			if (bta == null) {
				return null;
			}

			if (mStop) {
				return null;
			}

			try {
				Log.d(TAG, "listening server: name = " + SERVER_NAME + " / uuid = " + SERVER_UUID);
				mBtServer = bta.listenUsingInsecureRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));

				Log.d(TAG, "listened and accepting " + mBtServer.toString());
				mBtSocket = mBtServer.accept();	// blocking
				Log.d(TAG, "accepted " + mBtSocket.toString());

				Log.d(TAG, "closing " + mBtServer.toString());
				mBtServer.close();
				Log.d(TAG, "closed " + mBtServer.toString());
				mBtServer = null;
				if (mStop) {
					return null;
				}
			} catch (IOException e) {
				//e.printStackTrace();
			} finally {
				close();
				mBtSocket = null;
				mBtServer = null;
			}

			return null;
		}

		private void close() {
			BluetoothSocket btSocket = mBtSocket;
			if (btSocket != null) {
				try {
					Log.d(TAG, "closing " + btSocket.toString());
					btSocket.close();
					Log.d(TAG, "closed " + btSocket.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			BluetoothServerSocket btServer = mBtServer;
			if (btServer != null) {
				try {
					Log.d(TAG, "closing " + btServer.toString());
					btServer.close();
					Log.d(TAG, "closed " + btServer.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void stopServer() {
		if (mServerTask == null) {
			return;
		}
		mServerTask.cancel();
		mServerTask = null;
	}
}
