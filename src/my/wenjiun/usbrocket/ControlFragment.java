package my.wenjiun.usbrocket;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ControlFragment extends Fragment implements OnTouchListener {

	private UsbManager mgr;
	protected UsbInterface intf;
	protected UsbDeviceConnection conn;
	private BroadcastReceiver usbReceiver;
	protected UsbDevice device;
	private ImageView imageViewFire;
	private ImageView imageViewUp;
	private ImageView imageViewDown;
	private ImageView imageViewLeft;
	private ImageView imageViewRight;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		mgr = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
		usbReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
						false)) {
					device = (UsbDevice) (intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE));
					intf = device.getInterface(0);
					conn = mgr.openDevice(device);
				}

			}
		};

	}
	
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_search).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_control, null, false);
		imageViewFire = (ImageView)view.findViewById(R.id.imageViewFire);
		imageViewUp = (ImageView)view.findViewById(R.id.imageViewUp);
		imageViewDown = (ImageView)view.findViewById(R.id.imageViewDown);
		imageViewLeft = (ImageView)view.findViewById(R.id.imageViewLeft);
		imageViewRight = (ImageView)view.findViewById(R.id.imageViewRight);
		imageViewFire.setOnTouchListener(this);
		imageViewUp.setOnTouchListener(this);
		imageViewDown.setOnTouchListener(this);
		imageViewLeft.setOnTouchListener(this);
		imageViewRight.setOnTouchListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().registerReceiver(usbReceiver,
				new IntentFilter("my.wenjiun.usbrocket.USB_DEVICE_ATTACH"));

	}

	@Override
	public void onDestroyView() {
		super.onDestroy();
		getActivity().unregisterReceiver(usbReceiver);
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			if (conn != null) {
				if (conn.claimInterface(intf, true)) {
					byte[] message = { (byte) 0 };
					switch(v.getId()) {
					case R.id.imageViewUp:
						message[0] = (byte)1;
						imageViewUp.setImageResource(R.drawable.up_pressed);
						break;
					case R.id.imageViewDown:
						message[0] = (byte)2;
						imageViewDown.setImageResource(R.drawable.down_pressed);
						break;
					case R.id.imageViewLeft:
						message[0] = (byte)4;
						imageViewLeft.setImageResource(R.drawable.left_pressed);
						break;
					case R.id.imageViewRight:
						message[0] = (byte)8;
						imageViewRight.setImageResource(R.drawable.right_pressed);
						break;
					case R.id.imageViewFire:
						message[0] = (byte)16;
						imageViewFire.setImageResource(R.drawable.fire_pressed);
						break;
					}
					conn.controlTransfer(0x21, 0x9, 0x200, 0,
							message, message.length, 0);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			imageViewUp.setImageResource(R.drawable.up);
			imageViewDown.setImageResource(R.drawable.down);
			imageViewLeft.setImageResource(R.drawable.left);
			imageViewRight.setImageResource(R.drawable.right);
			imageViewFire.setImageResource(R.drawable.fire);
			if (conn != null) {
				if (conn.claimInterface(intf, true)) {
					byte[] message = { (byte) 32 };
					conn.controlTransfer(0x21, 0x9, 0x200, 0,
							message, message.length, 0);
				}
			}
			break;
		}
		return true;
	}

}
