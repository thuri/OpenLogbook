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

import gueei.binding.app.BindingActivity;
import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate;
import net.lueckonline.android.openlogbook.viewmodels.createcar.CarCreateViewModel;
import net.lueckonline.android.openlogbook.viewmodels.createcar.CreateCarDelegate;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * Activity to create some cars for the application
 * 
 * Implements the DelegateInterface to save a new Car in  the repository
 * 
 * @author thuri
 *
 */
public class CreateCar extends BindingActivity implements CreateCarDelegate, FinishDelegate{

	/**
	 * Reference to the repository.
	 * Used to get currently present cars and to add new cars
	 */
	private ILogbookRepository repository = null;
	
	/**
	 * ViewModel for the {@see R.layout.craetecar} layout
	 */
	private final CarCreateViewModel vm = new CarCreateViewModel();
	
	/**
	 * A AlertDialog with one button but exchangeable texts
	 */
	private AlertDialog.Builder okAlert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		okAlert = new AlertDialog.Builder(this);
		
		repository = RepositoryFactory.getInstance(this.getApplicationContext());
		
		//add this activity as the CreateCarDelegate so the activity is informed if a new Car should be added 
		vm.addCarCreateDelegate(this);
		//add this activity as the FinishDelegate so activity is informed if it should close itself
		vm.addFinishDelegate(this);
		
		//get all the current cars and add it to the observable and bound array
		vm.cars.setArray(repository.getCars().toArray(new Car[0]));
		
		//bind the viewmodel and display it
		setAndBindRootView(R.layout.createcar, vm);
		
		okAlert.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.createcar.CarCreateDelegate#AddCar(Car)
	 */
	@Override
	public void AddCar(Car newCar) {
		try {
			if(newCar == null){
				okAlert.setMessage(getString(R.string.ErrorAddingCar) + System.getProperty("line.separator") + getString(R.string.InternalError));
				okAlert.create().show();
				return;
			}
			
			if(newCar.getLicensePlate()  == null || newCar.getLicensePlate().trim().length() == 0){
				okAlert.setMessage(getString(R.string.ErrorAddingCar) + System.getProperty("line.separator") + getString(R.string.LicensePlateEmpty));
				okAlert.create().show();
				return;
			}
			
			//add the car
			repository.addCar(newCar);
			//if no exception was thrown, the car must have been added
			//so add it to the list so the user can see it.
			vm.cars.add(newCar);
		}
		catch(DataAccessException dae){
			//the user should see that the car couldn't be added because it won't appear in the list
			//but in the future a Dialog should inform the user directly
		}
	}
	
	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate#Finish()
	 */
	@Override
	public void Finish() {
		if(vm.cars.size() > 0)
			finish();
		else {
			okAlert.setMessage(R.string.OneCarMinimum);
			okAlert.create().show();
		}
	}
}
