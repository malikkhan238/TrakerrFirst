package com.malikapps.trakerr.DA;

import android.content.Context;
import android.support.v4.app.DialogFragment;

import com.malikapps.trakerr.AddEditGroupFragment;
import com.malikapps.trakerr.LoginDialogFragment;
import com.malikapps.trakerr.MainActivity;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by abdulmalik_khan on 12/08/15.
 */
public class GroupUser extends BaseTable {
    @com.google.gson.annotations.SerializedName("id")
    public String Id;

    @com.google.gson.annotations.SerializedName("group_id")
    public String Group_Id;

    @com.google.gson.annotations.SerializedName("user_id")
    public String User_Id;

    @com.google.gson.annotations.SerializedName("__updatedAt")
    public String UpdatedAt;


    public boolean insert(final AddEditGroupFragment addEditGroupFragment, final User user) throws MalformedURLException {
        boolean retval = true;
        final MainActivity mainActivity = (MainActivity)addEditGroupFragment.getActivity();
        mainActivity.showProgressDialog(true);
        //setLocatoin(context);
        getMobileServiceClient(mainActivity).getTable(GroupUser.class).insert(this, new TableOperationCallback<GroupUser>() {
            public void onCompleted(GroupUser entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                    error = SUCCESS;
                    //MyPreferances.setNamePhone(context, entity.Name, entity.Phone, entity.Id);

                    addEditGroupFragment.dataAdapter.add(user);
                    addEditGroupFragment.dataAdapter.notifyDataSetChanged();

                } else {
                    // Insert failed
                    error = exception.getMessage();
                    //retval = false;
                }
                mainActivity.showProgressDialog(false);
            }
        });
        return retval;
    }
}
