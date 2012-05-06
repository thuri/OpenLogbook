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
	private static final int DATABASE_VERSION = 1;
	
	/*
	 * CAR TABLE
	 */
	protected static final String 	CAR_TABLE_NAME 		= "cars";
	protected static final String 	CAR_COLUMN_ID 		= "id";
	protected static final String 	CAR_COLUMN_PLATE 	= "license_plate";
	private static final String 	CAR_TABLE_CREATE 	= "CREATE TABLE "+CAR_TABLE_NAME+ " ("+
															CAR_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
															CAR_COLUMN_PLATE+" varchar(255) UNIQUE)";
	
	/*
	 * PERSON TABLE
	 */
	protected static final String 	PERSON_TABLE_NAME 		= "persons";
	protected static final String 	PERSON_COLUMN_ID 		= "id";
	protected static final String 	PERSON_COLUMN_NAME 		= "name";
	private static final String 	PERSON_TABLE_CREATE		= "CREATE TABLE "+PERSON_TABLE_NAME+" ("+
																PERSON_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
																PERSON_COLUMN_NAME +" varchar(255))";
	
	/*
	 * LOG TABLE
	 */
	protected static final String	LOG_TABLE_NAME 			= "logs";
	protected static final String 	LOG_COLUMN_ID			= "id";
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
								"	foreign key("+LOG_COLUMN_CAR_FK+") references "+CAR_TABLE_NAME+"(id)," +
								" 	foreign key("+LOG_COLUMN_DRIVER_FK+") references "+PERSON_TABLE_NAME+"(id))";

	public DbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PERSON_TABLE_CREATE);
		db.execSQL("INSERT INTO "+DbHelper.PERSON_TABLE_NAME +" ("+DbHelper.PERSON_COLUMN_NAME+") VALUES (\"First Driver\")");
		
		db.execSQL(CAR_TABLE_CREATE);
		db.execSQL("INSERT INTO "+DbHelper.CAR_TABLE_NAME +" ("+DbHelper.CAR_COLUMN_PLATE+") VALUES (\"A-PP 1234\")");
		db.execSQL("INSERT INTO "+DbHelper.CAR_TABLE_NAME +" ("+DbHelper.CAR_COLUMN_PLATE+") VALUES (\"A-PP 4321\")");
		
		db.execSQL(LOG_TABLE_CREATE);

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}	
}
