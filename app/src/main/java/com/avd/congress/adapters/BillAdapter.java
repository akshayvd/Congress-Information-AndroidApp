package com.avd.congress.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avd.congress.R;
import com.avd.congress.models.Bill;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by avdmy on 11/16/2016.
 */

public class BillAdapter extends ArrayAdapter<Bill> {
    List<Bill> bills;

    public BillAdapter(Context context, int resource, List<Bill> objects) {
        super(context, resource, objects);
        bills = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bill_row, parent, false);
            }
            Bill bill = bills.get(position);
            ((TextView) convertView.findViewById(R.id.bill_id)).setText(bill.parsedJSON.getString("bill_id"));
            String titleText = "N.A";
            if(bill.parsedJSON.has("short_title")&&bill.parsedJSON.getString("short_title").equals(""))
                titleText = bill.parsedJSON.getString("shortTitle");
            else
                titleText = bill.parsedJSON.getString("official_title");
            ((TextView) convertView.findViewById(R.id.bill_title)).setText(titleText);
            Date introduced_on;
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            introduced_on = format.parse(bill.parsedJSON.getString("introduced_on"));
            java.text.SimpleDateFormat reqformat = new java.text.SimpleDateFormat("MMM dd,yyyy");
            ((TextView)convertView.findViewById(R.id.bill_date)).setText(reqformat.format(introduced_on));
            return convertView;
        } catch(Exception e){
            return null;
        }
    }
}
