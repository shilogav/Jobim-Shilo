package com.hyperactive.shilo.jobimshilo.drawer_layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.JobWindowFragment;
import com.hyperactive.shilo.jobimshilo.search_jobs.SearchJobsFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindJobsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FindJobsFragment extends Fragment implements JobWindowFragment.OnFragmentInteractionListener {

    private OnFragmentInteractionListener mListener;

    public FindJobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_find_jobs, container, false);
        // Inflate the layout for this fragment


        //manages search button
        Button searchJobImageButton=(Button)view.findViewById(R.id.search_job_image_button);
        searchJobImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction= getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide,R.anim.slide);
                //transaction.addToBackStack("search job");

                transaction.add(R.id.content_frame,new SearchJobsFragment()).commit();
            }
        });

/*
        final JobWindowFragment fragment=new JobWindowFragment();


        new Thread(new Runnable() {
            @Override
            public void run() {
                FragmentManager manager=getActivity().getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.add(R.id.windowsLayout, fragment);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {

                }
                transaction.commit();
            }
        }).start();*/

        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        Log.i("shilo","find jobs fragment stopped");
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        mListener.onFragmentInteraction(this);
        super.onStart();
    }

    //JobWindowsFragment listener
    @Override
    public void onFragmentInteraction(Fragment fragment) {

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
