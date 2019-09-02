package com.hyperactive.shilo.jobimshilo.search_jobs;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.Job;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdTabFragment extends Fragment {
    private FirstTabFragment.OnFragmentInteractionListener mListener;
    ArrayList<Careers> rows;
    ArrayList<Job> jobs;
    ListAdapter adapter;
    final String JOBS_NAME_DATABASE="jobs";

    public ArrayList<Careers> getRows() {
        return rows;
    }

    public ThirdTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_third_tab, container, false);

        //SearchView searchView=(SearchView) view.findViewById(R.id.searchViewCompany);
        LinearLayout layout=(LinearLayout) view.findViewById(R.id.second_tab_layout);
        final ListView listView=(ListView) view.findViewById(R.id.companyList);

        rows = new ArrayList<Careers>();
        jobs=new ArrayList<Job>();

        //I load the list of rows from database
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(JOBS_NAME_DATABASE);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot jobDataSnapshot: dataSnapshot.getChildren()) {
                    Job job = jobDataSnapshot.getValue(Job.class);
                    jobs.add(job);
                }
                for (Job job:jobs)
                {
                    rows.add(new Careers(job.getCompanyName()));
                }

                adapter=new ListAdapterForCompanyTab(getContext(),rows);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //rows.add(new Careers(getString(R.string.super_pharm)));
        //rows.add(new Careers(getString(R.string.samsung)));
        //rows.add(new Careers(getString(R.string.delek)));
        //rows.add(new Careers(getString(R.string.mobileye)));




        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FirstTabFragment.OnFragmentInteractionListener) {
            mListener = (FirstTabFragment.OnFragmentInteractionListener) context;
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
    public void onStop() {
        mListener.onFragmentInteraction(this);
        super.onStop();
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
