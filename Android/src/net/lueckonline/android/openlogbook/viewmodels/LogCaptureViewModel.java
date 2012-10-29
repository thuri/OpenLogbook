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
import java.util.Date;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


/**
 * @author thuri
 *
 */
public class LogCaptureViewModel {
	
	public interface Eventhandler{
		
		public void AddLog(Log log);
		
		public void StartLogging();
		
		public void StopLogging();

		public void ToggleLogging(CharSequence text);
		
	}

	
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

	public final IntegerObservable selectedCar = new IntegerObservable(-1);
	
	public final IntegerObservable selectedDriver = new IntegerObservable(-1);
 
	
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
	private Log log = null;

	private final Eventhandler controller;
	
	public LogCaptureViewModel(DateFormat dateFormat, DateFormat timeFormat, LogCaptureViewModel.Eventhandler controller){
		this.timeFormat = timeFormat;
		this.dateFormat = dateFormat;
		this.controller = controller;
	}
	
	public void setLog(Log log){

		this.log = log;
		
		setStart(log.getStart());
		setStop(log.getStop());
		
		setDriver(log.getDriver());
		
		setCar(log.getCar());
		
		this.distance.set(log.getDistance());
			
	}
	
	public Log getLog(){
		return this.log;
	}
	
	/**
	 * @param car
	 */
	private void setCar(Car car) {
		this.selectedCar.set(indexOf(this.cars, car));
	}
	
	public Car getCar(){
		return getSelected(this.cars, this.selectedCar);
	}
	
	/**
	 * @param driver
	 */
	private void setDriver(Person driver) {
		this.selectedDriver.set(indexOf(this.drivers, driver));
	}
	
	public Person getDriver(){
		return getSelected(this.drivers, this.selectedDriver);
	}

	private static <T> T getSelected(ArrayListObservable<T> list, IntegerObservable position){
		int p = position.get();
		
		if(p < 0) 	return null;
		else  		return list.getItem(p);
	}
	
	private static <T> int indexOf(ArrayListObservable<T> list, T o){
		
		int result = -1;
		
		if( o == null ) return result;
		
		for(T item : list){
			if(item.equals(o))
				return list.indexOf(item);
		}
		
		return -1;
	}

	private void setStart(Date start){
		this.start.set(formatDate(start));
	}
	
	private void setStop(Date stop){
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
			if(vButton instanceof Button){
				Button btn = (Button) vButton;
				controller.ToggleLogging(btn.getText());
			}
		}
	};

	/**
	 * real logic for the clicked event of the toggle GPS button
	 * 
	 * @param the toggle GPS button
	 * @see LogCaptureViewModel.cmdToggleGPS
	 */
	/*private void btnToggleGPS_Clicked(Button button) {
		
		switch(state){
			case STOPPED:
				controller.StartLogging();
				setState(State.STARTED);
				button.setText(R.string.StopGPS);
				break;
			case STARTED:
				controller.StopLogging();
				setState(State.READYTOSAVE);
				button.setText(R.string.SaveLog);
				break;
			case READYTOSAVE:
				controller.AddLog(log);
				setState(State.STOPPED);
				button.setText(R.string.StartGPS);
				break;
		}
	}*/
	
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
}
