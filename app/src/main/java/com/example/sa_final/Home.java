package com.example.sa_final;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity {
    private Button logout;
    private ListView listview;
    Member member;
    StorageReference mStorageRef;
    ArrayAdapter adapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        setTitle("主頁");
        TextView name=(TextView)findViewById(R.id.user_name);
        TextView money=(TextView)findViewById(R.id.money);
        ImageView imageView=(ImageView)findViewById(R.id.img_head);

        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        //如果是廠商連接到...商品qrcode頁面(字樣顯示商品架);如果是會員連接到money_get(字樣顯示收紅包)
        Button btn_get = (Button)findViewById(R.id.btn_get);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home.this,money_get.class);
                startActivity(intent);
            }
        });

        //如果是廠商連接到money_pay(字樣顯示給紅包);如果是會員連接到掃描商品qrcode頁面(字樣顯示兌換商品)
        Button btn_pay = (Button)findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
               // intent.setClass(Home.this,money_pay.class);
               // startActivity(intent);
                payCode();
            }
        });

        //希望可以做按鈕點下去有芬頁可以選擇的那個(一個是收送紅包的紀錄頁;一個是商品買賣的紀錄頁)
        Button btn_record = (Button)findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home.this,record.class);
                startActivity(intent);
            }
        });

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        getData();
    }

    public void payCode() {
        IntentIntegrator scanintegrator = new IntentIntegrator(Home.this);
        scanintegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        scanintegrator.setPrompt("請掃描");
        scanintegrator.setOrientationLocked(false);
        scanintegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (scanningResult!= null)
        {
            if (scanningResult.getContents()==null)
            {
                Toast.makeText(this, "取消掃描", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(this, money_pay.class);
                String send=scanningResult.getContents();
                intent.putExtra("result",send);
                startActivity(intent);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getData() {
        mAuth = FirebaseAuth.getInstance();
        //   if(mAuth.getCurrentUser())
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("member");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    if(mAuth.getCurrentUser().getUid()!=null) {
                        String uid = mAuth.getCurrentUser().getUid();//取得使用者UID
                        String who = dataSnapshot.child(uid).child("who").getValue(String.class);
                        String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                        String money = dataSnapshot.child(uid).child("money").getValue(String.class);
                        String imageid = dataSnapshot.child(uid).child("imageid").getValue(String.class);


                        if (who != null) {
                            ImageView imageView = (ImageView) findViewById(R.id.img_head);
                            if (!isDestroy((Activity) Home.this)) {
                                Glide.with(Home.this).load("https://firebasestorage.googleapis.com/v0/b/cycusa-71b70.appspot.com/o/Images%2F" + imageid + "?alt=media").into(imageView);
                            }

                            if (who.matches("一般會員")) {
                                TextView show_name = (TextView) findViewById(R.id.user_name);
                                show_name.setText(name + " 歡迎回來");
                                TextView show_money = (TextView) findViewById(R.id.money);
                                show_money.setText("代幣: $" + money);
                                Button btn_activity = (Button) findViewById(R.id.btn_activity);
                                btn_activity.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction("member");
                                        intent.putExtra("phonenumber",  mAuth.getCurrentUser().getPhoneNumber());

                                        intent = Intent.createChooser(intent, "Please choose");
                                        startActivity(intent);
                                    }
                                });
                            } else if (who.matches("廠商")) {

                                TextView show_name = (TextView) findViewById(R.id.user_name);
                                show_name.setText(name + " 歡迎回來");
                                TextView show_money = (TextView) findViewById(R.id.money);
                                show_money.setText("代幣: $" + money);
                                Button btn_activity = (Button) findViewById(R.id.btn_activity);
                                btn_activity.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction("company");
                                        intent.putExtra("phonenumber",  mAuth.getCurrentUser().getPhoneNumber());
                                        intent = Intent.createChooser(intent, "Please choose");
                                        startActivity(intent);
                                    }
                                });

                            }
                            //adapter.clear();

//                for (DataSnapshot contact : dataSnapshot.getChildren()){
//                    adapter.add(contact.getKey().toString()+"-"+contact.child("phone").getValue().toString());
//                }
//
//                listview.setAdapter(adapter);
                            //String value = dataSnapshot.child().getValue(String.class);
                        } else {
                            Intent intent1 = new Intent(Home.this, register.class);

                            intent1.putExtra("phonenumber",  mAuth.getCurrentUser().getPhoneNumber());

                            startActivity(intent1);

                        }
                    }else {
                        Intent intent = new Intent(Home.this, MainActivity.class);


                        startActivity(intent);

                    }
                }catch (Exception e){
                    Intent intent = new Intent(Home.this, MainActivity.class);


                    startActivity(intent);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public static boolean isDestroy(Activity activity) {
        if (activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
}