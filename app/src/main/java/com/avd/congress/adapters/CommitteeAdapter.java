package com.avd.congress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.avd.congress.R;
import com.avd.congress.models.Committee;

import java.util.List;

/**
 * Created by avdmy on 11/16/2016.
 */

public class CommitteeAdapter extends ArrayAdapter<Committee> {
    List<Committee> committees;

    public CommitteeAdapter(Context context, int resource, List<Committee> objects) {
        super(context, resource, objects);
        committees = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.committee_row, parent, false);
            }
            Committee comm = committees.get(position);
            TextView tv;
            tv = (TextView) convertView.findViewById(R.id.committee_id);
            tv.setText(comm.parsedJSON.getString("committee_id"));
            tv = (TextView) convertView.findViewById(R.id.committee_name);
            tv.setText(comm.parsedJSON.getString("name"));
            tv = (TextView) convertView.findViewById(R.id.committee_chamber);
            tv.setText(comm.parsedJSON.getString("chamber"));
            return convertView;
        } catch(Exception e){
            return null;
        }
    }
}
