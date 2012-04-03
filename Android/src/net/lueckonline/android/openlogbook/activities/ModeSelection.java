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
