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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author thuri
 * 
 * Model class representing one trip
 *
 */
public class Log {
	
	private Person driver;
	private Car car;
	private float distance;
	private Date start;
	private Date stop;
	private List<Person> passengers;
	
	public Log(){
		this.start = new Date();
		passengers = new ArrayList<Person>();
	}
	
	/*
	 * Getters and setters
	 */
	public Person getDriver() {
		return driver;
	}
	
	public void setDriver(Person driver) {
		this.driver = driver;
	}
	
	public Car getCar() {
		return car;
	}
	
	public void setCar(Car car) {
		this.car = car;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getStop() {
		return stop;
	}
	
	public void setStop(Date stop) {
		this.stop = stop;
	}
	
	public List<Person> getPassengers() {
		return passengers;
	}
	
	public void setPassengers(List<Person> passengers) {
		this.passengers = passengers;
	}
}
