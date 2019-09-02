package com.hyperactive.shilo.jobimshilo.enterance_and_sign;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyperactive.shilo.jobimshilo.R;


public class ConnectivityDialog extends DialogFragment {

    View view;
    TextView ishur;


    @NonNull
    @Override
        public android.app.AlertDialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            view=inflater.inflate(R.layout.connetivity_dialog, null);
            builder.setView(view);
            ishur= view.findViewById(R.id.ishur);
            ishur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.exit(0);
                }
            });

            // Create the AlertDialog object and return it
            return builder.create();
        }

}

