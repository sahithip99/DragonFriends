package com.drexel.team47.dragonfriends;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.Iterator;

public class RosterActivity extends AppCompatActivity {

    String crn;
    TextView headerText;

    AsyncHttpClient client;
    JSONObject studentList; //display results of CRN query



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster);

        //Initialize variables
        client = new AsyncHttpClient();
        headerText = findViewById(R.id.headerText);

        //Add back button to the action bar


        //Get CRN parameter from redirecting
        Bundle b = getIntent().getExtras();
        if (b != null){
            crn = b.getString("crn");
            System.out.println("The crn>>>" + crn);
            //Initialize header text
            headerText.setText("Course CRN: " + crn);
        }

        getRosterInfo(crn);
    }

    private void getRosterInfo(String crn){
        //Add parameters for POST request
        RequestParams params = new RequestParams("crn", crn); //create a key value pair of 'crn': crn
        String url = "https://dragonfriends-eb4fc.firebaseapp.com/getRosterInfo";

        //Create progress dialog
        final ProgressDialog dialog = new ProgressDialog(RosterActivity.this);
        dialog.setMessage("Getting roster info...");
        dialog.show();

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    studentList = new JSONObject(new String(responseBody));
                    System.out.println("the result");
                    System.out.println(studentList);

                    //Hide progress dialog
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                    //Populate the list

                    //Setup data source
                    ArrayList<RosterItem> rosterList = generateRosterItemList(studentList);
                    System.out.println(rosterList);

                    RosterAdapter rosterAdapter = new RosterAdapter(getApplicationContext(), rosterList);

                    //Get the list view
                    ListView rosterListView = (ListView) findViewById(R.id.rosterListView);
                    rosterListView.setAdapter(rosterAdapter);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error getting class by crn");
                Log.e("error", "get class by crn", error);
                Toast.makeText(RosterActivity.this,"Class not found. Please try another CRN.",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Generate the rows of ListView
    private ArrayList<RosterItem> generateRosterItemList(JSONObject uidData) {
        ArrayList<RosterItem> rosterList = new ArrayList<RosterItem>();
        //Loop through UIDs List
        Iterator<?> keys = uidData.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            try {
                if (uidData.get(key) instanceof JSONObject){
                    System.out.println("key" + key);
                    String studentName = ((JSONObject) uidData.get(key)).getString("name");
                    String studentEmail = ((JSONObject) uidData.get(key)).getString("email");
                    RosterItem student = new RosterItem(studentName, studentEmail);
//                    System.out.println("Student" + student.toString());

                    rosterList.add(student);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        return rosterList;

    }
}

