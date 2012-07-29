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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.utils.DistanceProvider;
import net.lueckonline.android.openlogbook.utils.IDistanceChangedListener;
import net.lueckonline.android.openlogbook.viewmodels.createlog.CreateLogDelegate;
import android.content.Context;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


/**
 * @author thuri
 *
 */
public class LogCaptureViewModel implements IDistanceChangedListener{

	/*
	 * Fields for View-Binding
	 */
	
	/**
	 * bindable field referring to the data for the spinner "spinnerDrivers". 
	 * The Person class overrides the toString() Method, so that the spinner item text can be bound to "."
	 * 
	 * @see Person.toString()
	 * @see R.id.spinnerDrivers
	 */
	public final ArrayListObservable<Person> drivers = new ArrayListObservable<Person>(Person.class);
	
	/**
	 * bindable field referring to the data for the spinner "spinnerCars".
	 * The Car class overrides the toString() Method, so that the spinner item text can be bound to "."
	 * 
	 * @see Car.toString()
	 * @see R.id.spinnerCars
	 */
	public final ArrayListObservable<Car> cars = new ArrayListObservable<Car>(Car.class);
	
	/**
	 * bindable field referring to the data for the readonly edittext "tbDistance"
	 * 
	 * @see R.id.tbDistance
	 */
	public final FloatObservable distance = new FloatObservable(0.0f);
	
	/**
	 * bindable field referring to the data for the readonly edittext "tbStarttime"
	 * 
	 * @see R.id.tbStarttime
	 */
	public final Observable<String> start = new Observable<String>(String.class);
	
	/**
	 * bindable field referring to the data for the readonly edittext "tbStoptime"
	 * 
	 * @see R.id.tbStoptime
	 */
	public final Observable<String> stop = new Observable<String>(String.class);

	/*
	 * Utility Fields
	 */
	
	/**
	 * DateFormat object to render a string for the date part of a Date object according to android settings
	 * depends on the context the application is running in. Must therefor be created within the constructor 
	 */
	private final DateFormat dateFormat;
	
	/**
	 * DateFormat object to render a string for the time part of a Date object according to android settings
	 * depends on the context the application is running in. Must therefor be created within the constructor
	 */
	private final DateFormat timeFormat;
	
	/*
	 * Business data
	 */
	
	/**
	 * the log to track
	 */
	private Log log = new Log();
	
	/**
	 * the provider for the movement tracking system
	 */
	private DistanceProvider distanceProvider;
	
	/**
	 * Constructor 
	 * 
	 * @param context The Android Context the application is executed in
	 * @param locMgr  The LocationManager Object used to measure the distance of the trip
	 */
	public LogCaptureViewModel(Context context, LocationManager locMgr){
		
		//initialize the dateformatting objects
		dateFormat = android.text.format.DateFormat.getLongDateFormat(context);
		timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		
		//create a distanceProvider Object and register this object as listener for updates
		distanceProvider = new DistanceProvider(locMgr);
		distanceProvider.addSumChangedListener(this);

	}

	/**
	 * Command bound to the onClick Event of btnToggleGPS
	 * 
	 * If not already started the Event starts the Tracking
	 * otherwise tracking is stopped and the tracked log/trip is saved to the repository
	 * 
	 * @see R.id.btnToggleGPS
	 */
	public final Command cmdToggleGPS = new Command(){
		@Override
		public void Invoke(View vButton, Object... arg1) {
			btnToggleGPS_Clicked((Button)vButton);
		}
	};
	
	/**
	 * Command-Proxy bound to the onItemSelected Event of the spinnerCars Spinner
	 * 
	 * sets the currently selected Car on the currently tracked log
	 * 
	 *  @see R.id.spinnerCars
	 */
	public final Command carSelected = new Command(){
		@Override
		public void Invoke(View spinner, Object... arg1) {
			Integer carIdx = (Integer)arg1[1];
			spinnerCars_SelectedIdxChanged((Spinner)spinner, carIdx);
		}
	};
	
