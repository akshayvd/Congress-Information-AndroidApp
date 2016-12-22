package com.avd.congress.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avd.congress.R;
import com.avd.congress.models.Legislator;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.List;

/**
 * Created by avdmy on 11/16/2016.
 */

public class LegislatorAdapter extends ArrayAdapter<Legislator> {
    List<Legislator> legislators;

    public LegislatorAdapter(Context context, int resource, List<Legislator> objects) {
        super(context, resource, objects);
        legislators = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.legislator_row, parent, false);
            }
            Legislator legi = legislators.get(position);
            TextView tv = (TextView) convertView.findViewById(R.id.legis_name);
            tv.setText(legi.parsedJSON.getString("last_name")+", " +legi.parsedJSON.getString("first_name"));
            tv = (TextView) convertView.findViewById(R.id.legis_details);
            tv.setText( "(" + legi.parsedJSON.getString("party") + ") " +
                    legi.parsedJSON.getString("state_name") + " - District " +
                    ((!legi.parsedJSON.has("district"))||legi.parsedJSON.getString("district").equals("null")?"0":legi.parsedJSON.getString("district")));
            String urlStr = "http://theunitedstates.io/images/congress/225x275/"+
                    legi.parsedJSON.getString("bioguide_id")+".jpg";
            Picasso.with(getContext()).load(urlStr).into((ImageView)convertView.findViewById(R.id.legis_image));
            return convertView;
        } catch(Exception e){
            return null;
        }
    }
}
