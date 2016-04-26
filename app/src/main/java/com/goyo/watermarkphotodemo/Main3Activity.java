package com.goyo.watermarkphotodemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main3Activity extends AppCompatActivity {

    private Bitmap icon;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        image = (ImageView) this.findViewById(R.id.ImageView);

        Bitmap mark = BitmapFactory.decodeResource(this.getResources(), R.mipmap.icon);
        Bitmap photo = BitmapFactory.decodeResource(this.getResources(), R.mipmap.text);

        Bitmap a = createBitmap(photo,mark);
        image.setImageBitmap(a);
        saveMyBitmap(a);
    }

    public void btnClick(View view) {
        String str = "PICC要写的文字";


        Bitmap photo = BitmapFactory.decodeResource(this.getResources(), R.mipmap.pic00);
        int width = photo.getWidth(), hight = photo.getHeight();
        System.out.println("宽"+width+"高"+hight);
        icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些

        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
        canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
        textPaint.setTextSize(20.0f);//字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
        textPaint.setColor(Color.RED);//采用的颜色
        //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
        canvas.drawText(str, 20, 26, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        image.setImageBitmap(icon);
        saveMyBitmap(icon);
    }
    private Bitmap createBitmap( Bitmap src, Bitmap watermark )

    {

        String tag = "createBitmap";

        // Log.d( tag, "create a new bitmap" );

        if( src == null )

        {

            return null;

        }

        int w = src.getWidth();

        int h = src.getHeight();

        int ww = watermark.getWidth();

        int wh = watermark.getHeight();

        //create the new blank bitmap

        Bitmap newb = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );
        //创建一个新的和SRC长度宽度一样的位图

        Canvas cv = new Canvas( newb );

        //draw src into

        cv.drawBitmap( src, 0, 0, null );//在 0，0坐标开始画入src

        //draw watermark into

        cv.drawBitmap( watermark, w - ww + 5, h - wh + 5, null );//在src的右下角画入水印

        //save all clip

        cv.save( Canvas.ALL_SAVE_FLAG );//保存

        //store

        cv.restore();//存储

        return newb;

    }



//保存图片到data下面



    public void saveMyBitmap(Bitmap bmp){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("image1.jpg", Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    private void doSomeThing(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        int width = options.outWidth;

        int inSampleSize = 1;
        int reqHeight=800;
        int reqWidth=480;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

        //在内存中创建bitmap对象，这个对象按照缩放大小创建的
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] b = baos.toByteArray();

    }

}
