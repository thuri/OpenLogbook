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
package net.lueckonline.android.openlogbook.activities;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.activities.base.BaseActivity;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.viewmodels.createcar.CarCreateViewModel;
import net.lueckonline.android.openlogbook.viewmodels.createcar.CreateCarDelegate;
import android.os.Bundle;

/**
 * Activity to create some cars for the application
 * 
 * Implements the DelegateInterface to save a new Car in  the repository
 * 
 * @author thuri
 *
 */
public class CreateCar extends BaseActivity implements CreateCarDelegate {

	
	/**
	 * ViewModel for the {@see R.layout.craetecar} layout
	 */
	private final CarCreateViewModel vm = new CarCreateViewModel();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		//add this activity as the CreateCarDelegate so the activity is informed if a new Car should be added 
		vm.addCarCreateDelegate(this);
		//add this activity as the FinishDelegate so activity is informed if it should close itself
		vm.addFinishDelegate(this);
		
		//get all the current cars and add it to the observable and bound array
		vm.cars.setArray(getRepository().getCars().toArray(new Car[0]));
		
		//bind the viewmodel and display it
		setAndBindRootView(R.layout.createcar, vm);
		
	}
	
	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.createcar.CarCreateDelegate#AddCar(Car)
	 */
	@Override
	public void AddCar(Car newCar) {
		try {
			if(newCar == null){
				getOkAlert().setMessage(getString(R.string.ErrorAddingCar) + System.getProperty("line.separator") + getString(R.string.InternalError));
				getOkAlert().create().show();
				return;
			}
			
			if(newCar.getLicensePlate()  == null || newCar.getLicensePlate().trim().length() == 0){
				getOkAlert().setMessage(getString(R.string.ErrorAddingCar) + System.getProperty("line.separator") + getString(R.string.LicensePlateEmpty));
				getOkAlert().create().show();
				return;
			}
			
			//add the car
			getRepository().add(newCar);
			//if no exception was thrown, the car must have been added
			//so add it to the list so the user can see it.
			vm.cars.add(newCar);
		}
		catch(DataAccessException dae){
			//TODO: the user should see that the car couldn't be added because it won't appear in the list
			//but in the future a Dialog should inform the user directly
		}
	}
	
	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate#Finish()
	 */
	@Override
	public void Finish() {
		Finish(vm.cars.size() > 0, getString(R.string.OneCarMinimum));
	}
}
