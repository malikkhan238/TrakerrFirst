package com.malikapps.trakerr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by abdulmalik_khan on 12/08/15.
 */
public class SettingsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public SettingsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        View rootView;
        switch (sectionNumber)
        {
            case 1:
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_profile, container, false);
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_profile, container, false);
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        }

        return rootView;*/
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
