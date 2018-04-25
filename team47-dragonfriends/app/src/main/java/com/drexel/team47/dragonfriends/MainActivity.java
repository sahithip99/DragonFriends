package com.drexel.team47.dragonfriends;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //    private FirebaseAuth mAuth;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private TextView textViewSignup;
    private TextView textViewSignin;

    private Button buttonSearch;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = firebaseAuth.getInstance();
        database = database.getInstance();





        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);


        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonSearch.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);


    }

    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        final String name = editTextName.getText().toString();

        if(TextUtils.isEmpty(email)){

            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;


        }

        if (email.indexOf("drexel.edu")==-1){
            Toast.makeText(this,"Please enter a Drexel email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Upload user info to Realtime Database
                            Toast.makeText(MainActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                            final String uid = firebaseAuth.getCurrentUser().getUid();
                            Map<String, Object> userData = new HashMap<String, Object>(){{
                                put("email", email);
                                put("name", name);
                                put("uid", uid);
                                put("classes", "");
                            }};
                            DatabaseReference userRef = database.getReference("users").child(uid);
                            userRef.setValue(userData);



                        }
                        else{
                            Toast.makeText(MainActivity.this,"Could not register...please try again",Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();

        }

        if(view == textViewSignin){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        //How to redirect to a different activity
        if (view == buttonSearch){
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }

    }
    public void onStart(){
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }






    /**FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     if (user != null) {
     String name = user.getDisplayName();
     String email = user.getEmail();
     Uri photoUrl = user.getPhotoUrl();
     boolean emailVerified = user.isEmailVerified();
     String uid = user.getUid();
     }
     **/
}
