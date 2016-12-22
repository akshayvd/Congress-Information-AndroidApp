package com.avd.congress;

import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avd.congress.Helper.DatabaseHelper;
import com.avd.congress.models.Legislator;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public class LegislatorViewDetails extends AppCompatActivity {

    private Legislator legislator;
    private String legislatorJSON;
    boolean isFavorite =false;
    private String fileName = "LegislatorFav";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legislator_view_details);
        getSupportActionBar().setTitle("Legislator Info");
        legislatorJSON = getIntent().getExtras().getString("legislatorJSON");
        legislator = new Legislator();
        try{
            legislator.parsedJSON = new JSONObject(legislatorJSON);
            String urlStr = "http://theunitedstates.io/images/congress/225x275/"+ legislator.parsedJSON.getString("bioguide_id")+".jpg";
            Picasso.with(this).load(urlStr).into((ImageView)findViewById(R.id.im_le_photo));
            String temp = legislator.parsedJSON.getString("party");
            ((ImageView)findViewById(R.id.im_le_party)).setImageResource(temp.equals("R")?R.drawable.republican:temp.equals("D")?R.drawable.democrat:R.drawable.independent);
            temp = temp.equals("R")?"Republican":temp.equals("D")?"Democrat":"Independent";
            ((TextView)findViewById(R.id.txt_le_party)).setText(temp);
            temp = legislator.parsedJSON.getString("title") + ". " + legislator.parsedJSON.getString("last_name") + ", " + legislator.parsedJSON.getString("first_name");
            ((TextView)findViewById(R.id.txt_le_name)).setText(temp);
            ((TextView)findViewById(R.id.txt_le_email)).setText(!legislator.parsedJSON.has("oc_email")?"N.A":legislator.parsedJSON.getString("oc_email").equals("null")?"N.A":legislator.parsedJSON.getString("oc_email"));
            ((TextView)findViewById(R.id.txt_le_chamber)).setText(!legislator.parsedJSON.has("chamber")?"N.A":legislator.parsedJSON.getString("chamber"));
            ((TextView)findViewById(R.id.txt_le_contact)).setText(!legislator.parsedJSON.has("phone")?"N.A":legislator.parsedJSON.getString("phone"));
            ((TextView)findViewById(R.id.txt_le_office)).setText(!legislator.parsedJSON.has("office")?"N.A":legislator.parsedJSON.getString("office"));
            ((TextView)findViewById(R.id.txt_le_state)).setText(!legislator.parsedJSON.has("state")?"N.A":legislator.parsedJSON.getString("state"));
            ((TextView)findViewById(R.id.txt_le_fax)).setText(!legislator.parsedJSON.has("fax")?"N.A":legislator.parsedJSON.getString("fax").equals("null")?"N.A":legislator.parsedJSON.getString("fax"));
            ((ImageView)findViewById(R.id.im_le_facebook)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (legislator.parsedJSON.has("facebook_id") &&
                                (!legislator.parsedJSON.getString("facebook_id").equals("")&&!legislator.parsedJSON.getString("facebook_id").equals("null"))) {
                            OpenURL("http://facebook.com/"+legislator.parsedJSON.getString("facebook_id"));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.fb_error, Toast.LENGTH_LONG).show();
                        }
                    } catch(Exception e){
                        Log.d("",e.getMessage(),e);
                    }
                }
            });
            ((ImageView)findViewById(R.id.im_le_twitter)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (legislator.parsedJSON.has("twitter_id") &&
                                (!legislator.parsedJSON.getString("twitter_id").equals("")&&!legislator.parsedJSON.getString("twitter_id").equals("null"))) {
                            OpenURL("http://twitter.com/"+legislator.parsedJSON.getString("twitter_id"));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.twitter_error, Toast.LENGTH_LONG).show();
                        }
                    } catch(Exception e){
                        Log.d("",e.getMessage(),e);
                    }
                }
            });
            ((ImageView)findViewById(R.id.im_le_website)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (legislator.parsedJSON.has("website") &&
                                (!legislator.parsedJSON.getString("website").equals("")&&!legislator.parsedJSON.getString("website").equals("null"))) {
                            OpenURL(legislator.parsedJSON.getString("website"));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.website_error, Toast.LENGTH_LONG).show();
                        }
                    } catch(Exception e){
                        Log.d("",e.getMessage(),e);
                    }
                }
            });
            ImageView favStar = ((ImageView)findViewById(R.id.im_le_star));
            isFavorite = checkFavorite();
            ((ImageView) favStar).setImageResource(isFavorite ? R.drawable.yellowstar : R.drawable.star);
            favStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DatabaseHelper.init(v.getContext());
                        if (isFavorite) {
                            DatabaseHelper.execSQL("DELETE FROM LEGISLATOR WHERE bioguide_id = \"" + legislator.parsedJSON.getString("bioguide_id") + "\"");
                        } else {
                            DatabaseHelper.execSQL("INSERT INTO LEGISLATOR VALUES (\"" + legislator.parsedJSON.getString("bioguide_id") + "\",\"" + (Base64.encodeToString(legislatorJSON.getBytes(), Base64.DEFAULT)) + "\")");
                        }
                        isFavorite = !isFavorite;
                        ((ImageView) v).setImageResource(isFavorite ? R.drawable.yellowstar : R.drawable.star);
                    } catch(Exception e){
                        e.getMessage();
                    }
                }
            });
            Date termStart, termEnd, birthday;
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            termStart = format.parse(legislator.parsedJSON.getString("term_start"));
            termEnd = format.parse(legislator.parsedJSON.getString("term_end"));
            birthday = format.parse(legislator.parsedJSON.getString("birthday"));
            java.text.SimpleDateFormat reqformat = new java.text.SimpleDateFormat("MMM dd,yyyy");
            Date now = new Date();
            ((TextView)findViewById(R.id.txt_le_STerm)).setText(reqformat.format(termStart));
            ((TextView)findViewById(R.id.txt_le_ETerm)).setText(reqformat.format(termEnd));
            ProgressBar pb =(ProgressBar)findViewById(R.id.pb_le_term);
            (pb).setMax(100);
            int progress = (int)Math.round(((double)(now.getTime() - termStart.getTime())/(termEnd.getTime() - termStart.getTime()))*100);
            (pb).setProgress(progress);
            ((TextView)findViewById(R.id.txt_le_term)).setText(progress+"%");
            ((TextView)findViewById(R.id.txt_le_birthday)).setText(reqformat.format(birthday));

        } catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void OpenURL(String facebook_id) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_id));
        startActivity(intent);
    }

    private boolean checkFavorite() {
        DatabaseHelper.init(this);
        try{
            Cursor c = DatabaseHelper.select("SELECT bioguide_id FROM LEGISLATOR WHERE bioguide_id = \"" + legislator.parsedJSON.getString("bioguide_id") + "\"");
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
