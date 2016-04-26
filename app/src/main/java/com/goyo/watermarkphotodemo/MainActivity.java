package com.goyo.watermarkphotodemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.img);
    }

    public void btnPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = data.getParcelableExtra("data");
        Bitmap mark = BitmapFactory.decodeResource(getResources(), R.mipmap.mark);
        Bitmap newBitmap = createBitmap(bitmap,mark,"123");
        imageView.setImageBitmap(newBitmap);


    }
    public static Bitmap createBitmap(Bitmap src, Bitmap waterMak, String
            title) {
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
//        int ww = waterMak.getWidth();
//        int wh = waterMak.getHeight();
//        Log.i("jiangqq", "w = " + w + ",h = " + h + ",ww = " + ww + ",wh = "
//                + wh);
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        // 在src的右下角添加水印
//        Paint paint = new Paint();
//        //paint.setAlpha(100);
//        mCanvas.drawBitmap(waterMak, w - ww - 5, h - wh - 5, paint);
        // 开始加入文字
        if (null != title) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(16);
            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName,
                    Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText(title, 25, 25, textPaint);
            }
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        return newBitmap;
        }

//    /**
//
//     　　* 把图片村保存在相应的文件当中
//     　　* @param pBitmap
//     　　* @param pPathName
//     　　*/
//    public static void saveFile(Bitmap pBitmap,String fileName) {
//        File file = new File("/sdcard/aaa");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String filePathName = file.getAbsolutePath() + "/" + fileName;
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(filePathName);
//            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            Log.i("jiangqq", "保存图片到sdcard卡成功.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
