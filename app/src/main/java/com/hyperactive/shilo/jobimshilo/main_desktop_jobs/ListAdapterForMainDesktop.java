package com.hyperactive.shilo.jobimshilo.main_desktop_jobs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.User;

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

import static android.content.Context.MODE_PRIVATE;


public class ListAdapterForMainDesktop extends BaseAdapter implements View.OnClickListener
{
    Activity activity;
    ArrayList<Job> windows;
    HorizontalScrollView center;
    ListAdapterForMainDesktop adapter;
    View view;
    RelativeLayout centerContainer;
    TextView companyName, kindOfJob, title, content,location,distance;
    ImageButton sendSMS, phoneCall, sendMail, rateStar, more;
    String distanceString;
    //RatingBar ratingBar;
    FirebaseDatabase database;
    DatabaseReference userRef;
    boolean starOn;
    private OnListAdapterInteractionListener mListener;
    ListView listView;
    private int position;
    private final String PHONE_NUMBER = "phone number",
            FAVORITES = "favoriteJobs",
            INTERESTS = "interestJobs";
    private String phonenumber;
    private ArrayList<String> favoriteJob;
    boolean jobAlreadyFavorite;
    private User user;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final int REQUEST_CODE_SMS = 0, REQUEST_CODE_MAIL = 1, REQUEST_PHONE_CALL = 2;
    private double latitude,longitude;
    private static final String DISTANCE_GET_HTTPS = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String API_KEY = "AIzaSyCqZl-8zoFmkw0VSlZOh2Qb-4bTrL7vxwM";



    public ListAdapterForMainDesktop(Activity activity, ArrayList<Job> windows, ListView listView,
                                     double latitude,double longitude) {
        super();
        this.activity = activity;
        this.windows = windows;
        this.listView = listView;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public ListAdapterForMainDesktop(Activity activity, ArrayList<Job> windows, ListView listView) {
        super();
        this.activity = activity;
        this.windows = windows;
        this.listView = listView;
        this.latitude=0;
        this.longitude=0;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return windows.size();
    }

    @Override
    public Object getItem(int position) {
        return windows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_for_desktop_job_window_scrollview, null);
            //view = inflater.inflate(R.layout.fragment_center, null);
        } else
            view = convertView;

        prefs = activity.getSharedPreferences("save", MODE_PRIVATE);
        editor = prefs.edit();

        mListener = (ListAdapterForMainDesktop.OnListAdapterInteractionListener) activity;
        center = (HorizontalScrollView) view.findViewById(R.id.mainContainer);
        if (center.getScrollX() != 0)//make sure that scrolling back to default state
            center.setScrollX(0);

        adapter=this;
        centerContainer = (RelativeLayout) view.findViewById(R.id.center_Container);
        centerContainer.setTag(position);

        companyName = (TextView) view.findViewById(R.id.companyTextView);
        kindOfJob = (TextView) view.findViewById(R.id.kindOfJobTextView);
        title = (TextView) view.findViewById(R.id.titleTextView);
        location=(TextView)view.findViewById(R.id.textViewLocation);
        distance=(TextView)view.findViewById(R.id.textViewDistance);
        distance.setTag(position);

        companyName.setText(((Job) getItem(position)).getCompanyName());
        kindOfJob.setText(((Job) getItem(position)).getKindOfJob());
        title.setText(((Job) getItem(position)).getInfoTitle());
        location.setText(((Job)getItem(position)).getLocation());
        Log.i("shilo", "(ListAdapter)the latitude and longitude are: " + latitude+','+longitude);
        //TODO: delete the getDistance if I finish the fix of distance running
        //getDistance(latitude,longitude,location.getText().toString());
        //Log.i("shilo", "the distance in the job window is: " + StringDistance);
        String stringDistance=((Job)getItem(position)).getDistance();
        if(stringDistance!=null &&!stringDistance.equals("0"))
            distance.setText(((Job)getItem(position)).getDistance());


        sendSMS = (ImageButton) view.findViewById(R.id.imageButtonSendSMS);
        sendSMS.setTag(position);
        phoneCall = (ImageButton) view.findViewById(R.id.imageButtonPhoneCall);
        phoneCall.setTag(position);
        sendMail = (ImageButton) view.findViewById(R.id.imageButtonSendMail);
        sendMail.setTag(position);
        //ratingBar=(RatingBar)view.findViewById(R.id.ratingBarFavorite);
        rateStar = (ImageButton) view.findViewById(R.id.imageButtonRateStar);
        rateStar.setTag(position);

        more = (ImageButton) view.findViewById(R.id.imageButtonMore);
        more.setTag(position);


