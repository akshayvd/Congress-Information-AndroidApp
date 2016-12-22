package com.avd.congress;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.avd.congress.adapters.CommitteeAdapter;
import com.avd.congress.models.Committee;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class CommitteeFragment extends Fragment {

    List<Committee> committeeSenateList;
    CommitteeAdapter commSenateAdapter;
    List<Committee> committeeHouseList;
    CommitteeAdapter commHouseAdapter;
    List<Committee> committeeJointList;
    CommitteeAdapter commJointAdapter;

    public class CommitteeQueryAPI extends AsyncTask<String, Void, JSONArray> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            committeeSenateList = new ArrayList<>();
            committeeHouseList = new ArrayList<>();
            committeeJointList = new ArrayList<>();
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
                        Committee temp = new Committee();
                        temp.parsedJSON = arr.getJSONObject(i);
                        switch (temp.parsedJSON.getString("chamber")){
                            case "house":
                                committeeHouseList.add(temp);
                                break;
                            case "senate":
                                committeeSenateList.add(temp);
                                break;
                            case "joint":
                                committeeJointList.add(temp);
                                break;
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
                    Collections.sort(committeeHouseList, comparator);
                    Collections.sort(committeeJointList, comparator);
                    Collections.sort(committeeSenateList, comparator);
                    commSenateAdapter.notifyDataSetChanged();
                    commHouseAdapter.notifyDataSetChanged();
                    commJointAdapter.notifyDataSetChanged();
                } catch(Exception e){

                }
            }
        }
    }

    public CommitteeFragment() {
        // Required empty public constructor
        (new CommitteeQueryAPI()).execute("http://lowcost-env.ifrmtfmgxv.us-west-2.elasticbeanstalk.com/?database=committees&per_page=all");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(commSenateAdapter ==null){
            commSenateAdapter = new CommitteeAdapter(getContext(), R.layout.committee_row, committeeSenateList);
        }
        if(commHouseAdapter ==null){
            commHouseAdapter = new CommitteeAdapter(getContext(), R.layout.committee_row, committeeHouseList);
        }
        if(commJointAdapter ==null){
            commJointAdapter = new CommitteeAdapter(getContext(), R.layout.committee_row, committeeJointList);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_committee, container, false);
        TabHost host  = (TabHost)v.findViewById(R.id.committee_tabhost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("House");
        spec.setContent(R.id.comm_house_tab);
        spec.setIndicator("House");
        host.addTab(spec);

        spec = host.newTabSpec("Senate");
        spec.setContent(R.id.comm_senate_tab);
        spec.setIndicator("Senate");
        host.addTab(spec);

        spec = host.newTabSpec("Joint");
        spec.setContent(R.id.comm_joint_tab);
        spec.setIndicator("Joint");
        host.addTab(spec);

        ListView lv = (ListView) v.findViewById(R.id.comm_house_lv);
        lv.setAdapter(commHouseAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Committee c = commHouseAdapter.getItem(position);
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

        lv = (ListView) v.findViewById(R.id.comm_senate_lv);
        lv.setAdapter(commSenateAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Committee c = commSenateAdapter.getItem(position);
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

        lv = (ListView) v.findViewById(R.id.comm_joint_lv);
        //lv.bringToFront();
        lv.setAdapter(commJointAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Committee c = commJointAdapter.getItem(position);
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
        return v;
    }

}
