package com.example.sa_final;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class choose_goods extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choose_goods);
        setTitle("商品列");

        getData();
    }
    private void getData() {
        mAuth = FirebaseAuth.getInstance();
        final int[] i = {0};
        final String[] Smoney=new String[100];
        final String[] Sname=new String[100];
        final String[] Simageid=new String[100];
        final String[] Snumber=new String[100];
        final String[] Scontent=new String[100];
        final String[] Sid=new String[100];
        //   if(mAuth.getCurrentUser())
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Good");
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String name=snapshot.child("name").getValue().toString();
                    String imageid=snapshot.child("imageid").getValue().toString();
                    String money=snapshot.child("money").getValue().toString();
                    String number=snapshot.child("number").getValue().toString();
                    String content=snapshot.child("intro").getValue().toString();
                    String id=snapshot.child("id").getValue().toString();
                    Smoney[i[0]]=money;
                    Sname[i[0]]=name;
                    Simageid[i[0]]=imageid;
                    Snumber[i[0]]=number;
                    Scontent[i[0]]=content;
                    Sid[i[0]]=id;
                    i[0]++;
                    Log.e("GG",money);
                    Log.e("GG",name);

                }

                ImageView imageView[]=new ImageView[100];
                final TextView tvmoney[]=new TextView[100];
                final TextView tvname[]=new TextView[100];
                LinearLayout ll[] = new LinearLayout[100];
                LinearLayout lle = new LinearLayout(choose_goods.this);
                lle.setOrientation(LinearLayout.VERTICAL);
                lle.setBackgroundColor(Color.parseColor("#FDEBD4"));
                setContentView(lle);
                Button btn[]=new Button[100];
                for (int j=0;j<i[0];j++){
                    ll[j] = new LinearLayout(choose_goods.this);
                    ll[j].setOrientation(LinearLayout.HORIZONTAL);
                    ll[j].setGravity(Gravity.CENTER_VERTICAL);
                    lle.addView(ll[j]);
                    imageView[j]=new ImageView(choose_goods.this);
                    imageView[j].setMinimumWidth(250);
                    imageView[j].setMinimumHeight(250);
                    if(!isDestroy((Activity)choose_goods.this)){
                        Glide.with(choose_goods.this).load("https://firebasestorage.googleapis.com/v0/b/cycusa-71b70.appspot.com/o/GImages%2F"+Simageid[j]+"?alt=media").into(imageView[j]);
                    }
                    Log.e("GG","https://firebasestorage.googleapis.com/v0/b/cycusa-71b70.appspot.com/o/GImages%2F"+Simageid[j]+"?alt=media");

                    ll[j].addView(imageView[j]);
                    tvname[j]=new TextView(choose_goods.this);
                    tvname[j].setText("  "+Sname[j]);
                    tvname[j].setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                    tvname[j].setWidth(400);
                    ll[j].addView(tvname[j]);

                    tvmoney[j]=new TextView(choose_goods.this);
                    tvmoney[j].setText("$"+Smoney[j]);
                    tvmoney[j].setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                    tvmoney[j].setWidth(200);
                    ll[j].addView(tvmoney[j]);

                    btn[j]=new Button(choose_goods.this);
                    btn[j].setText("查看");
                    btn[j].setId(j);
                    btn[j].setRight(View.FOCUS_RIGHT);
                    btn[j].setBackgroundColor(Color.parseColor("#CCC79C"));
                    btn[j].setWidth(300);
                    btn[j].setMinWidth(20);
                    btn[j].setMinHeight(20);
                    int k=j-1;
                    ll[j].addView(btn[j]);

                    btn[j].setNextFocusDownId(k);
                    final String finalJ = Sname[j];
                    final String finalk = Smoney[j];
                    final String finalg = Simageid[j];
                    final String finala=Snumber[j];
                    final String finalb=Scontent[j];
                    final String finalc=Sid[j];

                    btn[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("名稱", finalJ);
                            intent.putExtra("圖片",finalg);
                            intent.putExtra("價格",finalk);
                            intent.putExtra("數量",finala);
                            intent.putExtra("介紹",finalb);
                            intent.putExtra("ID",finalc);
                            intent.setClass(choose_goods.this,goods.class);
                            startActivity(intent);
                        }
                    });

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