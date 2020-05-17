package com.example.sa_final;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class goods  extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("商品架");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods);
        TextView name=(TextView)findViewById(R.id.goods_name) ;
        final TextView money=(TextView)findViewById(R.id.goods_cost) ;
        TextView number=(TextView)findViewById(R.id.goods_stock) ;
        TextView content=(TextView)findViewById(R.id.goods_intro) ;
        ImageView img=(ImageView)findViewById(R.id.img_goods);
        final String getname= getIntent().getStringExtra("名稱");
        final String getmoney= getIntent().getStringExtra("價格");
        String getid= getIntent().getStringExtra("圖片");
        final String getnumber= getIntent().getStringExtra("數量");
        final String getcontent= getIntent().getStringExtra("介紹");


        name.setText("商品名稱:"+getname);
        money.setText("商品價格:$"+getmoney);
        number.setText("商品數量:"+getnumber);
        content.setText("商品介紹:"+getcontent);
        if(!isDestroy((Activity)goods.this)){
            Glide.with(goods.this).load("https://firebasestorage.googleapis.com/v0/b/cycusa-71b70.appspot.com/o/GImages%2F"+getid+"?alt=media").into(img);
        }

        //錢錢不夠就兌換失敗
        //廠商一開始註冊就直接登記商品，然後有修改紐?
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int[] m1=new int[100];
                final int[] m2=new int[100];
                final int[] m5=new int[100];
                mAuth = FirebaseAuth.getInstance();
                //   if(mAuth.getCurrentUser())
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("member");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uid=mAuth.getCurrentUser().getUid();//取得使用者UID

                        String money=dataSnapshot.child(uid).child("money").getValue(String.class);
                        String name=dataSnapshot.child(uid).child("name").getValue(String.class);

                         m1[0]=Integer.parseInt(money);
                         m2[0]=Integer.parseInt(getmoney);
                         m5[0]=Integer.parseInt(getnumber);
                        Log.e("kk", money);
                        Log.e("kk", getmoney);
                        if (m1[0]>=m2[0]&&m5[0]>0){
                            Log.e("GG", String.valueOf(m1[0]));
                            Log.e("GG", String.valueOf(m2[0]));
                            int m3=m1[0]-m2[0];
                            int m4=m5[0]-1;
                            member(m3);
                            company(m2[0]);
                            record(name,uid,m2[0]);
                            good(m4,getname);
                            new AlertDialog.Builder(goods.this)
                                    .setMessage("兌換成功")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }

                                    })
                                    .show();
                        }else{
                            new AlertDialog.Builder(goods.this)
                                    .setMessage("兌換失敗\n您的代幣不足或商品已售完")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }

                                    })
                                    .show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
    public void good(int cc,String aa){
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference ContactsRef ;
        ContactsRef = FirebaseDatabase.getInstance().getReference("Good");

        String kk=String.valueOf(cc);
        ContactsRef.child(aa).child("number").setValue(kk);
    }
    public  void  member(int cc){
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference ContactsRef ;
        ContactsRef = FirebaseDatabase.getInstance().getReference("member");
        String uid = mAuth.getCurrentUser().getUid();//取得使用者UID
        String kk=String.valueOf(cc);
        ContactsRef.child(uid).child("money").setValue(kk);

    }
    public void company(final int cc){
        mAuth = FirebaseAuth.getInstance();
        //   if(mAuth.getCurrentUser())
        final String uid= getIntent().getStringExtra("ID");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("member");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String money=dataSnapshot.child(uid).child("money").getValue(String.class);
               int m1=Integer.parseInt(money);
                int kk=m1+cc;
                String gg= String.valueOf(kk);
                Log.e("kk", money);
                myRef.child(uid).child("money").setValue(gg);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void record(final String aa, final String bb, final int cc){
        mAuth = FirebaseAuth.getInstance();
        //   if(mAuth.getCurrentUser())
        final String uid= getIntent().getStringExtra("ID");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("member");
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT 08"));
        final String ee = dff.format(new Date());
        final String time = ee;

        final String yy = String.valueOf(cc);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                final String name=dataSnapshot.child(uid).child("name").getValue(String.class);
                final String payname = aa;
                final String payid = bb;
                final String getname = name;
                final String getid=uid;
                final  String money=String.valueOf(cc);
                final DatabaseReference kk = database.getReference("record");
          record data_r = new record(payname, payid, getname, getid, money, time);

                String key = kk.push().getKey();
                //將contact1放人子目錄
                kk.child(key).setValue(data_r);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public class record {

        private  String payname;
        private  String payid;
        private  String getname;
        private  String getid;
        private String money;
        private String time;

        public record() {
        }

        public record(String payname, String payid, String getname,String getid,String money,String time){
            this.payname = payname;
            this.payid = payid;
            this.getname = getname;
            this.getid = getid;
            this.money = money;
            this.time = time;
        }

        public String getpayname(){
            return this.payname=payname;
        }
        public String getpayid(){
            return this.payid=payid;
        }
        public String getgetname(){
            return this.getname=getname;
        }
        public String getgetid(){
            return this.getid=getid;
        }
        public String getmoney(){
            return this.money=money;
        }
        public String gettime(){
            return this.time=time;
        }
    }

    }



