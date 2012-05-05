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
package net.lueckonline.android.openlogbook.dataaccess;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * @author thuri
 *
 */
public class RepositoryFactory {

	private static Map<Context, ILogbookRepository> instanceMap = new HashMap<Context, ILogbookRepository>(); 
	
	public static ILogbookRepository getInstance(Context context){
		
		ILogbookRepository instance;	
		if(instanceMap.containsKey(context)) 
			 instance = instanceMap.get(context);
		else {
			instance = new LogbookRepository(context);
			instanceMap.put(context, instance);
		}
		
		return instance;
	}
}
