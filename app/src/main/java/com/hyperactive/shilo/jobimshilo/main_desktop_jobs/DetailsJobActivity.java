package com.hyperactive.shilo.jobimshilo.main_desktop_jobs;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailsJobActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {
    Job job;
    private final int REQUEST_CODE_SMS = 0, REQUEST_CODE_MAIL = 1, REQUEST_PHONE_CALL = 2;
    private User user;
    FirebaseDatabase database;
    DatabaseReference userRef;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final String FAVORITES = "favoriteJobs", INTERESTS = "interestJobs";
    private String phonenumber;
    private static final String STATIC_MAP_GET_HTTPS = "https://maps.googleapis.com/maps/api/staticmap?center=";
    private static final String API_KEY = "AIzaSyCqZl-8zoFmkw0VSlZOh2Qb-4bTrL7vxwM";
    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        setContentView(R.layout.activity_details);

        changeLocal();


        prefs = this.getSharedPreferences("save", MODE_PRIVATE);
        editor = prefs.edit();

        TextView company=(TextView) findViewById(R.id.company);
        TextView kindOfJob=(TextView) findViewById(R.id.kindOfJob);
        TextView title=(TextView) findViewById(R.id.TitleOfJob);
        TextView content=(TextView) findViewById(R.id.contentOfJob);
        TextView location=(TextView) findViewById(R.id.textViewLocationA);
        TextView distance=(TextView) findViewById(R.id.textViewDistanceA);
        ImageButton phone=(ImageButton) findViewById(R.id.PhoneCall);
        ImageButton sms=(ImageButton) findViewById(R.id.SendSMS);
        ImageButton mail=(ImageButton) findViewById(R.id.SendMail);
        ImageButton rateStar=(ImageButton) findViewById(R.id.rateStar);
        ImageButton exit=(ImageButton) findViewById(R.id.exitActivity);

        mMapView = (MapView) findViewById(R.id.mapViewActivity);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        mMapView.getMapAsync(this);



        phone.setOnClickListener(this);
        sms.setOnClickListener(this);
        mail.setOnClickListener(this);
        rateStar.setOnClickListener(this);
        exit.setOnClickListener(this);

        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        user = gson.fromJson(json, User.class);

        job=(Job) getIntent().getSerializableExtra("job");



        company.setText(job.getCompanyName());
        kindOfJob.setText(job.getKindOfJob());
        title.setText(job.getInfoTitle());
        content.setText(job.getInfoContent());
        location.setText(job.getLocation());
        if (job.getDistance()!=null)
            distance.setText(job.getDistance());




        /////
        /*show static map
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return getStaticMapFromLocation(job.getLocation());
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                staticMap.setImageBitmap(bitmap);
                super.onPostExecute(bitmap);
            }
        }.execute();*/
        /////
    }

    private Bitmap getStaticMapFromLocation(String location)
    {
        HttpURLConnection conn = null;
        String message = "";
        Bitmap bmp = null;

        try {//define the URL request
            StringBuilder sb = new StringBuilder(STATIC_MAP_GET_HTTPS);
            sb.append(URLEncoder.encode(location,"utf8"));
            sb.append("&zoom=15&size=400x150");
            sb.append("&markers=color:blue%7Clabel:S%7C");
            sb.append(URLEncoder.encode(location,"utf8"));
            sb.append("&key=" + API_KEY);


            Log.i("shilo", "the url is: " + sb.toString());

            //get the image
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            message = conn.getResponseMessage();
            bmp = BitmapFactory.decodeStream(conn.getInputStream());

        } catch (MalformedURLException e) {
            Log.e("shilo", "Error processing Distance google API URL", e);
        } catch (IOException e) {
            Log.e("shilo", "Error connecting to Distance google API-response code:" + message, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return bmp;
    }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SendSMS:
                Log.i("shilo", "in the sms case");
                manageInterest();

                /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("address", "0508524453");
                sendIntent.putExtra("sms_body", activity.getString(R.string.send_message));

                sendIntent.setType("vnd.android-dir/mms-sms");
                activity.startActivity(sendIntent);*/
                sendSMS();

                Log.i("shilo", "came here-send SMS: ");
                break;
            case R.id.PhoneCall:
                manageInterest();


                Log.i("shilo", "came here-phonecall: ");

                managePermissionCallPhone();



                break;
            case R.id.SendMail:
                manageInterest();

                sendEmail();
                Log.i("shilo","came here-send mail: ");
                break;
            case R.id.rateStar:
                //TODO:I didn's solve the problem with changing picture
                //rateStar.setImageResource(R.drawable.star_on);
                //manageFavoriteAdding();
                //manageFavoriteAddingList();
                manageFavorite();
                break;
            case R.id.exitActivity:
                finish();
                overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
                break;
        }
    }

    private void managePermissionCallPhone() {
        Log.i("shilo", "get into manage permission function");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                Log.i("shilo", "get into explenation");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_PHONE_CALL);

            } else {
                Log.i("shilo", "get into no need of explenation");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_PHONE_CALL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i("shilo", "get into callIntent");
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String phone=job.getPhonenumber();
            if (phone!=null)
                callIntent.setData(Uri.parse("tel:"+phone));
            else
                callIntent.setData(Uri.parse("tel:1800400400"));

            this.startActivity(callIntent);
        }
    }

    private void manageInterest()
    {
        Log.i("shilo","in the manage interest");
        database = FirebaseDatabase.getInstance();
        userRef= database.getReference("users");
        phonenumber=user.getUserPhoneNumber();
        DatabaseReference interestRef=userRef.child(phonenumber).child(INTERESTS);
        ArrayList<String> interestJobs=user.getInterestJobs();
        Log.i("shilo","the interestJobs size is: "+ interestJobs.size());

        manageAddingJobFavoriteOrInterest(INTERESTS,interestRef,interestJobs);
    }

    private void manageFavorite()
    {
        database = FirebaseDatabase.getInstance();
        userRef= database.getReference("users");
        phonenumber=user.getUserPhoneNumber();
        DatabaseReference favoritesRef=userRef.child(phonenumber).child(FAVORITES);
        ArrayList<String> favoriteJobs=user.getFavoriteJobs();
        manageAddingJobFavoriteOrInterest(FAVORITES,favoritesRef,favoriteJobs);
    }

    private void manageAddingJobFavoriteOrInterest(String currentListName,DatabaseReference currentListRef,ArrayList<String> currentList)
    {
        phonenumber=user.getUserPhoneNumber();
        final String currentJob=job.getJobID();
        Log.i("shilo","the current job is "+currentJob);
        if (currentList.size()==0) {
            //for first current job
            currentList.add(currentJob);
            currentListRef.setValue(currentList);
            if(currentListName.equals(FAVORITES))
                Toast.makeText(this,this.getResources().getString(R.string.add_to_favorites),Toast.LENGTH_SHORT).show();
        }
        else {
            //if this is not first job in the current list
            if (!(currentList.contains(currentJob))) {
                //if the job not in current list
                currentList.add(currentJob);
                currentListRef.setValue(currentList);
                if(currentListName.equals(FAVORITES))
                    Toast.makeText(this, this.getResources().getString(R.string.add_to_favorites), Toast.LENGTH_SHORT).show();
            } else if(currentListName.equals(FAVORITES)) {
                //if the job already in list,remove it. relevant just to favorite list!!!
                currentList.remove(currentJob);
                currentListRef.setValue(currentList);

                Toast.makeText(this, this.getResources().getString(R.string.remove_from_favorites), Toast.LENGTH_SHORT).show();
            }
        }

        //currentList.clear();

        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();

    }

    protected void sendSMS() {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , "0507405923");
        smsIntent.putExtra("sms_body"  , this.getString(R.string.send_message));

        try {
            this.startActivityForResult(smsIntent,REQUEST_CODE_SMS);
            //activity.finishActivity(REQUEST_CODE_SMS);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"shilogavra@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.send_mail_object));
        emailIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.send_message));

        try {
            this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //activity.finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Geocoder geocoder=new Geocoder(this);
        List<Address> addresses = null;
        Address address;
        try {
            addresses = geocoder.getFromLocationName(job.getLocation(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses!=null) {
            address = addresses.get(0);

            LatLng jobLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(jobLatLng));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(jobLatLng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(jobLatLng).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
