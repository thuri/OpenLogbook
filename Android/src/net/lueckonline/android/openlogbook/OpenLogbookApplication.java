package net.lueckonline.android.openlogbook;

import net.lueckonline.android.openlogbook.activities.CreateCar;
import net.lueckonline.android.openlogbook.activities.CreateDriver;
import net.lueckonline.android.openlogbook.activities.Preferences;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.dataaccess.RepositoryFactory;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import gueei.binding.Binder;
import android.app.Application;
import android.content.Intent;

public class OpenLogbookApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
		
		ILogbookRepository repository = RepositoryFactory.getInstance(getApplicationContext());
		int mode = repository.getMode();
		
		switch (mode) {
			case OperationModes.UNKOWN:
				
				Intent intent = new Intent(this, Preferences.class);
				Intent carIntent = new Intent(this, CreateCar.class);
				Intent driverIntent = new Intent(this, CreateDriver.class);
				
				startActivity(intent);
				startActivity(carIntent);
				startActivity(driverIntent);
				
				break;
			case OperationModes.COMMUTER:
			case OperationModes.FIELDSTAFF:
				// mode already selected, so nothing necessary
				break;
			default:
				throw new IllegalArgumentException("Unknown OperationMode");
		}
	}
}
