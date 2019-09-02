package com.hyperactive.shilo.jobimshilo.main_desktop_jobs;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.hyperactive.shilo.jobimshilo.search_jobs.Careers;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobWindowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class JobWindowFragment extends Fragment implements AdapterView.OnItemClickListener//,
        //GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener
{

    private OnFragmentInteractionListener mListener;
    ArrayList<Job> windowOfJob;
    ListAdapterForMainDesktop adapter2;
    ListView listViewJobWindow;
    FirebaseDatabase database;
    DatabaseReference myRef;
    final String JOBS_NAME_DATABASE = "jobs";
    ArrayList<Careers> careers;
    private String searchBy;
    final String KIND_OF_JOB = "kind of jobs", LOCATION = "locate", COMPANY = "company";
    LatLng chosenLatLng;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    final int REQUEST_LOCATION = 3;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    double latitude, longitude;
    //final String LATITUDE = "latitude", LONGITUDE = "longitude";
    SharedPreferences.Editor editor;
    private static final String DISTANCE_GET_HTTPS = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String API_KEY = "AIzaSyCqZl-8zoFmkw0VSlZOh2Qb-4bTrL7vxwM";
    Job job;
    View rootView;


    public JobWindowFragment() {
        // Required empty public constructor
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public void setCareers(ArrayList<Careers> careers) {
        this.careers = careers;
    }

    public void setChosenLatLng(LatLng chosenLatLng) {
        this.chosenLatLng = chosenLatLng;
    }

    public void setWindowOfJob(ArrayList<Job> windowOfJob) {
        this.windowOfJob = windowOfJob;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        //SharedPreferences prefs = getActivity().getSharedPreferences("save", MODE_PRIVATE);
        //editor = prefs.edit();
        //latitude = Double.valueOf(prefs.getString(LATITUDE,"0"));
        //longitude = Double.valueOf(prefs.getString(LONGITUDE,"0"));
        //Log.i("shilo","the LATITUDE\\LONGITUDE is"+latitude+','+longitude);

        // Inflate the layout for this fragment

        if (savedInstanceState == null) {
            rootView = inflater.inflate(R.layout.fragment_job_window, container, false);


            listViewJobWindow = (ListView) rootView.findViewById(R.id.listViewMainDesktop2);
            //windowOfJob = new ArrayList<>();

            passToAdapter();

            /*
            //Source: http://www.androidhive.info/2015/02/android-location-api-using-google-play-services/
            // First we need to check availability of play services
            if (checkPlayServices()) {

                // Building the GoogleApi client
                buildGoogleApiClient();
            }
            */

            Log.i("shilo","(JobWindowFragment) onCreateView invoked");
/*
            //get permission for GPS for main desktop windows
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                displayLocation();
            } else {
                // Show rationale and request permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
*/

            //loadJobsFromDatabase();

            //Log.i("shilo", "the boolean of question if size of windows list and num in database equals is: " + (windowOfJob.size() == numInDatabase));
            //Log.i("shilo", "number of objects in database is: " + numInDatabase);
            //Log.i("shilo", "number of objects in windowOfJob is: " + windowOfJob.size());
            //Log.d("shilo", "Company name: " + job.getCompanyName() + ", info title: " + job.getInfoTitle());

            //windowOfJob.add(new Job());
            //Log.i("shilo","second check:number of objects in windowOfJob is: "+windowOfJob.size());
            //windowOfJob.add("first");
            //windowOfJob.add("second");
            //windowOfJob.add("third");
            //windowOfJob.add("fourth");


/*
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    boolean stop=false;
                    int counter=0;
                    while (counter!=100)
                        counter++;
                    while (counter==100) {
                        Log.i("shilo","size of windows: "+windowOfJob.size());
                        Log.i("shilo","num in database: "+numInDatabase);
                        Log.i("shilo","create list view:the boolean of question if size of windows list and num in database equals is: "+(windowOfJob.size()==numInDatabase));
                        adapter2 = new ListAdapterForMainDesktop(getActivity(), windowOfJob, handler);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listViewJobWindow.setAdapter(adapter2);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                        /*listViewJobWindow.setAdapter(null);
                        listViewJobWindow.setAdapter(adapter2);
                            }
                        });
                        counter++;

                        //stop=true;
                    }

                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
/*
            //LinearLayout layout=(LinearLayout) view.findViewById(R.id.layoutJob);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.addRule(RelativeLayout.BELOW, R.id.search_job_image_button);

            Log.i("shilo", "view is " + view);

            view.setLayoutParams(params);
*/

        /*listViewJobWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),"second list",Toast.LENGTH_SHORT).show();
            }
        });*/

            mListener.onFragmentInteraction(this);

            return rootView;
        }
        return null;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onFragmentInteraction(this);//update the chosen carrers
            //loadJobsFromDatabase();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("shilo", "(Item click listener)the item is: " + view);
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

/*
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to display the location on UI
     * *
    private void displayLocation() {
        try {
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
                        loadJobsFromDatabase();
                    } else

                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //////////
                                // Begin polling for new location updates.
                                startLocationUpdates();
                                //////////
                            }
                        });

                        loadJobsFromDatabase();
                        Log.i("shilo", "Couldn't get the location. Make sure location is enabled on the device");
                    }
                    return null;
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating google api client object
     * *
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * *
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
*/

    @Override
    public void onStop() {
        super.onStop();
        //mGoogleApiClient.disconnect();
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
        //Log.i("shilo","the JobFragmentWindow onResume invoked and do the mGoogleApiClient is null? "+mGoogleApiClient);
        //checkPlayServices();
    }

    /*public void loadJobsFromDatabase() {
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
                            String distance = getDistance(latitude, longitude, job.getLocation());
                            job.setDistance(distance);
                            /////

                            Log.i("shilo", "the careers are: " + careers);
                            //TODO:to filter to chosen careers only
                            if (!searchBy.equals(LOCATION)) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... params) {
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


                                                return null;
                                            }
                                        }.execute();
                                    }
                                });

                                ////////////


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
                                passToAdapter();
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        passToAdapter();
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


    }*/

    private void passToAdapter()
    {
        Log.i("shilo","passToAdapter revoked");
        RelativeLayout container=(RelativeLayout)rootView.findViewById(R.id.layoutJob);
        ///////
        //add textview if no results
        TextView textView=new TextView(getActivity());
        textView.setId(R.id.edit_text_no_jobs);
        ViewGroup.LayoutParams textViewLayoutParams = textView.getLayoutParams();
        RelativeLayout.LayoutParams rl_lp=new RelativeLayout.LayoutParams(container.getLayoutParams());
        //rl_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        rl_lp.setMargins(0,0,0,80);
        textView.setLayoutParams(rl_lp);
        ///////
        Log.i("shilo","(JobWindowFragment)the windows of job is "+windowOfJob);
        if (windowOfJob.isEmpty())
        {
            Log.i("shilo","should show textview");
            textView.setText(getString(R.string.there_are_no_jobs));
            container.addView(textView);
        }
        else {
            if (container.findViewById(R.id.edit_text_no_jobs)!=null)
                container.removeView(textView);
            Log.i("shilo", "(before calling adapter)the windowOfJob is empty? " + windowOfJob.isEmpty());
            adapter2 = new ListAdapterForMainDesktop(getActivity(), windowOfJob, listViewJobWindow, latitude, longitude);
            if (listViewJobWindow != null) {
                listViewJobWindow.setAdapter(adapter2);
                Log.i("shilo", "should show the listview");
                //listViewJobWindow.setOnItemClickListener(JobWindowFragment.this);
            }
        }
    }
}

