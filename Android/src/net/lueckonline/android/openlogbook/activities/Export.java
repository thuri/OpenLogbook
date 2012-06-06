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

import java.util.ArrayList;
import java.util.List;

import net.lueckonline.android.openlogbook.R;
import net.lueckonline.android.openlogbook.activities.base.BaseActivity;
import net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.viewmodels.export.ExportViewModel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

/**
 * @author thuri
 *
 */
public class Export extends BaseActivity implements StartExportDelegate {

	private ExportViewModel vm = new ExportViewModel();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		vm.addStartExportDelegate(this);
		
		this.setAndBindRootView(R.layout.export, vm);
	}
	
	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.activities.StartExportDelegate#startExport()
	 */
	@Override
	public void startExport(){
		TaskParams params = new TaskParams();
		params.setRepository(getRepository());
		
		ProgressBar pB = (ProgressBar) findViewById(R.id.exportProgressBar);
		if(pB != null)
			pB.setVisibility(View.VISIBLE);

		new ExportTask().execute(params);
	}
	
	class TaskParams {
		private ILogbookRepository repository;

		public ILogbookRepository getRepository() {
			return repository;
		}

		public void setRepository(ILogbookRepository repository) {
			this.repository = repository;
		}
	}

	class ExportTask extends AsyncTask<TaskParams, Integer, Boolean> {
		
		@Override
		protected Boolean doInBackground(TaskParams... params) {
			if(params == null || params.length != 1){
				android.util.Log.e(ACTIVITY_SERVICE, "Can't execute task because of invalid Params");
				return false;
			}
		
			final ILogbookRepository repo = params[0].getRepository();
			
			if(repo == null){
				android.util.Log.e(ACTIVITY_SERVICE, "No repository to export from");
				return false;
			}
			
			final long count = repo.getLogCount();
			final long countPerLoop = 100;
			final long limitPerLoop = count / countPerLoop;
			
			//0  / 100		= 0
			//50 / 100		= 0
			//100 / 100     = 1
			//200 / 100		= 2
			//1000 / 100	= 10
			//10000 / 100	= 100
			//25000 / 100   = 250

			final List<Log> logs;

			if(count < 10000){
				logs = repo.getLogs();
			}
			else {
				logs = new ArrayList<Log>();
			
				for(int loop=0; loop < limitPerLoop; loop++){
					logs.addAll(repo.getLogs(loop * countPerLoop, (loop + 1) * countPerLoop - 1)); 
				}
			}
			
			if(!isCancelled()){
				return true;
			}
			else
				return false;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		
		}
	}
}


