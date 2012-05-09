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
package net.lueckonline.android.openlogbook.viewmodels.createdriver;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;
import java.util.List;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate;
import android.view.View;

/**
 * ViewModel Class for {@see R.layout.createdriver}
 * 
 * @see R.layout.createdriver
 * @author thuri
 */
public class DriverCreateViewModel {
	
	/**
	 * the observable bound to the textbox. Will contain user input
	 */
	public final StringObservable name = new StringObservable();
	
	/**
	 * an observable list bound to a list view in the activity used to display available drivers
	 */
	public final ArrayListObservable<Person> drivers = new ArrayListObservable<Person>(Person.class);
	
	/**
	 * the delegates to inform if a new car should be added
	 */
	private final List<CreateDriverDelegate> createDriverDelegates = new ArrayList<CreateDriverDelegate>();
	
	/**
	 * the delegates to inform if the view can be closed
	 */
	private final List<FinishDelegate> finishDelegates = new ArrayList<FinishDelegate>();
	
	/**
	 * the command bound to the button to press to add a new driver
	 * 
	 * the command don't adds the driver to a repository. Add a {@see CreateDriverDelegate}
	 * to get the {@see Person} object to add and put it where you want it.
	 * 
	 *  must be final so it can't be changed from the outside
	 */
	public final Command addDriver = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			
			Person p = new Person();
			p.setName(name.get());
			raiseCreateDriver(p);
		}
	};
	
	/**
	 * the command bound to the button to press if the user finished with the view
	 */
	public final Command finish = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			raiseFinish();
		}
	};
	
	/**
	 * add delegate to which a new {@see Person} object is passed if it should be added to a repository 
	 * 
	 * @param delegate
	 */
	public void addDriverCreateDelegate(CreateDriverDelegate delegate){
		this.createDriverDelegates.add(delegate);
	}
	
	/**
	 * add a delegate to inform that the view can be closed
	 * 
	 * @param delegate
	 */
	public void addFinishDelegate(FinishDelegate delegate){
		this.finishDelegates.add(delegate);
	}
	
	/**
	 * helper method to inform all delegates of a new car 
	 * 
	 * @param car the new Person, to add to a repository for example
	 */
	private void raiseCreateDriver(Person person){
		for(CreateDriverDelegate d : this.createDriverDelegates)
			d.AddDriver(person);
	}
	
	/**
	 * helper method to inform all delegates that the view can be closed
	 */
	private void raiseFinish(){
		for(FinishDelegate d : finishDelegates)
			d.Finish();
	}
}
