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

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.dataaccess.DataAccessException;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate;
import net.lueckonline.android.openlogbook.viewmodels.createdriver.CreateDriverDelegate;
import net.lueckonline.android.openlogbook.viewmodels.createdriver.DriverCreateViewModel;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import gueei.binding.app.BindingActivity;

/**
 * @author thuri
 *
 */
public class CreateDriver extends BindingActivity implements FinishDelegate, CreateDriverDelegate{

	private ILogbookRepository repository = null;
	
	private final DriverCreateViewModel vm = new DriverCreateViewModel();
	
	/**
	 * A AlertDialog with one button but exchangeable texts
	 */
	private AlertDialog.Builder okAlert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		repository = RepositoryFactory.getInstance(getApplicationContext());
		
		this.okAlert = new AlertDialog.Builder(this);
		
		this.vm.addDriverCreateDelegate(this);
		
		this.vm.addFinishDelegate(this);
		
		this.vm.drivers.setArray(repository.getDrivers().toArray(new Person[0]));
		
		this.setAndBindRootView(R.layout.createdriver, vm);
		
		okAlert.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.createdriver.CreateDriverDelegate#AddDriver(net.lueckonline.android.openlogbook.model.Person)
	 */
	@Override
	public void AddDriver(Person person) {
		
		if(person == null){
			okAlert.setMessage(getString(R.string.ErrorAddingDriver) + System.getProperty("line.separator") + getString(R.string.InternalError));
			okAlert.create().show();
			return;
		}
		
		if(person.getName() == null || person.getName().trim().length() == 0){
			okAlert.setMessage(getString(R.string.ErrorAddingCar) + System.getProperty("line.separator") + getString(R.string.DriverNameEmpty));
			okAlert.create().show();
			return;
		}
		
		try{
			repository.add(person);
			vm.drivers.add(person);
		}
		catch(DataAccessException dae){
			//TODO:
			//the user should see that the car couldn't be added because it won't appear in the list
			//but in the future a Dialog should inform the user directly
		}
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate#Finish()
	 */
	@Override
	public void Finish() {
		if(vm.drivers.size() > 0)
			this.finish();
		else {
			okAlert.setMessage(R.string.OneDriverMinimum);
			okAlert.create().show();
		}
	}
}
