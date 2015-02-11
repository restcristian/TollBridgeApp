package restituyo.androidbootcamp.tollbridgeapp;

import java.text.DecimalFormat;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TollResults extends Activity {
	
	protected TollBridgeDatabaseHelper m_Database = null;
	public TextView toll,addAxleToll,numberOfTrips,xtimesToll;
	public TextView ezpassToll,ezpassAddAxleToll,ezpassNumberOfTrips,ezpassXTimesToll;
	public TextView ezpassSavings;
	public DecimalFormat moneyFormat = new DecimalFormat("$0.00");
	public Button showBridge;
	Bundle j;
	public DecimalFormat money = new DecimalFormat("$##.##");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toll_results);
		j = getIntent().getExtras();
		m_Database = new TollBridgeDatabaseHelper(this.getApplicationContext());
		m_Database.createDatabase();
		
		toll = (TextView)findViewById(R.id.normalToll);
		addAxleToll = (TextView)findViewById(R.id.AddToll);
		numberOfTrips = (TextView)findViewById(R.id.xnumberOfTrips);
		xtimesToll = (TextView)findViewById(R.id.xtimesToll);
		
		ezpassToll = (TextView)findViewById(R.id.ezpassToll);
		ezpassAddAxleToll = (TextView)findViewById(R.id.ezpassAddToll);
		ezpassNumberOfTrips = (TextView)findViewById(R.id.ezpassnumberOfTrips);
		ezpassXTimesToll = (TextView)findViewById(R.id.ezpassxtimesToll);
		ezpassSavings = (TextView)findViewById(R.id.ezpassSavings);
		
		showBridge = (Button)findViewById(R.id.btnViewMap);
		
		populateData(j);
		
		showBridge.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(TollResults.this,MapBridge.class);
				i.putExtra("vehicleSelected", j.getString("vehicleSelected"));
				i.putExtra("facilitySelected",j.getString("facilitySelected"));
				i.putExtra("toll",toll.getText().toString());
				i.putExtra("eztoll", ezpassToll.getText().toString());
				startActivity(i);
			}
			
		});
		
	}
	
	private void populateData(Bundle bundle)
	{
		double ntoll,addtoll,numberoftrips,xtotal, eztotal;
		
		
		SQLiteQueryBuilder queryBuilder1 = new SQLiteQueryBuilder();
		queryBuilder1.setTables(Facility.FACILITY_TABLE_NAME+","+Price.PRICE_TABLE_NAME+","+Vehicle.VEHICLE_TABLE_NAME);
		String innerJoin =
				Price.PRICE_TABLE_NAME+"."+Price.VEHICLE_ID+"="+Vehicle.VEHICLE_TABLE_NAME+"."+"vehicle"+Vehicle._ID
				   +" AND " +Facility.FACILITY_TABLE_NAME+"."+"facility"+Facility._ID+"="+Price.PRICE_TABLE_NAME+"."+Price.FACILITY_ID
				   +" AND " +Vehicle.VEHICLE_TABLE_NAME+"."+Vehicle.VEHICLE_NAME+"='"+bundle.getString("vehicleSelected")+"'"
				   +" AND "+Facility.FACILITY_TABLE_NAME+"."+Facility.FACILITY_NAME+"='"+bundle.getString("facilitySelected")+"'";
		
		queryBuilder1.appendWhere(innerJoin);
		SQLiteDatabase db = m_Database.getReadableDatabase();
		String asColumnsToReturn[] = {
				Facility.FACILITY_TABLE_NAME+"."+Facility.FACILITY_NAME,
				Price.PRICE_TABLE_NAME+"."+Price.TOLLPRICE_CASH,
				Price.PRICE_TABLE_NAME+"."+Price.TOOLPRICE_EZPASS,
				Facility.FACILITY_TABLE_NAME +"."+Facility.FACILITY_AXLETOLL,
				Vehicle.VEHICLE_TABLE_NAME+"."+Vehicle.VEHICLE_NAME
		};
		Cursor cursor1 = queryBuilder1.query(db, asColumnsToReturn, null, null, null, null, null);
		
		if(cursor1.moveToFirst())
		{
			try{
			int trips = Integer.parseInt(bundle.getString("tripsNumber"));
			ntoll = cursor1.getDouble(cursor1.getColumnIndex(Price.TOLLPRICE_CASH));
			addtoll = (cursor1.getString(cursor1.getColumnIndex(Vehicle.VEHICLE_NAME))!="Car")?cursor1.getDouble(cursor1.getColumnIndex(Facility.FACILITY_AXLETOLL))* Integer.parseInt(bundle.getString("additionalAxle")):0.00; 
			xtotal = (ntoll + addtoll) * trips;
			
			
			
			toll.setText(money.format(ntoll));
			addAxleToll.setText(money.format(addtoll));
			numberOfTrips.setText(bundle.getString("tripsNumber"));
			
			ntoll = cursor1.getDouble(cursor1.getColumnIndex(Price.TOOLPRICE_EZPASS));
			 
			eztotal = (ntoll + addtoll) * trips;
			
		    ezpassToll.setText(money.format(ntoll));
		    ezpassAddAxleToll.setText(money.format(addtoll));
		    ezpassNumberOfTrips.setText(bundle.getString("tripsNumber"));
		    
		    xtimesToll.setText(money.format(xtotal));
		    ezpassXTimesToll.setText(money.format(eztotal));
		    
		    ezpassSavings.setText(money.format((xtotal - eztotal)));
		    
			}catch(Exception e)
			{
				Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toll_results, menu);
		return true;
	}

}
