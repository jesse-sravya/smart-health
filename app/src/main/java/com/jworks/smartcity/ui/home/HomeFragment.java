package com.jworks.smartcity.ui.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.service.controls.Control;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jworks.smartcity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.R.layout.simple_list_item_1;
import static android.bluetooth.BluetoothProfile.GATT;
import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    private HomeViewModel homeViewModel;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 0;
    private static final int REQUEST_GPS_CODE_PERMISSION = 2;
    String mGPSPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    public static String EXTRA_ADDRESS = "H-C-2010-06-01, 00:18:91:D8:0D:B4";

    public static final String DEVICE_EXTRA = "SOCKET";



    TextView mPairedTv, bluetoothOnStatus, mGPSOnStatus;
    Button mBluetooothOnBtn, mDiscoverBtn, mPairedBtn, mGPSOnButton;
    ListView devicelist;

    private BluetoothAdapter mBlueAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private GoogleMap googleMap;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);



        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBluetooothOnBtn        = getView().findViewById(R.id.bluetoothOnBtn);
        mGPSOnButton  = getView().findViewById(R.id.GPSOnBtn);
        mDiscoverBtn  = getView().findViewById(R.id.discoverableBtn);
        mPairedBtn    = getView().findViewById(R.id.pairedBtn);
        mPairedTv     = getView().findViewById(R.id.pairedTv);
        bluetoothOnStatus = getView().findViewById(R.id.bluetoothOnStatus);
        mGPSOnStatus = getView().findViewById(R.id.GPSOnStatus);

        // bluetooth adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        devicelist = (ListView) getView().findViewById(R.id.listView);


        updateBluetoothStatus();

        // on btn click
        mBluetooothOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isEnabled()){
                    showToast("Turning On Bluetooth...");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                    updateBluetoothStatus();
                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });

        // on btn click
        mGPSOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
            }
        });

        //discover bluetooth btn click
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isDiscovering()){
                    showToast("Making Your Device Discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });

        // get paired devices btn click
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()){
//                    mPairedTv.setText("Paired Devices");
//                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
//                    for (BluetoothDevice device: devices){
//                        mPairedTv.append("\nDevice: " + device.getName() + ", " + device);
//                    }

                    pairedDevicesList();

//                    BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//                    List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
//
//                    mPairedTv.append("\nConnected devices: " + devices.size());
//                    for(BluetoothDevice device : devices) {
//                        mPairedTv.append("\nDevice: " + device.getName()+ ", " + device);
//                    }
                } else {
                    // bluetooth is off so can't get paired devices
                    showToast("Turn on bluetooth to get paired devices");
                }
            }
        });
    }

    private void pairedDevicesList() {
        pairedDevices = mBlueAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if ( pairedDevices.size() > 0 ) {
            for ( BluetoothDevice bt : pairedDevices ) {
                list.add(bt.getName().toString() + "\n" + bt.getAddress().toString());
            }
            showToast("Paired Bluetooth Devices Found " + list.size());
        } else {
            showToast("No Paired Bluetooth Devices Found.");
        }

        final ArrayAdapter adapter = new ArrayAdapter(getContext(), simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("------------", String.valueOf(position));

                BluetoothDevice device = null;
                int deviceCounter = 0;
                for ( BluetoothDevice bt : pairedDevices ) {
                    if (deviceCounter == position) {
                        device = bt;
                        break;
                    }
                    deviceCounter++;
                }

//                showToast("Found device "+ showUUID(device).length());

                Intent intent = new Intent(getContext(), MonitoringScreen.class);
                intent.putExtra(DEVICE_EXTRA, device);
                startActivity(intent);
            }
        });
    }



    private void updateBluetoothStatus() {
        // set according to bluetooth status(on/off)
        if (mBlueAdapter.isEnabled()) {
            bluetoothOnStatus.setVisibility(View.VISIBLE);
            mBluetooothOnBtn.setVisibility(View.GONE);
        } else {
            bluetoothOnStatus.setVisibility(View.GONE);
            mBluetooothOnBtn.setVisibility(View.VISIBLE);
        }
    }

    private void updateGPSStatus() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mGPSOnStatus.setVisibility(View.GONE);
            mGPSOnButton.setVisibility(View.VISIBLE);
            showToast("not granted");
        } else {
            mGPSOnStatus.setVisibility(View.VISIBLE);
            mGPSOnButton.setVisibility(View.GONE);
            showToast("granted");

        }
    }


    private void setUpMap() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        try {
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation != null) {
                Log.d("TAG", "Not null");
            }
            else {
                Log.d("TAG", "NULL");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        showToast("location changed");
                    }
                });
            }
        }
        catch (SecurityException se) {
            Log.d("TAG", "SE CAUGHT");
            se.printStackTrace();
        }
    }

    private void getLocationPermission() {
//        String permissions[] = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
//
//        Log.d(TAG, "getLocationPermission: before if condition");
//        if (ContextCompat.checkSelfPermission(getContext(),
//
//                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            if  (ContextCompat.checkSelfPermission(getContext(),
//                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                Log.d(TAG, "getLocationPermission: calling init");
//            } else {
//                ActivityCompat.requestPermissions(getActivity(),
//                        permissions,
//                        LOCATION_PERMISSION_REQUEST_CODE);
//            }
//        } else {
//            ActivityCompat.requestPermissions(getActivity(),
//                    permissions,
//                    LOCATION_PERMISSION_REQUEST_CODE);
//
//        }
//
//        updateGPSStatus();
            showToast("fetching ...");
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                showToast("fetched gps");
                return;
            }

            ActivityCompat.requestPermissions(getActivity(), new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    LOCATION_PERMISSION_REQUEST_CODE);
            showToast("error fetching gps");
    }



    //toast message function
    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}