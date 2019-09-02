package com.hyperactive.shilo.jobimshilo.drawer_layout;

import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hyperactive.shilo.jobimshilo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SmartAgentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SmartAgentFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    View view;
    View GPS_On, GPS_Off;
    boolean GPS_is_Off;
    Handler handler;

    public SmartAgentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_smart_agent, container, false);
        //prepare the GPS_Off layout and inflate it
        GPS_Off = inflater.inflate(R.layout.smart_agent_gps_off, (FrameLayout) view, false);
        ((FrameLayout) view).addView(GPS_Off);
        GPS_is_Off=true;

        //prepare the GPS_On layout
        GPS_On = inflater.inflate(R.layout.smart_agent_gps_on, (FrameLayout) view, false);

        //TODO: to inflate the right layouts if gps on/off

        handler=new Handler();



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(GPS_is_Off)
                {
                    if(checkStatusGPS()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((FrameLayout) view).removeView(GPS_Off);
                                ((FrameLayout) view).addView(GPS_On);
                            }
                        });
                        GPS_is_Off=false;
                    }

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }}}
        }).start();
        super.onActivityCreated(savedInstanceState);
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
        void onFragmentInteraction(Fragment fragment);
    }

    private boolean checkStatusGPS()
    {
        if(getActivity()!=null) {
            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return statusOfGPS;
        }
        else return false;
    }
}
