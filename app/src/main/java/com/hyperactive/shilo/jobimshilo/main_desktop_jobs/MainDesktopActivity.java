package com.hyperactive.shilo.jobimshilo.main_desktop_jobs;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.drawer_layout.AlertsFragment;
import com.hyperactive.shilo.jobimshilo.drawer_layout.FindJobsFragment;
import com.hyperactive.shilo.jobimshilo.drawer_layout.InfoFragment;
import com.hyperactive.shilo.jobimshilo.drawer_layout.ListAdapterForDrawerLayouts;
import com.hyperactive.shilo.jobimshilo.drawer_layout.MyDetailsFragment;
import com.hyperactive.shilo.jobimshilo.drawer_layout.MyJobsFragment;
import com.hyperactive.shilo.jobimshilo.drawer_layout.PublisNewJobFragment;
import com.hyperactive.shilo.jobimshilo.drawer_layout.RowForDrawerLayout;
import com.hyperactive.shilo.jobimshilo.drawer_layout.SmartAgentFragment;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.MainActivity;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.maps.MapsActivity;
import com.hyperactive.shilo.jobimshilo.search_jobs.Careers;
import com.hyperactive.shilo.jobimshilo.search_jobs.FirstTabFragment;
import com.hyperactive.shilo.jobimshilo.search_jobs.SearchJobsFragment;
import com.hyperactive.shilo.jobimshilo.search_jobs.SecondTabFragment;
import com.hyperactive.shilo.jobimshilo.search_jobs.ThirdTabFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class MainDesktopActivity extends AppCompatActivity implements SearchJobsFragment.OnFragmentInteractionListener,
        ListAdapterForMainDesktop.OnListAdapterInteractionListener, AdapterView.OnItemClickListener,
        FindJobsFragment.OnFragmentInteractionListener,
        ListAdapterForDrawerLayouts.OnDrawerListAdapterClickListener,
        MyDetailsFragment.OnFragmentInteractionListener,
        FirstTabFragment.OnFragmentInteractionListener,
        JobWindowFragment.OnFragmentInteractionListener,
        SecondTabFragment.OnFragmentInteractionListener,
        ShowJobsMapFragment.OnFragmentInteractionListener//,
        //GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener

{
    final int MAIN_DESKTOP_ACTIVITY=2;

    String phoneNumber;
    public DrawerLayout drawer;
    ListView listView, listViewJobWindow;
    FrameLayout frameLayout;
    ListAdapterForDrawerLayouts adapter;
    ArrayList<RowForDrawerLayout> rowsDrawer;


    ListAdapterForMainDesktop adapter2;

    ImageButton actionImageButtonLeft, actionImageButtonRight;
    final int MAP_SHOW=10,WINDOWS_SHOW=11;
    int showStateIs;
    TableRow actionBarViewGroup;
    ImageButton searchJobImageButton;
    TextView actionBarTitle;
    ArrayList<Careers> chosenCareers;
    LatLng chosenLatLng;
    String searchBy;
    final String KIND_OF_JOB = "kind of jobs", LOCATION = "locate", COMPANY = "company";
    final int REQUEST_PHONE_CALL = 2, REQUEST_LOCATION = 3;
    Handler handler;

    final String FIRST_ROW = "first row", SECOND_ROW = "second row",
            THIRD_ROW = "third row", FOURTH_ROW = "fourth row",
            FAVORITES = "favoriteJobs",
            IMTERESTS = "interestJobs",
            FIND_JOB_FRAGMENT="find_job_fragment";
    Fragment fragment;

    //final String MY_DETAILS_TITLE=getResources().getString(R.string.my_details);
    String mainTitle;
    Drawable mainLeftTitle;
    TextView leftTitle;
    final String PHONE_NUMBER = "phone number";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Job job;
    final String JOBS_NAME_DATABASE = "jobs";
    FirebaseDatabase database;
    DatabaseReference myRef;
    long numInDatabase;
    ArrayList<Careers> careers;


    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_desktop);
        SharedPreferences prefs = getSharedPreferences("save", MODE_PRIVATE);
        editor = prefs.edit();
        /////////////////////////////////////////////////////////////////////////
        //TODO: recommended way to handle exceptions. http://stackoverflow.com/questions/16561692/android-exception-handling-best-practice
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        /////////////////////////////////////////////////////////////////////////

        //default value for state
        showStateIs=WINDOWS_SHOW;


        changeLocal();

        String phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);//get the phonenumber of user
        //Toast.makeText(getContext(),"phonenumber is "+phoneNumber,Toast.LENGTH_SHORT).show();
        Log.i("shilo", "MAIN DESKTOP:phonenumber is " + phoneNumber);

        handler = new Handler();

        //////////////
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //////////////


        manageActionBar();
        //managePermissionsLocation();

