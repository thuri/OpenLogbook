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
package net.lueckonline.android.openlogbook.model;

/**
 * A data Object representing Bluetooth-Devices known to the app. Those devices
 * can be used as triggers to start logging if the phone is near the device or
 * to stop logging of the phone isn't near the device any longer
 * 
 * @author thuri
 * 
 */
public class Device {

	/**
	 * @see #setCar(Car)
	 */
	private Car car;

	/**
	 * @see #setName(String)
	 */
	private String name;

	/**
	 * @return the car referenced by this Device. May be null if no connection
	 *         saved
	 * @see #setCar(Car)
	 */
	public Car getCar() {
		return this.car;
	}

	/**
	 * @param car
	 *            the car that should be connected with this device. Logs made
	 *            because this device was connected are automatically assigned
	 *            this car.
	 */
	public void setCar(Car car) {
		this.car = car;
	}

	/**
	 * @return the name of this device object. May not be null if the Device was
	 *         loaded from the DB
	 * @see #setName(String)
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name of this device. This is compared to bonded devices to
	 *            find a so called trigger device. That means a device which
	 *            triggers starting and stopping of a log if the phone is near
	 *            it.<br>
	 */
	public void setName(String name) {
		this.name = name;
	}

}
