package com.jworks.smartcity.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jworks.smartcity.R;

public class ScanFragment extends Fragment {

    private ScanViewModel scanViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);
        View root = inflater.inflate(R.layout.fragment_scan, container, false);
        return root;
    }
}