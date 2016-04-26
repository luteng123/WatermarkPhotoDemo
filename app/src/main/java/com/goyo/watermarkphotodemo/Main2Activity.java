package com.goyo.watermarkphotodemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class Main2Activity extends AppCompatActivity {
    //自定义变量
    private Button openImageBn;           //打开图片
    private Bitmap bmp;                   //原始图片
    private TextView pathText;            //路径TextView
    private String path;                  //存储图片路径
    private ImageView imageShow;          //显示图片
    private final int IMAGE_CODE = 0;     //打开图片
    //触屏缩放图片
    private static final int NONE = 0;     //初始状态
    private static final int DRAG = 1;     //拖动
    private static final int ZOOM = 2;     //缩放
    private int mode = NONE;               //当前事件
    private float oldDist;
    private PointF startPoint = new PointF();
    private PointF middlePoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    //新增按钮
    private Button wordAddBn;              //添加文字
    private Button changeImageBn;          //缩放图片
    private Button drawImageBn;            //绘制图片
    //图片处理时显示备份
    private Bitmap alteredBitmap;          //图片
    private Canvas canvas;                 //画布
    private Paint paint;                   //画刷
    private RelativeLayout layout;
    //标识变量  1-显示图片 2-添加文字 3-缩放图片 4-画图
    private int flagOnTouch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        openImageBn = (Button) findViewById(R.id.button1);
        pathText = (TextView) findViewById(R.id.textView1);
        imageShow = (ImageView) findViewById(R.id.imageView1);
        //打开图片
        openImageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_CODE);
            }
        });

        //缩放图片 点击按钮"缩放"
        changeImageBn = (Button) findViewById(R.id.button3);
        changeImageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagOnTouch = 3; //缩放
            }
        });
//绘制画图 点击按钮"绘制"
        drawImageBn = (Button) findViewById(R.id.button4);
        drawImageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagOnTouch = 4; //绘图
                //画图 图片移动至(0,0) 否则绘图线与手指存在误差
                matrix = new Matrix();
                matrix.postTranslate(0, 0);
                imageShow.setImageMatrix(matrix);
                imageShow.setImageBitmap(alteredBitmap);
                canvas.drawBitmap(bmp, matrix, paint);
            }
        });



        imageShow.setOnTouchListener(new View.OnTouchListener() {
            //设置两个点 按下坐标(downx, downy)和抬起坐标(upx, upy)
            float downx = 0;
            float downy = 0;
            float upx = 0;
            float upy = 0;
            //触摸事件
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                if(flagOnTouch == 1) { //显示图片
                    return true;
                }
                else if(flagOnTouch == 3)  { //图片缩放
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN: //手指按下
                            savedMatrix.set(matrix);
                            startPoint.set(event.getX(), event.getY());
                            mode = DRAG;
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = spacing(event); //如果两点距离大于10 多点模式
                            if (oldDist > 10f) {
                                savedMatrix.set(matrix);
                                midPoint(middlePoint, event);
                                mode = ZOOM;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) { //拖动
                                matrix.set(savedMatrix);
                                matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                            } else if (mode == ZOOM) { //缩放
                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
                                }
                            }
                            break;
                    } //end switch
                    view.setImageMatrix(matrix);
                    return true;
                }
                else if(flagOnTouch == 2) { //图片文字添加
                    return true;
                }
                else if(flagOnTouch == 4) { //绘制图像
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downx = event.getX();
                            downy = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            upx = event.getX();
                            upy = event.getY();
                            canvas.drawLine(downx, downy, upx, upy, paint);
                            imageShow.invalidate();
                            downx = upx;
                            downy = upy;
                            break;
                        case MotionEvent.ACTION_UP:
                            upx = event.getX();
                            upy = event.getY();
                            canvas.drawLine(downx, downy, upx, upy, paint);
                            imageShow.invalidate();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                        default:
                            break;
                    }
                    return true;
                }
                else {
                    return false;
                }
            }  //end  onTouch
            //两点距离
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }
            //两点中点
            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });


    }
    //打开图片
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==IMAGE_CODE) {
            Uri imageFileUri = data.getData();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;    //手机屏幕水平分辨率
            int height = dm.heightPixels;   //手机屏幕垂直分辨率
            try {
                //标识变量=1 图片显示
                flagOnTouch = 1;
                //载入图片尺寸大小没载入图片本身 true
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
                int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
                int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);
                //inSampleSize表示图片占原图比例 =1表示原图
                if(heightRatio>1&&widthRatio>1) {
                    if(heightRatio>widthRatio) {
                        bmpFactoryOptions.inSampleSize = heightRatio;
                    }
                    else {
                        bmpFactoryOptions.inSampleSize = widthRatio;
                    }
                }
                //图像真正解码 false
                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
                //imageShow.setImageBitmap(bmp);
                //显示文件路径
                String[] filePathColumn= {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageFileUri, filePathColumn, null, null, null);
                cursor.moveToFirst(); //将光标移至开头
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]); //获得图片索引值
                path = cursor.getString(columnIndex);
                cursor.close();
                pathText.setText("path="+path);
                //加载备份图片
                alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
                        .getHeight(), bmp.getConfig());
                canvas = new Canvas(alteredBitmap);  //画布
                paint = new Paint(); //画刷
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(5);
                paint.setTextSize(30);
                paint.setTypeface(Typeface.DEFAULT_BOLD);  //无线粗体
                matrix = new Matrix();
                canvas.drawBitmap(bmp, matrix, paint);
                imageShow.setImageBitmap(alteredBitmap);

            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }  //end if
    }


}
