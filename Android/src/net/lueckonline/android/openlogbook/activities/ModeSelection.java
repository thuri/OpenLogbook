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
import net.lueckonline.android.openlogbook.utils.OperationModes;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

public class ModeSelection extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modeselection);
		
		Button btn = (Button) findViewById(R.id.btnCommitMode);
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent data = new Intent();
		
		RadioGroup modeGroup = (RadioGroup) findViewById(R.id.radioGroupMode);
		
		int mode = OperationModes.UNKOWN;
		
		switch(modeGroup.getCheckedRadioButtonId()){
			case R.id.radioCommuter:
				mode = OperationModes.COMMUTER;
				break;
			case R.id.radioFieldstaff:
				mode = OperationModes.FIELDSTAFF;
				break;
			default:
				throw new UnsupportedOperationException("Unsupported Mode selected");
		}
		
		data.putExtra("Mode", mode);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

}
