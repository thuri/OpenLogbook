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
package net.lueckonline.android.openlogbook.viewmodels.export;

import gueei.binding.Command;

import java.util.ArrayList;
import java.util.List;

import net.lueckonline.android.openlogbook.activities.StartExportDelegate;
import android.view.View;

/**
 * @author thuri
 *
 */
public class ExportViewModel {
	
	private final List<StartExportDelegate> delegates = new ArrayList<StartExportDelegate>();
	
	public final Command export = new Command(){
		@Override
		public void Invoke(View arg0, Object... arg1) {
			raiseStartExport();
		}
	};

	/**
	 * @param delegate
	 */
	public void addStartExportDelegate(StartExportDelegate delegate) {
		this.delegates.add(delegate);
	}
	
	private void raiseStartExport(){
		
		for(StartExportDelegate d : delegates)
			d.startExport();
	}
}
