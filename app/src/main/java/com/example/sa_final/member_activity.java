package com.example.sa_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class member_activity extends AppCompatActivity {
    ArrayAdapter adapter;
    private ListView listview;
    private FirebaseAuth mAuth;
    public  String name;
    ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("已創建活動");
        setContentView(R.layout.activity_member_activity);
        listview = (ListView)findViewById(R.id.list_activity);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        getData();

        getInformation();
        Button btn_previous = (Button)findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(member_activity.this, Home.class);
                startActivity(intent);
            }
        });
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
                String who = dataSnapshot.child(uid).child("who").getValue(String.class);
                String name=dataSnapshot.child(uid).child("name").getValue(String.class);
                String phone=dataSnapshot.child(uid).child("phone").getValue(String.class);
                setName(name);
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

    private void getInformation() {
        mAuth = FirebaseAuth.getInstance();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activity");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                myListView = (ListView) findViewById(R.id.list_activity);

                //创建ArrayList对象 并添加数据
                final ArrayList<HashMap<String, String>> myArrayList = new ArrayList<HashMap<String, String>>();
                for (DataSnapshot contact : dataSnapshot.getChildren()) {
                    // String user_uid = contact.child("uid").getValue().toString();
                    //String  key= contact.getRef().getKey() ;



                    String unit=contact.child("unit").getValue(String.class);



                    if(unit.matches(getName())){
                        String activityname = contact.child("name").getValue(String.class);
                        String activitydate = contact.child("date").getValue(String.class);
                        String activitycontent = contact.child("content").getValue(String.class);
                        String activityunit=contact.child("unit").getValue(String.class);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("name", activityname);
                        map.put("date", activitydate);
                        map.put("content",activitycontent);
                        map.put("unit",activityunit);
                        myArrayList.add(map);
                    }



                }
                SimpleAdapter mySimpleAdapter = new SimpleAdapter(member_activity.this, myArrayList,
                        R.layout.activity_layout,//ListView内部数据展示形式的布局文件listitem.xml
                        new String[] {"name", "date","content","unit"},//HashMap中的两个key值 itemTitle和itemContent
                        new int[]{R.id.activity_layout_name, R.id.activity_layout_date, R.id.activity_layout_content,R.id.activity_layout_unit});/*布局文件listitem.xml中组件的id
                                                            布局文件的各组件分别映射到HashMap的各元素上，完成适配*/

                myListView.setAdapter(mySimpleAdapter);
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                            long arg3) {
                        HashMap<String, String> map = (HashMap<String, String>) myListView.getItemAtPosition(arg2);

                        String name=map.get("name");
                        String date=map.get("date");
                        String content=map.get("content");
                        String unit=map.get("unit");

                        Intent intent = new Intent(member_activity.this, activity_member_list.class);
                        intent.putExtra("活動名稱",name);
                        intent.putExtra("活動日期",date);
                        intent.putExtra("活動內容",content);
                        intent.putExtra("主辦單位",unit);
                        startActivity(intent);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}