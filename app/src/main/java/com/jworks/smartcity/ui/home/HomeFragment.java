package com.jworks.smartcity.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jworks.smartcity.R;

import java.util.List;
import java.util.Set;

import static android.bluetooth.BluetoothProfile.GATT;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;


    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 0;


    TextView mPairedTv, bluetoothOnStatus;
    Button mOnBtn, mDiscoverBtn, mPairedBtn;

    BluetoothAdapter mBlueAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });



        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOnBtn        = getView().findViewById(R.id.onBtn);
        mDiscoverBtn  = getView().findViewById(R.id.discoverableBtn);
        mPairedBtn    = getView().findViewById(R.id.pairedBtn);
        mPairedTv     = getView().findViewById(R.id.pairedTv);
        bluetoothOnStatus = getView().findViewById(R.id.bluetoothOnStatus);

        // adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();


        updateBluetoothStatus();

        // on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {
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
                    mPairedTv.setText("Paired Devices");
                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    for (BluetoothDevice device: devices){
                        mPairedTv.append("\nDevice: " + device.getName() + ", " + device);
                    }


//                    BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//                    List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
//
//                    mPairedTv.append("\nConnected devices: " + devices.size());
//                    for(BluetoothDevice device : devices) {
//                        mPairedTv.append("\nDevice: " + device.getName()+ ", " + device);
//                    }
                }
                else {
                    //bluetooth is off so can't get paired devices
                    showToast("Turn on bluetooth to get paired devices");
                }
            }
        });
    }

    private void updateBluetoothStatus() {
        // set according to bluetooth status(on/off)
        if (mBlueAdapter.isEnabled()) {
            bluetoothOnStatus.setVisibility(View.VISIBLE);
            mOnBtn.setVisibility(View.GONE);
        } else {
            bluetoothOnStatus.setVisibility(View.GONE);
            mOnBtn.setVisibility(View.VISIBLE);
        }
    }


    //toast message function
    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}