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

import java.io.Serializable;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.activities.base.BaseActivity;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.services.DistanceService;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import net.lueckonline.android.openlogbook.viewmodels.LogCaptureViewModel;
import net.lueckonline.android.openlogbook.viewmodels.createlog.CreateLogDelegate;
import net.lueckonline.android.openlogbook.viewmodels.mainmenu.MainMenuViewModel;
import net.lueckonline.android.openlogbook.viewmodels.mainmenu.StartIntentDelegate;
import android.content.Intent;
import android.os.Bundle;

public class OpenLogbook extends BaseActivity implements CreateLogDelegate, StartIntentDelegate {

	public static final String ACTION_START_LOG 	= "net.lueckonline.android.openlogbook.activities.OpenLogbook.ACTION_START_LOG";

	public static final String ACTION_STOP_LOG 	= "net.lueckonline.android.openlogbook.activities.OpenLogbook.ACTION_STOP_LOG";
	
	private LogCaptureViewModel vm;
	
	private ILogbookRepository repository;

	private void initializeApp(){
		//TODO: Move initialization elsewhere!
		int mode = repository.getMode();
		switch (mode) {
			case OperationModes.UNKOWN:
				
				Intent intent = new Intent(this, Preferences.class);
				Intent carIntent = new Intent(this, CreateCar.class);
				Intent driverIntent = new Intent(this, CreateDriver.class);
				
				startActivity(intent);
				startActivity(carIntent);
				startActivity(driverIntent);
				
				break;
			case OperationModes.COMMUTER:
			case OperationModes.FIELDSTAFF:
				// mode already selected, so nothing necessary
				break;
			default:
				throw new IllegalArgumentException("Unknown OperationMode");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		repository = RepositoryFactory.getInstance(getApplicationContext());

		initializeApp();
		
		Log log = getLogFromIntent();
		
		vm = new LogCaptureViewModel(android.text.format.DateFormat.getLongDateFormat(this), android.text.format.DateFormat.getTimeFormat(this));
		vm.addCreateLogDelegate(this);
		updateLists();
		
		vm.setLog(log);
		
		MainMenuViewModel menuVm = new MainMenuViewModel();
		menuVm.addStartIntentDelegate(this);
		
		setAndBindRootView(R.layout.main, vm);
		setAndBindOptionsMenu(R.menu.main_menu, menuVm);
		
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		updateLists();
	}

	/**
	 * @return
	 */
	private Log getLogFromIntent() {
		Intent intent = getIntent();
		//check whether the intent contains a log!
		Serializable s = intent.getSerializableExtra(DistanceService.LogExtra);
		Log log = null;
		if(s instanceof Log)
			log = (Log) s;
		
		if(log == null)
			log = new Log();
		return log;
	}
	
	private void updateLists(){
		vm.cars.setArray(getRepository().getCars().toArray(new Car[0]));
		vm.drivers.setArray(getRepository().getDrivers().toArray(new Person[0]));
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.createlog.CreateLogDelegate#AddLog(net.lueckonline.android.openlogbook.model.Log)
	 */
	@Override
	public void AddLog(Log log) {
		try {
			getRepository().add(log);
		}
		catch(DataAccessException dae){
			//TODO: inform user
		}
	}

	@Override
	public void Start(Class<?> clazz) {
		startActivity(new Intent(this, clazz));
	}
	
	
}