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
 * @author thuri
 *
 */
public class Car {
	
	/**
	 * The id of the car
	 */
	private int id = -1;
	/**
	 * The licensePlate of the car
	 */
	private String licensePlate = null;

	/**
	 * @param the Id of the car
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return the id of the car. May be -1 if not set before
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return the licensePlate of the car. May be null if not set before
	 */
	public String getLicensePlate() {
		return licensePlate;
	}

	/**
	 * @param licensePlate the license plate of the car
	 */
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

}
