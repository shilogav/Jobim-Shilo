package com.hyperactive.shilo.jobimshilo.enterance_and_sign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.MainDesktopActivity;
import com.hyperactive.shilo.jobimshilo.R;


public class MainActivity extends AppCompatActivity {
    View view;
    RelativeLayout mainLayout;
    ConnectivityDialog connectivityDialog;
    Intent intent;
    Handler handler;
    final int CREATE_USER_ACTIVITY_CALL=1,MAIN_DESKTOP_ACTIVITY=2,CREATE_USER_ACTIVITY_CALL_DENY=-10;;
    private DatabaseReference mDatabase;
    boolean isAlreadyConnected;
    String phoneNumber;
    ActionBar bar;
    final String PHONE_NUMBER="phone number",ALREADY_CONNECTED="alreadyConnected";
    User user;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    DatabaseReference userRef;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar=getSupportActionBar();
        if (bar!=null)
            bar.hide();

        Log.i("shilo","onCreate running");



        mainLayout = findViewById(R.id.activity_main);
        ///////////////////////////////////////////////////////////////
        //SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        prefs = getSharedPreferences("save", MODE_PRIVATE);
        editor=prefs.edit();
        editor.apply();
        //Log.i("shilo","check about save file and if there is user already connected:"+prefs.getString("user",null));



        //inflating enterance layout
        addEnteranceFragment();

        ////////////////////////////////////////////////////////
        //checks if the user connected to user in database
        manageTwoWaysOfEnterance();
        ////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //checking if there is connection
        if (!isNetworkAvailable()) {
            connectivityDialog = new ConnectivityDialog();
            connectivityDialog.show(getSupportFragmentManager(), "ConnectivityDialog");
            toastMassegeIfNoNetwork();
        }
        else
        {
            Log.i("shilo","second check-The user already connected? "+isAlreadyConnected);
            if(!isAlreadyConnected)
            //first time the application running,creating new user or connetcing to exist one
                addCreateActivityUser();
            else
                addMainDesktopActivity();
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }//end of onCreate



    @Override
    protected void onStop() {
        //Log.i("Main activity-shilo","on stop running");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.i("Main activity-shilo","on restart running");
    }




    //checks if the network is on
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void toastMassegeIfNoNetwork()
    {
        if(!isNetworkAvailable())
            Toast.makeText(this,"no internet connection",Toast.LENGTH_SHORT).show();
    }

    //send the UI to activity that create new user
    private void addCreateActivityUser()
    {
        Log.i("shilo","get into addCreateActivityUser-for first time");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intent=new Intent(MainActivity.this,CreateUserActivity.class);
                startActivityForResult(intent,CREATE_USER_ACTIVITY_CALL);
            }
        }).start();
    }

    //adding fragment to info at the beginning of application
    private void addEnteranceFragment()
    {
        handler=new Handler();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        if (inflater != null) {
                            view = inflater.inflate(R.layout.activity_enterance, mainLayout);
                        } else {
                            new AlertDialog.Builder(MainActivity.this).setMessage(R.string.mistake).create().show();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.exit(0);
                        }
                    }
                });
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CREATE_USER_ACTIVITY_CALL)
        {
            mainLayout.removeView(view);
            Log.i("shilo","arrived to onActivityResult func");
            //phoneNumber=data.getStringExtra("phone number");
            phoneNumber=getSharedPreferences("save",MODE_PRIVATE).getString(PHONE_NUMBER,null);
            Log.i("shilo","the phone number in onActivityResult func is: "+phoneNumber);

            //create internal file which will be indicator for application to bypass the create user process
            createIndicatorForMainDesktopPassage();

            //continue to main desktop after registration
            addMainDesktopActivity();

        }
        if (requestCode==CREATE_USER_ACTIVITY_CALL_DENY)
        {
            System.exit(0);
        }
        if (requestCode==MAIN_DESKTOP_ACTIVITY)
        {
            Log.i("shilo", "should exit application");
            finish();
        }
    }

    //pass to the main desktop activity
    private void addMainDesktopActivity()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                prefs=getSharedPreferences("save",MODE_PRIVATE);
                user=fromSharedPreferences(prefs);

                //upload this user's info from database if exist
                Log.i("shilo","the user is: "+user);
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                if(user==null) {
                    finish();
                    System.exit(0);
                }
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        // This is where you do your work in the UI thread.
                        // Your worker tells you in the message what to do.
                        userRef = database.getReference("users").child(user.getUserPhoneNumber());
                    }
                };

                Log.i("shilo","the userRef is: "+userRef);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (userRef != null) {
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            Log.i("shilo","the user is "+user);
                            Log.i("shilo", "the user name is: " + user.getUsername());

                            /*Gson gson=new Gson();
                            String json=gson.toJson(user);
                            editor.putString("user",json);
                            editor.commit();*/
                            toSharedPreferences(editor,user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                phoneNumber=user.getUserPhoneNumber();
                intent=new Intent(MainActivity.this,MainDesktopActivity.class);
                intent.putExtra(PHONE_NUMBER,phoneNumber);
                Log.i("shilo","main activity. the phonenumber is "+phoneNumber);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivityForResult(intent,MAIN_DESKTOP_ACTIVITY);
                            }
                        });
                    }
                }).start();


            }
        }).start();





    }






    /*  manage two ways of enterance, if application new in the smartphone-create new user or connect to exist one
        if already connected pass over the create user activity
    */
    private void manageTwoWaysOfEnterance()
    {
        prefs = getSharedPreferences("save", MODE_PRIVATE);

        //if(prefs.getBoolean(ALREADY_CONNECTED,false))
        isAlreadyConnected = fromSharedPreferences(prefs) != null;

        Log.i("shilo","the user from sharedPreferences is: "+fromSharedPreferences(prefs));

        Log.i("shilo","The user already connected? "+isAlreadyConnected);
    }

    private void createIndicatorForMainDesktopPassage()
    {
        boolean alreadyConnected=true;
        //SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean(ALREADY_CONNECTED,alreadyConnected);
        editor.commit();

    }

    public static User fromSharedPreferences(SharedPreferences prefs)
    {
        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        User user = gson.fromJson(json, User.class);
        return user;
    }

    public static void toSharedPreferences(SharedPreferences.Editor editor,User user)
    {
        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();
    }

}





/*

    private void manageTwoWaysOfEnterance()
    {
        String filePath="user";
        File file = new File(MainActivity.this.getFilesDir().getAbsolutePath()+"/"+filePath);
        if(file.exists())
            isAlreadyConnected=true;
        else
            isAlreadyConnected=false;

        Log.i("shilo","The user already connected? "+isAlreadyConnected);
    }

    private void createFileForMainDesktopPassage()
    {
        String FILENAME = "user";
        String string = phoneNumber;

        FileOutputStream fos;

        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Log.i("shilo","The path is "+MainActivity.this.getFilesDir().getAbsolutePath());
    }


*/






    /*private AlertDialog createConnetivityDialog()
    {
        return new AlertDialog.Builder(MainActivity.this)
                .setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setTitle(R.string.enteranceDialogTitle)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                })
                .create();
    }*/

