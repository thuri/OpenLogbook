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

import java.text.DateFormat;
import java.util.Date;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.utils.DistanceProvider;
import net.lueckonline.android.openlogbook.utils.IDistanceChangedListener;
import android.content.Context;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;


/**
 * @author thuri
 *
 */
public class TripCaptureViewModel implements IDistanceChangedListener{
	
	public final ArrayListObservable<Person> drivers = new ArrayListObservable<Person>(Person.class);
	public final ArrayListObservable<Car> cars = new ArrayListObservable<Car>(Car.class);
	public final FloatObservable distance = new FloatObservable(0f);
	public final Observable<String> start = new Observable<String>(String.class);
	public final Observable<String> stop = new Observable<String>(String.class);
	
	public final Command cmdToggleGPS = new Command(){
		@Override
		public void Invoke(View vButton, Object... arg1) {
			Button btn = (Button)vButton;
			Date now = new Date();
			
			if(distanceProvider.isStarted()){
				distanceProvider.Stop();
				btn.setText(R.string.StartGPS);
				setStop(now);
				save();
			}
			else {
				distanceProvider.Start();
				btn.setText(R.string.StopGPS);
				setStart(now);
			}
		}
	};
	
	public final Command carSelected = new Command(){
		@Override
		public void Invoke(View spinner, Object... arg1) {
			Integer carIdx = (Integer)arg1[1];
			log.setCar(cars.get(carIdx));
		}
	};
	
	public final Command driverSelected = new Command(){
		@Override
		public void Invoke(View spinner, Object... arg1) {
			Integer driverIdx = (Integer) arg1[1];
			log.setDriver(drivers.get(driverIdx));
		}
	};
	
	private DistanceProvider distanceProvider;

	private ILogbookRepository repository;
	
	private final DateFormat dateFormat;
	private final DateFormat timeFormat;
	
	private Log log = new Log();
	
	public TripCaptureViewModel(Context context, LocationManager locMgr){
		
		dateFormat = android.text.format.DateFormat.getLongDateFormat(context);
		timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		
		distanceProvider = new DistanceProvider(locMgr);
		distanceProvider.addSumChangedListener(this);
		
		repository = RepositoryFactory.getInstance(context);
		
		this.cars.setArray(repository.getCars().toArray(new Car[0]));
		
		this.drivers.setArray(repository.getDrivers().toArray(new Person[0]));
		
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.utils.IDistanceChangedListener#DistanceChanged(float)
	 */
	@Override
	public void DistanceChanged(float distance) {
		this.distance.set(distance);
	}
	

	private void save() {
		this.log.setDistance(this.distance.get());
		repository.addLog(log);
	}
	
	private void setStart(Date starttime){
		String strNow = dateFormat.format(starttime) + " " + timeFormat.format(starttime);
		this.start.set(strNow);
		this.log.setStart(starttime);
	}
	
	private void setStop(Date stoptime){
		String strNow = dateFormat.format(stoptime) + " " + timeFormat.format(stoptime);
		this.stop.set(strNow);
		this.log.setStop(stoptime);
	}

}
