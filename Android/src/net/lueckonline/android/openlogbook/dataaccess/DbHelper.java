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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author thuri
 *
 */
public class DbHelper extends SQLiteOpenHelper {

	protected static final String DATABASE_NAME = "logbook";
	private static final int DATABASE_VERSION = 2;
	
	/*
	 * CAR TABLE
	 */
	protected static final String 	CAR_TABLE_NAME 		= "cars";
	protected static final String 	CAR_COLUMN_ID 		= "car_id";
	protected static final String 	CAR_COLUMN_PLATE 	= "license_plate";
	private static final String 		CAR_TABLE_CREATE 	= "CREATE TABLE "+CAR_TABLE_NAME+ " ("+
															CAR_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
															CAR_COLUMN_PLATE+" varchar(255) UNIQUE)";
	
	/*
	 * PERSON TABLE
	 */
	protected static final String 	PERSON_TABLE_NAME 		= "persons";
	protected static final String 	PERSON_COLUMN_ID 		= "person_id";
	protected static final String 	PERSON_COLUMN_NAME 		= "name";
	private static final String 		PERSON_TABLE_CREATE		= "CREATE TABLE "+PERSON_TABLE_NAME+" ("+
																PERSON_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
																PERSON_COLUMN_NAME +" varchar(255))";
	
	/*
	 * LOG TABLE
	 */
	protected static final String		LOG_TABLE_NAME 			= "logs";
	protected static final String 	LOG_COLUMN_ID			= "log_id";
	protected static final String 	LOG_COLUMN_CAR_FK 		= "car_fk";
	protected static final String 	LOG_COLUMN_DRIVER_FK 	= "driver_fk";
	protected static final String 	LOG_COLUMN_START 		= "start";
	protected static final String 	LOG_COLUMN_STOP 		= "stop";
	protected static final String 	LOG_COLUMN_DISTANCE 	= "distance";
	
	private static final String LOG_TABLE_CREATE ="CREATE TABLE "+LOG_TABLE_NAME + 
								" (	"+
									LOG_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
									LOG_COLUMN_CAR_FK +" integer not null, " +
									LOG_COLUMN_DRIVER_FK+" integer not null,"+
									LOG_COLUMN_START +" integer not null," +
									LOG_COLUMN_STOP +" integer not null," +
									LOG_COLUMN_DISTANCE +" real not null," +
								"	foreign key("+LOG_COLUMN_CAR_FK+") references "+CAR_TABLE_NAME+"("+CAR_COLUMN_ID+")," +
								" 	foreign key("+LOG_COLUMN_DRIVER_FK+") references "+PERSON_TABLE_NAME+"("+PERSON_COLUMN_ID+"))";
	
	/*
	 * DEVICES TABLE 
	 */
	protected static final String 	DEVICE_TABLE_NAME		= "devices";
	protected static final String 	DEVICE_COLUMN_ID		= "id";
	protected static final String 	DEVICE_COLUMN_CAR_FK	= "car_fk";
	protected static final String		DEVICE_COLUMN_NAME		= "name";
//	protected static final String		DEVICE_COLUMN_ISTRIGGER = "isTrigger";
	
	private static final String DEVICE_TABLE_CREATE ="CREATE TABLE "+DEVICE_TABLE_NAME+
								" ( " + 
									DEVICE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ 
									DEVICE_COLUMN_CAR_FK + " integer ," + 
									DEVICE_COLUMN_NAME + " varchar(255) not null," +
//									DEVICE_COLUMN_ISTRIGGER + " integer not null default 0," +
								"	foreign key("+DEVICE_COLUMN_CAR_FK+") references "+CAR_TABLE_NAME+"("+CAR_COLUMN_ID+")"+		
								")";
	
	/*
	 * SETTINGS TABLE
	 */
	protected static final String 	SETTINGS_TABLE_NAME = "settings";
	protected static final String 	SETTINGS_COLUMN_KEY = "key";
	protected static final String 	SETTINGS_COLUMN_VALUE = "value";
	
	private static final String SETTINGS_TABLE_CREATE = "CREATE TABLE "+SETTINGS_TABLE_NAME + "(" + 
														  SETTINGS_COLUMN_KEY 	+ " varchar(255) not null," +
														  SETTINGS_COLUMN_VALUE + " varchar(1024) not null " +
														  ")";

	public DbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(PERSON_TABLE_CREATE);
		
		db.execSQL(CAR_TABLE_CREATE);
		
		db.execSQL(LOG_TABLE_CREATE);
		
		db.execSQL(SETTINGS_TABLE_CREATE);
		
		db.execSQL(DEVICE_TABLE_CREATE);

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}	
}
