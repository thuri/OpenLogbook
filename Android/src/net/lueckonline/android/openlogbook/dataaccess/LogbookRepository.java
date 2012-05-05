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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;

/**
 * @author thuri
 *
 */
public class LogbookRepository implements ILogbookRepository {

	private DbHelper dbHelper;
	
	protected LogbookRepository(Context context){
		dbHelper = new DbHelper(context);
	}
	
	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository#getCars()
	 */
	@Override
	public List<Car> getCars() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor query = db.query(DbHelper.CAR_TABLE_NAME, null, null, null, null, null, null);
		
		List<Car> cars = new ArrayList<Car>(query.getCount());
		while(query.moveToNext()){
			Car car = new Car();
			car.setId(query.getInt(query.getColumnIndex(DbHelper.CAR_COLUMN_ID)));
			car.setLicensePlate(query.getString(query.getColumnIndex(DbHelper.CAR_COLUMN_PLATE)));
			cars.add(car);
		}
		
		return cars;
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository#getDrivers()
	 */
	@Override
	public List<Person> getDrivers() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor query = db.query(DbHelper.PERSON_TABLE_NAME, null, null, null, null, null, null);
		
		List<Person> drivers = new ArrayList<Person>(query.getCount());
		while(query.moveToNext()){
			Person p = new Person();
			p.setId(query.getInt(query.getColumnIndex(DbHelper.PERSON_COLUMN_ID)));
			p.setName(query.getString(query.getColumnIndex(DbHelper.PERSON_COLUMN_NAME)));
			
			drivers.add(p);
		}
		
		return drivers;
	}

	/* (non-Javadoc)
	 * @see net.lueckonline.android.openlogbook.dataaccess.ILogbookRepository#addLog(net.lueckonline.android.openlogbook.model.Log)
	 */
	@Override
	public void addLog(Log log) {
		// TODO Auto-generated method stub

	}

}
