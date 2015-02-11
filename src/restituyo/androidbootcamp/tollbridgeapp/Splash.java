package restituyo.androidbootcamp.tollbridgeapp;

import java.util.Timer;
import java.util.TimerTask;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				finish();
				startActivity(new Intent(Splash.this,MainActivity.class));
			}
		};
		Timer opening = new Timer();
		opening.schedule(task, 4000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
