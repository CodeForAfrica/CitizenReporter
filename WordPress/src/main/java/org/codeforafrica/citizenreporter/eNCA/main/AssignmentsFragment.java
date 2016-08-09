package org.codeforafrica.citizenreporter.eNCA.main;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.codeforafrica.citizenreporter.eNCA.R;

public class AssignmentsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ripoti_assignments, container, false);
    }

    public static android.app.Fragment newInstance() {
        return new AssignmentsFragment();
    }
}