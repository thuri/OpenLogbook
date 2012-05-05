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

package net.lueckonline.android.openlogbook;

import gueei.binding.app.BindingActivity;
import net.lueckonline.android.openlogbook.activities.ModeSelection;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import net.lueckonline.android.openlogbook.viewmodels.TripCaptureViewModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;

public class OpenLogbook extends BindingActivity {

	private static final int MODE_REQUEST = 1;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		int mode = preferences.getInt("Mode", OperationModes.UNKOWN);

		switch (mode) {
		case OperationModes.UNKOWN:
			Intent intent = new Intent(this, ModeSelection.class);
			// although startActivityForResult is asynchronous the View of this
			// onCreate will not be displayed
			// until the Activity Returned a result (see Android Dev Doku)
			startActivityForResult(intent, MODE_REQUEST);
			break;
		case OperationModes.COMMUTER:
		case OperationModes.FIELDSTAFF:
			// mode already selected, so nothing necessary
			break;
		default:
			throw new IllegalArgumentException("Unknown OperationMode");
		}
		
		TripCaptureViewModel vm = new TripCaptureViewModel(this.getApplicationContext(), (LocationManager) getSystemService(Context.LOCATION_SERVICE));
		
		setAndBindRootView(R.layout.main, vm);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == MODE_REQUEST) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			preferences.edit().putInt("Mode", data.getExtras().getInt("Mode"))
					.commit();
		}
	}
}