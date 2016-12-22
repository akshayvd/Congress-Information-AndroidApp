package com.avd.congress;

import android.database.Cursor;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avd.congress.Helper.DatabaseHelper;
import com.avd.congress.models.Bill;
import com.avd.congress.models.Committee;

import org.json.JSONObject;

public class CommitteeViewDetails extends AppCompatActivity {

    private Committee committee;
    private String committeeJSON;
    private Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee_view_details);
        getSupportActionBar().setTitle("Committee Info");
        committeeJSON = getIntent().getExtras().getString("committeeJSON");
        committee = new Committee();
        try{
            committee.parsedJSON = new JSONObject(committeeJSON);
            ((ImageView)findViewById(R.id.im_co_chamber)).setImageResource(committee.parsedJSON.getString("chamber").equals("house")?R.drawable.house:R.drawable.senate);
            ((TextView)findViewById(R.id.txt_co_comm_id)).setText(committee.parsedJSON.getString("committee_id"));
            ((TextView)findViewById(R.id.txt_co_name)).setText(committee.parsedJSON.has("name")?committee.parsedJSON.getString("name"):"N.A");
            ((TextView)findViewById(R.id.txt_co_chamber)).setText(committee.parsedJSON.has("chamber")?committee.parsedJSON.getString("chamber"):"N.A");
            ((TextView)findViewById(R.id.txt_co_parent_comm)).setText(committee.parsedJSON.has("parent_committee_id")?committee.parsedJSON.getString("parent_committee_id"):"N.A");
            ((TextView)findViewById(R.id.txt_co_contact)).setText(committee.parsedJSON.has("phone")?committee.parsedJSON.getString("phone"):"N.A");
            ((TextView)findViewById(R.id.txt_co_office)).setText(committee.parsedJSON.has("office")?committee.parsedJSON.getString("office"):"N.A");
            ImageView favStar = ((ImageView)findViewById(R.id.im_co_star));
            isFavorite = checkFavorite();
            ((ImageView) favStar).setImageResource(isFavorite ? R.drawable.yellowstar : R.drawable.star);
            favStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DatabaseHelper.init(v.getContext());
                        if (isFavorite) {
                            DatabaseHelper.execSQL("DELETE FROM COMMITTEE WHERE committee_id = \"" + committee.parsedJSON.getString("committee_id") + "\"");
                        } else {
                            DatabaseHelper.execSQL("INSERT INTO COMMITTEE VALUES (\"" + committee.parsedJSON.getString("committee_id") + "\",\"" + (Base64.encodeToString(committeeJSON.getBytes(), Base64.DEFAULT)) + "\")");
                        }
                        isFavorite = !isFavorite;
                        ((ImageView) v).setImageResource(isFavorite ? R.drawable.yellowstar : R.drawable.star);
                    } catch(Exception e){
                        e.getMessage();
                    }
                }
            });
        } catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();e.getMessage();
        }
    }

    private boolean checkFavorite() {
        DatabaseHelper.init(this);
        try{
            Cursor c = DatabaseHelper.select("SELECT committee_id FROM COMMITTEE WHERE committee_id = \"" + committee.parsedJSON.getString("committee_id") + "\"");
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
