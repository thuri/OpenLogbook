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
package net.lueckonline.android.openlogbook.dialogs;

import gueei.binding.Command;
import gueei.binding.app.BindingDialog;
import gueei.binding.collections.ArrayListObservable;

import java.util.Iterator;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Device;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * @author thuri
 *
 */
public class AddTriggerDialog extends BindingDialog {

	private final ILogbookRepository repository;
	
	private ViewModel vm;

	private final Eventhandler controller;
	
	public interface Eventhandler {
		public void onTriggerAdded(Device device);
	}
	
	/**
	 * @param context
	 */
	public AddTriggerDialog(Context context, Eventhandler controller) {
		super(context);

		this.controller = controller;
		this.repository = RepositoryFactory.getInstance(context);
		this.setTitle(R.string.AddDevice);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		vm = new ViewModel();
		
		//call add on the collection because it may (and it will) already contain values we don't want to override
		//for example the "No Car" entry, defining that a new trigger device is not bound to any car!
		vm.cars.addAll(repository.getCars());
		
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		Iterator<BluetoothDevice> iterator = btAdapter.getBondedDevices().iterator();
		
		while(iterator.hasNext()){
			vm.devices.add(new Device(iterator.next().getName()));
		}
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Object getViewModel() {
		return this.vm;
	}
	
	public void onDeviceSelected(Device device){
		try {
			//if this fails, we won't call the controller because an exception is thrown. That's OK because 
			//the controller only needs to know if the device was added!
			this.repository.add(device);
			//call the controller so it can add the new device to the list of already added devices!
			this.controller.onTriggerAdded(this.vm.device);
		} catch (DataAccessException e) {
			//TODO inform user
		}
	}

	public void onFinish(){
		this.hide();
	}

	private class ViewModel {
		
		public final ArrayListObservable<Car> cars = new ArrayListObservable<Car>(Car.class);
		
		public final ArrayListObservable<Device> devices = new ArrayListObservable<Device>(Device.class);

		private final Device device = new Device();
		
		public ViewModel(){
			//Add a default "Car" representing that a new trigger device won't be bound to a car, an headset for example!
			Car car = new Car();
			car.setLicensePlate(getContext().getString(R.string.NoCar));
			//Define an negative id to show that this is not a valid entry
			car.setId(-1);
			this.cars.add(car);
		}
		
		@SuppressWarnings("unused")
		public final Command deviceSelected = new Command(){
			@Override
			public void Invoke(View spinner, Object... arg1) {
				Integer pos = (Integer)arg1[1];
				device.setName(devices.getItem(pos).getName());
			}
		};

		@SuppressWarnings("unused")
		public final Command carSelected = new Command(){
			@Override
			public void Invoke(View spinner, Object... arg1) {
				Integer pos = (Integer)arg1[1];
				Car selectedCar = cars.getItem(pos);
				//check that the selected car is not the "No Car" Entry. If so set the car reference of the current device to null
				if(selectedCar != null && selectedCar.getId() == -1)
					selectedCar = null;
				device.setCar(selectedCar);
			}
		}; 
		
		@SuppressWarnings("unused")
		public final Command finish = new Command(){
			@Override
			public void Invoke(View arg0, Object... arg1) {
				onDeviceSelected(device);
				onFinish();
			}
		};
	}
}
