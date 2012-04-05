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

import net.lueckonline.android.openlogbook.activities.ModeSelection;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OpenLogbook extends Activity implements LocationListener {

	private static final int MODE_REQUEST = 1;
	
	private boolean locationListener = false;

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

		setContentView(R.layout.main);

		findViewById(R.id.btnStartGPS).setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					startGPS(v);
				}
			}
		);
	}

	private void startGPS(View v) {
		if(this.locationListener){
			removeLocationListener();
		}
		else {
			addLocationListener();
		}
	}
	
	private void removeLocationListener(){
		final LocationManager lMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final Button btn = (Button) findViewById(R.id.btnStartGPS);
		
		lMgr.removeUpdates(this);
		btn.setText(R.string.StartGPS);
		this.locationListener = false;
	}
	
	private void addLocationListener(){
		final LocationManager lMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final Button btn = (Button) findViewById(R.id.btnStartGPS);
		
		lMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,	this);
		btn.setText(R.string.StopGPS);
		this.locationListener = true;
	}
	
	private Location lastLocation = null;
	private float distance = 0.0f;
	
	@Override
	public void onLocationChanged(Location l) {
		final EditText editLong = (EditText) findViewById(R.id.tbLongitude);
		editLong.setText(String.valueOf(l.getLongitude()));

		final EditText editLat = (EditText) findViewById(R.id.tbLatitude);
		editLat.setText(String.valueOf(l.getLatitude()));

		final EditText editAlt = (EditText) findViewById(R.id.tbAltitude);
		editAlt.setText(String.valueOf(l.getAltitude()));
		
		if(lastLocation != null) 
			distance += l.distanceTo(lastLocation);
		
		final EditText editDist = (EditText) findViewById(R.id.tbDistance);
		editDist.setText(String.valueOf(distance));
		
		lastLocation = l;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == MODE_REQUEST) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			preferences.edit().putInt("Mode", data.getExtras().getInt("Mode"))
					.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		TextView tv = (TextView) findViewById(R.id.helloTextView);

		tv.setText("Mode="
				+ getPreferences(MODE_PRIVATE).getInt("Mode",
						OperationModes.UNKOWN));
	}
}