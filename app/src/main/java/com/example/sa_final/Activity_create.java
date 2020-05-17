package com.example.sa_final;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;



public class Activity_create extends AppCompatActivity {
    private EditText acname,accontent;
    private TextView acunit;
    private FirebaseAuth mAuth;
    public String name;
    DatabaseReference ActivityRef;
    activity activity;
    private Button doSetDate;
    private Button doSetTime;
    private TextView textDate;
    private TextView textTime;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Calendar mCalendar = null; // 日曆
    public String Date;
    public String Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("新增活動");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        acname = (EditText)findViewById(R.id.activity_name);
        accontent = (EditText)findViewById(R.id.activity_content);
        acunit=(TextView)findViewById(R.id.activity_unit);
        activity=new activity();
        mCalendar = Calendar.getInstance();
        doFindView();
        GregorianCalendar calendar = new GregorianCalendar();

        // 實作DatePickerDialog的onDateSet方法，設定日期後將所設定的日期show在textDate上
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            //將設定的日期顯示出來
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                String Date= year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                setDate(Date);
                textDate.setText(year + "/" + (monthOfYear+1) + "/" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // 實作TimePickerDialog的onTimeSet方法，設定時間後將所設定的時間show在textTime上
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            //將時間轉為12小時製顯示出來
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time= (hourOfDay > 12 ? "早上" : "下午")+(hourOfDay > 12 ? hourOfDay - 12 : hourOfDay)
                        + ":" + minute + " " ;
                setTime(time);
                textTime.setText(time);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE),
                false);

        ActivityRef= FirebaseDatabase.getInstance().getReference("activity");
        //有資料未填寫->創建失敗
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Date= getDate();

                String time= getTime();

                String finaltime=Date+time;
                String activity_name = acname.getText().toString();
                String activity_date =finaltime;
                String activity_content = accontent.getText().toString();
                String activity_unit = acunit.getText().toString();
                if(activity_name.matches("")||activity_date.matches("")||activity_content.matches("")||activity_unit.matches("")){
                    new androidx.appcompat.app.AlertDialog.Builder(Activity_create.this)
                            .setMessage("請輸入完整資料")
                            .setPositiveButton("確定", null)
                            .show();
                    try {

                    }catch (Exception e){
                        Log.v("ss","vv"+e);
                    }
                }
                else {
                    activity.setName(acname.getText().toString().trim());
                    activity.setDate(finaltime);
                    activity.setContent(accontent.getText().toString().trim());
                    activity.setUnit(acunit.getText().toString().trim());
                    ActivityRef.push().setValue(activity);
                    new AlertDialog.Builder(Activity_create.this)
                            .setMessage("創建成功")
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
                intent.setClass(Activity_create.this, Home.class);
                startActivity(intent);
            }
        });
        getData();
    }

    private void getData() {
        mAuth = FirebaseAuth.getInstance();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("member");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid=mAuth.getCurrentUser().getUid();//取得使用者UID
                String who = dataSnapshot.child(uid).child("who").getValue(String.class);
                String name=dataSnapshot.child(uid).child("name").getValue(String.class);

                acunit = (TextView)findViewById(R.id.activity_unit);
                TextView show_name = (TextView) findViewById(R.id.activity_unit);
                show_name.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    // 連接layout上的物件
    public void doFindView() {
        doSetDate = (Button) findViewById(R.id.buttonDate);
        doSetTime = (Button) findViewById(R.id.buttonTime);
        textDate = (TextView) findViewById(R.id.datetext);
        textTime = (TextView) findViewById(R.id.timetext);
    }

    // setDate Button onClick 顯示日期設定視窗
    public void setDate(View v) {
        datePickerDialog.show();
    }

    // setTime Button onClick 顯示時間設定視窗
    public void setTime(View v) {
        timePickerDialog.show();
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
