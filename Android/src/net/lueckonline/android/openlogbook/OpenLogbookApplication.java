package net.lueckonline.android.openlogbook;

import gueei.binding.Binder;
import android.app.Application;

public class OpenLogbookApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
	}
}
