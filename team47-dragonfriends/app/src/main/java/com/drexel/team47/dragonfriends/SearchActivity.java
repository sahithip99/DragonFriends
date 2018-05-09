package com.drexel.team47.dragonfriends;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private TextView className;
    private TextView secNum;
    private TextView crnNum;
    private TextView classType;
    private TextView instructor;
    private TextView dayTime;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private FloatingActionButton addClass;

    private String classInfo;

    private SearchView classSearch;

    private JSONObject res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        classSearch = (SearchView) findViewById(R.id.classSearchView);

        className = (TextView) findViewById(R.id.class_name);
        secNum = (TextView) findViewById(R.id.sec_num);
        crnNum = (TextView) findViewById(R.id.crn_num);
        classType = (TextView) findViewById(R.id.instr_type);
        instructor = (TextView) findViewById(R.id.instr_name);
        dayTime = (TextView) findViewById(R.id.day_time);

        database = database.getInstance();
        firebaseAuth = firebaseAuth.getInstance();

        addClass = (FloatingActionButton) findViewById(R.id.add_class);

        addClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                System.out.println("Added class " + res);
                //replace with the backend function to add the class to the user's info in database
                final String uid = firebaseAuth.getCurrentUser().getUid();
                try {
                    AsyncHttpClient client2 = new AsyncHttpClient();
                    RequestParams params2 = new RequestParams();
                    System.out.println("UID:");
                    System.out.println(firebaseAuth.getCurrentUser().getUid());
                    params2.put("uid", firebaseAuth.getCurrentUser().getUid());
                    params2.put("crn", res.getString("crn"));
                    String url2 = "https://dragonfriends-eb4fc.firebaseapp.com/addUserToClass";
                    client2.post(url2, params2, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println("Added user uid to class");
                            Toast.makeText(SearchActivity.this, "Added Class", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            System.out.println("Failed to add user uid to class");
                            Toast.makeText(SearchActivity.this, "Failed to Add Class", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

        //Listen to query text
        classSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Do something when text is submitted


                AsyncHttpClient client = new AsyncHttpClient();

                //Add parameters for POST request
                RequestParams params = new RequestParams("crn", s); //create a key value pair of 'crn': s
                System.out.println("logging");
                String url = "https://dragonfriends-eb4fc.firebaseapp.com/classByCrn";

                System.out.println("url" + url);

                final ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
                dialog.setMessage("Getting class data...");
                dialog.show();

                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        try {

                            res= new JSONObject(new String(responseBody));
                            System.out.println(res);
                            displayData(res);
                            dialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        System.out.println("Error getting class by crn");
                        dialog.dismiss();
                        Log.e("error", "get class by crn", error);
                        Toast.makeText(SearchActivity.this,"Class not found. Please try another CRN.",Toast.LENGTH_SHORT).show();

                    }
                });



                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //do something when text changes
                return false;
            }
        });

    }

    /**
     * private TextView className;
     private TextView secNum;
     private TextView crnNum;
     private TextView classType;
     private TextView instructor;
     private TextView dayTime;
     * @param info
     */
    private void displayData(JSONObject info){
        try {
            String name = info.getString("courseTitle");
            String sec = info.getString("sec");
            String crnN = info.getString("crn");
            String classT = info.getString("instrType");
            String instr = info.getString("instructor");
            String dayT = info.getString("dayTime");
            classInfo = crnN;
            className.setText(name);
            secNum.setText(sec);
            crnNum.setText(crnN);
            classType.setText(classT);
            instructor.setText(instr);
            dayTime.setText(dayT);
            addClass.setVisibility(View.VISIBLE);
            addClass.setClickable(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
