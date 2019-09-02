package com.hyperactive.shilo.jobimshilo.enterance_and_sign;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.Manifest;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.R;

import java.io.Serializable;

public class CreateUserActivity extends FragmentActivity implements CreateUserFragmentLower.OnFragmentInteractionListener
        ,MessageCodeFragment.OnFragmentInteractionListener {


    FragmentTransaction fragmentTransaction;
    CreateUserFragmentUpper upper;
    CreateUserFragmentLower lower;
    MessageCodeFragment userCodeFragment;
    AlertDialog dialog;
    Intent intent;
    Handler handler;
    String messageCode,userCodeInserted;
    final String codeKey="Code";
    boolean isPermissionGranted;
    final int CREATE_USER_ACTIVITY_CALL=1,CREATE_USER_ACTIVITY_CALL_DENY=-10;
    public User user;
    final int MY_PERMISSIONS_REQUEST_SEND_MESSAGES=1;

    public String phoneNumber;
    public final String PHONE_NUMBER="phone number";
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Log.i("shilo","got into- addCreateActivityUser");

///////////////////////////////////

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        //android:windowSoftInputMode="stateAlwaysVisible"
///////////////////////////////////




        handler=new Handler();

        lower=new CreateUserFragmentLower();
        upper=new CreateUserFragmentUpper();
        userCodeFragment=new MessageCodeFragment();

        //fragmentTransaction=getSupportFragmentManager().beginTransaction();

//////////////////////////////////////////////
        //add the fragments
        if(savedInstanceState==null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog = new AlertDialog.Builder(CreateUserActivity.this).create();
                                dialog.setMessage("hello friend,loading...");
                                dialog.show();
                            }
                        });

                        Thread.sleep(1500);
                        dialog.dismiss();
                        //managePermission();
                        //if (isPermissionGranted) {
                            Thread.sleep(500);
                                managePermission();

                        //Log.i("shilo","is permission granted? "+ isPermissionGranted);
                        /*if(isPermissionGranted) {
                            fragmentTransaction.add(R.id.activity_create_user, upper);
                            fragmentTransaction.add(R.id.activity_create_user, lower);
                            fragmentTransaction.commit();
                        }*/

                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }

                }
            }).start();

            //for keyboard to show
            //imm.showSoftInput(findViewById(R.id.activity_enterance),InputMethodManager.SHOW_IMPLICIT);not working
            //fragmentTransaction.commit();
            //managePermission();

        }

//////////////////////////////////////////////



    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onFragmentInteraction(final Fragment fragment) {
        Log.i("Fragment Class",fragment.getClass()+"");

        //first fragment called
        if(fragment instanceof CreateUserFragmentLower) {
            messageCode = ((CreateUserFragmentLower)fragment).getMessageCode();
            phoneNumber=((CreateUserFragmentLower)fragment).getPhoneNumber();

            Log.i("shilo","check the message code in activity: "+messageCode);

            getSupportFragmentManager().beginTransaction().remove(upper).commit();
            getSupportFragmentManager().beginTransaction().remove(lower).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user,userCodeFragment).commit();
            //getSupportFragmentManager().beginTransaction().replace()
            Log.i("shilo","it manage to get to first fragment");
        }
        //second fragmnet code
        if(fragment instanceof MessageCodeFragment){

            Log.i("shilo","it manage to get to second fragment");

            userCodeInserted=((MessageCodeFragment)fragment).getCodeKey();

            Log.i("shilo","check the user code: "+userCodeInserted);

            //checks if the codes equals
            if(userCodeInserted.equals(messageCode))
            {
                dialog=new AlertDialog.Builder(this).setTitle("Thanks!!").create();
                dialog.show();
                Log.i("shilo","it manage to get code");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                        //add the user to memory
                        manageNewUser();

                    }
                }).start();
            }
            else
            {
                dialog=new AlertDialog.Builder(this).setTitle("wrong code").create();
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();

                        getSupportFragmentManager().beginTransaction().remove(userCodeFragment).commit();
                        getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user,upper).commit();
                        getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user,lower).commit();
                    }
                }).start();
            }

        }
/////////////////////////////////////////////////////////////////////////////////////////////////////////

    }



    private void manageNewUser()
    {
        prefs = getSharedPreferences("save", MODE_PRIVATE);
        editor=prefs.edit();

        writeNewUser(this.phoneNumber);

        Log.i("shilo","create user activity:phonenumber is "+phoneNumber);
        editor.putString(PHONE_NUMBER,phoneNumber);
        editor.apply();

        //intent=new Intent();
        //intent.putExtra(PHONE_NUMBER,phoneNumber);
        setResult(CREATE_USER_ACTIVITY_CALL);
        Log.i("shilo","end of createUserActivity-finish");
        finish();
    }



    private void writeNewUser(String phoneNumber) {
        user = new User(phoneNumber);
        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();
        //user.setCity("petach");


        //mDatabase.child("users").child(user.getUserPhoneNumber()).setValue(user);
        //mDatabase.child("users").child(user.getUserPhoneNumber()).setValue(user);
        ////////////////
/*
        DatabaseReference userPhoneNumberRef=mDatabase.child("users").child(user.getUserPhoneNumber());
        userPhoneNumberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                map=(Map<String, String>) dataSnapshot.getValue();
                word=map.get("city");
                Log.i("shilo","The word in event listener is "+word);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
    }


    private void managePermission()
    {
        Log.i("shilo","get into manage permission function");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                Log.i("shilo","get into explenation");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

            } else {
                Log.i("shilo","get into no need of explenation");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user, upper).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user, lower).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("shilo","start managing onRequestPermissions");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_MESSAGES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted=true;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user, upper).commit();
                                getSupportFragmentManager().beginTransaction().add(R.id.activity_create_user, lower).commit();
                            } catch (IllegalStateException e) {
                                Log.d("shilo:", "Can not perform this action after onSaveInstanceState-Exception", e);
                            }

                        }
                    });
                    //next level. sending and gettind code number
                    ////////mListener.onFragmentInteraction(CreateUserFragmentLower.this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    setResult(CREATE_USER_ACTIVITY_CALL_DENY);
                    finish();
                    System.exit(1);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    isPermissionGranted=false;
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

