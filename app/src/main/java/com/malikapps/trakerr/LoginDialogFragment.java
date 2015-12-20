package com.malikapps.trakerr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.malikapps.trakerr.DA.BaseTable;
import com.malikapps.trakerr.DA.User;

import java.net.MalformedURLException;


/**
 * Created by abdulmalik_khan on 29/07/15.
 */
public class LoginDialogFragment extends DialogFragment {

    public User user;
    // Use this instance of the interface to deliver action events
    LoginDialogListener mListener;
    View currentView;
    public LoginDialogFragment() {
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        currentView = inflater.inflate(R.layout.dialog_signin, null);
        builder.setView(currentView)
                // Add action buttons
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText txtName = (EditText) currentView.findViewById(R.id.name);
                        EditText txtPhone = (EditText) currentView.findViewById(R.id.phone);
                        // following line is to save in prefs
                        //((MainActivity) mListener).setNamePhone(txtName.getText().toString(), txtPhone.getText().toString());
                        user = new User();
                        user.Name = txtName.getText().toString();
                        user.Phone = txtPhone.getText().toString();
                        user.setLocatoin(currentView.getContext());
                        try {
                            user.insert(currentView.getContext(), mListener, LoginDialogFragment.this);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        user = new User();
                        user.error = BaseTable.USER_CANCELLED;
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

    public interface LoginDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }
}

