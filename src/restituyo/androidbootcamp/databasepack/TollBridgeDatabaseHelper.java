package restituyo.androidbootcamp.databasepack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TollBridgeDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_PATH ="/data/data/restituyo.androidbootcamp.tollbridgeapp/databases/";
	private static final String DATABASE_NAME ="tollbridgedb.db";
	private static final int DATABASE_VERSION = 1;
	
	public SQLiteDatabase dbSqlite;
	private final Context myContext;
	
	public TollBridgeDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}
	
	public void createDatabase()
	{
		createDB();
	}
	
	private void createDB()
	{
		boolean dbExists = DBExists();
		
		if(!dbExists)
		{
			this.getReadableDatabase();
			copyDBFromResource();
		}
	}
	private void copyDBFromResource()
	{
		InputStream instream = null;
		OutputStream outstream = null;
		
		String dbFilePath = DATABASE_PATH + DATABASE_NAME;
		
		try
		{
			instream = myContext.getAssets().open(DATABASE_NAME);
			outstream = new FileOutputStream(dbFilePath);
			
			byte[] buffer = new byte[1024];
			int lenght;
			while((lenght = instream.read(buffer)) > 0)
			{
				outstream.write(buffer,0,lenght);
			}
			outstream.close();
			outstream.flush();
			instream.close();
			
		}catch(IOException e)
		{
			throw new Error("Problem copying the database!!!");
		}
	}
	private boolean DBExists()
	{
		
		
		try
		{
			String databasePath = DATABASE_PATH + DATABASE_NAME;
			dbSqlite = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
			dbSqlite.setLocale(Locale.getDefault());
			//db.setLockingEnabled(true);
			dbSqlite.setVersion(1);
		}catch(SQLException e)
		{
			Log.e("SQLHELPER","DATABASE NOT FOUND!!");
		}
		if(dbSqlite != null)
		{
			dbSqlite.close();
		}
		
		return (dbSqlite!=null)?true:false;
	}
	@Override
	public void onCreate(SQLiteDatabase sqlDB) {
		// TODO Auto-generated method stub
		
		/*
		//Creating the Facility Table
		sqlDB.execSQL("CREATE TABLE " + Facility.FACILITY_TABLE_NAME+"("
						+Facility._ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"
						+Facility.FACILITY_NAME + "TEXT,"
						//+Facility.FACILITY_TOLLPRICE_CASH + "NUMBER,"
						//+Facility.FACILITY_TOLLPRICE_EZPASS +"NUMBER,"
						+Facility.FACILITY_LATITUDE +"NUMBER,"
						+Facility.FACILITY_LONGITUDE +"NUMBER" +
						");");
		
		//Creating the Vehicle Table
		sqlDB.execSQL("CREATE TABLE "+ Vehicle.VEHICLE_TABLE_NAME +"("
						 +Vehicle._ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"
						 +Vehicle.VEHICLE_NAME + "TEXT" +
						 ");");
		
		//Creating Axle Table
		sqlDB.execSQL("CREATE TABLE " + Axle.AXLE_TABLE_NAME + "(" 
					  +Axle._ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"
					  +Axle.AXLE_MIN + "INTEGER,"
					  +Axle.AXLE_MAX + "INTEGER,"
					  +Axle.VEHICLE_ID + "INTEGER, " 
					  +"FOREIGN KEY("+Axle.VEHICLE_ID+") REFERENCES "+Vehicle.VEHICLE_TABLE_NAME +"("+Vehicle._ID+")"
					  +");");
		//Creating Price Table
		sqlDB.execSQL("CREATE TABLE " + Price.PRICE_TABLE_NAME + "("
					   +Price._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," 
					   +Price.TOLLPRICE_CASH +"NUMBER,"
					   +Price.TOOLPRICE_EZPASS +"NUMBER,"
					   +Price.VEHICLE_ID + "INTEGER,"
					   +Price.FACILITY_ID + "INTEGER,"
					   +"FOREIGN KEY("+Price.FACILITY_ID+") REFERENCES "+Facility.FACILITY_TABLE_NAME +"("+Facility._ID+"),"
					   +"FOREIGN KEY("+Price.VEHICLE_ID+") REFERENCES "+Vehicle.VEHICLE_TABLE_NAME +"("+Vehicle._ID+")" +
					   		");");
					   		*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onOpen(SQLiteDatabase sqlDB)
	{
		super.onOpen(sqlDB);
	}

}
