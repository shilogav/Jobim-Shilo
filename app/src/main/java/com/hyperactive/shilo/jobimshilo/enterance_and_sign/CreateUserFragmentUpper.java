package com.hyperactive.shilo.jobimshilo.enterance_and_sign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyperactive.shilo.jobimshilo.R;


public class CreateUserFragmentUpper extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListenerUpper mListener;

    public CreateUserFragmentUpper() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_create_user_upper, container, false);
        LinearLayout layout=(LinearLayout)view.findViewById(R.id.upper);
        ImageView createUserImage1=new ImageView(getContext());
        createUserImage1.setImageResource(R.drawable.create_user_background);
        createUserImage1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(createUserImage1);
        ImageView createUserImage2=new ImageView(getContext());
        createUserImage2.setImageResource(R.drawable.create_user_background2);
        layout.addView(createUserImage2);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerUpper) {
            mListener = (OnFragmentInteractionListenerUpper) context;
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
    public interface OnFragmentInteractionListenerUpper {
        void onFragmentInteraction(Uri uri);
    }
}
