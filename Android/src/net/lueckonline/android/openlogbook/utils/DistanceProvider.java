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
package net.lueckonline.android.openlogbook.utils;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.location.LocationManager;

/**
 * @author thuri
 *
 */
public class DistanceProvider extends LocationAdapter {
	
	private float distance = 0.0f;
	
	private boolean started = false;
	
	private Location lastLocation = null;
	
	private final LocationManager locationMgr;
	
	private List<IDistanceChangedListener> listeners = new ArrayList<IDistanceChangedListener>();
	
	public DistanceProvider(LocationManager locMgr){
		this.locationMgr = locMgr;
	}
	
	/**
	 * Method starts the distance calculation 
	 */
	public void Start(){
		started = true;
		this.distance = 0.0f;
		locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,	this);
	}
	
	/**
	 * Method stops distance calculation
	 */
	public void Stop(){
		started = false;
		locationMgr.removeUpdates(this);
	}
	
	/**
	 * Method resets the distance already determined
	 */
	public void Reset(){
		this.distance = 0.0f;
	}
	
	/**
	 * Add a Listener which will be informed if the sum changed
	 */
	public void addSumChangedListener(IDistanceChangedListener l){
		this.listeners.add(l);
	}
	
	private void OnDistanceChanged(){
		for (IDistanceChangedListener l : this.listeners) 
			l.DistanceChanged(this.distance);
	}
	
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location l) {
		if(lastLocation != null) 
			distance += l.distanceTo(lastLocation);
		
		lastLocation = l;
		
		OnDistanceChanged();
	}

	/* Getters and setters*/
	
	public float getDistance() {
		return distance;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isEnabled() {
		return locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
