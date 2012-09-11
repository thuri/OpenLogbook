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
package net.lueckonline.android.openlogbook.viewmodels.addtrigger;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Device;
import android.view.View;

/**
 * @author thuri
 *
 */
public class AddTriggerViewModel {

	public final ArrayListObservable<Car> cars = new ArrayListObservable<Car>(Car.class);
	
	public final ArrayListObservable<Device> devices = new ArrayListObservable<Device>(Device.class);
	
	public final Command deviceSelected = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
		}
	};

	public final Command carSelected = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
		}
	}; 
	
	public final Command finish = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
		}
	};
}