///////////////////////////////////////////////////////////////////////////////////////
        //inflate the toolbar
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.right_drawer);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
///////////////////////////////////////////////////////////////////////////////////////

        //manage actions in toolbar
        rowsDrawer = new ArrayList<RowForDrawerLayout>();
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.image_and_username), R.drawable.image_user));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.my_details), R.drawable.user));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.alerts), R.drawable.alerts));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.my_jobs), R.drawable.my_jobs));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.find_jobs), R.drawable.find_jobs));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.smart_agent), R.drawable.detective));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.info), R.drawable.info));
        rowsDrawer.add(new RowForDrawerLayout(getString(R.string.add_job), R.drawable.add_job));

        adapter = new ListAdapterForDrawerLayouts(this, rowsDrawer);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new FindJobsFragment(),FIND_JOB_FRAGMENT).commit();

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //open and close the toolbar

        actionImageButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "right Button is clicked", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
                                if (getCurrentFocus() != null) {
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                                Log.i("shilo", "bar right button clicked!!!!!");
                                if (drawer.isDrawerOpen(Gravity.END)) {
                                    drawer.closeDrawer(Gravity.END);
                                } else {
                                    drawer.openDrawer(Gravity.END);
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        //open the map
        actionImageButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionImageButtonLeft.getDrawable();
                if (showStateIs==WINDOWS_SHOW) {
                    Toast.makeText(getApplicationContext(), "left Button is clicked", Toast.LENGTH_SHORT).show();
                    actionImageButtonLeft.setImageResource(R.drawable.windows_show_2);
                    //the showJobMapFragment is in findJobsFragment layout
                    getSupportFragmentManager().beginTransaction().add(R.id.windowsLayout, new ShowJobsMapFragment()).commit();
                    showStateIs=MAP_SHOW;
                    /*Intent intent = new Intent(MainDesktopActivity.this, MapsActivity.class);
                    startActivityForResult(intent, 0);*/
                } else {
                    actionImageButtonLeft.setImageDrawable(mainLeftTitle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FindJobsFragment(),FIND_JOB_FRAGMENT).commit();
                    showStateIs=WINDOWS_SHOW;
                }

            }
        });

        //ask permission for location service. We need it for the distance
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);

        ///////////////////////////////////////////////////
