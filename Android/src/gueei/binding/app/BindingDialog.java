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
package gueei.binding.app;

import gueei.binding.AttributeBinder;
import gueei.binding.Binder;
import gueei.binding.Binder.InflateResult;
import net.lueckonline.android.openlogbook.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * @author thuri
 *
 */
public abstract class BindingDialog extends Dialog {

	private final Context context;
	
	/**
	 * @param context
	 */
	public BindingDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		InflateResult result = Binder.inflateView(context, R.layout.addtrigger, null, false);
		this.setContentView(result.rootView);
		
		for(View v: result.processedViews){
			AttributeBinder.getInstance().bindView(context, v, getViewModel());
		}		
	};
	
	protected abstract Object getViewModel();
	
}
