package restituyo.androidbootcamp.tollbridgeapp;

import java.text.DecimalFormat;

import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Axle;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Facility;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Price;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Vehicle;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {
	protected TollBridgeDatabaseHelper m_Database = null;
	public Spinner vehicleSpinner;
	public Spinner axleSpinner;
	public Spinner facilitySpinner;
	public Button calculateButton;
	public EditText numberOfTrips;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_Database = new TollBridgeDatabaseHelper(this.getApplicationContext());
		m_Database.createDatabase();
		
		vehicleSpinner = (Spinner)findViewById(R.id.vehicleSpinner);
		facilitySpinner = (Spinner)findViewById(R.id.facilitiesSpinner);
		axleSpinner = (Spinner)findViewById(R.id.axleSpinner);
		calculateButton = (Button)findViewById(R.id.calcToll);
		numberOfTrips = (EditText)findViewById(R.id.numberOfTrips);
		
		calculateButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent(MainActivity.this, TollResults.class);
				i.putExtra("vehicleSelected", vehicleSpinner.getSelectedItem().toString());
				i.putExtra("facilitySelected", facilitySpinner.getSelectedItem().toString());
				i.putExtra("tripsNumber",(numberOfTrips.getText().toString().isEmpty())?"0":numberOfTrips.getText().toString());
				i.putExtra("additionalAxle", axleSpinner.getSelectedItem().toString());
				startActivity(i);
			}
			
		});
		vehicleSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				fill_axle_spinner();
				fill_facility_spinner();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		//try{
		fill_vehicle_spinner();
		
		//fill_facility_spinner();
		//fill_axle_spinner();
		//}catch(Exception e){
		
			//Toast.makeText(this.getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
		//}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void fill_vehicle_spinner()
	{
		//Creating the Query Builder
		SQLiteQueryBuilder queryBuild = new SQLiteQueryBuilder();
		queryBuild.setTables(Vehicle.VEHICLE_TABLE_NAME);
		//Getting the database and executing
		SQLiteDatabase db = m_Database.getReadableDatabase();
		String asColumnsToReturn[] = {Vehicle.VEHICLE_NAME};
		Cursor cursor1 = queryBuild.query(db, asColumnsToReturn, null, null, null, null, Vehicle.DEFAULT_SORT_ORDER);
		
		
		if(cursor1.moveToFirst())
		{
			ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
			
			for(int x = 0; x < cursor1.getCount(); x++)
			{
				adapter.add(cursor1.getString(cursor1.getColumnIndex(Vehicle.VEHICLE_NAME)));
				//Toast.makeText(this.getApplicationContext(), cursor1.getString(cursor1.getColumnIndex(Vehicle.VEHICLE_NAME)), Toast.LENGTH_SHORT).show();
				cursor1.moveToNext();
			}
			
			vehicleSpinner.setAdapter(adapter);
		}
		
		cursor1.close();
		db.close();
			
	}
	private void fill_facility_spinner()
	{
		//Creating the Query Builder
		SQLiteQueryBuilder queryBuild = new SQLiteQueryBuilder();
		queryBuild.setTables(Facility.FACILITY_TABLE_NAME+","+Vehicle.VEHICLE_TABLE_NAME+","+Price.PRICE_TABLE_NAME);
		String innerJoin = Price.PRICE_TABLE_NAME+"."+Price.VEHICLE_ID+"="+Vehicle.VEHICLE_TABLE_NAME+"."+"vehicle"+Vehicle._ID
						   +" AND " +Facility.FACILITY_TABLE_NAME+"."+"facility"+Facility._ID+"="+Price.PRICE_TABLE_NAME+"."+Price.FACILITY_ID
						   +" AND " +Vehicle.VEHICLE_TABLE_NAME+"."+Vehicle.VEHICLE_NAME+"='"+vehicleSpinner.getSelectedItem().toString()+"'";
		
		queryBuild.appendWhere(innerJoin);
		//Getting the database and executing
		SQLiteDatabase db = m_Database.getReadableDatabase();
		String asColumnsToReturn[] = {Facility.FACILITY_NAME};
		Cursor cursor1 = queryBuild.query(db, asColumnsToReturn, null, null, null, null, Facility.DEFAULT_SORT_ORDER);
		
		
		
		if(cursor1.moveToFirst())
		{
			ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
			
			for(int x = 0; x < cursor1.getCount(); x++)
			{
				adapter.add(cursor1.getString(cursor1.getColumnIndex(Facility.FACILITY_NAME)));
				//Toast.makeText(this.getApplicationContext(), cursor1.getString(cursor1.getColumnIndex(Vehicle.VEHICLE_NAME)), Toast.LENGTH_SHORT).show();
				cursor1.moveToNext();
			}
			
			facilitySpinner.setAdapter(adapter);
		}
		
		cursor1.close();
		db.close();
			
	}
	public void fill_axle_spinner()
	{
		SQLiteQueryBuilder queryBuild = new SQLiteQueryBuilder();
		queryBuild.setTables(Axle.AXLE_TABLE_NAME+","+Vehicle.VEHICLE_TABLE_NAME);
		//Subquery
		String subQuery = "(SELECT "+"Vehicle"+Vehicle._ID + " FROM "+Vehicle.VEHICLE_TABLE_NAME+" WHERE "+Vehicle.VEHICLE_NAME+"='"+vehicleSpinner.getSelectedItem().toString()+"')";
		//Getting the database and executing
		SQLiteDatabase db = m_Database.getReadableDatabase();
		String asColumnsToReturn[] = {Axle.AXLE_TABLE_NAME+"."+Axle.AXLE_MIN,Axle.AXLE_TABLE_NAME+"."+Axle.AXLE_MAX};
		queryBuild.appendWhere(Axle.AXLE_TABLE_NAME+"."+Axle.VEHICLE_ID +"="+subQuery);
		Cursor cursor1 = queryBuild.query(db, asColumnsToReturn, null, null, null, null, null);
		
		
		
		if(cursor1.moveToFirst())
		{
			ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
			
			for(int x = cursor1.getInt(cursor1.getColumnIndex(Axle.AXLE_MIN)); x <= cursor1.getInt(cursor1.getColumnIndex(Axle.AXLE_MAX)); x++)
			{
				adapter.add(Integer.toString(x));
			}
			
			axleSpinner.setAdapter(adapter);
		}
		
		cursor1.close();
		db.close();
			
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(m_Database != null)
		{
			m_Database.close();
		}
	}

}
