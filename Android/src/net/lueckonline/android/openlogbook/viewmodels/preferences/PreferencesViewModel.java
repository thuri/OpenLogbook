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
package net.lueckonline.android.openlogbook.viewmodels.preferences;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.model.Device;
import net.lueckonline.android.openlogbook.services.BluetoothService;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.view.View;
import android.widget.CheckBox;

/**
 * @author thuri
 *
 */
public class PreferencesViewModel {
	
	public interface Eventhandler {
		
		public void onModeChanged(int newMode);
		
		public void onFinish();

		public void onAddDevice();
		
		public void onStartService();
		
		public void onStopService();
	}
	
	private final Eventhandler controller;
	
	public final Command modeClicked = new Command(){
		@Override
		public void Invoke(View button, Object... arg1) {
			int mode;
			switch(button.getId()){
				case R.id.radioCommuter:
					mode = OperationModes.COMMUTER;
					break;
				case R.id.radioFieldstaff:
					mode = OperationModes.FIELDSTAFF;
					break;
				default:
					mode = OperationModes.UNKOWN;
					break;
			}
			
			setOperationMode(mode);
			controller.onModeChanged(mode);
		}
	};
	
	
	public final Command finish = new Command(){
		@Override
		public void Invoke(View button, Object... arg1) {
			controller.onFinish();
		}
	};
	
	public final Command addDevice = new Command(){
		@Override
		public void Invoke(View button, Object... arg1) {
			controller.onAddDevice();
		}
	};
	
	public final Command toggleBluetooth = new Command(){

		@Override
		public void Invoke(View button, Object... arg1) {
			CheckBox cb = (CheckBox) button;
			if(cb.isChecked())
				controller.onStartService();
			else
				controller.onStopService();
		}
		
	};
	
	public final ArrayListObservable<Device> triggerDevices = new ArrayListObservable<Device>(Device.class);
	
	public final BooleanObservable isCommuter = new BooleanObservable();
	
	public final BooleanObservable isFieldstaff = new BooleanObservable();
	
	public final BooleanObservable isBluetoothServiceRunning = new BooleanObservable();
	
	private final ActivityManager activityMgr;
	
	private boolean isBTServiceRunning(){
	    for (RunningServiceInfo service : activityMgr.getRunningServices(Integer.MAX_VALUE)) {
	    	if (BluetoothService.class.getName().equals(service.service.getClassName())) 
	            return true;
	    }
	    return false;
	}
	
	public PreferencesViewModel(Eventhandler controller, ActivityManager activityMgr){
		this.controller = controller;
		
		this.isCommuter.set(true);
		
		this.activityMgr = activityMgr;
		this.isBluetoothServiceRunning.set(isBTServiceRunning());
	}
	
	public void setOperationMode(int mode){
		switch(mode){
			case OperationModes.COMMUTER:
				isCommuter.set(true);
				isFieldstaff.set(false);
				break;
			case OperationModes.FIELDSTAFF:
				isFieldstaff.set(true);
				isCommuter.set(false);
				break;
			default:
				isFieldstaff.set(false);
				isCommuter.set(false);
				break;
		}
	}
}
