package com.malikapps.trakerr.DA;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.malikapps.trakerr.AddEditGroupFragment;
import com.malikapps.trakerr.LoginDialogFragment;
import com.malikapps.trakerr.MainActivity;
import com.malikapps.trakerr.MainFragment;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.query.ExecutableQuery;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.add;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Created by abdulmalik_khan on 02/08/15.
 */
public class User extends BaseTable {
    @com.google.gson.annotations.SerializedName("id")
    public String Id;

    @com.google.gson.annotations.SerializedName("name")
    public String Name;

    @com.google.gson.annotations.SerializedName("phone")
    public String Phone;

    @com.google.gson.annotations.SerializedName("last_Location")
    public String Last_Location;

    @com.google.gson.annotations.SerializedName("last_Time")
    public String Last_Time;

    @com.google.gson.annotations.SerializedName("__updatedAt")
    public String UpdatedAt;


    public ArrayList<Group> adminForGroups;

    public List<User> applicableUsers;

    private boolean isSelected;
    public boolean getSelected () {
        return isSelected;
    }
    public void setSelected (boolean value)
    {
        this.isSelected = value;
    }
    public static void UpdateAll(final Context context, final String id, final String last_Location) {
        User.update(context, id, last_Location);

        //User.getAllById(this, userId);
    }

