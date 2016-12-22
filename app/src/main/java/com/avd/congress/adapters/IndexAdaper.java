package com.avd.congress.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.avd.congress.R;
import com.avd.congress.models.IndexElement;

import java.util.List;

/**
 * Created by avdmy on 11/23/2016.
 */

public class IndexAdaper extends ArrayAdapter<IndexElement> {
    List<IndexElement> indexElementList;

    public IndexAdaper(Context context, int resource, List<IndexElement> objects) {
        super(context, resource, objects);
        indexElementList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.index_row, parent, false);
            }
            IndexElement element = indexElementList.get(position);
            ((TextView) convertView.findViewById(R.id.index_text)).setText(String.valueOf(element.vlaue));
            return convertView;
        } catch(Exception e){
            return null;
        }
    }
}
