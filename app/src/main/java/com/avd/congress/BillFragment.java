package com.avd.congress;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.avd.congress.adapters.BillAdapter;
import com.avd.congress.models.Bill;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BillFragment extends Fragment {

    List<Bill> activeBillList;
    List<Bill> newBillList;
    BillAdapter activeBillAdapter;
    BillAdapter newBillAdapter;

    public class ActiveBillQueryAPI extends AsyncTask<String, Void, JSONArray> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            activeBillList = new ArrayList<>();
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
                        Bill temp = new Bill();
                        temp.parsedJSON = arr.getJSONObject(i);
                        activeBillList.add(temp);
                    }
                    Collections.sort(activeBillList, new Comparator<Bill>() {
                        @Override
                        public int compare(Bill o1, Bill o2) {
                            Date o1Date, o2Date;
                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
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
                    Collections.reverse(activeBillList);
                    activeBillAdapter.notifyDataSetChanged();
                } catch(Exception e){

                }
            }
        }
    }
    public class NewBillQueryAPI extends AsyncTask<String, Void, JSONArray> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            newBillList = new ArrayList<>();
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
                        Bill temp = new Bill();
                        temp.parsedJSON = arr.getJSONObject(i);
                        newBillList.add(temp);
                    }
                    Collections.sort(newBillList, new Comparator<Bill>() {
                        @Override
                        public int compare(Bill o1, Bill o2) {
                            Date o1Date, o2Date;
                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
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
                    Collections.reverse(newBillList);
                    newBillAdapter.notifyDataSetChanged();
                } catch(Exception e){

                }
            }
        }
    }

    public BillFragment() {
        // Required empty public constructor
        (new ActiveBillQueryAPI()).execute("http://lowcost-env.ifrmtfmgxv.us-west-2.elasticbeanstalk.com/?database=bills&per_page=50&bill_active=true&bill_haspdf=true");
        (new NewBillQueryAPI()).execute("http://lowcost-env.ifrmtfmgxv.us-west-2.elasticbeanstalk.com/?database=bills&per_page=50&bill_active=false&bill_haspdf=true");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(activeBillAdapter==null){
            activeBillAdapter = new BillAdapter(getContext(), R.layout.bill_row, activeBillList);
        }
        if(newBillAdapter==null){
            newBillAdapter = new BillAdapter(getContext(), R.layout.bill_row, newBillList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bill, container, false);
        TabHost host  = (TabHost)v.findViewById(R.id.bill_tabhost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Active Bills");
        spec.setContent(R.id.bill_active_tab);
        spec.setIndicator("Active Bills");
        host.addTab(spec);

        spec = host.newTabSpec("New Bills");
        spec.setContent(R.id.bill_new_tab);
        spec.setIndicator("New Bills");
        host.addTab(spec);

        ListView lv = (ListView) v.findViewById(R.id.bill_active_lv);
        lv.setAdapter(activeBillAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bill b = activeBillAdapter.getItem(position);
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

        lv = (ListView) v.findViewById(R.id.bill_new_lv);
        lv.setAdapter(newBillAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bill b = newBillAdapter.getItem(position);
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

        return v;
    }

}
