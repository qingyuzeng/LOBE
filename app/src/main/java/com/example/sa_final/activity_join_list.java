package com.example.sa_final;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_join_list extends AppCompatActivity {
    private TextView activityjoinmembername,activityjoinmemberphone,activityname,activitydate,activitycontent,activityunit;
    DatabaseReference JoinRef;
    private FirebaseAuth mAuth;
    activity activity;
    Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("活動內容");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_list);
        activityjoinmembername=(TextView)findViewById(R.id.activity_join_member_name);
        activityjoinmemberphone=(TextView)findViewById(R.id.activity_join_member_phone);
        activityname=(TextView)findViewById(R.id.activity_join_name);
        activitydate=(TextView)findViewById(R.id.activity_join_date);
        activitycontent=(TextView)findViewById(R.id.activity_join_content);
        activityunit=(TextView)findViewById(R.id.activity_join_unit);
        activity= new activity();
        member=new Member();

        Intent intent = getIntent();
        String name = intent.getStringExtra("活動名稱");
        String date =intent.getStringExtra("活動日期");
        String content=intent.getStringExtra("活動內容");
        String unit=intent.getStringExtra("主辦單位");
        activityname.setText(name);
        activitydate.setText(date);
        activitycontent.setText(content);
        activityunit.setText(unit);

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("phonenumber",  mAuth.getCurrentUser().getPhoneNumber());
                intent.setClass(activity_join_list.this, activitycomplete.class);
                startActivity(intent);
            }
        });

        Button btn_previous = (Button)findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity_join_list.this, activity_list_join.class);
                startActivity(intent);
            }
        });
        getData();
    }

    private void getData() {
        mAuth = FirebaseAuth.getInstance();
        //   if(mAuth.getCurrentUser())
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("member");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid=mAuth.getCurrentUser().getUid();//取得使用者UID
                String name=dataSnapshot.child(uid).child("name").getValue(String.class);
                String phone=dataSnapshot.child(uid).child("phone").getValue(String.class);

                TextView show_name = (TextView) findViewById(R.id.activity_join_member_name);
                show_name.setText(name);
                TextView show_phone=(TextView)findViewById(R.id.activity_join_member_phone);
                show_phone.setText(phone);
                //adapter.clear();

//                for (DataSnapshot contact : dataSnapshot.getChildren()){
//                    adapter.add(contact.getKey().toString()+"-"+contact.child("phone").getValue().toString());
//                }
//
//                listview.setAdapter(adapter);
                //String value = dataSnapshot.child().getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}