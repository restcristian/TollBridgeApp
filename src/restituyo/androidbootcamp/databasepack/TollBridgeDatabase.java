package restituyo.androidbootcamp.databasepack;

import android.provider.BaseColumns;
public final class TollBridgeDatabase {
	
	private TollBridgeDatabase(){}
	
	//Facility Table
	public static final class Facility implements BaseColumns {

        private Facility() {}
        
        public static final String FACILITY_TABLE_NAME = "table_facility";
        
        public static final String FACILITY_NAME = "facility_name";
       // public static final String FACILITY_ID = "facility_id";
        //public static final String FACILITY_TOLLPRICE_CASH = "facility_tollprice_cash";
       // public static final String FACILITY_TOLLPRICE_EZPASS ="facility_tollprice_ezpass";
        public static final String FACILITY_LATITUDE ="facility_latitude";
        public static final String FACILITY_LONGITUDE="facility_longitude";
        public static final String FACILITY_AXLETOLL = "facility_axletoll";
        public static final String DEFAULT_SORT_ORDER = "facility_name ASC";
    }
	
	
	//Vehicle Table
	public static final class Vehicle implements BaseColumns
	{
		private Vehicle(){}
		
		public static final String VEHICLE_TABLE_NAME = "table_Vehicle";
		
		//public static final String VEHICLE_ID = "vehicle_id";
		public static final String VEHICLE_NAME = "vehicle_name";
		public static final String DEFAULT_SORT_ORDER = "vehicle_name ASC";
	}
	//Axle Table
	public static final class Axle implements BaseColumns
	{
			private Axle(){}
			
			public static final String AXLE_TABLE_NAME = "table_axle";
			public static final String AXLE_MIN = "axle_min";
			public static final String AXLE_MAX = "axle_max";
			public static final String VEHICLE_ID = "vehicle_id";
	}
	//Price table
	public static final class Price implements BaseColumns
	{
		public static final String PRICE_TABLE_NAME ="table_price";
		public static final String TOLLPRICE_CASH ="tollprice_cash";
		public static final String TOOLPRICE_EZPASS ="tollprice_ezpass";
		public static final String VEHICLE_ID = "vehicle_id";//Foreign Key
		public static final String FACILITY_ID = "facility_id"; //Foreign key
	}

}
