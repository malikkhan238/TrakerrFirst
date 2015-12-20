package com.malikapps.trakerr.DA;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.TextView;

import com.malikapps.trakerr.AddEditGroupFragment;
import com.malikapps.trakerr.LoginDialogFragment;
import com.malikapps.trakerr.MainActivity;
import com.malikapps.trakerr.MainFragment;
import com.malikapps.trakerr.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.query.ExecutableQuery;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Created by abdulmalik_khan on 12/08/15.
 */
public class Group extends BaseTable {
    @com.google.gson.annotations.SerializedName("id")
    public String Id;

    @com.google.gson.annotations.SerializedName("name")
    public String Name;

    @com.google.gson.annotations.SerializedName("adminUser_id")
    public String AdminUser_Id;

    @com.google.gson.annotations.SerializedName("__updatedAt")
    public String UpdatedAt;



    public boolean insert(final Fragment fragment, final Context context) throws MalformedURLException {
        boolean retval = true;
        getMobileServiceClient(context).getTable(Group.class).insert(this, new TableOperationCallback<Group>() {
            public void onCompleted(Group entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                    error = SUCCESS;
                    MainActivity ma = (MainActivity)context;
                    if (null != ma.currentUser.adminForGroups) {
                        // TODO: new strategy is not to refresh complete activity, but to update only the relevant components
                        //ma.refresh();
                        ma.showProgressDialog(false);
                        TextView textView = (TextView) ma.findViewById(R.id.textGroupName);
                        textView.setText("");
                        ma.currentUser.adminForGroups.add(Group.this);
                        ((AddEditGroupFragment) fragment).refreshGroupsSpinner(ma);
                    }

                } else {
                    // Insert failed
                    error = exception.getMessage();
                    //retval = false;
                }
                if (null != context) {
                    //((MainActivity)context).GroupInsertedSuccessfully();
                }
            }
        });
        return retval;
    }

    public static void getGroupsByUser(final MainFragment mainFragment, final String userId) {
        ((MainActivity)mainFragment.getActivity()).showProgressDialog(true);
        AsyncTask<Void, Void, ArrayList<Group>> task = new AsyncTask<Void, Void, ArrayList<Group>>() {
            @Override
            protected ArrayList<Group> doInBackground(Void... params) {
                try {
                    MainActivity ma = ((MainActivity) mainFragment.getActivity());
                    MobileServiceClient mobileServiceClient = getMobileServiceClient(ma);
                    MobileServiceTable<Group> mGroupTable = mobileServiceClient.getTable(Group.class);
                    MobileServiceTable<GroupUser> mGroupUserTable = mobileServiceClient.getTable(GroupUser.class);
                    final List<GroupUser> groupMembers = mGroupUserTable.where().field("user_id").eq(val(userId)).execute().get();

                    if(null != groupMembers && !groupMembers.isEmpty()) {
                        ExecutableQuery<Group> query = mGroupTable.where();
                        boolean first = true;
                        for (GroupUser groupUser : groupMembers) {
                            if (first) {
                                first = false;
                            } else {
                                query.or();
                            }
                            query.field("id").eq(val(groupUser.Group_Id));
                        }

                        final List<Group> applicableGroup = query.execute().get();
                        return new ArrayList<Group>(applicableGroup);
                    }

                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                }
                return new ArrayList<Group>();
                //return new ArrayList<User>(((MainActivity) addEditGroupFragment.getActivity()).currentUser.applicableUsers);
            }

            @Override
            protected void onPostExecute(ArrayList<Group> groups) {
                super.onPostExecute(groups);
                ((MainActivity)mainFragment.getActivity()).showProgressDialog(false);
                mainFragment.updateGroupListView(groups);
                ((MainActivity)mainFragment.getActivity()).updateCommonData();
            }
        };
        ((MainActivity) mainFragment.getActivity()).currentGroup.runAsyncTaskWithReturnValue(task);
    }

    protected AsyncTask<Void, Void, ArrayList<Group>> runAsyncTaskWithReturnValue(AsyncTask<Void, Void, ArrayList<Group>> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}
