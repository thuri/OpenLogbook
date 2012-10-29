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
package net.lueckonline.android.openlogbook.viewmodels.mainmenu;

import gueei.binding.Command;
import net.lueckonline.android.openlogbook.activities.CreateCar;
import net.lueckonline.android.openlogbook.activities.CreateDriver;
import net.lueckonline.android.openlogbook.activities.Export;
import net.lueckonline.android.openlogbook.activities.Preferences;
import android.view.View;

/**
 * @author thuri
 *
 */
public class MainMenuViewModel {
	
	public interface Eventhandler {
		void Start(Class<?> clazz);
	}
	
	private final Eventhandler controller;
	
	public MainMenuViewModel(Eventhandler controller){
		this.controller = controller;
	}

	public final Command openPreferences = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			startIntent(Preferences.class);
		}
	};
	
	public final Command openCarCreate = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			startIntent(CreateCar.class);
		}
	};
	
	public final Command openDriverCreate = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			startIntent(CreateDriver.class);
		}
	};
	
	public final Command openExport = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			startIntent(Export.class);
		}
	};
	
	protected void startIntent(Class<?> clazz) {
		this.controller.Start(clazz);
	}
}
