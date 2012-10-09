/**
 *  OpenLogbook - App logging driven distances and times
 *  Copyright (C) 2012 Michael Lück
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package net.lueckonline.android.openlogbook.services;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Basically this service exists because it makes the BroadcastReceiver
 * implemented by {@link BluetoothChangeDetector} available at all times. A
 * {@link BroadcastReceiver} may not be present all the time because the process
 * it runs in may be stopped by the systems if there are any resource shortages.
 * the service is build to run all the time and is able to restart (register) the 
 * the receiver again.
 * 
 * @author thuri
 * 
 */
public class BluetoothService extends IntentService {

	private static final IntentFilter DETECTFILTER;

	static {
		DETECTFILTER = new IntentFilter();
		DETECTFILTER.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		DETECTFILTER.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		DETECTFILTER.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	}

	private BluetoothChangeDetector changeDetector = null;

	public BluetoothService() {
		super("BluetoothChecker");
	}

	/**
	 * this method creates and registers the Detector {@link #changeDetector} so
	 * Messages are received from the system. Counterpart to
	 * {@link #stopDetector()}. By checking {@link #changeDetector} for null
	 * shows whether the detector is started or not
	 * 
	 * @see #stopDetector()
	 */
	private void startDetector() {
		if (!isDetectorStarted()) {
			changeDetector = new BluetoothChangeDetector();
			registerReceiver(changeDetector, DETECTFILTER);
		}
	}

	/**
	 * this method unregisters the Detector {@link #changeDetector} so no
	 * Messages are received from the system. Counterpart to
	 * {@link #stopDetector()}. By checking {@link #changeDetector} for null
	 * shows whether the detector is started or not
	 * 
	 * @see #startDetector()
	 */
	private void stopDetector() {
		if (isDetectorStarted()) {
			unregisterReceiver(changeDetector);
			changeDetector = null;
		}
	}

	/**
	 * @return true if the Detector {@link #changeDetector} is already started
	 */
	private boolean isDetectorStarted() {
		return changeDetector != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent arg0) {

		// check that the changeDetector is still valid!
		// probably the service was halted or something. Just in case.
		if (!isDetectorStarted())
			startDetector();

		synchronized (this) {
			try {
				wait(60000);
			} catch (InterruptedException e) {
				// Ok so we're interrupted while waiting. Go on
			}
		}

		startService(new Intent(this, BluetoothService.class));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);

//		if (btAdapter == null || distanceProvider == null)
//			return START_NOT_STICKY;

		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		startDetector();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopDetector();
	}

}
