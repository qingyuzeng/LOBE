package com.example.sa_final;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class activity_join extends AppCompatActivity {
    private TextView activityjoinmembername,activityjoinmemberphone,activityname,activitydate,activitycontent,activityunit;
    DatabaseReference JoinRef;
    private FirebaseAuth mAuth;
    activity activity;
    Member member;
    signup_activity_for_member signupActivityForMember;
    int a=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("活動報名");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
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

        signupActivityForMember=new signup_activity_for_member();
        JoinRef= FirebaseDatabase.getInstance().getReference("join");
        //有資料未填寫->報名失敗
        JoinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot contact : dataSnapshot.getChildren()) {
                    // String user_uid = contact.child("uid").getValue().toString();
                    //String  key= contact.getRef().getKey() ;
                    String phonenumber = mAuth.getCurrentUser().getPhoneNumber();

                    String sr[] = phonenumber.split(Pattern.quote("+"));
                    String phone=contact.child("member_phone").getValue(String.class);
                    String sr1[] = phone.split(Pattern.quote("+"));
                    Log.v("memberphone1",phone);
                    Log.d("memberphone",phonenumber);
                    String activityname1=contact.child("activity_name").getValue(String.class).trim();
                    String name=activityname.getText().toString().trim();
                    Log.i("activityname1",activityname1);
                    Log.w("name",name);
                    if(sr[1].matches(sr1[1])) {
                        if (activityname1 .matches(name) ) {

                            setA(2);

                            break;
                        }
                    }

                }

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
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getA() ==2){
                    new AlertDialog.Builder(activity_join.this)
                            .setMessage("活動已參加過了")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }

                            })
                            .show();
                }
                else if (getA()==1){
                    signupActivityForMember.setMember_name(activityjoinmembername.getText().toString().trim());
                    signupActivityForMember.setMember_phone(activityjoinmemberphone.getText().toString().trim());
                    signupActivityForMember.setActivity_name(activityname.getText().toString().trim());
                    signupActivityForMember.setActivity_date(activitydate.getText().toString().trim());
                    signupActivityForMember.setActivity_content(activitycontent.getText().toString().trim());
                    signupActivityForMember.setActivity_unit(activityunit.getText().toString().trim());
                    JoinRef.push().setValue(signupActivityForMember);
                    new AlertDialog.Builder(activity_join.this)
                            .setMessage("報名成功")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }

                            })
                            .show();
                }
            }
        });

        Button btn_previous = (Button)findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity_join.this, activity_list.class);
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

    public int getA() {
        Log.e("B", String.valueOf(a));
        return a;
    }

    public void setA(int a) {
        this.a = a;
        Log.e("a", String.valueOf(a));
    }


}
