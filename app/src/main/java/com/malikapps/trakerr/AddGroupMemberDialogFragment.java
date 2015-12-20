package com.malikapps.trakerr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.malikapps.trakerr.DA.User;

import java.util.ArrayList;


/**
 * Created by abdulmalik_khan on 29/07/15.
 */
public class AddGroupMemberDialogFragment extends DialogFragment {

    public User user;
    // Use this instance of the interface to deliver action events
    //AddEditUserDialogListener mListener;
    View currentView;
    UserCustomAdapter dataAdapter = null;

    public AddGroupMemberDialogFragment() {
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        /*try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddEditUserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        displayListView();
        //checkButtonClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        currentView = inflater.inflate(R.layout.dialog_add_group_member, null);
        builder.setView(currentView)
                // Add action buttons
                .setPositiveButton(R.string.add_user, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        EditText txtName = (EditText) currentView.findViewById(R.id.name);
                        EditText txtPhone = (EditText) currentView.findViewById(R.id.phone);
                        // following line is to save in prefs
                        //((MainActivity) mListener).setNamePhone(txtName.getText().toString(), txtPhone.getText().toString());
                        user = new User();
                        user.Name = txtName.getText().toString();
                        user.Phone = txtPhone.getText().toString();
                        user.setLocatoin(currentView.getContext());
                        try {
                            user.insert(currentView.getContext(), mListener, AddGroupMemberDialogFragment.this);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }*/
                        StringBuffer responseText = new StringBuffer();
                        responseText.append("The following were selected...\n");

                        ArrayList<User> userList = dataAdapter.usersList;
                        ArrayList<User> selectedUsers = new ArrayList<User>();
                        for (int i = 0; i < userList.size(); i++) {
                            User user = userList.get(i);
                            if (user.getSelected()) {
                                selectedUsers.add(user);
                                responseText.append("\n" + user.Name);
                            }
                        }
                        AddEditGroupFragment addEditGroupFragment = (AddEditGroupFragment) getTargetFragment();
                        addEditGroupFragment.addMembersToSelectedGroup(selectedUsers);
                        Toast.makeText(currentView.getContext().getApplicationContext(),
                                responseText, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        user = new User();
                        user.error = BaseTable.USER_CANCELLED;
                        */
                        AddGroupMemberDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*
        if (null != this.user) {
            if (this.user.error == null) {
                Toast.makeText(currentView.getContext(), "Not Submitted", Toast.LENGTH_SHORT);
            } else if (this.user.error == BaseTable.USER_CANCELLED) {
                mListener.onDialogNegativeClick(this);
            }
            else if (this.user.error == BaseTable.SUCCESS) {
                mListener.onDialogPositiveClick(this);
            }

        }
        */
    }
/*
    public interface AddEditUserDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }
    */

    private void displayListView() {

        final MainActivity ma = (MainActivity) currentView.getContext();
        ArrayList<User> userList = new ArrayList<User>(ma.currentUser.applicableUsers);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new UserCustomAdapter(ma,
                R.layout.list_view_users, userList);
        ListView listView;
        listView = (ListView) currentView.findViewById(R.id.listViewContacts);
        //listView = (ListView)this.getParentFragment().getActivity().findViewById(R.id.listViewContacts);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                User user = (User) parent.getItemAtPosition(position);
                Toast.makeText(ma.getApplicationContext(),
                        "Clicked on Row: " + user.Name,
                        Toast.LENGTH_LONG).show();
            }
        });

    }
/*
    private void checkButtonClick() {
        final MainActivity ma = (MainActivity) currentView.getContext();
        Button myButton = (Button) currentView.findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<User> userList = dataAdapter.usersList;
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    if (user.getSelected()) {
                        responseText.append("\n" + user.Name);
                    }
                }

                Toast.makeText(ma.getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });
    }
    */
}

