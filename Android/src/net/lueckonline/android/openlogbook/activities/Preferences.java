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

import java.util.List;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.activities.base.BaseActivity;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.dialogs.AddTriggerDialog;
import net.lueckonline.android.openlogbook.model.Device;
import net.lueckonline.android.openlogbook.services.BluetoothService;
import net.lueckonline.android.openlogbook.viewmodels.preferences.PreferencesViewModel;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

public class Preferences extends BaseActivity implements PreferencesViewModel.Eventhandler, AddTriggerDialog.Eventhandler{

	/**
	 * 
	 */
	private static final int CREATE_TRIGGER_DIALOG = 0;

	private PreferencesViewModel vm;
	
	private ILogbookRepository repository;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.vm = new PreferencesViewModel(this, (ActivityManager) getSystemService(ACTIVITY_SERVICE));
		
		this.repository = RepositoryFactory.getInstance(getApplicationContext());
		
		List<Device> devices = repository.getDevices();
		
		if(devices.size() == 0)
		{
			Device fake = new Device();
			fake.setName("Fake BluetoothDevice");
			devices.add(fake);
		}
		
		vm.triggerDevices.setArray(devices.toArray(new Device[0]));
		vm.setOperationMode(repository.getMode());
		
		setAndBindRootView(R.layout.preferences, vm);
		
	}

	@Override
	public void onModeChanged(int newMode) {
		
		try {
			repository.setMode(newMode);
		} catch (DataAccessException e) {
			//TODO: inform user
		}
	}

	@Override
	public void onAddDevice() {
		showDialog(CREATE_TRIGGER_DIALOG);
	}
	
	@Override
	public void onTriggerAdded(Device device) {
		this.vm.triggerDevices.add(device);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		if(id == CREATE_TRIGGER_DIALOG){
			//TODO: add the new Trigger device defined in the Dialog to the trigger devices list in the view model
			return new AddTriggerDialog(this, this);
		}
		else
			return null;
	}

	@Override
	public void onFinish() {
		finish();
	}

	@Override
	public void onStartService() {
		startService(new Intent(this, BluetoothService.class));
	}

	@Override
	public void onStopService() {
		stopService(new Intent(this, BluetoothService.class));
	}

	
	
}
