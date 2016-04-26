package com.goyo.watermarkphotodemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileOutputStream;

public class Main22Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView waterPhoto;
    private Button btnPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);

        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        waterPhoto = (ImageView) findViewById(R.id.waterPhoto);

        btnPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 998);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 998 && resultCode == Activity.RESULT_OK && data != null) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                return;
            }
            Bitmap bitmap = data.getParcelableExtra("data");
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(newBitmap);
            // 往位图中开始画入src原始图片
            mCanvas.drawBitmap(bitmap, 0, 0, null);
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(16);
            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName,
                        Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText("11", width - 55, height - 55, textPaint);
            mCanvas.drawText("22", 55, 55, textPaint);
            mCanvas.drawText("33", 55, height - 55, textPaint);
            mCanvas.save(Canvas.ALL_SAVE_FLAG);
            mCanvas.restore();




            waterPhoto.setImageBitmap(newBitmap);

//            cv.drawBitmap( watermark, w - ww + 5, h - wh + 5, null );//在src的右下角画入水印
            }
        }
    }
