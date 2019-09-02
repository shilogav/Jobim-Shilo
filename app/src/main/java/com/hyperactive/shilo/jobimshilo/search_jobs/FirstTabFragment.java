package com.hyperactive.shilo.jobimshilo.search_jobs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.Job;

import java.util.ArrayList;
import java.util.HashMap;


public class FirstTabFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Careers> rows;
    private ListAdapterForjobsTab adapter;
    final String JOBS_NAME_DATABASE="jobs";
    ArrayList<Job> jobs;
    ArrayList<String> jobsTypesStrings;



    public ArrayList<Careers> getRows() {
        return rows;
    }

    public FirstTabFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_first_tab,container,false);
        //SearchView searchView=(SearchView) view.findViewById(R.id.searchView);
        LinearLayout layout=(LinearLayout) view.findViewById(R.id.first_tab_layout);
        final ListView listView=(ListView) view.findViewById(R.id.jobsList);

        Log.i("shilo","get in FirstTabFragmnet");
        rows = new ArrayList<Careers>();
        jobs=new ArrayList<Job>();
        jobsTypesStrings=new ArrayList<String>();

        //I load the list of rows from database
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(JOBS_NAME_DATABASE);

        final HashMap<String,Integer> icons=iconForJobTypes();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //I did this complicate way to download the list from database and to make sure that
                //want be duplicate rows in this listview.
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot jobDataSnapshot: dataSnapshot.getChildren()) {
                            Job job = jobDataSnapshot.getValue(Job.class);
                            jobs.add(job);//list of jobs from database
                        }
                        for (Job job:jobs)
                        {
                            String currentKindOfJobName=job.getKindOfJob();
                            if(!jobsTypesStrings.contains(currentKindOfJobName)) {
                                jobsTypesStrings.add(currentKindOfJobName);
                                Careers currentCareer = new Careers(icons.get(currentKindOfJobName), currentKindOfJobName);
                                rows.add(currentCareer);
                                Log.i("shilo","(FirstTabFragmnet)get in addListenerForSingleValueEvent");
                            }
                        }
                        adapter=new ListAdapterForjobsTab(getContext(),rows);
                        listView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return null;
            }
        }.execute();


        return view;
    }

/*TODO: WRITE SEARCH VIEW
<SearchView
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_marginTop="34dp"
android:layout_alignParentTop="true"
android:layout_alignParentEnd="true"
android:queryHint="@string/find_job"
android:layout_marginEnd="21dp"
android:iconifiedByDefault="false"
android:id="@+id/searchView" />


 */



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
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i("shilo","onDetach called from FirstTabFragment");
    }

    @Override
    public void onStop() {
        //pass the details of chosen rows to MainDesktopActivity
        mListener.onFragmentInteraction(this);
        super.onStop();
    }

    private HashMap<String,Integer> iconForJobTypes()
    {
        HashMap<String,Integer> icons=new HashMap<String, Integer>();

        icons.put(getString(R.string.counter_worker),R.drawable.counter_worker);
        icons.put(getString(R.string.barman),R.drawable.bavarian_beer);
        icons.put(getString(R.string.host),R.drawable.host);
        icons.put(getString(R.string.messenger),R.drawable.messenger);
        icons.put(getString(R.string.cleaner),R.drawable.cleaner);
        icons.put(getString(R.string.maintenance_man),R.drawable.maintenance);
        icons.put(getString(R.string.service_and_sale),R.drawable.service);
        icons.put(getString(R.string.cashier),R.drawable.cashier);
        icons.put(getString(R.string.usher),R.drawable.usher);
        icons.put(getString(R.string.waiter),R.drawable.waiter);
        icons.put(getString(R.string.guard),R.drawable.guard);
        icons.put(getString(R.string.driver),R.drawable.driver);


        return icons;
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
