package com.malikapps.trakerr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.malikapps.trakerr.DA.Group;
import com.malikapps.trakerr.DA.User;

import java.util.ArrayList;

/**
 * Created by abdulmalik_khan on 28/07/15.
 */
public class MainFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    GroupCustomAdapter dataAdapter = null;
    public View currentView;

    public MainFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_main, container, false);
        return currentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(((MainActivity)getActivity()).userId != "") {
            Group.getGroupsByUser(this, ((MainActivity) getActivity()).userId);
        }
    }

    /*
        public void updateGroupMembers(ArrayList<User> groupMembers)
        {
            final FragmentActivity fa = this.getActivity();
            //ArrayList<User> userList = new ArrayList<User>(ma.currentUser.applicableUsers);

            //create an ArrayAdaptar from the String Array
            dataAdapter = new UserCustomAdapter(currentView.getContext(),
                    R.layout.list_view_users, groupMembers);
            ListView listView = (ListView) currentView.findViewById(R.id.listViewGroupMembers);
            //listView = (ListView)this.getParentFragment().getActivity().findViewById(R.id.listViewContacts);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);

            //String colors[] = {"Red", "Blue", "White", "Yellow", "Black", "Green", "Purple", "Orange", "Grey"};
            //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, colors);
            //listView.setAdapter(spinnerArrayAdapter);

            ((MainActivity) this.getActivity()).showProgressDialog(false);
        }
    */
    public void updateGroupListView(ArrayList<Group> groups)
    {
        /*// tmp code start
        ArrayList<String> tmpGroupArray = new ArrayList<String>();
        for (Group g:groups) {
            tmpGroupArray.add(g.Name);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, tmpGroupArray.toArray(new String[tmpGroupArray.size()]));
        ListView listView = (ListView) currentView.findViewById(R.id.list_view_groups);
        listView.setAdapter(spinnerArrayAdapter);
        //tmp code end*/
        final MainActivity ma = (MainActivity) currentView.getContext();
        //create an ArrayAdaptar from the String Array
        dataAdapter = new GroupCustomAdapter(ma,
                R.layout.list_view_groups, groups);

        ListView listViewGroups = (ListView) currentView.findViewById(R.id.listViewGroups);
        //listView = (ListView)this.getParentFragment().getActivity().findViewById(R.id.listViewContacts);
        // Assign adapter to ListView
        listViewGroups.setAdapter(dataAdapter);


        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Group group = (Group) parent.getItemAtPosition(position);
                Toast.makeText(ma.getApplicationContext(),
                        "Clicked on Row: " + group.Name,
                        Toast.LENGTH_LONG).show();
                User.getUsersByGroup(MainFragment.this, group.Id);
            }
        });



        ((MainActivity) this.getActivity()).showProgressDialog(false);
    }

    public void showUsers(ArrayList<User> users)
    {
        Toast.makeText(currentView.getContext().getApplicationContext(),
                "Showing users location: " + users.size(),
                Toast.LENGTH_LONG).show();
    }
}
