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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lueckonline.android.openlogbook.model.Car;
import net.lueckonline.android.openlogbook.model.Device;
import net.lueckonline.android.openlogbook.model.Log;
import net.lueckonline.android.openlogbook.model.Person;
import net.lueckonline.android.openlogbook.utils.Exporter;
import net.lueckonline.android.openlogbook.utils.OperationModes;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author thuri
 * 
 */
public class LogbookRepository implements ILogbookRepository {

	private DbHelper dbHelper;

	protected LogbookRepository(Context context) {
		dbHelper = new DbHelper(context);
	}

	@Override
	public List<Car> getCars() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor query = db.query(DbHelper.CAR_TABLE_NAME, null, null, null,
				null, null, null);

		List<Car> cars = new ArrayList<Car>(query.getCount());
		while (query.moveToNext()) {
			Car car = new Car();
			car.setId(query.getInt(query.getColumnIndex(DbHelper.CAR_COLUMN_ID)));
			car.setLicensePlate(query.getString(query
					.getColumnIndex(DbHelper.CAR_COLUMN_PLATE)));
			cars.add(car);
		}
		query.close();

		return cars;
	}

	@Override
	public List<Person> getDrivers() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor query = db.query(DbHelper.PERSON_TABLE_NAME, null, null, null,
				null, null, null);

		List<Person> drivers = new ArrayList<Person>(query.getCount());
		while (query.moveToNext()) {
			Person p = makePerson(query);

			drivers.add(p);
		}
		query.close();

		return drivers;
	}

	@Override
	public int getMode() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor query = db.query(DbHelper.SETTINGS_TABLE_NAME, null,
				DbHelper.SETTINGS_COLUMN_KEY + " = ?", new String[] { "Mode" },
				null, null, null);

		int mode = OperationModes.UNKOWN;
		if (query.moveToFirst())
			mode = query.getInt(query
					.getColumnIndex(DbHelper.SETTINGS_COLUMN_VALUE));

		query.close();

		return mode;
	}

	@Override
	public Person getBluetoothUser() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();

		/*
		 * find the pk to the bluetooth person in the settings table
		 */
		final Cursor settingsQuery = db.query(DbHelper.SETTINGS_TABLE_NAME,
				null, DbHelper.SETTINGS_COLUMN_KEY + " =?",
				new String[] { "BluetoothUser" }, null, null, null);
		String personPK = null;
		while (settingsQuery.moveToFirst())
			personPK = settingsQuery.getString(settingsQuery
					.getColumnIndex(DbHelper.SETTINGS_COLUMN_VALUE));
		settingsQuery.close();

		if (personPK == null)
			return null;

		/*
		 * if a pk was found look the person up in the person table
		 */
		final Cursor personQuery = db.query(DbHelper.PERSON_TABLE_NAME, null,
				DbHelper.PERSON_COLUMN_ID + " =?", new String[] { personPK },
				null, null, null);
		// initialize with null, so if not found we can return null
		Person bluetoothPerson = null;
		while (personQuery.moveToFirst())
			bluetoothPerson = makePerson(personQuery);
		personQuery.close();

		return bluetoothPerson;
	}

	@Override
	public List<Device> getDevices() {
		List<Device> devices = new ArrayList<Device>();
		
		final String deviceJoinCar = DbHelper.DEVICE_TABLE_NAME+ " d " +
				"LEFT OUTER JOIN "+DbHelper.CAR_TABLE_NAME + 
				" c ON (d."+DbHelper.DEVICE_COLUMN_CAR_FK+" = c."+DbHelper.CAR_COLUMN_ID+")";
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor query = db.query(deviceJoinCar, null, null, null, null, null, null);
		final int carIdIdx = (query.getColumnIndex(DbHelper.CAR_COLUMN_ID));
		while(query.moveToNext()){
			Car c = null;
			
			if(!query.isNull(carIdIdx)){ 
				c = new Car();
				c.setId(query.getInt(carIdIdx));
				c.setLicensePlate(query.getString(query.getColumnIndex(DbHelper.CAR_COLUMN_PLATE)));
			}
			
			Device d = new Device();
			d.setCar(c);
			d.setName(query.getString(query.getColumnIndex(DbHelper.DEVICE_COLUMN_NAME)));
			
			devices.add(d);
		}
		
		return devices;
	}

	/**
	 * @param query
	 * @return
	 */
	private Person makePerson(Cursor query) {
		Person p = new Person();
		p.setId(query.getInt(query.getColumnIndex(DbHelper.PERSON_COLUMN_ID)));
		p.setName(query.getString(query
				.getColumnIndex(DbHelper.PERSON_COLUMN_NAME)));
		return p;
	}

	@Override
	public void add(Car car) throws DataAccessException {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DbHelper.CAR_COLUMN_PLATE, car.getLicensePlate());

		try {
			db.insertOrThrow(DbHelper.CAR_TABLE_NAME, null, values);
		} catch (SQLException sqlex) {
			throw new DataAccessException(
					"Unable to insert car with licensePlate "
							+ car.getLicensePlate(), sqlex);
		}
	}

	@Override
	public void add(Person driver) throws DataAccessException {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DbHelper.PERSON_COLUMN_NAME, driver.getName());

		try {
			db.insertOrThrow(DbHelper.PERSON_TABLE_NAME, null, values);
		} catch (SQLException sqlex) {
			throw new DataAccessException("Unable to add Driver to database",
					sqlex);
		}

	}

	@Override
	public void add(Log log) throws DataAccessException {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DbHelper.LOG_COLUMN_CAR_FK, log.getCar().getId());
		values.put(DbHelper.LOG_COLUMN_DRIVER_FK, log.getDriver().getId());
		values.put(DbHelper.LOG_COLUMN_DISTANCE, log.getDistance());
		values.put(DbHelper.LOG_COLUMN_START, log.getStart().getTime());
		values.put(DbHelper.LOG_COLUMN_STOP, log.getStop().getTime());

		try {
			db.insertOrThrow(DbHelper.LOG_TABLE_NAME, null, values);
		} catch (SQLException sqlex) {
			throw new DataAccessException("Unable to add Log to database",
					sqlex);
		}
	}

	@Override
	public void setMode(int mode) throws DataAccessException {
		
		final String whereClause = DbHelper.SETTINGS_COLUMN_KEY+"=?";
		final String[] whereArgs = new String[]{"Mode"};
		
		SQLiteDatabase rdb = this.dbHelper.getReadableDatabase();
		Cursor query = rdb.query(DbHelper.SETTINGS_TABLE_NAME, null, whereClause, whereArgs, null, null, null);
		
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DbHelper.SETTINGS_COLUMN_KEY, "Mode");
		values.put(DbHelper.SETTINGS_COLUMN_VALUE, mode);
		
		try {
			if(query.getCount() == 0)		
				db.insertOrThrow(DbHelper.SETTINGS_TABLE_NAME, null, values);
			else
				db.update(DbHelper.SETTINGS_TABLE_NAME, values, whereClause, whereArgs);
		} catch (SQLException sqlex) {
			throw new DataAccessException("Unable to set mode in database",
					sqlex);
		}
	}

	private final String logJoin = DbHelper.LOG_TABLE_NAME + " l " + "JOIN "
			+ DbHelper.CAR_TABLE_NAME + " c ON (l."
			+ DbHelper.LOG_COLUMN_CAR_FK + " = c." + DbHelper.CAR_COLUMN_ID
			+ ") " + "JOIN " + DbHelper.PERSON_TABLE_NAME + " d ON (l."
			+ DbHelper.LOG_COLUMN_DRIVER_FK + " = d."
			+ DbHelper.PERSON_COLUMN_ID + ") ";

	@Override
	public long getLogCount() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();

		return DatabaseUtils.longForQuery(db,
				"SELECT COUNT(*) FROM " + logJoin, null);
	}

	@Override
	public void exportLogs(long offset, long limit, Exporter exporter)
			throws DataAccessException {
		final SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		final Cursor query;

		// final long dbgStart = System.currentTimeMillis();
		try {
			query = db.query(logJoin, null, null, null, null, null,
					DbHelper.LOG_COLUMN_START, offset + "," + limit);
		} catch (SQLException e) {
			throw new DataAccessException("Error while trying to fetch logs: "
					+ e.getMessage(), e);
		}
		// long queryTime = System.currentTimeMillis() - dbgStart;
		// android.util.Log.d(Context.ACTIVITY_SERVICE,"Time to fetch logs from db:"+
		// queryTime);

		String driver;
		int driverIdx = query.getColumnIndex(DbHelper.PERSON_COLUMN_NAME);
		String car;
		int carIdx = query.getColumnIndex(DbHelper.CAR_COLUMN_PLATE);
		long start;
		int startIdx = query.getColumnIndex(DbHelper.LOG_COLUMN_START);
		long stop;
		int stopIdx = query.getColumnIndex(DbHelper.LOG_COLUMN_STOP);
		float distance;
		int distanceIdx = query.getColumnIndex(DbHelper.LOG_COLUMN_DISTANCE);

		// long startIterateQuery = System.currentTimeMillis();
		// android.util.Log.d(Context.ACTIVITY_SERVICE,
		// "query.getCount()="+query.getCount());
		// int count = 0;
		while (query.moveToNext()) {

			// long startExportOneLog = System.currentTimeMillis();
			driver = query.getString(driverIdx);
			car = query.getString(carIdx);
			start = query.getLong(startIdx);
			stop = query.getLong(stopIdx);
			distance = query.getFloat(distanceIdx);
			// long fetchOneLogData = System.currentTimeMillis() -
			// startExportOneLog;
			// android.util.Log.d(Context.ACTIVITY_SERVICE,
			// "Time to fetch data of one log:"+fetchOneLogData);
			try {
				exporter.export(driver, car, start, stop, distance);
				// long writeOneLog = System.currentTimeMillis() -
				// startExportOneLog - fetchOneLogData;
				// android.util.Log.d(Context.ACTIVITY_SERVICE,"Time to write one log:"+writeOneLog);
			} catch (IOException e) {
				throw new DataAccessException("Unable to export all logs", e);
			}
			// count++;
		}
		// android.util.Log.d(Context.ACTIVITY_SERVICE,
		// "counted iterations"+count);
		// android.util.Log.d(Context.ACTIVITY_SERVICE,
		// "time iterate through query"+(System.currentTimeMillis() -
		// startIterateQuery));

		// long closingQuery = System.currentTimeMillis();

		query.close();

		// android.util.Log.d(Context.ACTIVITY_SERVICE, "Time to close query" +
		// (System.currentTimeMillis() - closingQuery));

	}

	@Override
	public void setBluetoothUser(Person user) {

		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		if (user == null)
			db.delete(DbHelper.SETTINGS_TABLE_NAME,
					DbHelper.SETTINGS_COLUMN_KEY + " =?",
					new String[] { "BluetoothUser" });
		else {
			ContentValues values = new ContentValues();
			values.put(DbHelper.SETTINGS_COLUMN_KEY, user.getId());

			int count = db.update(DbHelper.SETTINGS_TABLE_NAME, values,
					DbHelper.SETTINGS_COLUMN_KEY + " =?",
					new String[] { "BluetoothUser" });

			if (count == 0)
				db.insert(DbHelper.SETTINGS_TABLE_NAME, null, values);
		}
	}
}
