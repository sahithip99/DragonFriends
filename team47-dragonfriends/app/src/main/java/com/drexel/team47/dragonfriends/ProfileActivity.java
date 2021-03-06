package com.drexel.team47.dragonfriends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private Button buttonLogout;

    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase database;

    private JSONObject res;

    JSONObject userInfo;

    private Button buttonSearch;
    private TextView tvName;
    private TextView tvEmail;
    private TextView userClassesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        firebaseAuth = FirebaseAuth.getInstance();

        tvName = findViewById(R.id.name);
        tvEmail = findViewById(R.id.email);
        buttonSearch = findViewById(R.id.buttonSearch);
        userClassesText = (TextView) findViewById(R.id.userClasses);

        userClassesText.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);

        String uid = "";

        Bundle b = getIntent().getExtras();

        if (b != null){
            uid = b.getString("uid");
            System.out.println("The uid>>>" + uid);
            getUserInfo(uid);
        } else {
            getUserInfo(firebaseAuth.getCurrentUser().getUid());
        }



    }

    private void getUserInfo(String uid){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            String url = "https://dragonfriends-eb4fc.firebaseapp.com/getUserProfile";

            //Create progress dialog
            final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
            dialog.setMessage("Getting user info...");
            dialog.show();
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        userInfo = new JSONObject(new String(responseBody));
                        JSONObject classData = userInfo.getJSONObject("classes");
                        System.out.println("Classes:" + classData);

                        displayData(userInfo);
                        dialog.dismiss();

                        //Populate the list

                        //Setup data source
                        ArrayList<ClassItem> classList = generateClassItemList(classData);
                        System.out.println(classList);

                        ClassAdapter classAdapter = new ClassAdapter(getApplicationContext(), R.layout.profile_list_layout, classList);

                        //Get the list view
                        ListView classListView = (ListView) findViewById(R.id.classListView);
                        classListView.setAdapter(classAdapter);

                    }
                    catch (Exception e){
                        System.out.println("Failure getting user info");
                        dialog.dismiss();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("Failed to get user profile");
                    Toast.makeText(ProfileActivity.this, "Failed to get user profile", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void userLogout() {
        firebaseAuth.signOut();
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_LONG);
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));


                break;
        }

        return true;
    }

    private JSONObject cInfo(String crn) {
        AsyncHttpClient client = new AsyncHttpClient();

        //Add parameters for POST request
        RequestParams params = new RequestParams("crn", crn); //create a key value pair of 'crn': s
        System.out.println("logging");
        String url = "https://dragonfriends-eb4fc.firebaseapp.com/classByCrn";
        System.out.println("url" + url);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    res = new JSONObject(new String(responseBody));
                    System.out.println(res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error getting class by crn");
                Log.e("error", "get class by crn", error);
            }
        });
        return res;
    }


    private void displayData(JSONObject info){
        try {
            String fullName = info.getString("name");
            String email = info.getString("email");
            tvName.setText(fullName);
            tvEmail.setText(email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //Generate the rows of ListView
    private ArrayList<ClassItem> generateClassItemList(JSONObject classData) {
        ArrayList<ClassItem> classList = new ArrayList<ClassItem>();
        //Loop through UIDs List
        Iterator<?> keys = classData.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            try {
                if (classData.get(key) instanceof JSONObject){
                    System.out.println("key" + key);
                    String crn = ((JSONObject) classData.get(key)).getString("crn");
                    String name = ((JSONObject) classData.get(key)).getString("courseTitle");
                    ClassItem classItem = new ClassItem(crn, name);
//                    System.out.println("Student" + student.toString());

                    classList.add(classItem);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        return classList;

    }

    @Override
    public void onClick(View view){
        if (view == userClassesText){
            Intent intent = new Intent(ProfileActivity.this, RosterActivity.class);
            //Redirect with parameters
            Bundle b = new Bundle();
            String crn = "30003";
            b.putString("crn", crn);
            intent.putExtras(b);
            startActivity(intent);
        }

        if (view == buttonSearch){
            startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
        }
    }


}