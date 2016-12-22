package com.avd.congress;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.avd.congress.Helper.DatabaseHelper;
import com.avd.congress.adapters.BillAdapter;
import com.avd.congress.adapters.CommitteeAdapter;
import com.avd.congress.adapters.IndexAdaper;
import com.avd.congress.adapters.LegislatorAdapter;
import com.avd.congress.models.Bill;
import com.avd.congress.models.Committee;
import com.avd.congress.models.IndexElement;
import com.avd.congress.models.Legislator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    List<Legislator> favLegislatorsList;
    List<IndexElement> legislatorIndex;
    List<Bill> favBillsList;
    List<Committee> favCommitteesList;
    LegislatorAdapter legislatorAdapter;
    IndexAdaper indexAdaper;
    BillAdapter billAdapter;
    CommitteeAdapter committeeAdapter;
    Boolean isFirstLoad = true;

    public FavoriteFragment() {
        // Required empty public constructor
        favLegislatorsList = new ArrayList<>();
        favBillsList = new ArrayList<>();
        favCommitteesList = new ArrayList<>();
        legislatorIndex = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isFirstLoad){
            isFirstLoad = false;
        }
        else{
            getSavedFavorites();
            notifyAllAdapters();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_favorite, container, false);
        TabHost host  = (TabHost)v.findViewById(R.id.fav_tabhost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Legislators");
        spec.setContent(R.id.fav_legislator_tab);
        spec.setIndicator("Legislators");
        host.addTab(spec);

        spec = host.newTabSpec("Bills");
        spec.setContent(R.id.fav_bill_tab);
        spec.setIndicator("Bills");
        host.addTab(spec);

        spec = host.newTabSpec("Committees");
        spec.setContent(R.id.fav_committee_tab);
        spec.setIndicator("Committees");
        host.addTab(spec);

        getSavedFavorites();

        legislatorAdapter = new LegislatorAdapter(getContext(), R.layout.legislator_row, favLegislatorsList);
        committeeAdapter = new CommitteeAdapter(getContext(), R.layout.committee_row, favCommitteesList);
        billAdapter = new BillAdapter(getContext(), R.layout.bill_row, favBillsList);
        indexAdaper = new IndexAdaper(getContext(), R.layout.index_row, legislatorIndex);

        ListView lv = (ListView) v.findViewById(R.id.fav_legislator_lv);
        lv.setAdapter(legislatorAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Legislator l = legislatorAdapter.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), dummy + " clicked", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LegislatorViewDetails.class);
                    intent.putExtra("legislatorJSON", l.parsedJSON.toString());
                    startActivityForResult(intent, 0);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        lv = (ListView) v.findViewById(R.id.fav_legislator_index_lv);
        lv.setAdapter(indexAdaper);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IndexElement l = indexAdaper.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), "go to " + l.index + " , clicked " + l.vlaue, Toast.LENGTH_LONG).show();
                    ((ListView)getView().findViewById(R.id.fav_legislator_lv)).setSelection(l.index);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        lv = (ListView) v.findViewById(R.id.fav_bill_lv);
        lv.setAdapter(billAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bill b = billAdapter.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), dummy + " clicked", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), BillViewDetails.class);
                    intent.putExtra("billJSON", b.parsedJSON.toString());
                    startActivityForResult(intent, 0);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        lv = (ListView) v.findViewById(R.id.fav_committee_lv);
        lv.setAdapter(committeeAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Committee c = committeeAdapter.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), dummy + " clicked", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), CommitteeViewDetails.class);
                    intent.putExtra("committeeJSON", c.parsedJSON.toString());
                    startActivityForResult(intent, 0);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        notifyAllAdapters();
        return v;
    }

    private void notifyAllAdapters() {
        legislatorAdapter.notifyDataSetChanged();
        billAdapter.notifyDataSetChanged();
        committeeAdapter.notifyDataSetChanged();
        indexAdaper.notifyDataSetChanged();
    }

    private void getSavedFavorites() {
        DatabaseHelper.init(getContext());
        getListData("legislator");
        getListData("bill");
        getListData("committee");
    }

    private void getListData(String type) {
        try {
            switch (type) {
                case "legislator":
                    favLegislatorsList.clear();
                    Cursor c = DatabaseHelper.select("SELECT * FROM LEGISLATOR");
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        while (!c.isAfterLast()) {
                            Legislator temp = new Legislator();
                            temp.parsedJSON = new JSONObject(new String(Base64.decode(c.getString(1), Base64.DEFAULT)));
                            favLegislatorsList.add(temp);
                            c.moveToNext();
                        }
                    }
                    Collections.sort(favLegislatorsList, new Comparator<Legislator>() {
                        @Override
                        public int compare(Legislator o1, Legislator o2) {
                            try {
                                return o1.parsedJSON.getString("last_name").compareToIgnoreCase(o2.parsedJSON.getString("last_name"));
                            }
                            catch (Exception e){
                                return -1;
                            }
                        }
                    });
                    char tchar = ' ';
                    legislatorIndex.clear();
                    for(int i = 0;i<favLegislatorsList.size();i++){
                        Legislator l = favLegislatorsList.get(i);
                        IndexElement e = new IndexElement();
                        if(i==0) {
                            tchar = l.parsedJSON.getString("last_name").charAt(0);
                            e.vlaue = tchar;
                            e.index = i;
                            legislatorIndex.add(e);
                        }
                        else{
                            if(tchar!=l.parsedJSON.getString("last_name").charAt(0)){
                                tchar = l.parsedJSON.getString("last_name").charAt(0);
                                e.vlaue = tchar;
                                e.index = i;
                                legislatorIndex.add(e);
                            }
                        }
                    }
                    break;
                case "bill":
                    favBillsList.clear();
                    Cursor cb = DatabaseHelper.select("SELECT * FROM BILL");
                    if (cb.getCount() > 0) {
                        cb.moveToFirst();
                        while (!cb.isAfterLast()) {
                            Bill temp = new Bill();
                            temp.parsedJSON = new JSONObject(new String(Base64.decode(cb.getString(1), Base64.DEFAULT)));
                            favBillsList.add(temp);
                            cb.moveToNext();
                        }
                    }
                    Collections.sort(favBillsList, new Comparator<Bill>() {
                        @Override
                        public int compare(Bill o1, Bill o2) {
                            Date o1Date, o2Date;
                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-mm-dd");
                            try {
                                o1Date = format.parse(o1.parsedJSON.getString("introduced_on"));
                                o2Date = format.parse(o2.parsedJSON.getString("introduced_on"));
                                return o1Date.compareTo(o2Date);
                            }catch (Exception e){
                                Log.e("", e.getMessage());
                            }
                            return 0;
                        }
                    });
                    Collections.reverse(favBillsList);
                    break;
                case "committee":
                    favCommitteesList.clear();
                    Cursor cc = DatabaseHelper.select("SELECT * FROM COMMITTEE");
                    if (cc.getCount() > 0) {
                        cc.moveToFirst();
                        while (!cc.isAfterLast()) {
                            Committee temp = new Committee();
                            temp.parsedJSON = new JSONObject(new String(Base64.decode(cc.getString(1), Base64.DEFAULT)));
                            favCommitteesList.add(temp);
                            cc.moveToNext();
                        }
                    }
                    Comparator<Committee> comparator = new Comparator<Committee>() {
                        @Override
                        public int compare(Committee o1, Committee o2) {
                            try{
                                return o1.parsedJSON.getString("name").compareTo(o2.parsedJSON.getString("name"));
                            } catch (Exception e){

                            }
                            return 0;
                        }
                    };
                    Collections.sort(favCommitteesList, comparator);
                    break;
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

}