	/**
	 * Command-Proxy bound to the onItemSelected Event of the spinnerDrivers Spinner
	 * 
	 * sets the currently selected Driver on the currently selected log
	 * 
	 *  @see R.id.spinnerDrivers
	 */
	public final Command driverSelected = new Command(){
		@Override
		public void Invoke(View spinner, Object... arg1) {
			Integer driverIdx = (Integer) arg1[1];
			spinnerDrivers_SelectedIdxChanged((Spinner) spinner, driverIdx);
		}
	};

	private final List<CreateLogDelegate> delegates = new ArrayList<CreateLogDelegate>();

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.utils.IDistanceChangedListener#DistanceChanged(float)
	 */
	@Override
	public void DistanceChanged(float distance) {
		this.distance.set(distance);
	}
	

	/**
	 * Helper Method to finish setting necessary values on the tracked log 
	 * and pass it to the repository
	 */
	private void saveLog() {
		this.log.setDistance(this.distance.get());
		this.raiseCreateLog(this.log);
	}
	

	/**
	 * Stops the GPS Tracking
	 * Resets the Label of the Button to "Start GPS" or according localization
	 * sets the stop time on the tracked log and saves the log in the repository
	 * 
	 * @see saveLog
	 * 
	 * @param stoptime the date to use for the stop time of the tracked log
	 */
	private void stopLogging(Date stoptime) {
		distanceProvider.Stop();
		
		String strNow = dateFormat.format(stoptime) + " " + timeFormat.format(stoptime);
		this.stop.set(strNow);
		
		this.log.setStop(stoptime);
		
		saveLog();
	}

	/**
	 * Starts the GPS Tracking
	 * Resets the bound input elements (distance, start, stop)
	 * sets the passed Date as the start time of the tracked log
	 * 
	 * @param starttime the date to set as start time of new log to track
	 */
	private void startLogging(Date starttime) {
		
		Log newLog = new Log();
		newLog.setCar(this.log.getCar());
		newLog.setDriver(this.log.getDriver());
		this.log = newLog;
		this.distance.set(0.0f);
		this.stop.set("");
		
		String strNow = dateFormat.format(starttime) + " " + timeFormat.format(starttime);
		this.start.set(strNow);
		
		this.log.setStart(starttime);
		
		distanceProvider.Start();
	}

	/**
	 * real logic for the clicked event of the toggle GPS button
	 * 
	 * @param the toggle GPS button
	 * @see LogCaptureViewModel.cmdToggleGPS
	 */
	private void btnToggleGPS_Clicked(Button button) {
		Date now = new Date();
		
		if(distanceProvider.isStarted()){
			stopLogging(now);
			button.setText(R.string.StartGPS);
		}
		else {
			startLogging(now);
			button.setText(R.string.StopGPS);
		}
	}

	/**
	 * real logic for the selectedItem changed event of the spinner for the spinner spinnerCars 
	 * 
	 * @param index of the selected car in the array bound to the view
	 * @see LogCaptureViewModel.cars
	 */
	private void spinnerCars_SelectedIdxChanged(Spinner spinner, Integer newIdx) {
		log.setCar(cars.get(newIdx));
	}
	
	/**
	 * real logic for the selectedItem changed event of the spinner for the spinner spinnerDrivers
	 * 
	 * @param index of the selected car in the array bound to the view
	 * @see LogCaptureViewModel.drivers
	 */
	private void spinnerDrivers_SelectedIdxChanged(Spinner spinner, Integer newIdx) {
		log.setDriver(drivers.get(newIdx));
	}

	/**
	 * @param delegate
	 */
	public void addCreateLogDelegate(CreateLogDelegate delegate) {
		this.delegates.add(delegate);
	}
	
	/**
	 * @param newLog
	 */
	private void raiseCreateLog(Log newLog) {
		for(CreateLogDelegate d : this.delegates)
			d.AddLog(newLog);
	}
}