    public static void update(final Context context, final String id, final String last_Location) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final List<User> results;
                try {
                    MobileServiceTable<User> mTable = getMobileServiceClient(context).getTable(User.class);
                    results = mTable.where().field("id").eq(val(id)).execute().get();
                    User user = results.get(0);
                    //TODO: uncomment following line once locatoin API stabilizes
                    //user.Last_Location = last_Location;
                    user.Last_Time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()).toString();
                    mTable.update(user).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        ((MainActivity) context).currentUser.runAsyncTask(task);
    }

    public static void getAllByField(final Context context, final String field, final String value, final String last_Location) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    MainActivity ma = ((MainActivity) context);
                    MobileServiceClient mobileServiceClient = getMobileServiceClient(context);
                    MobileServiceTable<User> mUserTable = mobileServiceClient.getTable(User.class);
                    final List<User> currentUser = mUserTable.where().field(field).eq(val(value)).execute().get();
                    User user = currentUser.get(0);
                    user.Last_Location = last_Location;
                    user.Last_Time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()).toString();
                    ma.currentUser = user;
                    mUserTable.update(user).get();

                    // Set all Contacts available to be added in any of the group, use this whenever user need to add a member
                    List<ContactsProvider.Contact> contacts = ma.contactsProvider.getContacts();
                    //User.getAllUsersInContacts(this, contacts);
                    ExecutableQuery<User> query = mUserTable.where();
                    boolean first = true;
                    for (ContactsProvider.Contact contact : contacts) {
                        if(first)
                        {
                            first = false;
                        }
                        else
                        {
                            query.or();
                        }
                        issue here is contact.phone is null,correct this logic !!!
                        String phone = contact.phone.replaceAll("\\s+", "").replaceAll("\\-", "").replace("(", "").replace(")", "");
                        if(phone.length() > 10)
                            phone = phone.substring(phone.length()-10);
                        //query.field("phone").eq(val(phone));
                        query.endsWith("phone", phone);
                    }

                    final List<User> applicableUsers = query.execute().get();
                    ma.currentUser.applicableUsers = applicableUsers;


                    //Set all groups
                    MobileServiceTable<Group> mGroupTable = mobileServiceClient.getTable(Group.class);
                    MobileServiceList<Group> groups = mGroupTable.where().field("adminUser_id").eq(val(user.Id)).select("id", "name").execute().get();
                    //final List<User> results = mUserTable.where().field(field).eq(val(value)).execute().get();
                    //((MainActivity) context).currentUser = results.get(0);



                    if (null != groups && null != context && null != ((MainActivity) context).currentUser) {
                        if (null == ma.currentUser.adminForGroups)
                            ma.currentUser.adminForGroups = new ArrayList<Group>();
                        for (Group group : groups) {
                            ma.currentUser.adminForGroups.add(group);
                        }
                    }

                    ma.showProgressDialog(false);
                    ma.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Welcome " + ((MainActivity) context).currentUser.Name, Toast.LENGTH_LONG).show();
                         }
                    });
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        ((MainActivity) context).currentUser.runAsyncTask(task);
    }

    public static void getUsersByGroup(final Fragment fragment, final String groupIdValue) {
        ((MainActivity) fragment.getActivity()).showProgressDialog(true);
        AsyncTask<Void, Void, ArrayList<User>> task = new AsyncTask<Void, Void, ArrayList<User>>() {
            @Override
            protected ArrayList<User> doInBackground(Void... params) {
                try {
                    MainActivity ma = ((MainActivity) fragment.getActivity());
                    MobileServiceClient mobileServiceClient = getMobileServiceClient(ma);
                    MobileServiceTable<User> mUserTable = mobileServiceClient.getTable(User.class);
                    MobileServiceTable<GroupUser> mGroupUserTable = mobileServiceClient.getTable(GroupUser.class);
                    final List<GroupUser> groupMembers = mGroupUserTable.where().field("group_id").eq(val(groupIdValue)).execute().get();

                    if(null != groupMembers && !groupMembers.isEmpty()) {
                        ExecutableQuery<User> query = mUserTable.where();
                        boolean first = true;
                        for (GroupUser groupUser : groupMembers) {
                            if (first) {
                                first = false;
                            } else {
                                query.or();
                            }
                            query.field("id").eq(val(groupUser.User_Id));
                        }

                        final List<User> applicableUsers = query.execute().get();
                        return new ArrayList<User>(applicableUsers);
                    }

                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                }
                return new ArrayList<User>();
                //return new ArrayList<User>(((MainActivity) addEditGroupFragment.getActivity()).currentUser.applicableUsers);
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param users The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(ArrayList<User> users) {
                super.onPostExecute(users);
                if(fragment instanceof AddEditGroupFragment) {
                    ((AddEditGroupFragment)fragment).updateGroupMembers(users);
                }
                else if (fragment instanceof MainFragment) {
                    ((MainFragment)fragment).showUsers(users);
                }
                ((MainActivity) fragment.getActivity()).showProgressDialog(false);
            }
        };
        ((MainActivity) fragment.getActivity()).currentUser.runAsyncTaskWithReturnValue(task);
    }

    /*
    public static void getAllUsersInContacts(final Context context, final List<ContactsProvider.Contact> contacts) {
        final MainActivity ma = (MainActivity)context;
        ma.showProgressDialog(true);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    MobileServiceClient mobileServiceClient = getMobileServiceClient(context);
                    MobileServiceTable<User> mUserTable = mobileServiceClient.getTable(User.class);
                    //final List<User> currentUser = mUserTable.where().field(field).eq(val(value)).execute().get();
                    ExecutableQuery<User> query = mUserTable.where();
                    boolean first = true;
                    for (ContactsProvider.Contact contact : contacts) {
                        if(first)
                        {
                            first = false;
                        }
                        else
                        {
                            query.or();
                        }
                        query.field("phone").eq(val(contact.phone));
                    }

                    final List<User> applicableUsers = query.execute().get();
                    ((MainActivity) context).currentUser.applicableUsers = applicableUsers;


                    // TODO: show processing dialog
                    ma.showProgressDialog(false);


                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        ((MainActivity) context).currentUser.runAsyncTask(task);
    }
    */

    public static String getLocatoin(Context context) {
        GPSTracker gps = new GPSTracker(context);
        String retVal = "";
        // Check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
            //Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            retVal += latitude + "," + longitude;

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
        return retVal;
    }

    public boolean insert(final Context context, final LoginDialogFragment.LoginDialogListener dialogListener, final DialogFragment dialog) throws MalformedURLException {
        boolean retval = true;
        ((MainActivity)context).showProgressDialog(true);
        setLocatoin(context);
        getMobileServiceClient(context).getTable(User.class).insert(this, new TableOperationCallback<User>() {
            public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                    error = SUCCESS;
                    MyPreferances.setNamePhone(context, entity.Name, entity.Phone, entity.Id);
                    ((MainActivity)context).showProgressDialog(false);

                } else {
                    // Insert failed
                    error = exception.getMessage();
                    //retval = false;
                }
                if (null != dialogListener && null != dialog) {
                    dialogListener.onDialogPositiveClick(dialog);
                }
            }
        });
        return retval;
    }

    public void setLocatoin(Context context) {
        this.Last_Location = getLocatoin(context);
    }

    protected AsyncTask<Void, Void, ArrayList<User>> runAsyncTaskWithReturnValue(AsyncTask<Void, Void, ArrayList<User>> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}