/*
        //Source: http://www.androidhive.info/2015/02/android-location-api-using-google-play-services/
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        //get permission for GPS for main desktop windows
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            displayLocation();
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
*/

    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
    private void displayLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //Log.i("shilo","the client is connected : "+mGoogleApiClient.isConnected());
            //Log.i("shilo","there is permission and the mLastLocation is: "+mLastLocation);
        } else {
            Log.i("shilo","there isn't permission");
        }
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.i("shilo","the latitude and longitude are: "+latitude + ", " + longitude);

        } else {

            Log.i("shilo","Couldn't get the location. Make sure location is enabled on the device");
        }
    }

    /**
     * Creating google api client object
     * **
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                //finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("shilo", "Connection successed");
        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("shilo", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("shilo", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }
*/

    private void changeLocal()
    {
        //source: https://stackoverflow.com/questions/22863288/how-to-change-language-google-map-v2-android
        String languageToLoad = "iw_IL";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if (mGoogleApiClient != null) {
         //   mGoogleApiClient.connect();
       // }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkPlayServices();
    }

    //manage the appearance of action bar
    private void manageActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        View view = getSupportActionBar().getCustomView();
        ((View) view.getParent()).setBackground(getDrawable(R.drawable.action_bar_a));


        actionImageButtonRight = (ImageButton) view.findViewById(R.id.action_bar_right);

        actionImageButtonLeft = (ImageButton) view.findViewById(R.id.action_bar_left);
        //set show state
        showStateIs=WINDOWS_SHOW;

        actionBarViewGroup = (TableRow) view.findViewById(R.id.actionBarViewGroup);

        actionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
        mainTitle = actionBarTitle.getText().toString();
        mainLeftTitle = actionImageButtonLeft.getDrawable();
    }

    @Override
    public void onBackPressed() {
        //I want to catch just the back press on find job fragment or show map fragment
        Fragment fragment=getSupportFragmentManager().findFragmentByTag(FIND_JOB_FRAGMENT);
        Log.i("shilo","the findFragmentByTag(FIND_JOB_FRAGMENT) is "+fragment);
        if (fragment!=null && fragment.isVisible()) {
            Log.i("shilo","get into findjobfragment onbackpressed");
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(R.layout.exit_layout)
                    .show();
            Button accept = (Button) dialog.getWindow().findViewById(R.id.dialogAccpet);
            Button cancel = (Button) dialog.getWindow().findViewById(R.id.dialogCancel);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("shilo", "should finish activity");
                    setResult(MAIN_DESKTOP_ACTIVITY);
                    finish();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            actionImageButtonLeft.setImageDrawable(mainLeftTitle);
            actionBarTitle.setText(getString(R.string.titleEnteranceTextView));
            if (leftTitle != null)
                leftTitle.setText("");
            super.onBackPressed();
        }
    }




    @Override
    public void onFragmentInteraction(final Fragment fragment) {

        if (fragment instanceof SearchJobsFragment) {
            Log.i("shilo", "there is open stream between activity and SearchJobsFragment");
            //boolean saveChanges;
            SearchJobsFragment currentfragment = (SearchJobsFragment) fragment;
            ((SearchJobsFragment) fragment).setShowStateIs(showStateIs);

            //set the careers from first and third tab fragment(kind of job,comapny)
            if (chosenCareers!=null)
                currentfragment.setCareers(chosenCareers);
            Log.i("shilo","the chosenCareers is "+chosenCareers);
            //set the latlng from second tab fragment(location)
            if (chosenLatLng!=null) {
                currentfragment.setChosenLatLng(chosenLatLng);
                Log.i("shilo","MainDesktopActivity-2-the chosenLatLng is "+chosenLatLng);
            }

            //set the kind of searched for jobWindowfragment to know
            currentfragment.setSearchBy(searchBy);

            //saveChanges = currentfragment.getBundle().getBoolean("tab1");

            //if (saveChanges) {
            //    Log.i("shilo", "the boolean is " + saveChanges);
            //}
        }
        //get here from kind of job search
        if (fragment instanceof FirstTabFragment) {
            Log.i("shilo", "there is open stream between activity and FirstTabFragment");
            FirstTabFragment currentfragment = (FirstTabFragment) fragment;
            chosenCareers = new ArrayList<Careers>();

            for (Careers career : currentfragment.getRows()) {
                if (career.isChecked())
                    chosenCareers.add(career);
            }
            Log.i("shilo","(FirstTabFragment)the chosenCareers is "+chosenCareers);
            searchBy = KIND_OF_JOB;
            Log.i("shilo", "MainDesktopActivity- the rows are: " + currentfragment.getRows() + ". and the careers are: " + chosenCareers);
            //JobWindowFragment jobWindowFragment =(JobWindowFragment) getSupportFragmentManager().findFragmentById(R.id.layoutJob);
            //jobWindowFragment.setCareers(chosenCareers);
        }
        //get here from company search
        if (fragment instanceof ThirdTabFragment) {
            Log.i("shilo", "there is open stream between activity and FirstTabFragment");
            ThirdTabFragment currentfragment = (ThirdTabFragment) fragment;
            chosenCareers = new ArrayList<Careers>();

            for (Careers career : currentfragment.getRows()) {
                if (career.isChecked())
                    chosenCareers.add(career);
            }
            searchBy = COMPANY;
            Log.i("shilo", "MainDesktopActivity- the rows are: " + currentfragment.getRows() + ". and the careers are: " + chosenCareers);
            //JobWindowFragment jobWindowFragment =(JobWindowFragment) getSupportFragmentManager().findFragmentById(R.id.layoutJob);
            //jobWindowFragment.setCareers(chosenCareers);
        }
        //get here from location search
        if (fragment instanceof SecondTabFragment) {
            Log.i("shilo", "there is open stream between activity and SecondTabFragment");
            SecondTabFragment currentfragment = (SecondTabFragment) fragment;
            chosenLatLng=currentfragment.getLatLng();
            Log.i("shilo", "MainDesktopActivity-1- the chosenLatLng is "+chosenLatLng);

            searchBy = LOCATION;
        }



        if (fragment instanceof JobWindowFragment) {
            Log.i("shilo", "there is open stream between activity and JobWindowFragment");
            //boolean saveChanges;
            JobWindowFragment currentfragment = (JobWindowFragment) fragment;
            /*
            //set the careers from first and third tab fragment(kind of job,comapny)
            if (chosenCareers!=null)
                currentfragment.setCareers(chosenCareers);
            //set the latlng from second tab fragment(location)
            if (chosenLatLng!=null) {
                currentfragment.setChosenLatLng(chosenLatLng);
                Log.i("shilo","the chosenLatLng is "+chosenLatLng);
            }

            //set the kind of searched for jobWindowfragment to know
            currentfragment.setSearchBy(searchBy);*/
        }

        if (fragment instanceof FindJobsFragment) {
            Log.i("shilo", "there is open stream between activity and FindJobsFragment");
            //boolean saveChanges;
            FindJobsFragment currentfragment = (FindJobsFragment) fragment;
        }

        if (fragment instanceof MyDetailsFragment) {
            Log.i("shilo", "there is open stream between activity and MyDetailsFragment");
            //boolean saveChanges;
            MyDetailsFragment currentfragment = (MyDetailsFragment) fragment;
            listView.setAdapter(null);
            listView.setAdapter(adapter);
        }

    }

    //make bitmap rounded
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("shilo", "main desktop activity is in pause");
    }

    @Override
    public void onListAdapterInteraction(final HorizontalScrollView scrollView) {
        /*//scrollView.scrollTo(1000,0);
        handler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.setScrollX(500);
            }
        });*/

        Log.i("shilo", "came here");
    }

    //manage events in drawer layouts manu
    //first row->fourth row
    @Override
    public void onClick(String rowNumber) {
        Log.i("shilo", "string is " + rowNumber);
        switch (rowNumber) {
            case FIRST_ROW:
            case SECOND_ROW://my details fragment
                fragment = new MyDetailsFragment();
                actionBarTitle.setText(getResources().getString(R.string.my_details));
                //mainTitle=getResources().getString(R.string.my_details);
                actionImageButtonLeft.setImageResource(0);
                manageLeftTitle();
                break;
            case THIRD_ROW://alerts fragment
                fragment = new AlertsFragment();
                actionBarTitle.setText(getResources().getString(R.string.alerts));
                //mainTitle=getResources().getString(R.string.alerts);
                clearActionBar();
                break;
            case FOURTH_ROW://my jobs fragment
                fragment = new MyJobsFragment();
                actionBarTitle.setText(getResources().getString(R.string.my_jobs));
                //mainTitle=getResources().getString(R.string.my_jobs);
                clearActionBar();
                break;
        }
        if (fragment != null) {
            addFragmentfromDrawer(fragment);
        }
        Log.i("shilo","onClick-the name of title bar from is "+mainTitle);
    }

    //manage events in drawer layouts manu
    //fifth row->eighth row
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 1://find jobs fragment
                fragment = new FindJobsFragment();
                actionImageButtonLeft.setImageDrawable(mainLeftTitle);
                actionImageButtonLeft.setImageDrawable(mainLeftTitle);
                actionBarTitle.setText(getString(R.string.titleEnteranceTextView));
                //mainTitle=getString(R.string.titleEnteranceTextView);
                if (leftTitle != null)
                    leftTitle.setText("");
                break;
            case 2://smart agent fragment
                fragment = new SmartAgentFragment();
                actionBarTitle.setText(getResources().getString(R.string.smart_agent));
                //mainTitle=getResources().getString(R.string.smart_agent);
                clearActionBar();
                break;
            case 3://info fragment
                fragment = new InfoFragment();
                actionBarTitle.setText(getResources().getString(R.string.info));
                //mainTitle=getResources().getString(R.string.info);
                clearActionBar();
                break;
            case 4://publish new job fragment
                fragment = new PublisNewJobFragment();
                actionBarTitle.setText(getResources().getString(R.string.add_job));
                //mainTitle=getResources().getString(R.string.add_job);
                clearActionBar();
                break;
        }
        if (fragment != null)
            addFragmentfromDrawer(fragment);
    }

    private void addFragmentfromDrawer(Fragment fragment) {
        drawer.closeDrawer(Gravity.END);

        new ChangeFragmentAsynccTask(fragment, getSupportFragmentManager(), FIND_JOB_FRAGMENT).execute();
        /*if(!(fragment instanceof FindJobsFragment))
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new FindJobsFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).addToBackStack(null).commit();
        *///manageBackStack();
    }

    private static class ChangeFragmentAsynccTask extends AsyncTask<Void, Void, Void> {
        Fragment fragment;
        FragmentManager fragmentManager;
        String FIND_JOB_FRAGMENT;

        public ChangeFragmentAsynccTask(Fragment fragment, FragmentManager fragmentManager, String FIND_JOB_FRAGMENT) {
            this.fragment = fragment;
            this.fragmentManager = fragmentManager;
            this.FIND_JOB_FRAGMENT = FIND_JOB_FRAGMENT;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //the fragment find jobs should be main fragment
            //Log.i("shilo","1-should change the name of title bar from asynctask to "+mainTitle);
            if (!(fragment instanceof FindJobsFragment)) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FindJobsFragment()).commit();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(FIND_JOB_FRAGMENT).commit();
            }
            else {
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FIND_JOB_FRAGMENT).commit();
            }
            return null;
        }
    }



    private void manageLeftTitle() {
        if (leftTitle == null) {
            leftTitle = (TextView) findViewById(R.id.leftTitle);
        }
            /*leftTitle = new TextView(this);
            leftTitle.setText(getString(R.string.settings));
            leftTitle.setTextSize(18);
            leftTitle.setGravity(0);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leftTitle.setLayoutParams(lp);
            actionBarViewGroup.addView(leftTitle, 0);*/

            /*leftTitle.setText(getString(R.string.settings));

            leftTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(),new SettingsWindowFragment()).addToBackStack(null).commit();
                }
            });*/

    }

    public void loadImageFromStorage(String path, ImageView imageView) {
        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //return getResources().getIdentifier(path,"raw",getPackageName());
    }

    private void clearActionBar() {
        actionImageButtonLeft.setImageResource(0);
        if (leftTitle != null)
            leftTitle.setText("");
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("shilo", "start managing onRequestPermissions");

        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:0508524453"));
                    startActivity(callIntent);
                }

                //next level. sending and gettind code number
                ////////mListener.onFragmentInteraction(CreateUserFragmentLower.this);
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

                else {

                    System.exit(1);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
            case REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {
                    Toast.makeText(this,getString(R.string.no_gps_permission),Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}