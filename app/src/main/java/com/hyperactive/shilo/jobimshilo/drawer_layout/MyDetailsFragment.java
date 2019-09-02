package com.hyperactive.shilo.jobimshilo.drawer_layout;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.enterance_and_sign.User;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.MainDesktopActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.KeyEvent.KEYCODE_ENTER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MyDetailsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    public TextView name,city,year,email;
    public ImageView userPicture;
    public static final int REQUEST_IMAGE_CAPTURE = 1,SELECT_IMAGE=2;
    final String No_picture="user didn't put picture";
    public String picturePath;
    public Button acceptButton;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public User user;
    public final String NAME="username",CITY="city" ,YEAR_OF_BIRTH="dateOfBirth",EMAIL="email";

    public MyDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_details, container, false);

        /*//////////////////////////////////////////////////////
        //TODO for storage in firebase. I skipped that for now, moved on
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://jobim-shilo.appspot.com/");

        /////////////////////////////////////////////////////////////////////*/

        //get the user from sharedPreference
        prefs = getActivity().getSharedPreferences("save",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        user = gson.fromJson(json, User.class);

        //checks if picture is exist
        userPicture=(ImageView) view.findViewById(R.id.userPicture);
        picturePath=prefs.getString("path",No_picture);
        if(picturePath.equals(No_picture)) {
            Log.i("shilo","picture path is null");
            userPicture.setImageResource(R.drawable.picture_of_user);
        }
        else {
            Log.i("shilo","picture path NOT null");
            //userPicture.setImageResource(((MainDesktopActivity)getActivity()).loadImageFromStorage(picturePath));
            ((MainDesktopActivity)getActivity()).loadImageFromStorage(picturePath,userPicture);
        }
        /////////////////////////////////////////////////////////////////////

        //TODO to save the info of user to database
        name=(TextView)view.findViewById(R.id.userName);
        name.setHint(R.string.image_and_username);
        Log.i("shilo","the user is: "+user);
        Log.i("shilo","the user name is: "+user.getUsername());
        name.setText(user.getUsername());
        name.setSingleLine();
        //name.setText(prefs.getString(NAME,null));

        city=(TextView)view.findViewById(R.id.cityName);
        city.setHint(R.string.city);
        city.setText(user.getCity());
        city.setSingleLine();
        //city.setText(prefs.getString(CITY,null));

        year=(TextView)view.findViewById(R.id.yearOfBirth);
        year.setHint(R.string.year_of_birth);
        year.setText(user.getDateOfBirth());
        //year.setText(prefs.getString(YEAR_OF_BIRTH,null));

        email=(TextView)view.findViewById(R.id.emailOfUser);
        email.setHint(R.string.email);
        email.setText(user.getEmail());
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    acceptButton.performClick();
                }
                return false;
            }
        });
        //email.setText(prefs.getString(EMAIL,null));

        acceptButton=(Button)view.findViewById(R.id.acceptButton);

        userPicture.setOnClickListener(this);

        acceptButton.setOnClickListener(this);
        //name.setOnClickListener(this);
        //city.setOnClickListener(this);
        //year.setOnClickListener(this);
        //email.setOnClickListener(this);

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
    public void onPause() {
        Log.i("shilo","my detail fragment paused");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("shilo","my detail fragment stopped");
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.userPicture:

                final Dialog pictureDialog=new Dialog(getContext());
                pictureDialog.setContentView(R.layout.picture_dialog_layout);
                pictureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView camera= pictureDialog.findViewById(R.id.camera);
                TextView gallery= pictureDialog.findViewById(R.id.gallery);

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        manageCamera();
                        pictureDialog.dismiss();
                    }
                });
                //this is the resource of text string for gallery button
                //@string/picture_dialog_gallery
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,SELECT_IMAGE);
                        pictureDialog.dismiss();
                    }
                });
                pictureDialog.show();
                break;

            case R.id.acceptButton:
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //TODO maybe I need to check if user put legall details
            /*if( name.getHint().toString().isEmpty() || city.getHint().toString().isEmpty() ||
                        year.getHint().toString().isEmpty() || email.getHint().toString().isEmpty() ) {
                    new AlertDialog.Builder(getContext()).setTitle(R.string.error).setMessage(R.string.field_empty).create().show();
                }
                else {*/
                //new AlertDialog.Builder(getContext()).setTitle(R.string.details_saved).create().show();
                final Dialog acceptDialog=new Dialog(getContext());
                acceptDialog.setContentView(R.layout.dialog_accept_layout);
                acceptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                acceptDialog.show();

                editor = prefs.edit();
                //Toast.makeText(getContext(), name.getText(), Toast.LENGTH_SHORT).show();
                /*editor.putString(NAME, name.getText().toString());
                editor.putString(CITY, city.getText().toString());
                editor.putString(YEAR_OF_BIRTH, year.getText().toString());
                editor.putString(EMAIL, email.getText().toString());*/

                FirebaseDatabase database=FirebaseDatabase.getInstance();
                DatabaseReference userRef=database.getReference("users").child(user.getUserPhoneNumber());


                user.setUsername(name.getText().toString());
                user.setCity(city.getText().toString());
                user.setDateOfBirth(year.getText().toString());
                user.setEmail(email.getText().toString());

                userRef.setValue(user);

                Gson gson=new Gson();
                String json=gson.toJson(user);
                editor.putString("user",json);
                editor.apply();
                //}
                break;
        }
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


    private void manageCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //userPicture.setImageBitmap(imageBitmap);
            String pathToPicture=saveToInternalStorage(imageBitmap);
            ((MainDesktopActivity)getActivity()).loadImageFromStorage(pathToPicture,userPicture);
            mListener.onFragmentInteraction(this);

            //userPicture.setImageResource(((MainDesktopActivity)getActivity()).loadImageFromStorage(pathToPicture));
        }
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null)
        {
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.
                        getBitmap(getActivity().getContentResolver(), data.getData());
                String pathToPicture=saveToInternalStorage(bitmap);
                ((MainDesktopActivity)getActivity()).loadImageFromStorage(pathToPicture,userPicture);

                //userPicture.setImageBitmap(bitmap);

                mListener.onFragmentInteraction(this);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        picturePath=directory.getAbsolutePath();
        //save picture into memory
        editor=prefs.edit();
        editor.putString("path",picturePath);
        editor.apply();

        return picturePath;
    }


}
