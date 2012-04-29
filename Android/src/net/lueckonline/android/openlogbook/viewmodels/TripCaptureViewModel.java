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
package net.lueckonline.android.openlogbook.viewmodels;

import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.StringObservable;

import java.util.Date;

import net.lueckonline.android.openlogbook.utils.DistanceProvider;
import net.lueckonline.android.openlogbook.utils.IDistanceChangedListener;
import android.location.LocationManager;
import android.view.View;

/**
 * @author thuri
 *
 */
public class TripCaptureViewModel implements IDistanceChangedListener{
	
	public ArrayListObservable<String> drivers = new ArrayListObservable<String>(String.class);
	public StringObservable driver;
	
	public ArrayListObservable<String> cars = new ArrayListObservable<String>(String.class);
	public StringObservable car;
	
	public FloatObservable distance;
	
	public Observable<Date> start;
	
	public Observable<Date> stop;
	
	public final Command cmdToggleGPS;
	
	private DistanceProvider distanceProvider;
	
	public TripCaptureViewModel(LocationManager locMgr){
		
		distanceProvider = new DistanceProvider(locMgr);
		
		this.cmdToggleGPS = new Command(){
			@Override
			public void Invoke(View arg0, Object... arg1) {
				if(distanceProvider.isStarted())
					distanceProvider.Stop();
				else
					distanceProvider.Start();
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.utils.IDistanceChangedListener#DistanceChanged(float)
	 */
	@Override
	public void DistanceChanged(float distance) {
		this.distance.set(distance);
	}
}
