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
package net.lueckonline.android.openlogbook.viewmodels.createcar;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;
import java.util.List;

import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate;
import android.view.View;

/**
 * ViewModel Class for {@see R.layout.createcar}
 * 
 * @see R.layout.createcar
 * @author thuri
 */
public class CarCreateViewModel {
	
	/**
	 * the observable bound to the textbox. Will contain user input
	 */
	public final StringObservable licensePlate = new StringObservable();
	
	/**
	 * an observable list bound to a list view in the activity used to display available cars
	 */
	public final ArrayListObservable<Car> cars = new ArrayListObservable<Car>(Car.class);
	
	/**
	 * the delegates to inform if a new car should be added
	 */
	private final List<CreateCarDelegate> createCarDelegates = new ArrayList<CreateCarDelegate>();
	
	/**
	 * the delegates to inform if the view can be closed
	 */
	private final List<FinishDelegate> finishDelegates = new ArrayList<FinishDelegate>();
	
	/**
	 * the command bound to the button to press to add a new car
	 * 
	 * the command don't adds the car to a repository. Add a {@see CreateCarDelegate}
	 * to get the {@see Car} object to add and put it where you want it.
	 * 
	 *  must be final so it can't be changed from the outside
	 */
	public final Command addCar = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			
			Car newCar = new Car();
			newCar.setLicensePlate(licensePlate.get());
			raiseCreateCar(newCar);
			
		}
	};
	
	/**
	 * the command bound to the button to press if the user finished with the view
	 */
	public final Command finish = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			raiseFinish();
		}
	};
	
	/**
	 * add delegate to which a new car object is passed if it should be added to a repository 
	 * 
	 * @param delegate
	 */
	public void addCarCreateDelegate(CreateCarDelegate delegate){
		this.createCarDelegates.add(delegate);
	}
	
	/**
	 * add a delegate to inform that the view can be closed
	 * 
	 * @param delegate
	 */
	public void addFinishDelegate(FinishDelegate delegate){
		this.finishDelegates.add(delegate);
	}
	
	/**
	 * helper method to inform all delegates of a new car 
	 * 
	 * @param car the new Car, to add to a repository for example
	 */
	private void raiseCreateCar(Car car){
		for(CreateCarDelegate d : createCarDelegates)
			d.AddCar(car);
	}
	
	/**
	 * helper method to inform all delegates that the view can be closed
	 */
	private void raiseFinish(){
		for(FinishDelegate d : finishDelegates)
			d.Finish();
	}
}
