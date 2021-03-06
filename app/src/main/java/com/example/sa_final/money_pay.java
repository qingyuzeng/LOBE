package com.example.sa_final;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class money_pay extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText how_money;
    String total_money = "0";
    String p_how_money_now = "0";//付款者目前所有的樂幣
    String pay_how_money = "0";//付款者須支付的金額
    String outputString;
    String r_how_money_now = "0";//收款者目前的所有樂幣
    String total_get_money = "0";
    String pay_name, get_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setContentView(R.layout.money_pay);

        Button btn_previous = (Button) findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(money_pay.this, Home.class);
                startActivity(intent);
            }
        });

        outputString = intent.getStringExtra("result");//收款人的ID

        p_how_money_now(); //取得付款人總樂幣金額

        Button btn_ok = (Button) findViewById(R.id.btn_ok2);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pay_how = (EditText) findViewById(R.id.how_much);//取得需要支付的金額
                pay_how_money = pay_how.getText().toString();//取得需要支付的金額
                if (pay_how_money.matches("")) {
                    new AlertDialog.Builder(money_pay.this)
                            .setMessage("請輸入金額")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    finish();
                                }

                            })
                            .show();
                } else {
                    int all_money = Integer.parseInt(p_how_money_now); //目前有多少樂幣
                    int need_pay = Integer.parseInt(pay_how_money); //需要付多少樂幣
                    if ((need_pay <= all_money) && (need_pay > 0)) {
                        total_money = Integer.toString((all_money - need_pay));

                        String uid = mAuth.getCurrentUser().getUid();//取得付款者UID
                        FirebaseDatabase database = FirebaseDatabase.getInstance();//取得資料庫連結
                        DatabaseReference myRef = null;
                        myRef = database.getReference("member");


                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(uid + "/money", total_money);//前面的字是child後面的字是要修改的value值
                        myRef.updateChildren(childUpdates);


                        int get_all_money = Integer.parseInt(r_how_money_now); //目前有多少樂幣
                        int get_need_pay = Integer.parseInt(pay_how_money); //拿到多少樂幣
                        total_get_money = Integer.toString(get_all_money + get_need_pay);//執行收款人的資料更新

                        childUpdates.put(outputString + "/money", total_get_money);//前面的字是child後面的字是要修改的value值
                        myRef.updateChildren(childUpdates);

                        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dff.setTimeZone(TimeZone.getTimeZone("GMT 08"));
                        String ee = dff.format(new Date());

                        String payname = pay_name;
                        String payid = uid;
                        String getname = get_name;
                        String getid = outputString;
                        String money = Integer.toString(get_need_pay);
                        String time = ee;

                        //連接資料庫
                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                        //目錄 /ex1
                        DatabaseReference ContactsRef = database2.getReference("record");
                        //將資料放入record
                        record data_r = new record(payname, payid, getname, getid, money, time);

                        String key = ContactsRef.push().getKey();
                        //將contact1放人子目錄
                        ContactsRef.child(key).setValue(data_r);

                        new AlertDialog.Builder(money_pay.this)
                                .setMessage("轉帳成功")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }

                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(money_pay.this)
                                .setMessage("請輸入正確金額")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }

                                })
                                .show();
                    }

                }
            }
        });
    }

    private void p_how_money_now() {//取得資料庫資料
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("member");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = mAuth.getCurrentUser().getUid();//取得付款者UID
                p_how_money_now = dataSnapshot.child(uid).child("money").getValue(String.class);//取得付款者目前樂幣
                pay_name = dataSnapshot.child(uid).child("name").getValue(String.class);//取得付款者姓名
                TextView how_money = (TextView) findViewById(R.id.now_money);
                how_money.setText("目前樂幣數量:" + p_how_money_now);

                r_how_money_now = dataSnapshot.child(outputString).child("money").getValue(String.class);//取得收款者目前樂幣
                get_name = dataSnapshot.child(outputString).child("name").getValue(String.class);//取得收款者姓名

                TextView to_someone = (TextView) findViewById(R.id.tosomeone);
                to_someone.setText("您將轉帳給:" + get_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

