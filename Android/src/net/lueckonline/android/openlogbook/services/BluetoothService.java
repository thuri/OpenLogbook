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

import java.util.Date;
import java.util.Iterator;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Device;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.utils.DistanceProvider;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * 
 * @author thuri
 * 
 */
public class BluetoothService extends IntentService {

	private BluetoothAdapter btAdapter;
	private DistanceProvider distanceProvider;
	private Device currentTrigger;
	private ILogbookRepository repository;
	private Log log;

	public BluetoothService() {
		super("BluetoothChecker");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locMgr == null)
			distanceProvider = null;
		else
			distanceProvider = new DistanceProvider(locMgr);

		repository = RepositoryFactory.getInstance(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (btAdapter == null || distanceProvider == null)
			return START_NOT_STICKY;

		return START_STICKY;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		// if the btAdapter is null and bluetooth is not supported we just
		// return. therefore no new Intents are added for this service and the
		// base
		// implementation will stop the service
		if (btAdapter == null || distanceProvider == null)
			return;

		long waitTime;

		// if bluetooth is not enabled we just wait a little longer until the
		// next invocation
		if (!btAdapter.isEnabled()) {
			waitTime = 10 * 60 * 1000;
			// send Stats bar notification so the user can enable Bluetooth
			sendBluetoothDisabledNotification();
		}
		// are we already capturing the distance?
		else if (distanceProvider.isStarted()) {
			waitTime = 60 * 1000;

			if (!isDevicePresent(currentTrigger)) {
				currentTrigger = null;
				stopLogging(this.log);
			}
		} else {
			waitTime = 5 * 1000;

			Device trigger = getPresentTriggerDevice();
			
			if (trigger != null) {
				currentTrigger = trigger;
				this.log = startLogging();
			}

			// TODO if in commuter mode we can start GPS, wait for the fix stop
			// it again. Later if trigger device isn't present any more
			// we can again start GPS take the fix corrdinate, calculate shortes
			// distance (google maps? openstreetmap?)
			// and put that into the database instead of GPS tracked distance
			// we probably don't need to use GPS but network location could be
			// enough. For example on the first trip
			// we take the network location, show the user possible addresses
			// and similar and let her choose which one. (should to the same for
			// the starting point)

		}

		// Send a new Intent to this service so it won't be stopped
		this.startService(new Intent(this, BluetoothService.class));

		// the next intent is sent and will be delivered. BUt till then we'll
		// wait some time
		synchronized (this) {
			try {
				wait(waitTime);
			} catch (InterruptedException e) {

			}
		}
	}
	
	/**
	 * 
	 */
	private Log startLogging() {
		Log log = new Log();
		log.setStart(new Date());

		return log;
	}


	/**
	 * @return
	 * 
	 */
	private void stopLogging(Log log) {
		
		//what car and what driver to set?
		//most likely an app installation will only be used by one user because it's his smartphone the app is running on
		//TODO: The idea of the bluetooth service is that if the phone is near a car the logging can be automated
		//this means the car is the same as the device. So we probably can connect a car and a device in the db.
		//on the other hand what's with bluetooth headsets? They are mobile and can travel from car to car. So probably the 
		//connection between the device and the car must not be mandatory.
		//So: try to infer the car and the driver but if it's not possible to do so: add a notification which Sends an intent to the app filling out 
		//all known values and  let the user choose the rest!
		Car car = currentTrigger.getCar();
		Person driver = repository.getBluetoothUser();
		
		log.setCar(car);
		log.setDriver(driver);
		log.setDistance(distanceProvider.getDistance());
		log.setStop(new Date());
		
		if(car == null || driver == null)
			sendLogNotification(log);
		else {
			try {
				repository.add(log);
			} catch (DataAccessException e) {
				//TODO: Inform the user e.g. send another notification
				//probably save the log somewhere else and try to add it later
			}
		}
	}
	
	/**
	 * checks wether the passed {@link Device} is (still) present 
	 * @param device
	 * @return
	 */
	private boolean isDevicePresent(Device device) {
		
		Iterator<BluetoothDevice> devices = btAdapter.getBondedDevices().iterator();
		
		final String deviceName = device.getName();
		
		while(devices.hasNext()){
			BluetoothDevice dev = devices.next();
			if(deviceName.equals(dev.getName()))
				return true;
		}
			
		return false;
	}

	/**
	 * looks for the first known device in the which is present to the bluetooth adapter and returns it
	 * @return the first known device that is present, null if no device found
	 * @see #isDevicePresent(Device)
	 */
	private Device getPresentTriggerDevice() {
		Iterator<Device> devices = repository.getDevices().iterator();
		
		while(devices.hasNext()){
			Device d = devices.next();
			if(isDevicePresent(d)){
				return d;
			}
		}
		
		return null;
	}

	
	/**
	 * Send's the log to the application via intent so the user is able to add missing values
	 * @param incompleteLog a log object with missing values (car or driver for example)
	 */
	private void sendLogNotification(Log incompleteLog) {
		// TODO Auto-generated method stub
	}

	
	private void sendBluetoothDisabledNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notManager = (NotificationManager) getSystemService(ns);

		int icon = android.R.drawable.ic_menu_preferences;
		CharSequence tickerText = getString(R.string.BluetoothDisabled);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = getString(R.string.BluetoothDisabled);
		CharSequence contentText = getString(R.string.BluetoothDisabled);
		Intent notificationIntent = new Intent(
				Settings.ACTION_BLUETOOTH_SETTINGS);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		notManager.notify(1, notification);
	}

}
