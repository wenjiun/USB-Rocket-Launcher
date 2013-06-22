package my.wenjiun.usbrocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.ListFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class DeviceListFragment extends ListFragment {

	ArrayList<UsbDevice> devices = new ArrayList<UsbDevice>();
	ArrayList<String> deviceNames = new ArrayList<String>();
	private UsbManager mgr;
	private BroadcastReceiver attachReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_devicelist, null, false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_search:
			refreshList();
			break;
		}
		
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		menu.findItem(R.id.action_search).setVisible(true);
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, deviceNames));
		mgr = (UsbManager)getActivity().getSystemService(Context.USB_SERVICE);
		refreshList();
		attachReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				refreshList();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		getActivity().registerReceiver(attachReceiver, filter);

	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getActivity().unregisterReceiver(attachReceiver);
	}

	private void refreshList() {
		devices.clear();
		deviceNames.clear();
		HashMap<String, UsbDevice> list = mgr.getDeviceList();
		Iterator<UsbDevice> it = list.values().iterator();
		while(it.hasNext()) {
			UsbDevice device = it.next();
			devices.add(device);
			deviceNames.add(device.getVendorId() + ":" + device.getProductId());
		}
		((BaseAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		getActivity().getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new ControlFragment())
			.addToBackStack(null)
			.commit();
		Intent i = new Intent("my.wenjiun.usbrocket.USB_DEVICE_ATTACH");
		PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, 0);
		mgr.requestPermission(devices.get(position), pi);
	}
	
}
