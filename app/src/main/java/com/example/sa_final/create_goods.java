package com.example.sa_final;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class create_goods extends AppCompatActivity {
    private Button btnregister,btnbacktologin;
    private EditText edtmoney, edtintro, edtname,edtnumber;
    private FirebaseAuth mAuth;
    Button ch;
    ImageView img;
    Good Good;
    long Time=System.currentTimeMillis();
    StorageReference mStorageRef;
    DatabaseReference ContactsRef;

    public Uri imguri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("設置商品");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_goods);
        mStorageRef= FirebaseStorage.getInstance().getReference("GImages");
        ContactsRef = FirebaseDatabase.getInstance().getReference("Good");
        ch=(Button)findViewById(R.id.btn_choose);
        img=(ImageView)findViewById(R.id.imagege);
        edtmoney = (EditText)findViewById(R.id.money);
        edtintro = (EditText)findViewById(R.id.goods_intro);
        edtname = (EditText)findViewById(R.id.name);
        edtnumber=(EditText)findViewById(R.id.month);
        btnbacktologin = (Button)findViewById(R.id.btn_previous);
        btnregister = (Button) findViewById(R.id.btn_OK);
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
                    String number=edtnumber.getText().toString();
                    String money = edtmoney.getText().toString();
                    String intro = edtintro.getText().toString();
                    String name = edtname.getText().toString();
                    if(money.matches("")||intro.matches("")||name.matches("")||number.matches("")) {
                        new AlertDialog.Builder(create_goods.this)
                                .setMessage("請輸入正確的資料")
                                .setPositiveButton("確定", null)
                                .show();
                        try {
                            Fileuploader();
                        }catch (Exception e){
                            Log.v("ss","vv"+e);
                        }
                    }
                    else{
                        String imageid;
                        String uid = mAuth.getCurrentUser().getUid();//取得使用者UID
                          imageid=Time+"%2C"+getExtension(imguri);
                        Good contact1 = new Good(name, intro, money,imageid,uid,number);
                        //將contact1放人子目錄
                        ContactsRef.child(name).setValue(contact1);

                        new AlertDialog.Builder(create_goods.this)
                                .setMessage("設置成功")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                                    }
                }
                else{
                    new AlertDialog.Builder(create_goods.this)
                            .setMessage("請選擇圖片")
                            .setPositiveButton("確定", null)
                            .show();
                }

            }
        });
        btnbacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(create_goods.this, Home.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
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
