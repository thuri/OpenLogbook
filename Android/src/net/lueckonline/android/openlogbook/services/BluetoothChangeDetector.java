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
package net.lueckonline.android.openlogbook.services;

import java.util.List;

import net.lueckonline.android.openlogbook.activities.OpenLogbook;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Device;
import net.lueckonline.android.openlogbook.model.Log;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author thuri
 *
 */
public class BluetoothChangeDetector extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		ILogbookRepository repository = RepositoryFactory.getInstance(context);
		
		BluetoothDevice deviceFound = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		
		//TODO: to fetch the devices every time may be a performance killer. Especially if many devices have been saved.
		//Therefore the result may be cached in the repository (and the cache invalidated on changes of the device table). But that's to be
		//encapsulated in the repository!
		List<Device> devices = repository.getDevices();
		Device trigger = null;
		
		for(Device d : devices){
			if(deviceFound.getName().equals(d.getName())){ 
				trigger = d;
				break;
			}
		}
		
		if(trigger == null)
			return;
		
		String action = intent.getAction();
		
		if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
			this.deviceAvailable(context, trigger, repository);
		else if(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED .equals(action) || BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
			this.deviceUnavailable(context, trigger, repository);
	}

	private void deviceAvailable(Context context, Device trigger, ILogbookRepository repository) {
		//TODO: can a device be found multiple times? SO is it necessary to check whether tracking has already started?
		//TODO: what if a second tracking device is found while tracking is already started?
		
		Log log = new Log();
		log.setCar(trigger.getCar());
		
		Intent intent = new Intent(context, DistanceService.class);
		intent.setAction(OpenLogbook.ACTION_START_LOG);
		intent.putExtra(DistanceService.LOG_EXTRA, log);
		
		context.startService(intent);
	}
	
	private void deviceUnavailable(Context context, Device trigger, ILogbookRepository repository) {
		
		Intent intent = new Intent(context, DistanceService.class);
		intent.setAction(OpenLogbook.ACTION_STOP_LOG);
		
		context.stopService(intent);
	}
}
