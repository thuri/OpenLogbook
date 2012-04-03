package net.lueckonline.android.openlogbook;

import net.lueckonline.android.openlogbook.activities.ModeSelection;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class OpenLogbook extends Activity {
	
	private static final int MODE_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int mode = preferences.getInt("Mode", OperationModes.UNKOWN);
        
        switch(mode){
	        case OperationModes.UNKOWN:
	        	Intent intent = new Intent(this, ModeSelection.class);
	        	//although startActivityForResult is asynchronous the View of this onCreate will not be displayed
	        	//until the Activity Returned a result (see Android Dev Doku)
	        	startActivityForResult(intent, MODE_REQUEST);
	        	break;
	        case OperationModes.COMMUTER:
	        case OperationModes.FIELDSTAFF:
	        	//mode already selected, so nothing necessary
	        	break;
	        default:
	        	throw new IllegalArgumentException("Unknown OperationMode");
        }
        
        setContentView(R.layout.main);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode == Activity.RESULT_OK && requestCode == MODE_REQUEST){
    		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    		preferences.edit().putInt("Mode", data.getExtras().getInt("Mode")).commit();
    	}
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		TextView tv = (TextView) findViewById(R.id.helloTextView);
		
		tv.setText("Mode=" + getPreferences(MODE_PRIVATE).getInt("Mode", OperationModes.UNKOWN));
	}
}