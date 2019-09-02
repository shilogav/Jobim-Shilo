package com.hyperactive.shilo.jobimshilo.enterance_and_sign;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hyperactive.shilo.jobimshilo.R;


public class MessageCodeFragment extends Fragment {

    EditText codeOfUser;
    Button insertButton;
    ProgressDialog progressDialog;
    private String codeKey;

    private OnFragmentInteractionListener mListener;

    public MessageCodeFragment() {
        // Required empty public constructor
    }

    public String getCodeKey() {
        return codeKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_message_code, container, false);
            codeOfUser = (EditText) view.findViewById(R.id.editText);
            insertButton = (Button) view.findViewById(R.id.insertButton);
            insertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    codeKey = codeOfUser.getText().toString();

                    Log.i("shilo", "manage to get into message code class");

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Loading..");
                    progressDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    progressDialog.dismiss();
                    mListener.onFragmentInteraction(MessageCodeFragment.this);
                }
            });

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
    public void onPause() {
        codeOfUser.setText("");
        super.onPause();
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
        void onFragmentInteraction(Fragment fragment);
    }
}
