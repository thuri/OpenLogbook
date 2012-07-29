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
package net.lueckonline.android.openlogbook.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.lueckonline.android.openlogbook.model.Log;
import android.content.Context;
import android.os.Environment;

/**
 * @author thuri
 *
 */
public class ExportFile implements Exporter {

	private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private final File file;
	private final DateFormat dateFormat;
	private final DateFormat timeFormat;
	
	private BufferedWriter out = null;
	
	public ExportFile(Context context){
		String filename = ExportFile.formatter.format(new Date());
		File root = Environment.getExternalStorageDirectory();		
		this.file = new File(root, filename+".olexport");
		
		dateFormat = android.text.format.DateFormat.getDateFormat(context);
		timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		
	}

	public boolean open() throws IOException{
		try {
			if(!this.file.exists())
				this.file.createNewFile();
			
			if(out == null)
				out = new BufferedWriter(new FileWriter(file));
			
			return true;
		}
		catch(IOException e){
			android.util.Log.e(android.content.Context.ACTIVITY_SERVICE, "Error while trying to open file");
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.utils.ExportLog#export(net.lueckonline.android.openlogbook.model.Log)
	 */
	@Override
	public void export(final Log log) throws IOException {
		out.append(dateFormat.format(log.getStart()));
		out.append(";");
		out.append(timeFormat.format(log.getStart()));
		out.append(";");
		out.append(dateFormat.format(log.getStop()));
		out.append(";");
		out.append(timeFormat.format(log.getStop()));
		out.append(";");
		out.append(log.getCar().getLicensePlate());
		out.append(";");
		out.append(dateFormat.getNumberFormat().format(log.getDistance()));
		out.append(";");
		out.append(log.getDriver().getName());
		
		out.append(System.getProperty("line.separator"));
	}
	
	@Override
	public void export(String driver, String car, long start, long stop,
			float distance) throws IOException{
		
		final Date date = new Date();		
		
		date.setTime(start);
		out.append(dateFormat.format((date)));
		out.append(";");
		out.append(timeFormat.format(date));
		out.append(";");
		
		date.setTime(stop);
		out.append(dateFormat.format(date));
		out.append(";");
		out.append(timeFormat.format(date));
		out.append(";");

		out.append(dateFormat.getNumberFormat().format(distance));
		out.append(";");
		
		out.append(car);
		out.append(";");
		
		out.append(System.getProperty("line.separator"));
	}

	/**
	 * 
	 */
	public void close() {
		try {
			if(out != null)
				out.close();
		} catch (IOException e) {
			android.util.Log.e(android.content.Context.ACTIVITY_SERVICE, "Exception while trying to close export buffer writer:"+e.getMessage());
		}
	}
}
