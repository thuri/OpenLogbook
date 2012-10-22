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
import gueei.binding.observables.IntegerObservable;

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

	
	public final IntegerObservable selectedCar = new IntegerObservable(1);
	
	public final IntegerObservable selectedDriver = new IntegerObservable(1);
	/*
	 * Utility Fields
	 */
	
	/**
	 * DateFormat object to render a string for the date part of a Date object according to android settings
	 * depends on the context the application is running in. To be injected 
	 */
	private final DateFormat dateFormat;
	
	/**
	 * DateFormat object to render a string for the time part of a Date object according to android settings
	 * depends on the context the application is running in. To be injected
	 */
	private final DateFormat timeFormat;
	
	/*
	 * Business data
	 */
	
	/**
	 * the log to track
	 */
	//private Log log = new Log();
	private Log log = null;
	
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
	@Deprecated
	public LogCaptureViewModel(Context context, LocationManager locMgr){
		
		timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		dateFormat = android.text.format.DateFormat.getLongDateFormat(context);
		
		//create a distanceProvider Object and register this object as listener for updates
		distanceProvider = new DistanceProvider(locMgr);
		distanceProvider.addSumChangedListener(this);
	}
	
	public LogCaptureViewModel(DateFormat dateFormat, DateFormat timeFormat){
		
		this.timeFormat = timeFormat;
		this.dateFormat = dateFormat;
		
	}
	
	public void setLog(Log log){

		this.log = log;
		
		setStart(log.getStart());
		setStop(log.getStop());
		
		setDriver(log.getDriver());
		
		setCar(log.getCar());
		
		this.distance.set(log.getDistance());
			
	}
	
	/**
	 * @param car
	 */
	private void setCar(Car car) {
		this.selectedCar.set(selectObject(this.cars, car));
	}

	/**
	 * @param driver
	 */
	private void setDriver(Person driver) {
		this.selectedDriver.set(selectObject(this.drivers, driver));
	}
	
	private static <T> int selectObject(ArrayListObservable<T> list, T o){
		
		int result = -1;
		
		if( o == null ) return result;
		
		for(T item : list){
			if(item.equals(o))
				return list.indexOf(item);
		}
		
		return -1;
	}

	public void setStart(Date start){
		this.start.set(formatDate(start));
	}
	
	public void setStop(Date stop){
		this.stop.set(formatDate(stop));
	}
	
	private String formatDate(Date date){
		
		StringBuffer sStart = new StringBuffer("");
		
		if(date != null){
				sStart.append(dateFormat.format(date));
				sStart.append(" ");
				sStart.append(timeFormat.format(date));
		}
		
		return sStart.toString();
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
	 * real logic for the clicked event of the toggle GPS button
	 * 
	 * @param the toggle GPS button
	 * @see LogCaptureViewModel.cmdToggleGPS
	 */
	private void btnToggleGPS_Clicked(Button button) {
		
		if(distanceProvider.isStarted()){
			button.setText(R.string.StartGPS);
		}
		else {
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
}
