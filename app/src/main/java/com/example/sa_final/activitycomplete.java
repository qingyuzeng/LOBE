package com.example.sa_final;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class activitycomplete extends AppCompatActivity {
    private FirebaseAuth mAuth;
@Override
protected void onCreate(Bundle savedInstanceState) {
    setTitle("我的活動QRCODE");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activitycomple);
    getQRCode();
    Button btn_previous = (Button)findViewById(R.id.btn_previous);
    btn_previous.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(activitycomplete.this, Home.class);
            startActivity(intent);
        }
    });
}

    private void getQRCode() {
        mAuth = FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();//取得使用者UID
        ImageView ivCode = (ImageView) findViewById(R.id.img_complete);    //這行代表宣告ivCode即為介面中的imageView
        //TextView etContent = (TextView) findViewById(R.id.textView);    //這行代表宣告etContent即為介面中的textView

        BarcodeEncoder encoder = new BarcodeEncoder();                  //宣告他人寫好的套件
        try {
            Bitmap bit = encoder.encodeBitmap(uid             //etContent.getText().toString()
                    , BarcodeFormat.QR_CODE, 1000, 1000);         //這個就是說將etContent（也就是textView）的內容文字轉為字串
            ivCode.setImageBitmap(bit);                                         //那這個etContent.getText().toString()就是代表我前面textView裡面放什麼就生產啥的qr
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
