package net.lueckonline.android.openlogbook;

import net.lueckonline.android.openlogbook.services.BluetoothService;
import gueei.binding.Binder;
import android.app.Application;
import android.content.Intent;

public class OpenLogbookApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
		
		startService(new Intent(this, BluetoothService.class));
	}
	
}
