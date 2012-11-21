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

import java.io.Serializable;
import java.util.Date;

import net.lueckonline.android.openlogbook.activities.OpenLogbook;
import net.lueckonline.android.openlogbook.model.Log;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author thuri
 *
 */
public class DistanceService extends Service implements LocationListener {

	public static final String LOG_EXTRA = "net.lueckonline.android.openlogbook.START_LOG";
	
	private Log log = null;
	private LocationManager locMgr = null;
	private Location lastLocation = null;

	/**
	 * @return
	 */
	private Intent buildIntent(String action) {
		Intent intent = new Intent(this, OpenLogbook.class);
		intent.setAction(action);
		intent.putExtra(LOG_EXTRA, this.log);
		return intent;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		//float accuracy = location.getAccuracy();
		
		//TODO get a threshold of a good accuracy and only use the changed location if accuracy is good enough
		//if(accuracy <= 100.0f){
		
			float d = log.getDistance();
			
			if(lastLocation != null) 
				d += location.distanceTo(lastLocation);
			else
				d = 0.0f;
			
			log.setDistance(d);
			
			lastLocation = location;
			
			//Intent intent = buildIntent(OpenLogbook.ACTION_DISTANCE_CHANGED);
			Intent intent = new Intent(OpenLogbook.ACTION_DISTANCE_CHANGED);
			intent.putExtra(LOG_EXTRA, this.log);
			
			sendBroadcast(intent);

		//}
	}

	@Override
	public void onCreate() {
		
		super.onCreate();
		
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int onStartCommand = super.onStartCommand(intent, flags, startId);
		
		Serializable s = intent.getSerializableExtra(LOG_EXTRA);
		
		if(s instanceof Log){
			
			this.log = (Log)s;
			
			this.log.setStart(new Date());
		 
			//TODO: Evaluate which Provider to use based on settings (commuter vs. fieldstaff), connected bluetooth device and such!
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 500, this);
			//locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		}
		else {
			this.stopSelf();
		}
		
		return onStartCommand;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		locMgr.removeUpdates(this);
		
		this.log.setStop(new Date());

		Intent intent = buildIntent(OpenLogbook.ACTION_STOP_LOG);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		this.startActivity(intent);
		
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
