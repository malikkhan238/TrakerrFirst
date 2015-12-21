package com.malikapps.trakerr;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.malikapps.trakerr.DA.BaseTable;
import com.malikapps.trakerr.DA.ContactsProvider;
import com.malikapps.trakerr.DA.Group;
import com.malikapps.trakerr.DA.MyPreferances;
import com.malikapps.trakerr.DA.User;

import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
        , LoginDialogFragment.LoginDialogListener
        /*,GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener*/ {
    /*private static final String TAG = "android-drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;*/
    public User currentUser = new User();
    public Group currentGroup = new Group();
    String userId = "";
    Date syncTime = new Date(0);
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ProgressDialog ringProgressDialog;

    public ContactsProvider contactsProvider;

    public MainActivity()
    {

    }
    //private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(null == contactsProvider) {
            contactsProvider = new ContactsProvider(this);
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*// Drive Code
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        */



        userId = MyPreferances.getValue(this, MyPreferances.KeyId);
        // If user is logging in for the first time, Show SignUp dialog.
        if (userId == "") {
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getSupportFragmentManager(), "Tag");
            // above call makes central DB (cloud DB) entry here only, dont wait. If internet is absent ask user to login later !

        } else {
            // User's Data is already collected !
            //String phone = MyPreferances.getValue(this, MyPreferances.KeyPhone);
            //User user = new User();
            //User.getAllByPhone(this, phone);
            //User user1 = new User();


            // Set all Contacts available to be added in any of the group, use this whenever user need to add a member
            //List<ContactsProvider.Contact> contacts = this.contactsProvider.getContacts();
            //User.getAllUsersInContacts(this, contacts);
            //}

            //User.getAllById(this, userId);

        }

    }

    public void updateCommonData()
    {
        if (userId != "") {
            showProgressDialog(true);
            String locaton = User.getLocatoin(this);
            //if (locaton != "0.0,0.0") {
            //TODO: if we comment following line, main screen group list view works, I will need to figure out how to
            //correct and sequence following and group fetch call
            User.getAllByField(this, "id", userId, locaton);
        }
    }
    public void showProgressDialog(boolean showProgressDialog) {
        if (showProgressDialog && (null == ringProgressDialog || !ringProgressDialog.isShowing())) {
            ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Data Sync in progress ...", true);
        } else {
            if (null != ringProgressDialog /*&& ringProgressDialog.isShowing()*/)
                ringProgressDialog.dismiss();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment frag;
        switch (position) {
            case 0:
                frag = MainFragment.newInstance(position + 1);
                break;
            case 1:
                frag = AddEditGroupFragment.newInstance(position + 1);
                break;
            case 2:
                frag = SettingsFragment.newInstance(position + 1);
                break;
            default:
                frag = MainFragment.newInstance(1);
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1_Home);
                break;
            case 2:
                mTitle = getString(R.string.title_section2_AddOrEditGroup);
                break;
            case 3:
                mTitle = getString(R.string.title_section3_Settings);
                break;
        }
        //mNavigationDrawerFragment.get
        //TextView textView = (TextView)findViewById(R.id.textView);
        //textView.setText(String.valueOf( number));
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //MyPreferances.setValue(this, MyPreferances.KeyPhone, MyPreferances.KeyPhone);
        LoginDialogFragment loginDialogFragment = (LoginDialogFragment) dialog;
        String message;
        boolean error = true;
        if (loginDialogFragment.user != null && loginDialogFragment.user.error == BaseTable.SUCCESS) {
            message = Message.FIRST_LOGIN_WELCOME + " '" + loginDialogFragment.user.Name + "'";
            error = false;
            finish();
            startActivity(getIntent());
        } else {
            message = Message.FIRST_LOGIN_FAILED;
            error = true;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        if (error) {
            finish();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //close the application
        finish();
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }

    public boolean shouldUpdate()
    {
        final int MIN = 30;
        boolean returnValue = false;
        long diffInMilliSec = new Date().getTime() - syncTime.getTime();
        if (diffInMilliSec > 1000 * 60 * MIN); // if MIN minutes passed or not
        {
            syncTime = new Date();
            returnValue = true;
        }
        return returnValue;
    }
    /*
    boolean started = false;
    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            mGoogleApiClient.connect();
            started = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }

    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        //switch (requestCode) {
            //...
            //case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //mGoogleApiClient.connect();
                }
                //break;
        //}
    }
    */

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {
        *//**
     * The fragment argument representing the section number for this
     * fragment.
     *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        *//**
     * Returns a new instance of this fragment for the given section
     * number.
     *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView;
            switch (sectionNumber) {
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

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }*/

}
