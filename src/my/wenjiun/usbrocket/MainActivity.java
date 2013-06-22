package my.wenjiun.usbrocket;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getFragmentManager().findFragmentById(android.R.id.content)==null) {
			getFragmentManager().beginTransaction()
			.add(android.R.id.content, new DeviceListFragment())
			.commit();
		}
	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
}
