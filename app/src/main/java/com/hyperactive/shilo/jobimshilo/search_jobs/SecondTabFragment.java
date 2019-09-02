package com.hyperactive.shilo.jobimshilo.search_jobs;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hyperactive.shilo.jobimshilo.GooglePlacesAutocompleteAdapter;
import com.hyperactive.shilo.jobimshilo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondTabFragment extends Fragment implements GoogleMap.OnMapClickListener,OnMapReadyCallback {
    MapView mMapView;
    private GoogleMap googleMap;
    final int REQUEST_LOCATION = 3;
    //private EditText searchView;
    private AutoCompleteTextView searchView;
    private LatLng latLng;
    Marker marker;
    private FirstTabFragment.OnFragmentInteractionListener mListener;

    public SecondTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second_tab, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewTabLocation);
        //searchView=(EditText)view.findViewById(R.id.searchViewTabMaps);
        searchView=(AutoCompleteTextView) view.findViewById(R.id.searchViewTabMaps);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately


        searchView.requestFocus();
        searchView.performClick();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.performClick();
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);


        searchView.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(),R.layout.for_auto_completed_layout));
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateLatlng();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                Log.i("shilo","onItemClick invoked");
                mListener.onFragmentInteraction(SecondTabFragment.this);
            }
        });


        return view;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.setOnMapClickListener(SecondTabFragment.this);

        // For showing a move to my location button
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            //return;
        }
        googleMap.setMyLocationEnabled(true);

        // For dropping a marker at a point on the Map
        //LatLng sydney = new LatLng(-34, 151);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        LatLng latLng=new LatLng(31.776629,35.234957);
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(8).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("shilo","location tab-click on map");
        this.latLng=latLng;
        //for hebrew address
        Locale lHebrew = new Locale("he");
        Geocoder geocoder = new Geocoder(getActivity(), lHebrew);

        double latitude,longitude;
        latitude=latLng.latitude;
        longitude=latLng.longitude;
        Log.i("shilo","second tab-the latitude and longitude are "+latitude+","+longitude);
        //add marker
        if (marker!=null)
            marker.remove();
        marker=googleMap.addMarker(new MarkerOptions().position(latLng));

        //get the readable address to show it in edittext
        ArrayList<Address> addresses= new ArrayList<>();
        try {
            addresses = (ArrayList<Address>) geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder addressToShow=new StringBuilder();

        if (!addresses.isEmpty()) {
            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                addressToShow.append(addresses.get(0).getAddressLine(i)).append(" ");
            }

            searchView.setText(addressToShow.toString());
            Log.i("shilo", "second tab-the address is " + addresses.get(0).toString());
        }

        mListener.onFragmentInteraction(SecondTabFragment.this);
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
    private void updateLatlng()
    {

        List<Address> addresses=null;
        Address address;
        Locale lHebrew = new Locale("he");
        Geocoder geocoder = new Geocoder(getActivity(), lHebrew);
        Log.i("shilo","SecondTabFragment-the address is "+searchView.getText().toString());
        try {
            addresses=geocoder.getFromLocationName(searchView.getText().toString(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses!=null && !addresses.isEmpty()) {
            address = addresses.get(0);
            this.latLng=new LatLng(address.getLatitude(),address.getLongitude());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
