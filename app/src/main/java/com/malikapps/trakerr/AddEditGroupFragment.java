package com.malikapps.trakerr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.malikapps.trakerr.DA.ContactsProvider;
import com.malikapps.trakerr.DA.Group;
import com.malikapps.trakerr.DA.GroupUser;
import com.malikapps.trakerr.DA.User;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdulmalik_khan on 12/08/15.
 */
public class AddEditGroupFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public UserCustomAdapter dataAdapter = null;
    public String selectedGroup = "";
    public ArrayList<User> groupMembers;
    public View currentView;
    public AddEditGroupFragment() {
        groupMembers = new ArrayList<User>();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddEditGroupFragment newInstance(int sectionNumber) {
        AddEditGroupFragment fragment = new AddEditGroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_add_edit_group, container, false);
        return currentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        MainActivity ma = ((MainActivity) v.getContext());
        if(v.getId() == R.id.buttonAddNewGroup) {
            Group group = new Group();
            EditText txtName = (EditText) ((MainActivity) v.getContext()).findViewById(R.id.textGroupName);
            group.Name = txtName.getText().toString();
            group.AdminUser_Id = ma.currentUser.Id;
            try {
                ma.showProgressDialog(true);
                group.insert(this, v.getContext());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if(v.getId() == R.id.buttonAddMember) {
            // TODO: show add member dialog
            AddGroupMemberDialogFragment addGroupMemberDialogFragment = new AddGroupMemberDialogFragment();
            addGroupMemberDialogFragment.setTargetFragment(this, 1);
            addGroupMemberDialogFragment.show(ma.getSupportFragmentManager(), "Tag");
        }
        //startActivity(((MainActivity) v.getContext()).getIntent());
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
        FragmentActivity activity = this.getActivity();
        Button buttonAddGroup = (Button) activity.findViewById(R.id.buttonAddNewGroup);
        buttonAddGroup.setOnClickListener(this);
        refreshGroupsSpinner((MainActivity) activity);

        //TODO: add listener to combo box
        Spinner spinner = (Spinner) activity.findViewById(R.id.spinner_groups);
        spinner.setOnItemSelectedListener(this);

        Button buttonAddMember = (Button) activity.findViewById(R.id.buttonAddMember);
        buttonAddMember.setOnClickListener(this);
    }

    public void refreshGroupsSpinner(MainActivity mainActivity) {
        if (null != mainActivity.currentUser.adminForGroups) {
            //String colors[] = {"Red", "Blue", "White", "Yellow", "Black", "Green", "Purple", "Orange", "Grey"};
            ArrayList<String> groups = new ArrayList<String>();
            for (Group group : ((MainActivity) this.getActivity()).currentUser.adminForGroups) {
                groups.add(group.Name);
            }
            Spinner spinner = (Spinner) ((MainActivity) this.getActivity()).findViewById(R.id.spinner_groups);
            // Application of the Array to the Spinner
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, groups);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner.setAdapter(spinnerArrayAdapter);
            // Create an ArrayAdapter using the string array and a default spinner layout
            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResourthis, R.array.planets_array, android.R.layout.simple_spinner_item);
            //ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>();

            // Specify the layout to use when the list of choices appears
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            //spinner.setAdapter(new ArrayAdapter<CharSequence>(this, Adapter.NO_SELECTION));
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        this.selectedGroup = (String) parent.getItemAtPosition(pos);
        //Toast.makeText(view.getContext(), this.selectedGroup, Toast.LENGTH_LONG);
        // TODO: call db get query and update the list view once data is fetched
        String selectedGroupId = "";
        for (Group group : ((MainActivity) this.getActivity()).currentUser.adminForGroups) {
            if(group.Name == this.selectedGroup){
                selectedGroupId = group.Id;
                break;
            }
        }
        User.getUsersByGroup(this, selectedGroupId);
        //this.updateGroupMembers(new ArrayList<User>(((MainActivity) this.getActivity()).currentUser.applicableUsers));
    }

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
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void addMembersToSelectedGroup(ArrayList<User> selectedUsers)
    {
        String selectedGroupId = "";
        for (Group group : ((MainActivity) this.getActivity()).currentUser.adminForGroups) {
            if(group.Name == this.selectedGroup){
                selectedGroupId = group.Id;
                break;
            }
        }
        for (User user: selectedUsers){
            GroupUser groupUser = new GroupUser();
            groupUser.Group_Id = selectedGroupId;
            groupUser.User_Id = user.Id;
            try {
                groupUser.insert(this, user);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
