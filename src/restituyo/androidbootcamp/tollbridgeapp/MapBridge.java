package restituyo.androidbootcamp.tollbridgeapp;

import java.text.DecimalFormat;

import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Facility;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Price;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabase.Vehicle;
import restituyo.androidbootcamp.databasepack.TollBridgeDatabaseHelper;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
public class MapBridge extends Activity {

	protected TollBridgeDatabaseHelper m_Database = null;
	public Bundle bundle;
	public Spinner mapList;
	double currentLatitude,currentLongitude;
	String currentXtoll,currentEztoll;
	String currentFacility;
	public GoogleMap map;
	public TextView txtBridges;
	DecimalFormat money = new DecimalFormat("$##.##");
	boolean flag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_bridge);
		// Get a handle to the Map Fragment
		bundle = getIntent().getExtras();
		currentFacility = bundle.getString("facilitySelected");
		currentXtoll = bundle.getString("toll");
		currentEztoll = bundle.getString("eztoll");
		
		mapList = (Spinner)findViewById(R.id.facilitiesSpinner1);
		txtBridges = (TextView)findViewById(R.id.vehicletxt);
		
		txtBridges.setText("Bridges where "+ bundle.getString("vehicleSelected")+"s can go through. (Selectable)");
		mapList.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentFacility =(flag==false)?currentFacility: mapList.getSelectedItem().toString();
				get_current_map_data();
				update_map();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		get_current_map_data();
		fill_map_list();
		
		//update_map();
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		
	}
	
	private void update_map()
	{
		
		LatLng bridge = new LatLng(currentLatitude, currentLongitude);
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(bridge, 13));
		map.addMarker(new MarkerOptions()
        .title(currentFacility)
        .snippet("Normal Toll Fare:$"+currentXtoll+"\nEZPass Toll:$"+currentEztoll)
        .position(bridge));
		flag=true;
		
	}
	private void fill_map_list()
	{
		SQLiteQueryBuilder queryBuild = new SQLiteQueryBuilder();
		queryBuild.setTables(Facility.FACILITY_TABLE_NAME+","+Vehicle.VEHICLE_TABLE_NAME+","+Price.PRICE_TABLE_NAME);
		String innerJoin = Price.PRICE_TABLE_NAME+"."+Price.VEHICLE_ID+"="+Vehicle.VEHICLE_TABLE_NAME+"."+"vehicle"+Vehicle._ID
						   +" AND " +Facility.FACILITY_TABLE_NAME+"."+"facility"+Facility._ID+"="+Price.PRICE_TABLE_NAME+"."+Price.FACILITY_ID
						   +" AND " +Vehicle.VEHICLE_TABLE_NAME+"."+Vehicle.VEHICLE_NAME+"='"+bundle.getString("vehicleSelected")+"'";
		
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
			
			mapList.setAdapter(adapter);
		}
		
		cursor1.close();
		db.close();
	}
	private void get_current_map_data()
	{
		
		m_Database = new TollBridgeDatabaseHelper(this.getApplicationContext());
		m_Database.createDatabase();
		
		SQLiteQueryBuilder queryBuilder1 = new SQLiteQueryBuilder();
		queryBuilder1.setTables(Facility.FACILITY_TABLE_NAME+","+Price.PRICE_TABLE_NAME+","+Vehicle.VEHICLE_TABLE_NAME);
		String innerJoin =
				Price.PRICE_TABLE_NAME+"."+Price.VEHICLE_ID+"="+Vehicle.VEHICLE_TABLE_NAME+"."+"vehicle"+Vehicle._ID
				   +" AND " +Facility.FACILITY_TABLE_NAME+"."+"facility"+Facility._ID+"="+Price.PRICE_TABLE_NAME+"."+Price.FACILITY_ID
				   +" AND " +Vehicle.VEHICLE_TABLE_NAME+"."+Vehicle.VEHICLE_NAME+"='"+bundle.getString("vehicleSelected")+"'"
				   +" AND "+Facility.FACILITY_TABLE_NAME+"."+Facility.FACILITY_NAME+"='"+currentFacility+"'";
	 
		queryBuilder1.appendWhere(innerJoin);
		SQLiteDatabase db = m_Database.getReadableDatabase();
		String asColumnsToReturn[] = {
				Facility.FACILITY_TABLE_NAME+"."+Facility.FACILITY_NAME,
				Price.PRICE_TABLE_NAME+"."+Price.TOLLPRICE_CASH,
				Price.PRICE_TABLE_NAME+"."+Price.TOOLPRICE_EZPASS,
				Facility.FACILITY_TABLE_NAME +"."+Facility.FACILITY_AXLETOLL,
				Vehicle.VEHICLE_TABLE_NAME+"."+Vehicle.VEHICLE_NAME,
				Facility.FACILITY_TABLE_NAME+"."+Facility.FACILITY_LATITUDE,
				Facility.FACILITY_TABLE_NAME+"."+Facility.FACILITY_LONGITUDE
		};
		Cursor cursor1 = queryBuilder1.query(db, asColumnsToReturn, null, null, null, null, null);
		
		if(cursor1.moveToFirst())
		{
			currentFacility = cursor1.getString(cursor1.getColumnIndex(Facility.FACILITY_NAME));
			currentLatitude = cursor1.getDouble(cursor1.getColumnIndex(Facility.FACILITY_LATITUDE));
			currentLongitude = cursor1.getDouble(cursor1.getColumnIndex(Facility.FACILITY_LONGITUDE));
			currentXtoll = cursor1.getString(cursor1.getColumnIndex(Price.TOLLPRICE_CASH));
			currentEztoll = cursor1.getString(cursor1.getColumnIndex(Price.TOOLPRICE_EZPASS));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_bridge, menu);
		return true;
	}

}
