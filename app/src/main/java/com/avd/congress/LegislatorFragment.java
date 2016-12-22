package com.avd.congress;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.avd.congress.adapters.IndexAdaper;
import com.avd.congress.adapters.LegislatorAdapter;
import com.avd.congress.models.IndexElement;
import com.avd.congress.models.Legislator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LegislatorFragment extends Fragment {
    List<Legislator> legislatorList;
    List<Legislator> houseLegislatorList;
    List<Legislator> senateLegislatorList;
    List<IndexElement> byStateIndexList;
    List<IndexElement> houseIndexList;
    List<IndexElement> senateIndexList;
    LegislatorAdapter legiAdapter;
    LegislatorAdapter houseLegiAdapter;
    LegislatorAdapter senateLegiAdapter;
    IndexAdaper senateIndexAdaper;
    IndexAdaper houseIndexAdaper;
    IndexAdaper byStateIndexAdaper;

    public class LegislatorQueryAPI extends AsyncTask<String, Void, JSONArray> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            legislatorList = new ArrayList<>();
            senateLegislatorList = new ArrayList<>();
            houseLegislatorList = new ArrayList<>();
            byStateIndexList = new ArrayList<>();
            houseIndexList = new ArrayList<>();
            senateIndexList = new ArrayList<>();
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            try {
                for(int i=0;i<urls.length;i++) {
                    URL url = new URL(urls[i]);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        JSONObject parentObject = new JSONObject(stringBuilder.toString());
                        JSONArray pArray = parentObject.getJSONArray("results");
                        return pArray;
                    } finally {
                        urlConn.disconnect();
                    }
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray arr) {
            if (arr != null) {
                try {
                    for (int i = 0; i < arr.length(); i++) {
                        Legislator temp = new Legislator();
                        temp.parsedJSON = arr.getJSONObject(i);
                        legislatorList.add(temp);
                        if(temp.parsedJSON.getString("chamber").equals("senate")){
                            senateLegislatorList.add(temp);
                        }
                        else{
                            houseLegislatorList.add(temp);
                        }
                    }
                    Collections.sort(legislatorList, new Comparator<Legislator>() {
                        @Override
                        public int compare(Legislator o1, Legislator o2) {
                            try {
                                int c;
                                c = o1.parsedJSON.getString("state_name").compareToIgnoreCase(o2.parsedJSON.getString("state_name"));
                                if (c == 0) {
                                    return o1.parsedJSON.getString("last_name").compareToIgnoreCase(o2.parsedJSON.getString("last_name"));
                                }
                                return c;
                            }
                            catch (Exception e){
                                return -1;
                            }
                        }
                    });
                    Collections.sort(senateLegislatorList, new Comparator<Legislator>() {
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
                    Collections.sort(houseLegislatorList, new Comparator<Legislator>() {
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
                    for(int i = 0;i<legislatorList.size();i++){
                        Legislator l = legislatorList.get(i);
                        IndexElement e = new IndexElement();
                        if(i==0) {
                            tchar = l.parsedJSON.getString("state_name").charAt(0);
                            e.vlaue = tchar;
                            e.index = i;
                            byStateIndexList.add(e);
                        }
                        else{
                            if(tchar!=l.parsedJSON.getString("state_name").charAt(0)){
                                tchar = l.parsedJSON.getString("state_name").charAt(0);
                                e.vlaue = tchar;
                                e.index = i;
                                byStateIndexList.add(e);
                            }
                        }
                    }
                    for(int i = 0;i<houseLegislatorList.size();i++){
                        Legislator l = houseLegislatorList.get(i);
                        IndexElement e = new IndexElement();
                        if(i==0) {
                            tchar = l.parsedJSON.getString("last_name").charAt(0);
                            e.vlaue = tchar;
                            e.index = i;
                            houseIndexList.add(e);
                        }
                        else{
                            if(tchar!=l.parsedJSON.getString("last_name").charAt(0)){
                                tchar = l.parsedJSON.getString("last_name").charAt(0);
                                e.vlaue = tchar;
                                e.index = i;
                                houseIndexList.add(e);
                            }
                        }
                    }
                    for(int i = 0;i<senateLegislatorList.size();i++){
                        Legislator l = senateLegislatorList.get(i);
                        IndexElement e = new IndexElement();
                        if(i==0) {
                            tchar = l.parsedJSON.getString("last_name").charAt(0);
                            e.vlaue = tchar;
                            e.index = i;
                            senateIndexList.add(e);
                        }
                        else{
                            if(tchar!=l.parsedJSON.getString("last_name").charAt(0)){
                                tchar = l.parsedJSON.getString("last_name").charAt(0);
                                e.vlaue = tchar;
                                e.index = i;
                                senateIndexList.add(e);
                            }
                        }
                    }
                    legiAdapter.notifyDataSetChanged();
                    houseLegiAdapter.notifyDataSetChanged();
                    senateLegiAdapter.notifyDataSetChanged();
                    byStateIndexAdaper.notifyDataSetChanged();
                    houseIndexAdaper.notifyDataSetChanged();
                    senateIndexAdaper.notifyDataSetChanged();
                } catch(Exception e){

                }
            }
        }
    }

    public LegislatorFragment() {
        // Required empty public constructor
        (new LegislatorQueryAPI()).execute("http://lowcost-env.ifrmtfmgxv.us-west-2.elasticbeanstalk.com/?database=legislators&per_page=all");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(legiAdapter == null) {
            legiAdapter = new LegislatorAdapter(getContext(), R.layout.legislator_row, legislatorList);
        }
        if(senateLegiAdapter == null) {
            senateLegiAdapter = new LegislatorAdapter(getContext(), R.layout.legislator_row, senateLegislatorList);
        }
        if(houseLegiAdapter == null) {
            houseLegiAdapter = new LegislatorAdapter(getContext(), R.layout.legislator_row, houseLegislatorList);
        }
        if(byStateIndexAdaper == null) {
            byStateIndexAdaper = new IndexAdaper(getContext(), R.layout.index_row, byStateIndexList);
        }
        if(houseIndexAdaper == null) {
            houseIndexAdaper = new IndexAdaper(getContext(), R.layout.index_row, houseIndexList);
        }
        if(senateIndexAdaper == null) {
            senateIndexAdaper = new IndexAdaper(getContext(), R.layout.index_row, senateIndexList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_legislator, container, false);
        TabHost host  = (TabHost)v.findViewById(R.id.legislator_tabhost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("By State");
        spec.setContent(R.id.legislator_by_state_tab);
        spec.setIndicator("By State");
        host.addTab(spec);

        spec = host.newTabSpec("House");
        spec.setContent(R.id.legislator_house_tab);
        spec.setIndicator("House");
        host.addTab(spec);

        spec = host.newTabSpec("Senate");
        spec.setContent(R.id.legislator_senate_tab);
        spec.setIndicator("Senate");
        host.addTab(spec);

        ListView lv = (ListView) v.findViewById(R.id.legislator_bystate_lv);
        lv.setAdapter(legiAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Legislator l = legiAdapter.getItem(position);
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

        lv = (ListView) v.findViewById(R.id.legislator_house_lv);
        lv.setAdapter(houseLegiAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Legislator l = houseLegiAdapter.getItem(position);
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

        lv = (ListView) v.findViewById(R.id.legislator_senate_lv);
        //lv.bringToFront();
        lv.setAdapter(senateLegiAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Legislator l = senateLegiAdapter.getItem(position);
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
        lv = (ListView) v.findViewById(R.id.le_by_state_index_lv);
        //lv.bringToFront();
        lv.setAdapter(byStateIndexAdaper);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IndexElement l = byStateIndexAdaper.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), "go to " + l.index + " , clicked " + l.vlaue, Toast.LENGTH_LONG).show();
                    ((ListView)getView().findViewById(R.id.legislator_bystate_lv)).setSelection(l.index);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        lv = (ListView) v.findViewById(R.id.le_house_index_lv);
        //lv.bringToFront();
        lv.setAdapter(houseIndexAdaper);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IndexElement l = houseIndexAdaper.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), "go to " + l.index + " , clicked " + l.vlaue, Toast.LENGTH_LONG).show();
                    ((ListView)getView().findViewById(R.id.legislator_house_lv)).setSelection(l.index);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        lv = (ListView) v.findViewById(R.id.le_senate_index_lv);
        //lv.bringToFront();
        lv.setAdapter(senateIndexAdaper);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IndexElement l = senateIndexAdaper.getItem(position);
                try{
                    //String dummy = l.parsedJSON.getString("last_name");
                    //Toast.makeText(getContext(), "go to " + l.index + " , clicked " + l.vlaue, Toast.LENGTH_LONG).show();
                    ((ListView)getView().findViewById(R.id.legislator_senate_lv)).setSelection(l.index);
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }
}
