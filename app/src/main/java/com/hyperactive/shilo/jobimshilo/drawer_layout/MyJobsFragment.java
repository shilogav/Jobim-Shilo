package com.hyperactive.shilo.jobimshilo.drawer_layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.User;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.Job;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.JobWindowFragment;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.ListAdapterForMainDesktop;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyJobsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MyJobsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ListView favoriteListView,searchedJobsListView;
    final String JOBS_NAME_DATABASE="jobs",FAVORITE_JOBS="favoriteJobs",INTEREST_JOBS="interestJobs";
    FirebaseDatabase database;
    DatabaseReference userRef;
    ArrayList<String> searchedJobs,favoriteJobs;
    FrameLayout centerContainer;
    User user;

    public MyJobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_my_jobs, container, false);
        centerContainer=(FrameLayout) view.findViewById(R.id.centerContainerMyJobs);
        final View favoriteJobLayout=inflater.inflate(R.layout.layout_my_jobs_favorite,centerContainer,false);
        final View searchedJobsLayout=inflater.inflate(R.layout.layout_my_jobs_searched,centerContainer,false);

        final Button favoriteButton=(Button) view.findViewById(R.id.favoriteButton);
        final Button searchedJobsButton=(Button) view.findViewById(R.id.searchedJobsButton);

        //get the user from sharedPreference
        SharedPreferences prefs = getActivity().getSharedPreferences("save",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        user = gson.fromJson(json, User.class);
        String phonenumber=user.getUserPhoneNumber();

        favoriteJobs=new ArrayList<String>();

        database= FirebaseDatabase.getInstance();
        userRef= database.getReference("users").child(phonenumber);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    searchedJobs = (ArrayList<String>) dataSnapshot.child(INTEREST_JOBS).getValue();
                    favoriteJobs = (ArrayList<String>) dataSnapshot.child(FAVORITE_JOBS).getValue();
                    Log.i("shilo", "searchedJobs are: " + searchedJobs);
                    Log.i("shilo", "favoriteJobs are: " + favoriteJobs);
                    //if the list empty,avoid null exception
                    if (favoriteJobs == null)
                        favoriteJobs = new ArrayList<String>();
                    if (searchedJobs == null)
                        searchedJobs = new ArrayList<String>();

                favoriteListView= (ListView)favoriteJobLayout.findViewById(R.id.favoriteListView);
                loadJobsFromDatabase(favoriteJobs,favoriteListView,favoriteJobLayout);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //centerContainer.addView(favoriteJobLayout);


        /////////////////////////////////////////////////////////////////////////////////////
        //manages buttons
        searchedJobsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedJobsButton.setBackground(getActivity().getDrawable(R.color.white));
                favoriteButton.setBackground(getActivity().getDrawable(R.drawable.my_jobs_buttons));

                centerContainer.removeAllViews();
                searchedJobsListView= (ListView)searchedJobsLayout.findViewById(R.id.seachedJobsListView);
                loadJobsFromDatabase(searchedJobs,searchedJobsListView,searchedJobsLayout);
                //centerContainer.addView(searchedJobsLayout);
            }
        });

        //favoriteButton.set
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteButton.setBackground(getActivity().getDrawable(R.color.white));
                searchedJobsButton.setBackground(getActivity().getDrawable(R.drawable.my_jobs_buttons));

                centerContainer.removeAllViews();
                favoriteListView= (ListView)favoriteJobLayout.findViewById(R.id.favoriteListView);
                loadJobsFromDatabase(favoriteJobs,favoriteListView,favoriteJobLayout);
                //centerContainer.addView(favoriteJobLayout);
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////
        return view;
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




    public void loadJobsFromDatabase(final ArrayList<String> relevantList, final ListView relevantListView, final View relevantlayout)
    {
        DatabaseReference myRef = database.getReference(JOBS_NAME_DATABASE);
        //myRef.
        Log.i("shilo","the reference of databse is "+ myRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Job> windowOfJob = new ArrayList<>();
                //long numInDatabase=-11;
                //numInDatabase=dataSnapshot.getChildrenCount();
                for(DataSnapshot jobDataSnapshot: dataSnapshot.getChildren()) {
                    Job job = jobDataSnapshot.getValue(Job.class);
                    Log.i("shilo","the relevant list is: "+relevantList);
                    if(relevantList.contains(job.getJobID()))
                        windowOfJob.add(job);
                    Log.i("shilo","the size of windows of jobs is: "+windowOfJob.size());
                    Log.i("shilo","is relevantList.contains(job.getJobID(): "+relevantList.contains(job.getJobID()));
                    ListAdapterForMainDesktop adapter = new ListAdapterForMainDesktop(getActivity(), windowOfJob,relevantListView);
                    relevantListView.setAdapter(adapter);
                    centerContainer.removeAllViews();
                    centerContainer.addView(relevantlayout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("shilo","error!!!, the loading from database didn't execute");
            }
        });
    }

    /*public void loadJobsFromDatabase(final ArrayList<String> relevantList, final ListView relevantListView, final View relevantlayout)
    {
        String phonenumber=user.getUserPhoneNumber();
        userRef= database.getReference("users").child(phonenumber);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                searchedJobs = (ArrayList<String>) dataSnapshot.child(INTEREST_JOBS).getValue();
                favoriteJobs = (ArrayList<String>) dataSnapshot.child(FAVORITE_JOBS).getValue();
/////////////////////////////////////////////////////////////////////////////////////////////////////
                DatabaseReference jobSMyRef = database.getReference(JOBS_NAME_DATABASE);
                //myRef.
                Log.i("shilo","the reference of databse is "+ jobSMyRef);

                jobSMyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Job> windowOfJob = new ArrayList<>();
                        for(DataSnapshot jobDataSnapshot: dataSnapshot.getChildren()) {
                            Job job = jobDataSnapshot.getValue(Job.class);
                            Log.i("shilo","the relevant list is: "+relevantList);
                            if(relevantList.contains(job.getJobID()))
                                windowOfJob.add(job);
                            Log.i("shilo","the size of windows of jobs is: "+windowOfJob.size());
                            Log.i("shilo","is relevantList.contains(job.getJobID(): "+relevantList.contains(job.getJobID()));
                            ListAdapterForMainDesktop adapter = new ListAdapterForMainDesktop(getActivity(), windowOfJob,relevantListView);
                            relevantListView.setAdapter(adapter);
                            centerContainer.removeAllViews();
                            centerContainer.addView(relevantlayout);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("shilo","error!!!, the loading from database didn't execute");
                    }
                });

/////////////////////////////////////////////////////////////////////////////////////////////////////
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }*/
}