        sendSMS.setOnClickListener(this);
        phoneCall.setOnClickListener(this);
        sendMail.setOnClickListener(this);
        rateStar.setOnClickListener(this);
        more.setOnClickListener(this);



        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        user = gson.fromJson(json, User.class);
        Log.i("shilo", "the user reference is: " + user);
        Log.i("shilo", "in getView method-the size of favorite list is: " + user.getFavoriteJobs().size());
        Log.i("shilo", "the positionl is: "+position);
        //ratingBar.setOnClickListener(this);


/*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //center.scrollTo(1100, 0);

                        center.setScrollX((int) (((LinearLayout) ((LinearLayout) center.getChildAt(0)).getChildAt(0)).getChildAt(1).getX()));
                        Log.i("shilo","The resolution is: "+ ((LinearLayout) ((LinearLayout) center.getChildAt(0)).getChildAt(0)).getChildAt(1).getX() + "");
                    }
                }, 0);
            }
        });
        thread.start();*/

        //listener = (OnListAdapterInteractionListener) activity;

        return view;
    }



    protected void getDistance(final double latitude, final double longitude, final String destination)
    {
        //distanceString="";
        //try {
            new AsyncTask<Void,Void,String >(){
                @Override
                protected void onPostExecute(String aString) {
                        distance.setText(aString);
                        adapter.notifyDataSetChanged();
                        Log.i("shilo","(onPostExecute)the distance in the job window is: " +distance.getText());

                    super.onPostExecute(aString);
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }

                @Override
                protected String doInBackground(Void... params) {

                    HttpURLConnection conn = null;
                    StringBuilder jsonResults = new StringBuilder();
                    String message="";
                    try {


                        StringBuilder sb = new StringBuilder(DISTANCE_GET_HTTPS);
                        sb.append("origins=");
                        String from=String.valueOf(latitude+","+longitude);
                        Log.i("shilo", "the from destination is: "+from);
                        String fromencode=URLEncoder.encode(String.valueOf(latitude+","+longitude), "utf8");
                        Log.i("shilo", "the from destination encode is: "+fromencode);
                        sb.append(URLEncoder.encode(String.valueOf(latitude+","+longitude), "utf8"));
                        sb.append("&destinations=");
                        sb.append(URLEncoder.encode(destination, "utf8"));
                        String destinationEncode=URLEncoder.encode(destination, "utf8");
                        sb.append("&key=" + API_KEY);
                        Log.i("shilo", "the destinationEncode is: "+destinationEncode);
                        Log.i("shilo", "the url is: "+sb.toString());


                        URL url = new URL(sb.toString());
                        conn = (HttpURLConnection) url.openConnection();
                        message=conn.getResponseMessage();
                        //conn.setRequestMethod("GET");
                        InputStreamReader in = new InputStreamReader(conn.getInputStream());
                        int read;
                        char[] buff = new char[1024];
                        while ((read = in.read(buff)) != -1)
                            jsonResults.append(buff, 0, read);



                    }catch (MalformedURLException e) {
                        Log.e("shilo", "Error processing Distance google API URL", e);
                    } catch (IOException e) {
                        Log.e("shilo", "Error connecting to Distance google API-response code:"+message, e);
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }



                    try {
                        // Create a JSON object hierarchy from the results
                        JSONObject jsonObj = new JSONObject(jsonResults.toString());
                        JSONObject rows=(JSONObject) jsonObj.getJSONArray("rows").get(0);
                        Log.i("shilo","the string that I got is: "+rows);
                        JSONArray element=rows.getJSONArray("elements");
                        JSONObject distance=((JSONObject) element.get(0)).getJSONObject("distance");
                        Log.i("shilo", "the distance is " +distance);
                        distanceString=distance.getString("text");
                        Log.i("shilo","the distance that I got is: "+distanceString);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return distanceString;
                }
            }.execute();
        //} catch (InterruptedException | ExecutionException e) {
         //   e.printStackTrace();
        //}
        /*
        distance.setText(distanceString);
        Log.i("shilo","the distance in the job window is: " +distance.getText());
        */
    }




    public HorizontalScrollView getScrollView() {
        return center;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return windows.isEmpty();
    }

    private void managePermissionCallPhone(int position) {
        Log.i("shilo", "get into manage permission function");

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CALL_PHONE)) {
                Log.i("shilo", "get into explenation");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_PHONE_CALL);

            } else {
                Log.i("shilo", "get into no need of explenation");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_PHONE_CALL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i("shilo", "get into callIntent");
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String phone=((Job)getItem(position)).getPhonenumber();
            if (phone!=null)
                callIntent.setData(Uri.parse("tel:"+phone));
            else
                callIntent.setData(Uri.parse("tel:1800400400"));

            activity.startActivity(callIntent);
        }
    }


    @Override
    public void onClick(View v) {
        position = (int) v.getTag();
        //Log.i("shilo","(begining of method)This item's id is: "+((Job)getItem(position)).getJobID());
        Log.i("shilo", "typed on right fagment button or rating bar");
        switch (v.getId()) {
            case R.id.imageButtonSendSMS:
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
            case R.id.imageButtonPhoneCall:
                manageInterest();


                Log.i("shilo", "came here-phonecall: ");

                managePermissionCallPhone(position);



                break;
            case R.id.imageButtonSendMail:
                manageInterest();

                sendEmail();
                Log.i("shilo","came here-send mail: ");
                break;
            case R.id.imageButtonRateStar:


                //TODO:I didn's solve the problem with changing picture
                //rateStar.setImageResource(R.drawable.star_on);
                //manageFavoriteAdding();
                //manageFavoriteAddingList();
                manageFavorite();

                Log.i("shilo","This item's id is: "+((Job)getItem(position)).getJobID());

                //http://stackoverflow.com/questions/16165728/android-notifydatasetchanged-not-working
                //important info about notify
                break;
            case R.id.imageButtonMore:
                Toast.makeText(activity,activity.getString(R.string.loading),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(activity,DetailsJobActivity.class);
                intent.putExtra("job",(Job)getItem(position));
                activity.startActivity(intent);
                /*
                Dialog dialog=new Dialog(activity,R.style.DialogAnimation);
                dialog.setContentView(R.layout.job_full_content);
                if (dialog.getWindow()!=null)
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                TextView title=(TextView)dialog.findViewById(R.id.titleTextViewDialog);
                title.setText(((Job)getItem(position)).getInfoTitle());
                TextView content=(TextView)dialog.findViewById(R.id.contentTextViewDialog);
                content.setText(((Job)getItem(position)).getInfoContent());
                dialog.show();
                */
                break;

        }
    }



    public interface OnListAdapterInteractionListener {
        void onListAdapterInteraction(HorizontalScrollView scrollView);
    }

    protected void sendSMS() {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , "0507405923");
        smsIntent.putExtra("sms_body"  , activity.getString(R.string.send_message));

        try {
            activity.startActivityForResult(smsIntent,REQUEST_CODE_SMS);
            //activity.finishActivity(REQUEST_CODE_SMS);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity,
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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.send_mail_object));
        emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.send_message));

        try {
            activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //activity.finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void getFAVORITES_Value()
    {
        userRef.child(phonenumber).child(FAVORITES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteJob=(ArrayList<String>) dataSnapshot.getValue();

                Log.i("shilo","1-the favorite job from method favorite job is: "+favoriteJob);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void isJobAlreadyFavorite(DatabaseReference reference, final String jobForSearch)
    {
        /*jobAlreadyFavorite=false;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteJob=dataSnapshot.getValue(String.class);
                Log.i("shilo","the dataSnapshot key is: "+dataSnapshot.getValue());

                String singleJob="";
                boolean stop=false;
                for(int i=0 ;(!stop) && i<favoriteJob.length() ; i++)
                {
                    char currentChar=favoriteJob.charAt(i);
                    Log.i("shilo","current char is: "+currentChar);
                    if(currentChar==',')
                    {
                        if(singleJob.equals(jobForSearch)) {
                            stop = true;
                            jobAlreadyFavorite=true;
                            Log.i("shilo","single job is: "+singleJob+" and job for search is: "+jobForSearch);
                            Log.i("shilo","is jobs equals? "+singleJob.equals(jobForSearch));
                        }
                        else singleJob="";
                    }
                    else
                        singleJob+=currentChar;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void manageFavoriteAddingList()
    {
        database = FirebaseDatabase.getInstance();
        userRef= database.getReference("users").child(user.getUserPhoneNumber());

        phonenumber=user.getUserPhoneNumber();
        //getFAVORITES_Value();

        //phonenumber=activity.getSharedPreferences("save", Context.MODE_PRIVATE).getString(PHONE_NUMBER,null);
        //Log.i("shilo","the phonenumber is: "+phonenumber);

        final DatabaseReference favoritesRef=userRef.child(phonenumber).child(FAVORITES);
        final String currentJob=((Job) getItem(position)).getJobID();

        //Log.i("shilo","start-the size of favorite list is: "+user.getFavoriteJobs().size());

        if (user.getFavoriteJobs().size()==0) {//for first favorite job
            //Log.i("shilo","for first favorite job");
            user.getFavoriteJobs().add(currentJob);
            favoritesRef.setValue(user.getFavoriteJobs());
            Toast.makeText(activity,activity.getResources().getString(R.string.add_to_favorites),Toast.LENGTH_SHORT).show();
        }
        else {
            //user.setFavoriteJobs((ArrayList<String>) dataSnapshot.getValue());
            //Log.i("shilo","the user.getFavoriteJobs() is: "+user.getFavoriteJobs());
            //if the job not in favorite list
            if (!(user.getFavoriteJobs().contains(currentJob))) {
                //Log.i("shilo","if the job not in favorite list");
                user.getFavoriteJobs().add(currentJob);
                favoritesRef.setValue(user.getFavoriteJobs());
                Toast.makeText(activity, activity.getResources().getString(R.string.add_to_favorites), Toast.LENGTH_SHORT).show();
            } else {//if the job already in list,remove it
                //Log.i("shilo","if the job already in list");
                //Log.i("shilo","remove the job "+currentJob);
                user.getFavoriteJobs().remove(currentJob);
                favoritesRef.setValue(user.getFavoriteJobs());
                Toast.makeText(activity, activity.getResources().getString(R.string.remove_from_favorites), Toast.LENGTH_SHORT).show();
            }
        }
        //Log.i("shilo","end-the size of favorite list is: "+user.getFavoriteJobs().size()+"\n\n");
        Log.i("shilo","    h");


        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();

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
        final String currentJob=((Job) getItem(position)).getJobID();
        Log.i("shilo","the current job is "+currentJob);
        if (currentList.size()==0) {
            //for first current job
            currentList.add(currentJob);
            currentListRef.setValue(currentList);
            if(currentListName.equals(FAVORITES))
                Toast.makeText(activity,activity.getResources().getString(R.string.add_to_favorites),Toast.LENGTH_SHORT).show();
        }
        else {
            //if this is not first job in the current list
            if (!(currentList.contains(currentJob))) {
                //if the job not in current list
                currentList.add(currentJob);
                currentListRef.setValue(currentList);
                if(currentListName.equals(FAVORITES))
                    Toast.makeText(activity, activity.getResources().getString(R.string.add_to_favorites), Toast.LENGTH_SHORT).show();
            } else if(currentListName.equals(FAVORITES)) {
                //if the job already in list,remove it. relevant just to favorite list!!!
                currentList.remove(currentJob);
                currentListRef.setValue(currentList);

                Toast.makeText(activity, activity.getResources().getString(R.string.remove_from_favorites), Toast.LENGTH_SHORT).show();
            }
        }

        //currentList.clear();

        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();

    }

    private void manageFavoriteAdding()
    {
        /*jobAlreadyFavorite=false;
        Log.i("shilo","This item's id is: "+((Job)getItem(position)).getJobID());
        Log.i("shilo", activity.getSharedPreferences("save", Context.MODE_PRIVATE).getString(PHONE_NUMBER,null));
        phonenumber=activity.getSharedPreferences("save", Context.MODE_PRIVATE).getString(PHONE_NUMBER,null);
        if(phonenumber!=null) {
            //if(userRef.child(phonenumber).child("favorites").getRoot()==null)
            //userRef.child(phonenumber).child("favorites").setValue(((Job) getItem(position)).getJobID());
            final String currentJob=((Job) getItem(position)).getJobID();
            DatabaseReference favoritesRef=userRef.child(phonenumber).child(FAVORITES);
            if (getFAVORITES_Value()==null)//if there is not favorite jobs
                userRef.child(phonenumber).child(FAVORITES).setValue(currentJob+",");


            else//if there is favorite jobs. check if this job already in favorite list job
            {
                favoritesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        favoritesJobs=dataSnapshot.getValue(String.class);
                        Log.i("shilo","the favoriteJobs are: "+favoritesJobs);
                        //Log.i("shilo","the dataSnapshot key is: "+dataSnapshot.getValue());

                        String singleJob="";
                        boolean stop=false;

                        //check if the job alreadyin favorite list
                        for(int i=0 ;(!stop) && i<favoritesJobs.length() ; i++)
                        {
                            char currentChar=favoritesJobs.charAt(i);
                            //Log.i("shilo","current char is: "+currentChar);
                            if(currentChar==',')
                            {
                                if(singleJob.equals(currentJob)) {
                                    jobAlreadyFavorite=true;

                                    stop = true;
                                    //Log.i("shilo","single job is: "+singleJob+" and job for search is: "+currentJob);
                                    //Log.i("shilo","is jobs equals? "+singleJob.equals(currentJob));
                                }
                                else singleJob="";
                            }
                            else
                                singleJob+=currentChar;

                            //Log.i("shilo","the jobAlreadyFavorite boolean for now is: "+jobAlreadyFavorite);
                        }


                        if (!(jobAlreadyFavorite))
                            userRef.child(phonenumber).child(FAVORITES).setValue(favoritejobs + (currentJob)+",");


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //checkForFavoriteJob(userRef.child(phonenumber).child(FAVORITES));
            }
        }
    */}

}




/*ViewPager viewPager=(ViewPager) view.findViewById(R.id.viewPagerMainDesktop);
        PageAdapterDesktopJob adapter= new PageAdapterDesktopJob(activity.getSupportFragmentManager());
        viewPager.setAdapter(adapter);*/


/*
<HorizontalScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/horizontalScrollView">
 */