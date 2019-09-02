package com.hyperactive.shilo.jobimshilo.drawer_layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.drawer_layout.RowForDrawerLayout;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.MainDesktopActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class ListAdapterForDrawerLayouts implements ListAdapter, View.OnClickListener {

    Activity activity;
    ArrayList<RowForDrawerLayout> rows;
    private OnDrawerListAdapterClickListener mListener;


    RelativeLayout firstRow;
    LinearLayout secondRow,thirdRow,fourthRow;
    final String FIRST_ROW="first row",SECOND_ROW="second row",
            THIRD_ROW="third row",FOURTH_ROW="fourth row";

    View v;

    final String No_picture="user didn't put picture";
    String picturePath;

    public ListAdapterForDrawerLayouts(Activity activity, ArrayList<RowForDrawerLayout> rows)
    {
        this.activity=activity;
        this.rows=rows;
        //mListener = listener;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return rows.size()-3;
    }

    @Override
    public Object getItem(int i) {
        return rows.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mListener=(OnDrawerListAdapterClickListener)activity;
        SharedPreferences prefs = activity.getSharedPreferences("save",MODE_PRIVATE);
        TextView textView;

        if(view==null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            if(i==0)
                v = inflater.inflate(R.layout.first_row_layout, viewGroup,false);
            else
                v = inflater.inflate(R.layout.drawer_layout_row,viewGroup,false);
        }
        else
            v=view;

        if(i==0)
        {
            textView=(TextView)v.findViewById(R.id.textViewFirstRow);
            ImageView imageView=(ImageView)v.findViewById(R.id.imageViewFirstRow);
            TextView textView2=(TextView)v.findViewById(R.id.textViewSecondRow);
            ImageView imageView2=(ImageView)v.findViewById(R.id.imageViewSecondRow);
            TextView textView3=(TextView)v.findViewById(R.id.textViewThirdRow);
            ImageView imageView3=(ImageView)v.findViewById(R.id.imageViewThirdRow);
            TextView textView4=(TextView)v.findViewById(R.id.textViewFourthRow);
            ImageView imageView4=(ImageView)v.findViewById(R.id.imageViewFourthRow);

            textView.setText(rows.get(i).getNameOfRow());

            ///////////////////////////
            //handle the user picture
            picturePath=prefs.getString("path",No_picture);
            if(picturePath.equals(No_picture))
            {
                Log.i("shilo","picture path is null");
                imageView.setImageResource(rows.get(i).getPictureId());
            }
            else {
                Log.i("shilo","picture path NOT null");
                ((MainDesktopActivity)activity).loadImageFromStorage(picturePath,imageView);
                //imageView.setImageResource(((MainDesktopActivity)activity).loadImageFromStorage(picturePath));
            }

            ///////////////////////////

            textView2.setText(rows.get(i+1).getNameOfRow());
            imageView2.setImageResource(rows.get(i+1).getPictureId());
            textView3.setText(rows.get(i+2).getNameOfRow());
            imageView3.setImageResource(rows.get(i+2).getPictureId());
            textView4.setText(rows.get(i+3).getNameOfRow());
            imageView4.setImageResource(rows.get(i+3).getPictureId());

            //v.setClickable(false);

            RelativeLayout firstRow=(RelativeLayout) v.findViewById(R.id.firstRowContainer);
            LinearLayout secondRow=(LinearLayout) v.findViewById(R.id.secondRowContainer);
            LinearLayout thirdRow=(LinearLayout) v.findViewById(R.id.thirdRowContainer);
            LinearLayout fourthRow=(LinearLayout) v.findViewById(R.id.fourthRowContainer);
            firstRow.setOnClickListener(this);
            secondRow.setOnClickListener(this);
            thirdRow.setOnClickListener(this);
            fourthRow.setOnClickListener(this);
            //objects=new Object[]{firstRow,secondRow,thirdRow,fourthRow};
        }
        else
        {
            textView=(TextView)v.findViewById(R.id.textViewDrawerLayout);
            ImageView imageView=(ImageView)v.findViewById(R.id.imageViewDrawerLayout);
            LinearLayout container =(LinearLayout) textView.getParent();

            textView.setText(rows.get(i+3).getNameOfRow());
            imageView.setImageResource(rows.get(i+3).getPictureId());
        }



        Log.i("shilo", "the row in listView is: "+i);

        //RowForDrawerLayout currentRow= rows.get(i);

        return v;
    }

    @Override
    public void onClick(View view) {
        //View view=(View)((ListAdapterForDrawerLayouts)object).objects[1];
        String rowNumber;
        switch (view.getId())
        {
            case R.id.firstRowContainer:
                rowNumber=FIRST_ROW;
                mListener.onClick(rowNumber);
                break;
            case R.id.secondRowContainer:
                rowNumber=SECOND_ROW;
                mListener.onClick(rowNumber);
                break;
            case R.id.thirdRowContainer:
                rowNumber=THIRD_ROW;
                mListener.onClick(rowNumber);
                break;
            case R.id.fourthRowContainer:
                rowNumber=FOURTH_ROW;
                mListener.onClick(rowNumber);
                break;
        }
    }
    /**
     * Interface for receiving click events from cells.
     */
    public interface OnDrawerListAdapterClickListener {
        void onClick(String rowNumber);
    }

    @Override
    public int getItemViewType(int i) {
        return (i<3)?0:1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public boolean isEmpty() {
        return rows.size()==0;
    }




}
