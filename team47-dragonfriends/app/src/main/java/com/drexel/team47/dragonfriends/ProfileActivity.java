package com.drexel.team47.dragonfriends;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileActivity extends AppCompatActivity {
    private Button buttonLogout;
    private FirebaseAuth firebaseAuth;
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

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
    }

    private void userLogout(){
        firebaseAuth.signOut();
        Toast.makeText(this,"Signed out successfully", Toast.LENGTH_LONG);
        startActivity(new Intent(this,MainActivity.class));

    }



    public void onClick(View view) {
        if (view == buttonLogout) {
            userLogout();
        }
    }
}