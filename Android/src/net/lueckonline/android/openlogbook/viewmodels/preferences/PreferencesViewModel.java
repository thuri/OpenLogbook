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
import net.lueckonline.android.openlogbook.utils.OperationModes;
import android.view.View;

/**
 * @author thuri
 *
 */
public class PreferencesViewModel {
	
	private final PreferencesDelegate controller;
	
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
		public void Invoke(View arg0, Object... arg1) {
			controller.onFinish();
		}
	};
	
	public final ArrayListObservable<Device> triggerDevices = new ArrayListObservable<Device>(Device.class);
	
	public final BooleanObservable isCommuter = new BooleanObservable();
	
	public final BooleanObservable isFieldstaff = new BooleanObservable();
	
	public PreferencesViewModel(PreferencesDelegate controller){
		this.controller = controller;
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
