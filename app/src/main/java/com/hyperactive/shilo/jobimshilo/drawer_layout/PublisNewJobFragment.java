package com.hyperactive.shilo.jobimshilo.drawer_layout;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.GooglePlacesAutocompleteAdapter;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.MainActivity;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.User;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PublisNewJobFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PublisNewJobFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    ImageButton companyButton,kindOfJobButton,moreInfoButton,locationButton,attachButton;
    ViewGroup centerContainer;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    User user;
    View window;
    LayoutInflater inflater;
    ArrayList<String> list;
    EditText infoTitle;
    EditText infoContent;
    final String COMAPNY_NAME="company name",
            BRANCH_NAME="branch_name",
            KIND_OF_JOB="kind of job",
            INFO_TITLE="info title",
            INFO_CONTENT="info content",
            LOCATION_OF_JOB="location",
            PHONE_NUMBER="phone number";
    private static final String LOG_TAG = "Google Places shilo";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCqZl-8zoFmkw0VSlZOh2Qb-4bTrL7vxwM";

    //company page
    EditText editTextCompany,editTextBranch;
    boolean legalCompanyName,legalKindOfJob,legalInfoTitle,legalLocation;
    Handler handler;
    Dialog dialog;
    String errorMessage;

    public PublisNewJobFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_publis_new_job, container, false);
        //for saving data
        //prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        prefs = getActivity().getSharedPreferences("save", MODE_PRIVATE);
        editor=prefs.edit();
        //container=(ViewGroup)view.findViewById(R.id.containerNewJob);
        setCenterContainer((ViewGroup)view.findViewById(R.id.containerNewJob));
        handler=new Handler();
        manageCompanyWindow();

        checkingWhenDeleteUser();

        /*
        window=inflater.inflate(R.layout.layout_new_job_company,getCenterContainer(),false);
        editTextCompany=(EditText) window.findViewById(R.id.editTextCompany);
        editTextBranch=(EditText) window.findViewById(R.id.editTextBranch);
        /*
        //save the company & branch name
        saveCompanyName();
        getCenterContainer().addView(window);
        //Log.i("shilo","The container in new job is: "+ container);
        */


        companyButton=(ImageButton) view.findViewById(R.id.companyButton);
        kindOfJobButton=(ImageButton) view.findViewById(R.id.kindOfJobButton);
        moreInfoButton=(ImageButton) view.findViewById(R.id.moreInfoButton);
        locationButton=(ImageButton) view.findViewById(R.id.locationButton);
        //attachButton=(ImageButton) view.findViewById(R.id.attachButton);

        checkingWhenDeleteUser();

        companyButton.setOnClickListener(this);
        kindOfJobButton.setOnClickListener(this);
        moreInfoButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        //attachButton.setOnClickListener(this);



        return view;
    }

    private void setCenterContainer(ViewGroup container)
    {
        this.centerContainer=container;
    }
    private ViewGroup getCenterContainer()
    {
        return centerContainer;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } /*else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        editor=prefs.edit();
        //save the company and branch name
        //if(!(centerContainer.getChildAt(0).getId()==R.id.companyButton))
        //saveCompanyName();

        centerContainer=getCenterContainer();
        switch (view.getId())
        {
            case R.id.companyButton:
                Log.i("shilo","The container in new job is: "+ centerContainer);
                manageCompanyWindow();
                break;
            //////////
            case R.id.kindOfJobButton:
                //saveCompanyName();
                //legitimacyCompanyName();

                //if(legalCompanyName) {
                if (!(centerContainer.getChildAt(0).getId() == R.id.kindOfJobContainer)) {
                    window = inflater.inflate(R.layout.layout_new_job_kind_of_job, centerContainer, false);
                    centerContainer.removeAllViews();
                    centerContainer.addView(window);
                    final ListView listView = (ListView) window.findViewById(R.id.listViewKindOfJob);
                    list = new ArrayList<String>();

                    list.add(getString(R.string.barman));
                    list.add(getString(R.string.waiter));
                    list.add(getString(R.string.messenger));
                    list.add(getString(R.string.driver));
                    list.add(getString(R.string.maintenance_man));
                    list.add(getString(R.string.cleaner));
                    list.add(getString(R.string.service_and_sale));
                    list.add(getString(R.string.cashier));
                    list.add(getString(R.string.usher));
                    list.add(getString(R.string.guard));
                    list.add(getString(R.string.host));
                    list.add(getString(R.string.counter_worker));

                    checkingWhenDeleteUser();

                    //ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),R.layout.layout_for_new_job_kind_of_job,R.id.textViewKindOfJob,list);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, list);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //Toast.makeText(getContext(),i+" typed",Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getContext(), list.get(i) + " typed ", Toast.LENGTH_SHORT).show();
                            editor.putString(KIND_OF_JOB, list.get(i));
                            editor.apply();
                            Toast.makeText(getContext(),getString(R.string.details_saved),Toast.LENGTH_SHORT).show();
                            manageMoreInfoWindow();
                        }
                    });

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //}
                    /*else
                    {
                        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                        dialog.setTitle(getString(R.string.enter_name_of_company));
                        dialog.create();
                        dialog.show();
                    }*/
                break;
            /////////
            case R.id.moreInfoButton:
                manageMoreInfoWindow();
                break;
            /////////
            case R.id.locationButton:
                Log.i("shilo","came to location window");
                //legitimacyCompanyName();
                //legitimacyKindOJob();
                //legitimacyMoreInfoTitle();



                //if(legalCompanyName&&legalKindOfJob&&legalInfoTitle) {
                //TODO: I'm working at the location here.
                if (!(centerContainer.getChildAt(0).getId() == R.id.locationContainer)) {
                    window = inflater.inflate(R.layout.layout_new_job_location, centerContainer, false);
                    centerContainer.removeAllViews();
                    centerContainer.addView(window);
                    //final EditText editTextLocation=(EditText)window.findViewById(R.id.editTextNewJobLocation);
                    final AutoCompleteTextView autoCompleteLocation=(AutoCompleteTextView)window.findViewById(R.id.autoCompleteNewJobLocation);
                    autoCompleteLocation.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.for_auto_completed_layout));
                    autoCompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String str = (String) parent.getItemAtPosition(position);
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                        }
                    });
                    final Button acceptButton = (Button) window.findViewById(R.id.buttonNewJobAccept);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    errorMessage="";
                    legalCompanyName=true;
                    legalKindOfJob=true;
                    legalInfoTitle=true;
                    legalLocation=true;

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(autoCompleteLocation.length()!=0) {
                                Log.i("shilo",autoCompleteLocation+" length is "+autoCompleteLocation.length());
                                editor.putString(LOCATION_OF_JOB,autoCompleteLocation.getText().toString());
                                editor.commit();
                                Log.i("shilo","The location saved is "+prefs.getString(LOCATION_OF_JOB,""));
                            }
                            errorMessage=getString(R.string.error_message);

                            checkLegitemacyOfJob();

                            ///////////////////////////////////////////////////////////////
                            //create database for creating users
                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            ///////////////////////////////////////////////////////////////

                            if (legalCompanyName && legalKindOfJob && legalInfoTitle && legalLocation)
                            {
                                builder.setMessage(getString(R.string.createJob));
                                dialog=builder.create();
                                dialog.show();

                                //String phoneNumber=getActivity().getIntent().getStringExtra(PHONE_NUMBER);
                                String phoneNumber=prefs.getString(PHONE_NUMBER,null);
                                //Toast.makeText(getContext(),"phonenumber is "+phoneNumber,Toast.LENGTH_SHORT).show();
                                Log.i("shilo","phonenumber is "+phoneNumber);

                                checkingWhenDeleteUser();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Job job=new Job(prefs.getString(COMAPNY_NAME,null),
                                                prefs.getString(BRANCH_NAME,null),prefs.getString(KIND_OF_JOB,null),
                                                prefs.getString(INFO_TITLE,null),prefs.getString(INFO_CONTENT,null),
                                                prefs.getString(LOCATION_OF_JOB,null),( String.valueOf((int)(Math.random()*9000)+1000)));

                                        try {
                                            mDatabase.child("jobs").child(job.getJobID()).setValue(job);
                                        }
                                        catch (DatabaseException e)
                                        {
                                            Log.i("shilo","error in database");
                                        }
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                //getActivity().getSupportFragmentManager().beginTransaction()
                                                dialog.dismiss();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new FindJobsFragment()).commit();
                                            }
                                        });
                                    }
                                }).start();
                            }
                            else {
                                builder.setMessage(errorMessage);
                                dialog=builder.create();
                                dialog.show();
                            }
                        }
                    });
                    //TODO: TO CHECK LEGITIMACY OF CREATE JOB. THEN TO CREATE CLASS FOR JOB.
                }

                //}
                checkingWhenDeleteUser();
                break;
                /*case R.id.attachButton:
                    if(!(centerContainer.getChildAt(0).getId()==R.id.attachButton)) {
                    window=inflater.inflate(R.layout.layout_new_job_attach,centerContainer,false);
                        centerContainer.removeAllViews();
                        centerContainer.addView(window);
                    }
                    break;*/
        }
    }

    private void manageCompanyWindow()
    {

        inflater=getActivity().getLayoutInflater();
        if(centerContainer.getChildAt(0)==null||(!(centerContainer.getChildAt(0).getId()==R.id.companyContainer))) {
            window=inflater.inflate(R.layout.layout_new_job_company,centerContainer,false);
            final Button button=(Button)window.findViewById(R.id.companyAcceptButton);
            editTextCompany=(EditText) window.findViewById(R.id.editTextCompany);
            editTextBranch=(EditText) window.findViewById(R.id.editTextBranch);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editTextCompany.length()!=0) {
                        saveCompanyName();
                        Log.i("shilo", "The company name in company window is " + prefs.getString(COMAPNY_NAME, ""));
                        Toast.makeText(getContext(), getString(R.string.details_saved), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            editTextCompany.setText(prefs.getString(COMAPNY_NAME,""));
            editTextBranch.setText(prefs.getString(BRANCH_NAME,""));
            editTextBranch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        button.performClick();
                    }
                    return false;
                }
            });
            //Log.i("shilo","The edittext company in new job is: "+ editTextCompany);
            //Log.i("shilo","The edittext branch in new job is: "+ editTextBranch);
            centerContainer.removeAllViews();
            centerContainer.addView(window);




        }
        /*
        //save the company & branch name
        saveCompanyName();*/
        //Log.i("shilo","The container in new job is: "+ container);
    }
    private void saveCompanyName()
    {
        if(editTextCompany!=null) {
            editor.putString(COMAPNY_NAME, editTextCompany.getText().toString());

        }
        if(editTextBranch!=null) {
            editor.putString(BRANCH_NAME, editTextBranch.getText().toString());
        }
        editor.commit();
    }

    private  void manageMoreInfoWindow() {

        ///////////////////////////////////
        //checks the legitimacy of typing this button
        //legitimacyCompanyName();

        //legitimacyKindOJob();
        ///////////////////////////////////

        //if (legalCompanyName && legalKindOfJob) {
        if (!(centerContainer.getChildAt(0).getId() == R.id.moreInfoContainer)) {
            window = inflater.inflate(R.layout.layout_new_job_more_info, centerContainer, false);
            centerContainer.removeAllViews();
            centerContainer.addView(window);
            TextView search = (TextView) window.findViewById(R.id.textViewMoroInfoTitleSearch);
            TextView company=(TextView)window.findViewById(R.id.textViewMoreInfoCompany);
            TextView kind=(TextView)window.findViewById(R.id.textViewMoreInfoKind);
            search.setText(getString(R.string.search));
            company.setText(prefs.getString(COMAPNY_NAME, ""));
            Log.i("shilo","The company name is "+prefs.getString(COMAPNY_NAME, ""));
            kind.setText(prefs.getString(KIND_OF_JOB, ""));
            infoTitle=(EditText)window.findViewById(R.id.editTextTitle);
            infoContent=(EditText)window.findViewById(R.id.editTextContent);
            infoTitle.setText(prefs.getString(INFO_TITLE,""));
            infoContent.setText(prefs.getString(INFO_CONTENT,""));
            Button button=(Button)window.findViewById(R.id.more_info__accept_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(infoTitle.length()!=0) {
                        if (infoTitle != null)
                            editor.putString(INFO_TITLE, infoTitle.getText().toString());
                        if (infoContent != null)
                            editor.putString(INFO_CONTENT, infoContent.getText().toString());
                        editor.commit();
                        Toast.makeText(getContext(), getString(R.string.details_saved), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //}
    }

    private void checkLegitemacyOfJob()
    {
        if(prefs.getString(COMAPNY_NAME,"").equals("")) {
            errorMessage += "-" + COMAPNY_NAME + "\n";
            legalCompanyName=false;
        }
        if(prefs.getString(KIND_OF_JOB,"").equals("")) {
            errorMessage += "-" + KIND_OF_JOB + "\n";
            legalKindOfJob=false;
        }
        if(prefs.getString(INFO_TITLE,"").equals("")) {
            errorMessage += "-" + INFO_TITLE + "\n";
            legalInfoTitle=false;
        }
        if(prefs.getString(LOCATION_OF_JOB,"").equals("")) {
            errorMessage += "-" + LOCATION_OF_JOB + "\n";
            legalLocation=false;
        }
    }

    @Override
    public void onPause() {
        //I fixed bug here. I cannot clear the prefs without saving the object user. otherwise, user will be deleted and the application crash.

       // user=MainActivity.fromSharedPreferences(prefs);
        editor.putString(BRANCH_NAME,null);
        editor.putString(INFO_TITLE,null);
        editor.putString(INFO_CONTENT,null);
        editor.putString(LOCATION_OF_JOB,null);
        editor.putString(COMAPNY_NAME,null);
        //MainActivity.toSharedPreferences(editor,user);
        editor.commit();
        //Toast.makeText(getContext(),"paused",Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    private void checkingWhenDeleteUser()
    {
        ////////////////////////////////////
        //checking
        //get the user from sharedPreference
        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        user = gson.fromJson(json, User.class);
        Log.i("shilo","the user is: "+user);
        ////////////////////////////////////
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*private class StartLoginAsyncTask extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        private final Context context;

        public StartLoginAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // UI work allowed here
            dialog = new ProgressDialog(context);
            // setup your dialog here
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(context.getString(R.string.createJob));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... ignored) {
            Integer returnCode = doLogin();
            return returnCode;
        }

        @Override
        protected void onPostExecute(Integer returnCode) {
            // UI work allowed here
            dialog.dismiss();
            /*if (returnCode == LOGIN_OK) {
                // ... show other dialogs here that it was OK
            } else {
                // ... bad news dialog here
            }
        }
    }*/
}

/*
private void legitimacyCompanyName()
    {
        if(prefs.getString("company","").equals("")) {
            legalCompanyName = false;
            if ((centerContainer.getChildAt(0).getId() == R.id.companyContainer)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(getString(R.string.enter_name_of_company));
                dialog.create();
                dialog.show();
            }
        }
        else
            legalCompanyName=true;
    }

    private void legitimacyKindOJob()
    {
        if(prefs.getString("kind of job","").equals("")) {
            legalKindOfJob = false;
            if ((centerContainer.getChildAt(0).getId() == R.id.kindOfJobContainer)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(getString(R.string.choose_kind_of_job));
                dialog.create();
                dialog.show();
            }
        }
        else
            legalKindOfJob=true;
    }
    private void legitimacyMoreInfoTitle()
    {
        if(prefs.getString("info title","").equals("")) {
            legalInfoTitle = false;
            if ((centerContainer.getChildAt(0).getId() == R.id.kindOfJobContainer)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(getString(R.string.enter_info_title));
                dialog.create();
                dialog.show();
            }
        }
        else
            legalInfoTitle=true;
    }



 */