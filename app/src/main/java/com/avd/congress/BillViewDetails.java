package com.avd.congress;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avd.congress.Helper.DatabaseHelper;
import com.avd.congress.models.Bill;
import org.json.JSONObject;

import java.util.Date;

public class BillViewDetails extends AppCompatActivity {

    private Bill bill;
    private String billJSON;
    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view_details);
        getSupportActionBar().setTitle("Bill Info");
        billJSON = getIntent().getExtras().getString("billJSON");
        bill = new Bill();
        try{
            bill.parsedJSON = new JSONObject(billJSON);
            ((TextView)findViewById(R.id.txt_bi_bill_id)).setText(bill.parsedJSON.getString("bill_id"));
            String titleText = "N.A";
            if(bill.parsedJSON.has("short_title")&&bill.parsedJSON.getString("short_title").equals(""))
                titleText = bill.parsedJSON.getString("shortTitle");
            else
                titleText = bill.parsedJSON.getString("official_title");
            ((TextView)findViewById(R.id.txt_bi_title)).setText(titleText);
            ((TextView)findViewById(R.id.txt_bi_bill_type)).setText(bill.parsedJSON.getString("bill_type"));
            ((TextView)findViewById(R.id.txt_bi_sponsor)).setText(((JSONObject)bill.parsedJSON.get("sponsor")).getString("title")+". " +
                    ((JSONObject)bill.parsedJSON.get("sponsor")).getString("last_name") + ", "+
                    ((JSONObject)bill.parsedJSON.get("sponsor")).getString("first_name")
            );
            ((TextView)findViewById(R.id.txt_bi_chamber)).setText(bill.parsedJSON.getString("chamber"));
            ((TextView)findViewById(R.id.txt_bi_status)).setText((bill.parsedJSON.getJSONObject("history")).getBoolean("active")?"Active":"New");
            ((TextView)findViewById(R.id.txt_bi_cong_url)).setText(bill.parsedJSON.getJSONObject("urls").getString("congress"));
            ((TextView)findViewById(R.id.txt_bi_ver_status)).setText(bill.parsedJSON.getJSONObject("last_version").getString("version_name"));
            ((TextView)findViewById(R.id.txt_bi_bill_url)).setText(bill.parsedJSON.getJSONObject("last_version").getJSONObject("urls").getString("pdf"));
            ImageView favStar = ((ImageView)findViewById(R.id.im_bi_star));
            isFavorite = checkFavorite();
            ((ImageView) favStar).setImageResource(isFavorite ? R.drawable.yellowstar : R.drawable.star);
            favStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DatabaseHelper.init(v.getContext());
                        if (isFavorite) {
                            DatabaseHelper.execSQL("DELETE FROM BILL WHERE bill_id = \"" + bill.parsedJSON.getString("bill_id") + "\"");
                        } else {
                            DatabaseHelper.execSQL("INSERT INTO BILL VALUES (\"" + bill.parsedJSON.getString("bill_id") + "\",\"" + (Base64.encodeToString(billJSON.getBytes(), Base64.DEFAULT)) + "\")");
                        }
                        isFavorite = !isFavorite;
                        ((ImageView) v).setImageResource(isFavorite ? R.drawable.yellowstar : R.drawable.star);
                    }catch(Exception e){
                        e.getMessage();
                    }
                }
            });

            Date introduced_on;
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            introduced_on = format.parse(bill.parsedJSON.getString("introduced_on"));
            java.text.SimpleDateFormat reqformat = new java.text.SimpleDateFormat("MMM dd,yyyy");
            ((TextView)findViewById(R.id.txt_bi_intro)).setText(reqformat.format(introduced_on));

        } catch(Exception e){

        }
    }

    private boolean checkFavorite() {
        DatabaseHelper.init(this);
        try{
            Cursor c = DatabaseHelper.select("SELECT bill_id FROM BILL WHERE bill_id = \"" + bill.parsedJSON.getString("bill_id") + "\"");
            if(c==null||c.getCount()==0) return false;
            else if(c.getCount() == 1) return true;
            return false;
        }catch (Exception e){
            e.getMessage();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
}
