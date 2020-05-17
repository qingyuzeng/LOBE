package com.example.sa_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class register extends AppCompatActivity {

    private Button btnregister,btnbacktologin;
    private EditText edtnickname;
    private  TextView edtephone;
    private FirebaseAuth mAuth;
    private RadioGroup who_group;
    private RadioButton radio_member;
    private RadioButton radio_firm;
    private String str_who;
    Button ch;
    ImageView img;
    Member member;
    long Time=System.currentTimeMillis();
    StorageReference mStorageRef;
    DatabaseReference ContactsRef;

    public Uri imguri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("註冊");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        ContactsRef = FirebaseDatabase.getInstance().getReference("member");
        ch=(Button)findViewById(R.id.btn_choose);
        img=(ImageView)findViewById(R.id.imagege);
        edtephone = (TextView)findViewById(R.id.phone);
        edtnickname = (EditText)findViewById(R.id.name);
        btnbacktologin = (Button)findViewById(R.id.btn_previous);
        btnregister = (Button) findViewById(R.id.btn_register);
        final Intent intent = getIntent();
        final String phonenumber = getIntent().getStringExtra("phonenumber");
      ;
        edtephone.setText(phonenumber);
        ch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Filechoose();
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img.getDrawable()!=null){
                    Fileuploader();
                   String phone1=phonenumber;

                    String nickname = edtnickname.getText().toString();
                    if(phone1.matches("")||nickname.matches("")) {
                        new AlertDialog.Builder(register.this)
                                .setMessage("請輸入正確的資料")
                                .setPositiveButton("確定", null)
                                .show();
                        try {
                            Fileuploader();
                        }catch (Exception e){
                            Log.v("ss","vv"+e);
                        }
                    }
                    else {


                        EditText et_name = (EditText) findViewById(R.id.name);//取得使用者輸入的名稱
                        String name = et_name.getText().toString();//取得使用者輸入的名稱


                        String money;//給使用者樂幣
                        String uid = mAuth.getCurrentUser().getUid();//取得使用者UID
                        String who = str_who;

                        String imageid;
                        imageid = Time + "%2C" + getExtension(imguri);
                        if (str_who.matches("一般會員")) {
                            money = "0";
                            //連接資料庫

                            //目錄 /ex1

                            //將資料放入member
                            Member contact1 = new Member(name, phone1, money, who, imageid);
                            //將contact1放人子目錄
                            ContactsRef.child(uid).setValue(contact1);

                            new AlertDialog.Builder(register.this)
                                    .setMessage("註冊會員成功")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        } else if (str_who.matches("廠商")) {
                            money = "1000";
                            //連接資料庫
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //目錄 /ex1
                            DatabaseReference ContactsRef = database.getReference("member");
                            //將資料放入member
                            Member contact1 = new Member(name, phone1, money, who, imageid);
                            //將contact1放人子目錄
                            ContactsRef.child(uid).setValue(contact1);

                            new AlertDialog.Builder(register.this)
                                    .setMessage("註冊廠商成功")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                            Intent intent=new Intent(register.this,Home.class);
                            startActivity(intent);
                        } else {
                            new AlertDialog.Builder(register.this)
                                    .setMessage("請選擇註冊類別")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                            Intent intent=new Intent(register.this,Home.class);
                            startActivity(intent);
                        }
                    }
                                    }


                else{
                    new AlertDialog.Builder(register.this)
                            .setMessage("請選擇圖片")
                            .setPositiveButton("確定", null)
                            .show();
                }

            }
        });

        mAuth = FirebaseAuth.getInstance();
        who_group = (RadioGroup)findViewById(R.id.radio_group);
        radio_member = (RadioButton) findViewById(R.id.radio_m);
        radio_firm = (RadioButton) findViewById(R.id.radio_c);
        str_who = radio_member.getText().toString();

        who_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_member.getId() == checkedId) {
                    str_who = radio_member.getText().toString();
                    show_who();
                } else if (radio_firm.getId() == checkedId) {
                    str_who = radio_firm.getText().toString();
                    show_who();
                }
            }
        });
    }

    //顯示你選誰的settext改成空直
    private void show_who() { //顯示辦會員或是廠商的字
        TextView show_test = (TextView) findViewById(R.id.textView2);
        show_test.setText("");
    }


    private  String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void Fileuploader(){
        StorageReference Ref=mStorageRef.child(Time+","+getExtension(imguri));
        Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }
    private void Filechoose() {
        Intent intent = new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imguri=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
