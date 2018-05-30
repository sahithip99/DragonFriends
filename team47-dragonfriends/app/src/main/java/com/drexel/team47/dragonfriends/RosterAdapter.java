package com.drexel.team47.dragonfriends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TriLe on 5/7/2018.
 */

public class RosterAdapter extends ArrayAdapter<RosterItem> {
    public RosterAdapter(Context context, ArrayList<RosterItem> rosterItems) {
        super(context, 0, rosterItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final RosterItem student = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.roster_list_layout, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.text_view_student_name);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.text_view_student_email);
        // Populate the data into the template view using the data object
        tvName.setText("Name: " + student.getStudentName());
        tvEmail.setText("Email: " + student.getStudentEmail());
        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                System.out.println(student.getUid());
                Context context = getContext();
                Intent intent = new Intent(context, ProfileActivity.class);
                //Redirect with parameters
                Bundle b = new Bundle();
                String uid = student.getUid();
                b.putString("uid", uid);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}