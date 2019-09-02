package com.hyperactive.shilo.jobimshilo.search_jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.telephony.CarrierConfigManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyperactive.shilo.jobimshilo.R;
import com.hyperactive.shilo.jobimshilo.main_desktop_jobs.MainDesktopActivity;

import java.util.ArrayList;



public class ListAdapterForjobsTab implements ListAdapter, View.OnClickListener {

    Context context;
    ArrayList<Careers> rows;
    CheckBox checkBox;

    public ListAdapterForjobsTab(Context context, ArrayList<Careers> rows)
    {
        this.context=context;
        this.rows=rows;
    }



    @Override
    public boolean areAllItemsEnabled() {return true;}

    @Override
    public boolean isEnabled(int i) {return true;}

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return rows.size();
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
        View v;



        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_for_jobs_tab, null);
        }
        else
            v=view;

        //Careers currentRow= rows.get(i);
        TextView textView=(TextView)v.findViewById(R.id.careerTextView);
        ImageView imageView=(ImageView)v.findViewById(R.id.careerImageView);
        checkBox = (CheckBox) v.findViewById(R.id.careerCheckBox);
        checkBox.setChecked((((Careers) getItem(i)).isChecked()));
        Log.i("shilo","the checkox should changed. index "+i);
        Log.i("shilo","the proffesion of item is "+((Careers) getItem(i)).getProfession());
        //checkBox.setTag(i);


        RelativeLayout container =(RelativeLayout) textView.getParent();
        container.setTag(i);
        container.setOnClickListener(this);
        checkBox.setTag(i);
        checkBox.setOnClickListener(this);

        Log.i("shilo","the checkbox in Career item is: "+ ((Careers)getItem(i)).isChecked());
        Log.i("shilo","the number of item is: "+ i);




        if(!(rows.get(i).getProfession().equals("")))
                textView.setText(rows.get(i).getProfession());
        //if (!(rows.get(i).getCompany().equals("")))
            //textView.setText(rows.get(i).getCompany());

        //if(rows.get(i).getIdIcon()!=-1)
            imageView.setImageResource(rows.get(i).getIdIcon());



        return v;
    }



    @Override
    public int getItemViewType(int i) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return rows.size()==0;
    }

    @Override
    public void onClick(View v) {
        //checkBox=(CheckBox)v.getTag();
        //int position=(int)checkBox.getTag();
        int position=(int)v.getTag();
        if (v instanceof RelativeLayout)
            checkBox=(CheckBox) ((RelativeLayout)v).getChildAt(0);
        else checkBox=(CheckBox)v;

        if((((Careers) getItem(position)).isChecked())) {
            checkBox.setChecked(false);
            ((Careers) getItem(position)).setChecked(false);
        }
        else {
            checkBox.setChecked(true);
            ((Careers) getItem(position)).setChecked(true);
            Log.i("shilo","the career item checked and should be true");
            Log.i("shilo","the checkbox in Career item is: "+ ((Careers)getItem(position)).isChecked());
            Log.i("shilo","the index of item is: "+position);
        }

    }
}
