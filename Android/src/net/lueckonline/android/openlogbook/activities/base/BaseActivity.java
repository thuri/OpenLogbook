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
package net.lueckonline.android.openlogbook.activities.base;

import gueei.binding.app.BindingActivity;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.viewmodels.common.FinishDelegate;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * @author thuri
 *
 */
public abstract class BaseActivity extends BindingActivity implements FinishDelegate {

	private ILogbookRepository repository;
	
	private AlertDialog.Builder okAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.repository = RepositoryFactory.getInstance(getApplicationContext());
		
		okAlert = new AlertDialog.Builder(this);
		
		okAlert.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}
	
	@Override
	public void Finish() {
		this.finish();
	}
	
	public void Finish(boolean condition, String message){
		if(condition)
			this.finish();
		else {
			getOkAlert().setMessage(message);
			getOkAlert().create().show();
		}
	}
	
	public ILogbookRepository getRepository(){
		return this.repository;
	}

	public AlertDialog.Builder getOkAlert() {
		return okAlert;
	}
}
