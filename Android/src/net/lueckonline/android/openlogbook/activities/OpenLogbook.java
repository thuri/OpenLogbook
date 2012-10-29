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
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.services.DistanceService;
import net.lueckonline.android.openlogbook.viewmodels.LogCaptureViewModel;
import net.lueckonline.android.openlogbook.viewmodels.mainmenu.MainMenuViewModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;

public class OpenLogbook extends BaseActivity implements LogCaptureViewModel.Eventhandler, MainMenuViewModel.Eventhandler {

	public static final String ACTION_START_LOG 	= "net.lueckonline.android.openlogbook.activities.OpenLogbook.ACTION_START_LOG";

	public static final String ACTION_STOP_LOG 	= "net.lueckonline.android.openlogbook.activities.OpenLogbook.ACTION_STOP_LOG";
	
	public static final String ACTION_DISTANCE_CHANGED = "net.lueckonline.android.openlogbook.ACTION_DISTANCE_CHANGED";
	
	private LogCaptureViewModel vm;
	
	private final BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Serializable extra = intent.getSerializableExtra(DistanceService.LOG_EXTRA);
			if(extra != null && vm != null && extra instanceof Log){
				vm.setLog((Log) extra);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Log log = getLogFromIntent(intent);
		
		vm = new LogCaptureViewModel(android.text.format.DateFormat.getLongDateFormat(this), android.text.format.DateFormat.getTimeFormat(this), this);
		updateLists();
		
		vm.setLog(log);
		
		String action = intent.getAction();
		
		
		MainMenuViewModel menuVm = new MainMenuViewModel(this);
		
		setAndBindRootView(R.layout.main, vm);
		setAndBindOptionsMenu(R.menu.main_menu, menuVm);

		//if the intent came from a service, we need to adjust the text of the toggle Button
		//This is necessary because the ViewModel Object is newly created on each "onCreate" call and thus the state 
		//would always be initialized with the default value.
		//we set the text in here because we can't bind easily to a resource id but want to support internationalization
		if(ACTION_DISTANCE_CHANGED.equals(action) || ACTION_START_LOG.equals(action))
			getToggleButton().setText(R.string.StopGPS);
		//if the log is stopped from outside and the activity is created we set the state to ready to save.
		//by that we assume that the log is complete!!
		else if(ACTION_STOP_LOG.equals(action))
			getToggleButton().setText(R.string.SaveLog);
		else
			getToggleButton().setText(R.string.StartGPS);
		
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		updateLists();
		
		this.registerReceiver(this.br, new IntentFilter(OpenLogbook.ACTION_DISTANCE_CHANGED));
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		this.unregisterReceiver(this.br);
		
	}

	/**
	 * @return
	 */
	private Log getLogFromIntent(Intent intent) {
		
		//check whether the intent contains a log!
		Serializable s = intent.getSerializableExtra(DistanceService.LOG_EXTRA);
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

	@Override
	public void AddLog(Log log) {
		try {
			getRepository().add(log);
		}
		catch(DataAccessException dae){
			//TODO: inform user
		}
	}

	/**
	 * Manually starting the Logging using the distance service
	 */
	@Override
	public void StartLogging() {
		
		Log log = new Log();
		log.setCar(vm.getCar());
		log.setDriver(vm.getDriver());
		
		Intent intent = new Intent(this, DistanceService.class);
		intent.setAction(OpenLogbook.ACTION_START_LOG);
		intent.putExtra(DistanceService.LOG_EXTRA, log);
		
		this.startService(intent);
	}

	@Override
	public void StopLogging() {
		Intent intent = new Intent(this, DistanceService.class);
		intent.setAction(OpenLogbook.ACTION_STOP_LOG);
		
		this.stopService(new Intent(this, DistanceService.class));
	}
	
	private Button getToggleButton(){
		return (Button) findViewById(R.id.btnToggleGPS);
	}

	@Override
	public void ToggleLogging(final CharSequence text) {
		
		if(text.equals(getText(R.string.StartGPS))){
			this.StartLogging();
			getToggleButton().setText(R.string.StopGPS);
		}
		else if(text.equals(getText(R.string.StopGPS))){
			this.StopLogging();
			getToggleButton().setText(R.string.SaveLog);
		}
		else {
			this.AddLog(vm.getLog());
			getToggleButton().setText(R.string.StartGPS);
		}
	}

	@Override
	public void Start(Class<?> clazz) {
		startActivity(new Intent(this, clazz));
	}
	
}