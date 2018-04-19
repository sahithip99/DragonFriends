package com.drexel.team47.dragonfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

//import com.google.auth.oauth2.GoogleCredentials;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private SearchView classSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        classSearch = (SearchView) findViewById(R.id.classSearchView);

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

                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println("Successssss");
                        try {

                            JSONObject res= new JSONObject(new String(responseBody));
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



                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //do something when text changes
                return false;
            }
        });

    }




}
