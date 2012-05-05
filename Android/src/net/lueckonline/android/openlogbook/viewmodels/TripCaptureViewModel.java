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
import net.lueckonline.android.openlogbook.dataaccess.DbHelper;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.utils.DistanceProvider;
import net.lueckonline.android.openlogbook.utils.IDistanceChangedListener;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;

/**
 * @author thuri
 *
 */
public class TripCaptureViewModel implements IDistanceChangedListener{
	
	private Context context;
	
	public ArrayListObservable<String> drivers = new ArrayListObservable<String>(String.class);
	
	public ArrayListObservable<String> cars = new ArrayListObservable<String>(String.class);
	
	public FloatObservable distance = new FloatObservable();
	
	public Observable<String> start;
	
	public Observable<String> stop;
	
	public Command cmdToggleGPS = new Command(){
		@Override
		public void Invoke(View vButton, Object... arg1) {
			Button btn = (Button)vButton;
			DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(context);
			DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
			Date now = new Date();
			String strNow = dateFormat.format(now) + " " + timeFormat.format(now);
			if(distanceProvider.isStarted()){
				distanceProvider.Stop();
				btn.setText(R.string.StartGPS);
				stop.set(strNow);
				save();
			}
			else {
				distanceProvider.Start();
				btn.setText(R.string.StopGPS);
				start.set(strNow);
			}
		}
	};
	
	private DistanceProvider distanceProvider;

	private ILogbookRepository repository;
	
	public TripCaptureViewModel(Context context, LocationManager locMgr){
		
		distanceProvider = new DistanceProvider(locMgr);
		distanceProvider.addSumChangedListener(this);
		
		start = new Observable<String>(String.class);
		stop = new Observable<String>(String.class);
		
		repository = RepositoryFactory.getInstance(context);
		
		for(Car car : repository.getCars())
			this.cars.add(car.getLicensePlate());
		
		for(Person driver : repository.getDrivers())
			this.drivers.add(driver.getName());
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.utils.IDistanceChangedListener#DistanceChanged(float)
	 */
	@Override
	public void DistanceChanged(float distance) {
		this.distance.set(distance);
	}
	

	private void save() {
		
	}

}
