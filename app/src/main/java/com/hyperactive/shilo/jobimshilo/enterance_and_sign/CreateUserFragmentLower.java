package com.hyperactive.shilo.jobimshilo.enterance_and_sign;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hyperactive.shilo.jobimshilo.R;


public class CreateUserFragmentLower extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private String messageCode;
    private String phoneNumber;
    private EditText editText;
    Handler handler;
    ProgressDialog dialog;
    final int MY_PERMISSIONS_REQUEST_SEND_MESSAGES=1;
    boolean isPermissionGranted;
    private OnFragmentInteractionListener mListener;
    private Thread thread,permissionThread;





    public CreateUserFragmentLower() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CreateUserFragmentLower.
     */

    public static CreateUserFragmentLower newInstance(String param1) {
        CreateUserFragmentLower fragment = new CreateUserFragmentLower();
        Bundle args= new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler=new Handler();
    }


    public String getMessageCode() {
        return messageCode;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_create_user_lower, container, false);
        Log.i("shilo","get into create user fragment lower-onCreate view");
        editText = (EditText) view.findViewById(R.id.insertNumberEditText);


        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                Log.i("shilo","number of letters in editText: "+editText.length());
                if(editText.length()==9) {
                    phoneNumber="972"+editText.getText().toString();
                    Log.i("shilo","should run the progress dialog know");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SmsManager smsManager = SmsManager.getDefault();
                            messageCode=generatePIN();
                            smsManager.sendTextMessage(phoneNumber, null, messageCode, null, null);
                        }
                    }).start();

                    ProgressDialog progressDialog=new ProgressDialog(getContext());
                    ProgressDialogTask task=new ProgressDialogTask(progressDialog);
                    task.execute();

                    //next level. sending and gettind code number
                    mListener.onFragmentInteraction(CreateUserFragmentLower.this);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        /*
        //first level-to send the message to typed number
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(final View view, int i, KeyEvent keyEvent) {
                Log.i("shilo","number of letters in editText: "+editText.length());
                if(editText.length()==9) {
                    phoneNumber="972"+editText.getText().toString();
                    Log.i("shilo","should run the progress dialog know");


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SmsManager smsManager = SmsManager.getDefault();
                            messageCode=generatePIN();
                            smsManager.sendTextMessage(phoneNumber, null, messageCode, null, null);
                        }
                    }).start();

                    ProgressDialogTask task=new ProgressDialogTask();
                    task.execute();



                    //next level. sending and gettind code number
                    mListener.onFragmentInteraction(CreateUserFragmentLower.this);
                }
                return false;
            }
        });*/
        return view;
    }


    @Override
    public void onPause() {
        editText.setText("");
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("shilo","on attach function fragment lower");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ////////////////////////////////////////////
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Fragment fragment);
    }
////////////////////////////////////////////

    private static class ProgressDialogTask extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog progressDialog;


        ProgressDialogTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            progressDialog.setTitle("sending massege");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }
    }


    public String generatePIN()
    {
        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        //Store integer in a string

        messageCode=String.valueOf(randomPIN);

        return messageCode;
        //=String.valueOf(PINString);
    }

}


/*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("shilo","start managing onRequestPermissions");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_MESSAGES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted=true;

                    SmsManager smsManager = SmsManager.getDefault();
                    messageCode=generatePIN();
                    smsManager.sendTextMessage(phoneNumber, null, messageCode, null, null);


                    //next level. sending and gettind code number
                    mListener.onFragmentInteraction(CreateUserFragmentLower.this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    isPermissionGranted=false;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getContext()).setTitle("can't send messages").create().show();
                        }
                    });
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/