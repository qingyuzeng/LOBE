package com.example.sa_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class record extends AppCompatActivity {

    private ListView listview;
    ArrayAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("日記本");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        Button btn_previous = (Button)findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(record.this, Home.class);
                startActivity(intent);
            }
        });

        listview = (ListView)findViewById(R.id.list_record);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);

        getData();
    }
    private void getData() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("record");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                mAuth = FirebaseAuth.getInstance();
                String uid=mAuth.getCurrentUser().getUid();//取得目前使用者UID

                for (DataSnapshot contact : dataSnapshot.getChildren()){
                    String get_id=contact.child("getid").getValue().toString();//收到錢的人ID
                    String pay_id=contact.child("payid").getValue().toString();//支付錢的人ID
                    if(get_id.equals(uid)) {
                        adapter.add(contact.child("time").getValue().toString() + "   " + contact.child("payname").getValue().toString() + "轉帳給您   " + contact.child("money").getValue().toString() + "代幣");
                    }
                    else if(pay_id.equals(uid)){
                        adapter.add(contact.child("time").getValue().toString() + "   您轉帳給" + contact.child("getname").getValue().toString() + "   " + contact.child("money").getValue().toString() + "代幣");
                    }
                }
                listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

