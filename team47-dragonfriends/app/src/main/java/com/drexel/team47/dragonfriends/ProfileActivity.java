package com.drexel.team47.dragonfriends;

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
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ProfileActivity extends AppCompatActivity {
    private Button buttonLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private JSONObject res;

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
}