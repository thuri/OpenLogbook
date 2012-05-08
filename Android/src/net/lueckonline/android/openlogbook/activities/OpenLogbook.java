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
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import net.lueckonline.android.openlogbook.viewmodels.LogCaptureViewModel;
import net.lueckonline.android.openlogbook.viewmodels.createlog.CreateLogDelegate;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;

public class OpenLogbook extends BindingActivity implements CreateLogDelegate {

	private static final int MODE_REQUEST = 1;
	private LogCaptureViewModel vm;
	private ILogbookRepository repository;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		int mode = preferences.getInt("Mode", OperationModes.UNKOWN);

		switch (mode) {
			case OperationModes.UNKOWN:
				Intent intent = new Intent(this, ModeSelection.class);
				Intent carIntent = new Intent(this, CreateCar.class);
				// although startActivityForResult is asynchronous the View of this
				// onCreate will not be displayed
				// until the Activity Returned a result (see Android Dev Doku)
				startActivityForResult(intent, MODE_REQUEST);
				startActivity(carIntent);
				break;
			case OperationModes.COMMUTER:
			case OperationModes.FIELDSTAFF:
				// mode already selected, so nothing necessary
				break;
			default:
				throw new IllegalArgumentException("Unknown OperationMode");
		}
		
		//get a reference to the repository by using the Factory
		repository = RepositoryFactory.getInstance(this.getApplicationContext());
		
		vm = new LogCaptureViewModel(this.getApplicationContext(), (LocationManager) getSystemService(Context.LOCATION_SERVICE));
		
		vm.addCreateLogDelegate(this);
		
		setAndBindRootView(R.layout.main, vm);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		updateLists();
	}
	
	private void updateLists(){
		vm.cars.setArray(repository.getCars().toArray(new Car[0]));
		vm.drivers.setArray(repository.getDrivers().toArray(new Person[0]));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == MODE_REQUEST) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			preferences.edit().putInt("Mode", data.getExtras().getInt("Mode"))
					.commit();
		}
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.createlog.CreateLogDelegate#AddLog(net.lueckonline.android.openlogbook.model.Log)
	 */
	@Override
	public void AddLog(Log log) {
		try {
			repository.addLog(log);
		}
		catch(DataAccessException dae){
			//TODO: inform user
		}
	}
}