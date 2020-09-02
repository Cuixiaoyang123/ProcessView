package com.example.test.shundemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by DreamLost on 2020/7/31 at 11:28
 * Description: 贝塞尔曲线View
 */
public class BeiView extends View {

    private Path path;
    private Paint paint;
    private Point startPoint,endPoint;
    private Point assistPoint1,assistPoint2;

    public BeiView(Context context) {
        super(context);
        init();
    }

    public BeiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BeiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BeiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
        startPoint = new Point();
        startPoint.set(300,500);
        endPoint = new Point();
        endPoint.set(500,1000);
        assistPoint1 = new Point();
        assistPoint1.set(400, 500);
        assistPoint2 = new Point();
        assistPoint2.set(400, 1000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.moveTo(startPoint.x, startPoint.y);
        path.cubicTo(assistPoint1.x, assistPoint1.y, assistPoint2.x, assistPoint2.y, endPoint.x, endPoint.y);
        canvas.drawPath(path, paint);

        canvas.drawPoint(assistPoint1.x, assistPoint1.y, paint);
        canvas.drawPoint(assistPoint2.x, assistPoint2.y, paint);

        //画线
        canvas.drawLine(startPoint.x, startPoint.y, assistPoint1.x, assistPoint1.y, paint);
        canvas.drawLine(endPoint.x, endPoint.y, assistPoint2.x, assistPoint2.y, paint);
        canvas.drawLine(assistPoint1.x, assistPoint1.y, assistPoint2.x, assistPoint2.y, paint);


        path.reset();
        startPoint.set(300, 0);
        assistPoint1.set(300, 300);
        endPoint.set(600, 300);

        path.moveTo(startPoint.x, startPoint.y);
        path.quadTo(assistPoint1.x, assistPoint1.y, endPoint.x, endPoint.y);
        canvas.drawPath(path, paint);

        canvas.drawPoint(assistPoint1.x, assistPoint1.y, paint);
//
    }
}
