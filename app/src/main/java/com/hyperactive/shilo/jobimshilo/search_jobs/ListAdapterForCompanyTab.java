package com.hyperactive.shilo.jobimshilo.search_jobs;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.hyperactive.shilo.jobimshilo.R;

import java.util.ArrayList;

/**
 * Created by user on 19/12/2016.
 */

public class ListAdapterForCompanyTab implements ListAdapter, View.OnClickListener {
    Context context;
    ArrayList<Careers> rows;
    CheckBox checkBox;

    public ListAdapterForCompanyTab(Context context, ArrayList<Careers> rows) {
        this.context = context;
        this.rows = rows;
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
        if(view==null)
        {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.layout_for_company_tab,null);
        }
        else
        v= view;

        //String currentRow= rows.get(i).getCompany();
        TextView textView=(TextView)v.findViewById(R.id.companyTabTextView);
        checkBox=(CheckBox)v.findViewById(R.id.companyTabCheckBox);
        checkBox.setChecked((((Careers) getItem(i)).isChecked()));

        RelativeLayout container =(RelativeLayout) textView.getParent();
        container.setTag(i);
        container.setOnClickListener(this);
        checkBox.setTag(i);
        checkBox.setOnClickListener(this);

        textView.setText(rows.get(i).getCompany());


        /*container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked())
                    checkBox.setChecked(false);
                else
                    checkBox.setChecked(true);
            }
        });*/



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
        return rows.isEmpty();
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
