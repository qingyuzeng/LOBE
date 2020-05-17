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
import android.widget.EditText;
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

public class member_good  extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("修改庫存");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_good);
        TextView name=(TextView)findViewById(R.id.goods_name) ;
        final TextView money=(TextView)findViewById(R.id.goods_cost) ;
        final EditText number=(EditText)findViewById(R.id.goods_stock) ;
        TextView content=(TextView)findViewById(R.id.goods_intro) ;
        ImageView img=(ImageView)findViewById(R.id.img_goods);
        final String getname= getIntent().getStringExtra("名稱");
        final String getmoney= getIntent().getStringExtra("價格");
        String getid= getIntent().getStringExtra("圖片");
        final String getnumber= getIntent().getStringExtra("數量");
        final String getcontent= getIntent().getStringExtra("介紹");


        name.setText("商品名稱:"+getname);
        money.setText("商品價格:$"+getmoney);
        number.setText(getnumber);
        content.setText("商品介紹:"+getcontent);
        if(!isDestroy((Activity)member_good.this)){
            Glide.with(member_good.this).load("https://firebasestorage.googleapis.com/v0/b/cycusa-71b70.appspot.com/o/GImages%2F"+getid+"?alt=media").into(img);
        }

        //錢錢不夠就兌換失敗
        //廠商一開始註冊就直接登記商品，然後有修改紐?
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                DatabaseReference ContactsRef;
                ContactsRef = FirebaseDatabase.getInstance().getReference("Good");
                EditText editText = (EditText) findViewById(R.id.goods_stock);
                String kk = editText.getText().toString();
Log.e("kk",kk);
if(kk.isEmpty()){
    new androidx.appcompat.app.AlertDialog.Builder(member_good.this)
            .setMessage("修改失敗")
            .setPositiveButton("確定", null)
            .show();
}else{
    int aa = Integer.parseInt(kk);

    if (aa != 0 && aa > 0) {
        ContactsRef.child(getname).child("number").setValue(kk);

        new AlertDialog.Builder(member_good.this)
                .setMessage("修改成功")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent=new Intent();
                        intent.setClass(member_good.this,Home.class);
                        startActivity(intent);
                    }

                })
                .show();


    } else {
        new androidx.appcompat.app.AlertDialog.Builder(member_good.this)
                .setMessage("修改失敗")
                .setPositiveButton("確定", null)
                .show();
    }
}

                }






            }
        );
    }
    public static boolean isDestroy(Activity activity) {
        if (activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

}



