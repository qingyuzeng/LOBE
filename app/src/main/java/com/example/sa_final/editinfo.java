package com.example.sa_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class editinfo extends AppCompatActivity {
    private Button btn_editchoose,concern_edit,btn_previous;
    private ImageButton imageButton1;
    private TextView edit_name;
    private EditText edittext_name;
    private FirebaseAuth mAuth;
    ImageView img;
    long Time=System.currentTimeMillis();
    StorageReference mStorageRef;
    DatabaseReference ContactsRef;
    public Uri imguri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("修改資料");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editinfo);

        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        ContactsRef = FirebaseDatabase.getInstance().getReference("member");
        btn_editchoose=(Button)findViewById(R.id.btn_editchoose);
        img=(ImageView)findViewById(R.id.edit_imagege);
        imageButton1=(ImageButton)findViewById(R.id.revisename);
        edit_name = (TextView) findViewById(R.id.edit_name);
        edittext_name=(EditText)findViewById(R.id.edittext_name);
        btn_previous = (Button)findViewById(R.id.btn_previous);
        concern_edit = (Button) findViewById(R.id.concern_edit);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(editinfo.this, Home.class);
                startActivity(intent);
            }
        });
        getData();
    }
    private void getData() {
        mAuth = FirebaseAuth.getInstance();
        //   if(mAuth.getCurrentUser())
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("member");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = mAuth.getCurrentUser().getUid();//取得使用者UID
                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                String imageid = dataSnapshot.child(uid).child("imageid").getValue(String.class);
                TextView show_name = (TextView) findViewById(R.id.edit_name);
                show_name.setText(name);
                final int[] nameButton = {0};
                final int[] imageButton = {0};
                img=(ImageView)findViewById(R.id.edit_imagege);
                if(!isDestroy((Activity)editinfo.this)){
                    Glide.with(editinfo.this).load("https://firebasestorage.googleapis.com/v0/b/cycusa-71b70.appspot.com/o/Images%2F"+imageid+"?alt=media").into(img);
                }
                imageButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_name.setVisibility(View.INVISIBLE);
                        edittext_name.setVisibility(View.VISIBLE);
                        nameButton[0] =1;
                    }
                });
                btn_editchoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Filechoose();
                        imageButton[0] =1;
                    }
                });
                concern_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = mAuth.getCurrentUser().getUid();//取得使用者UID
                        String name = edittext_name.getText().toString();//取得使用者輸入的名稱
                        if(imageButton[0] == 1){
                        String imageid = Time + "%2C" + getExtension(imguri);
                        Fileuploader();
                        ContactsRef.child(uid).child("imageid").setValue(imageid);
                        }
                        if(   nameButton[0] ==1){
                        ContactsRef.child(uid).child("name").setValue(name);
                        }

                        Toast.makeText(getApplicationContext(), "修改成功嚕！", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(editinfo.this, Home.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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
    public static boolean isDestroy(Activity activity) {
        if (activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
}