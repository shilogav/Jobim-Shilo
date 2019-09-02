package com.hyperactive.shilo.jobimshilo.search_jobs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.Job;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.JobWindowFragment;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.ListAdapterForMainDesktop;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.ShowJobsMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class SearchJobsFragment extends Fragment implements TabHost.TabContentFactory,
        FirstTabFragment.OnFragmentInteractionListener,
        ThirdTabFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener

{

    private FragmentActivity fragmentActivity;
    private OnFragmentInteractionListener mListener;
    private FirstTabFragment firstTabFragment;
    private SecondTabFragment secondTabFragment;
    LinearLayout jobLayout;
    private boolean saveChanges;
    private Bundle bundle;
    final int MAP_SHOW=10,WINDOWS_SHOW=11;
    private int showStateIs;

    Job job;
    final String JOBS_NAME_DATABASE = "jobs";
    FirebaseDatabase database;
    DatabaseReference myRef;
    long numInDatabase;
    ArrayList<Careers> careers;
    ArrayList<Job> windowOfJob;
    private String searchBy;
    final String KIND_OF_JOB = "kind of jobs", LOCATION = "locate", COMPANY = "company";
    LatLng chosenLatLng;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    final int REQUEST_LOCATION = 3;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    double latitude, longitude;

    private static final String DISTANCE_GET_HTTPS = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String API_KEY = "AIzaSyCqZl-8zoFmkw0VSlZOh2Qb-4bTrL7vxwM";

    AlertDialog alertDialog;

    public SearchJobsFragment() {
        // Required empty public constructor
    }

    public void setShowStateIs(int showStateIs) {
        this.showStateIs = showStateIs;
    }

    public void setCareers(ArrayList<Careers> careers) {
        this.careers = careers;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public void setChosenLatLng(LatLng chosenLatLng) {
        this.chosenLatLng = chosenLatLng;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Animation slide = AnimationUtils.loadAnimation(getContext(), R.anim.slide);
        final View view=inflater.inflate(R.layout.fragment_search_jobs, container, false);
        //view.setAnimation(slide);
/////////////////////////////////////////////////////////////////////////////////////////////
        final FragmentTabHost tabHost=(FragmentTabHost) view.findViewById(R.id.tabHost);
        tabHost.setup(getContext(),getChildFragmentManager(),android.R.id.tabcontent);

        RelativeLayout layout=(RelativeLayout) view.findViewById(R.id.search_job_layout);

        bundle=new Bundle();
        firstTabFragment=new FirstTabFragment();
        secondTabFragment=new SecondTabFragment();
        windowOfJob = new ArrayList<>();




        //jobLayout=(LinearLayout) view.findViewById(R.id.tab3);

        /*FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.tab3,firstTabFragment,"third Fragment");
        transaction.commit();*/
////////////////////////////////////////////////////////////////////////////////////////////

        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab One");
        //spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.company));

        tabHost.addTab(spec,ThirdTabFragment.class,null);
        //tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        //Tab 2
        spec = tabHost.newTabSpec("Tab Two");
        //spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.place));
        tabHost.addTab(spec,SecondTabFragment.class,null);
        //tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);
        tabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        //Tab 3
        spec = tabHost.newTabSpec("Tab Three");
        //spec.setContent(R.id.tab3);
        spec.setIndicator(getString(R.string.job),null);
        tabHost.addTab(spec,FirstTabFragment.class,null);
        //tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
        //tabHost.getTabWidget().getChildAt(2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        //make the job's tab first the default tab
        tabHost.setCurrentTabByTag("Tab Three");
////////////////////////////////////////////////////////////////////////////////////
        //the tab listener for changing color
        manageTabsColor(tabHost);


////////////////////////////////////////////////////////////////////////////////////////////

        //typed cancel
        Button cancelButton=(Button)view.findViewById(R.id.searchJobButtonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().
                        remove(SearchJobsFragment.this).commit();
                Log.i("shilo","click on cancel button");
                saveChanges=false;
            }
        });

        //typed accept
        Button acceptButton=(Button)view.findViewById(R.id.searchJobButtonAccept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

                /*new AsyncTask<Void,Void,Void>() {//remove this fragment
                    @Override
                    protected Void doInBackground(Void... params) {
                        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up,R.anim.slide_up).remove(SearchJobsFragment.this).commit();
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();*/

                new AsyncTask<Void,Void,Void>() {//remove this fragment
                    @Override
                    protected Void doInBackground(Void... params) {
                        fragmentActivity=getActivity();
                        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up,R.anim.slide_up).remove(SearchJobsFragment.this).commit();
                        try {//for timing the setSearchBy in the onFragmentInteraction method in mainDeskopActivity
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mListener.onFragmentInteraction(SearchJobsFragment.this);
                        ///
                        loadJobsFromDatabase();
                        fragmentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog=new AlertDialog.Builder(fragmentActivity).create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                            }
                        });

                        try {//for get the animation to finish properly
                            Thread.sleep(450);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();



                /*new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        Log.i("shilo","click on accept button");
                        saveChanges=true;

                        final JobWindowFragment fragment=new JobWindowFragment();
                        FragmentManager manager=getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction=manager.beginTransaction();
                        transaction.replace(R.id.windowsLayout, fragment,"Job Window");
                        Log.i("shilo","job window fragment should appear");
                        try {
                            Thread.sleep(1800);
                        } catch (InterruptedException e) {
                            Log.i("shilo","Error!!");
                        }
                        transaction.commit();
                        //I don't need listener. if I would need, I should add the searchfragment to backstack.
                        // for now, i cancel it from back stack as I read in the source below.
                        //source: http://www.sapandiwakar.in/replacing-fragments/
                        //mListener.onFragmentInteraction(SearchJobsFragment.this);
                        return null;
                    }
                }.execute();*/

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("shilo","job window fragment should appear");
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.windowsLayout,new JobWindowFragment()).commit();
                    }
                }).start();*/
            }
        });

        //Source: http://www.androidhive.info/2015/02/android-location-api-using-google-play-services/
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }



        //mListener.onFragmentInteraction(SearchJobsFragment.this);

        return view;
    }//end of onCreateView

    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to display the location on UI
     * */
    @SuppressLint("StaticFieldLeak")
    private void displayLocation() {
        //     try {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                latitude = 0;
                longitude = 0;

                if (ContextCompat.checkSelfPermission(

                        getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)

                {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    //Log.i("shilo","the client is connected : "+mGoogleApiClient.isConnected());
                    //Log.i("shilo","there is permission and the mLastLocation is: "+mLastLocation);
                } else {
                    Log.i("shilo", "there isn't permission");
                    // Show rationale and request permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                }

                if (mLastLocation != null)
                {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    //editor.putString(LATITUDE,String.valueOf(latitude));
                    //editor.putString(LONGITUDE,String.valueOf(longitude));
                    //editor.apply();
                    Log.i("shilo", "(JobWindow)the latitude and longitude are: " + latitude + ", " + longitude);
                    //loadJobsFromDatabase();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //////////
                            // Begin polling for new location updates.
                            startLocationUpdates();
                            //////////
                        }
                    });

                    //loadJobsFromDatabase();
                    Log.i("shilo", "Couldn't get the location. Make sure location is enabled on the device");
                }
                return null;
            }
        }.execute();//.get();
        // } catch (InterruptedException | ExecutionException e) {
        ///     e.printStackTrace();
        //  }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
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

    ///////
    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        long UPDATE_INTERVAL = 10 * 1000;
        long FASTEST_INTERVAL = 2000;
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i("shilo","the method onLocationChanged invoked");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("StaticFieldLeak")
    public void loadJobsFromDatabase() {
        //try {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {


                database = FirebaseDatabase.getInstance();
                myRef = database.getReference(JOBS_NAME_DATABASE);

                Log.i("shilo", "the reference of databse is " + myRef);

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //numInDatabase=dataSnapshot.getChildrenCount();
                        //Iterable <DataSnapshot> dataSnapshots;
                        //dataSnapshots=dataSnapshot.getChildren();
                        for (DataSnapshot jobDataSnapshot : dataSnapshot.getChildren()) {
                            job = jobDataSnapshot.getValue(Job.class);
                            /////
                            //handle the distance
                            String distance = null;
                            if (job != null) {
                                distance = getDistance(latitude, longitude, job.getLocation());
                            }
                            if (job != null) {
                                job.setDistance(distance);
                            }
                            /////

                            Log.i("shilo", "the careers are: " + careers);
                            if (!searchBy.equals(LOCATION)) {

                                if (careers != null) {
                                    if (careers.isEmpty())
                                        windowOfJob.add(job);
                                    else {
                                        if (searchBy.equals(KIND_OF_JOB)) {
                                            for (Careers career : careers) {
                                                if ((career.getProfession()).equals(job.getKindOfJob())) {
                                                    windowOfJob.add(job);
                                                }
                                            }
                                        }
                                        if (searchBy.equals(COMPANY)) {
                                            for (Careers career : careers) {
                                                //String careerCompany = career.getCompany().trim();
                                                if ((career.getCompany()).equals(job.getCompanyName())) {
                                                    windowOfJob.add(job);
                                                    Log.i("shilo", "the details is equal");
                                                }
                                                Log.i("shilo", "the current job is: " + job);
                                                Log.i("shilo", "the career company is: " + career.getCompany());
                                                Log.i("shilo", "the job company name is: " + job.getCompanyName());

                                            }
                                        }
                                    }
                                }
                            } else//if the search is by location
                            {
                                if (chosenLatLng==null)
                                {
                                    windowOfJob.add(job);
                                }
                                else {
                                    manageByLocationSearch();
                                }
                            }
                        }
                        if (!searchBy.equals(LOCATION)||chosenLatLng==null)
                            Log.i("shilo", "invoke the first passTofragment method");
                        passToFragment();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("shilo", "error!!!, the loading from database didn't execute");
                    }
                });
                return null;
            }
        }.execute();
        //}
    }

    private void manageByLocationSearch()
    {
        fragmentActivity.runOnUiThread(new Runnable() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Log.i("shilo", "invoke the second passTofragment method");
                        passToFragment();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        String distanceFromChoseLatLng = getDistance(chosenLatLng.latitude, chosenLatLng.longitude, job.getLocation());
                        distanceFromChoseLatLng = distanceFromChoseLatLng.replaceAll("[^\\d.]", "");
                        Log.i("shilo","(by location search)the distance after change is "+distanceFromChoseLatLng);
                        if (Double.valueOf(distanceFromChoseLatLng) <= 20) {
                            Log.i("shilo","(by location search) add job after location filter");
                            windowOfJob.add(job);
                        }

                        return null;
                    }
                }.execute();
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private void passToFragment()
    {
        alertDialog.dismiss();
        if (showStateIs==WINDOWS_SHOW)
        {
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    Log.i("shilo","click on accept button");
                    //saveChanges=true;

                    final JobWindowFragment fragment=new JobWindowFragment();
                    Log.i("shilo","(SearchJobsFragment)the windows of job is "+windowOfJob);
                    fragment.setWindowOfJob(windowOfJob);
                    fragment.setLatitude(latitude);
                    fragment.setLongitude(longitude);
                    //I create reference to fragment manager because this fragment detached and getActivity returns null
                    FragmentManager manager=fragmentActivity.getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.windowsLayout, fragment,"Job Window");
                    //transaction.addToBackStack("Job Window");
                    Log.i("shilo","job window fragment should appear");
                    transaction.commit();
                    //I don't need listener. if I would need, I should add the searchfragment to backstack.
                    // for now, i cancel it from back stack as I read in the source below.
                    //source: http://www.sapandiwakar.in/replacing-fragments/
                    //mListener.onFragmentInteraction(SearchJobsFragment.this);
                    return null;
                }
            }.execute();
        }
        else if (showStateIs==MAP_SHOW)//for map show
        {
            ShowJobsMapFragment fragment=new ShowJobsMapFragment();
            fragment.setWindowOfJob(windowOfJob);
            //I create reference to fragment manager because this fragment detached and getActivity returns null
            FragmentManager manager=fragmentActivity.getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.replace(R.id.windowsLayout, fragment,"show map");
            //transaction.addToBackStack("show map");
            Log.i("shilo","show map fragment should appear");
            transaction.commit();
        }
    }

    //this method calculate the distance between lat/lont and destination using google maps distance matrix
    protected String getDistance(final double latitude, final double longitude, final String destination) {
        try {
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            String message = "";
            try {
                StringBuilder sb = new StringBuilder(DISTANCE_GET_HTTPS);
                sb.append("origins=");
                String from = String.valueOf(latitude + "," + longitude);
                Log.i("shilo", "the from destination is: " + from);
                String fromencode = URLEncoder.encode(String.valueOf(latitude + "," + longitude), "utf8");
                Log.i("shilo", "the from destination encode is: " + fromencode);
                sb.append(URLEncoder.encode(String.valueOf(latitude + "," + longitude), "utf8"));
                sb.append("&destinations=");
                sb.append(URLEncoder.encode(destination, "utf8"));
                String destinationEncode = URLEncoder.encode(destination, "utf8");
                sb.append("&key=" + API_KEY);
                Log.i("shilo", "the destinationEncode is: " + destinationEncode);
                Log.i("shilo", "the url is: " + sb.toString());


                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                message = conn.getResponseMessage();
                //conn.setRequestMethod("GET");
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1)
                    jsonResults.append(buff, 0, read);

            } catch (MalformedURLException e) {
                Log.e("shilo", "Error processing Distance google API URL", e);
            } catch (IOException e) {
                Log.e("shilo", "Error connecting to Distance google API-response code:" + message, e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            //use the data that get
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            final int numberOfItemsInResp = jsonObj.length();
            JSONObject rows = (JSONObject) jsonObj.getJSONArray("rows").get(0);
            Log.i("shilo", "the string that I got is: " + rows);
            JSONArray element = rows.getJSONArray("elements");
            JSONObject distance = ((JSONObject) element.get(0)).getJSONObject("distance");
            String distanceLocal=distance.getString("text");
            Log.i("shilo", "the distance is " + distance);
            //distanceString = distanceLocal;
            Log.i("shilo", "the distance that I got is: " + distanceLocal);
            //Log.i("shilo", "the distanceString that I got is: " + distanceString);
            return distanceLocal;

        } catch (JSONException e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        //Log.i("shilo","on pause of search job fragment");
        //bundle.putBoolean("tab1",saveChanges);
        //mListener.onFragmentInteraction(SearchJobsFragment.this);
        super.onPause();
    }

    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.i("shilo","on detach of search job fragment");
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("shilo","the SearchJobsFragment onResume invoked and do the mGoogleApiClient is null? "+mGoogleApiClient);
        checkPlayServices();
        //displayLocation();

        //loadJobsFromDatabase();
    }


    @Override
    public View createTabContent(String s) {
        //if(s=="Tab One")
        //getFragmentManager().beginTransaction().add(R.id.tab1,firstTabFragment).commit();
        return jobLayout;
    }

    @Override
    public void onFragmentInteraction(Fragment fragment) {

    }

    private void manageTabsColor(final TabHost tabHost)
    {
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("shilo","the tab was clicked is: "+tabId);
                //tabHost.getCurrentTabTag()
                Log.i("shilo","the current tab is: "+tabHost.getCurrentTabTag());

                if (tabId.equals("Tab Three")) {
                    tabHost.getTabWidget().getChildAt(2).setBackgroundColor(getResources().getColor(R.color.white));
                    tabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                if (tabId.equals("Tab Two")) {
                    tabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.white));
                    tabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tabHost.getTabWidget().getChildAt(2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                if (tabId.equals("Tab One")) {
                    tabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.white));
                    tabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tabHost.getTabWidget().getChildAt(2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Fragment fragment);
    }
}
