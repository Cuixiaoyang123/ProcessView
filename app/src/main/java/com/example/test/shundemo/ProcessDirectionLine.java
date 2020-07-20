package com.example.test.shundemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by DreamLost on 2020/7/10 at 15:49
 * Description:
 */
public class ProcessDirectionLine extends View {
    public interface LineCallBack {
        public void onClickLine(float x, float y);
    }

    LineCallBack lineCallBack = null;

    private Context context;
    private Matrix matrixPoint, matrixArrow ;
    private Bitmap point, arrow;
    private float pointX,pointY,arrowX, arrowY;
    private RectF titleRect, srcRect1, dstRect1, srcRect2, dstRect2, srcRect3, dstRect3;
    private final int LINE_HALF_WIDTH = 20;  //线的三个折线的半宽
    private int arrowHeight;
    private Paint paint, rectPaint, linePaint, textPaint ;
    private String title;
    private ProcessBox nextBox,preBox;
    private int drawStyle = LINE_REAL;


    /* 划线的类型 */
    public static final int LINE_NONE = 0;    //默认
    public static final int LINE_TRY = 1;     //试划线
    public static final int LINE_REAL = 2;    //最终划线

    /* 线的起点 终点 */
    public static final int LINE_START = 0;     //起点
    public static final int LINE_END = 1;       //终点

    public ProcessDirectionLine(Context context) {
        super(context);
        this.context = context;
        initData();
    }

    public ProcessDirectionLine(Context context, float pointX, float pointY, float arrowX, float arrowY) {
        super(context);
        this.context = context;
        initData();
        this.pointX = pointX;
        this.pointY = pointY;
        this.arrowX = arrowX;
        this.arrowY = arrowY;
    }

    public ProcessBox getNextBox() {
        return nextBox;
    }

    public void setNextBox(ProcessBox nextBox) {
        this.nextBox = nextBox;
    }

    public ProcessBox getPreBox() {
        return preBox;
    }

    public void setPreBox(ProcessBox preBox) {
        this.preBox = preBox;
    }

    public String getTitle() {
        return title;
    }

    /* 设置标题 */
    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
            invalidate();
        }else {
            Toast.makeText(context, "标题不能为空", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 初始化数据
     */
    private void initData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int id = generateViewId();
            setId(id);
            title = "线段文本" + id;
        }
        matrixPoint = new Matrix();
        matrixArrow = new Matrix();
        point = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.line_point);
        arrow = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.line_arrow);
        arrowHeight = arrow.getHeight();

        srcRect1 = new RectF();
        srcRect2 = new RectF();
        srcRect3 = new RectF();
        titleRect = new RectF();

        paint = new Paint();//绘制起点终点img的Paint

        rectPaint = new Paint();
        rectPaint.setColor(Color.LTGRAY);
        rectPaint.setAlpha(100);
        rectPaint.setAntiAlias(true);

        textPaint = new Paint();//绘制title的Paint
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(35);
        textPaint.setAntiAlias(true);

        linePaint = new Paint();//绘制实线的Paint
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.SQUARE);
        linePaint.setColor(Color.GRAY);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5);
        linePaint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));

        pointX = 0;
        pointY = 0;
        arrowX = 0;
        arrowY = 0;
    }

    public void setOnLineClick(LineCallBack callBack){
        lineCallBack = callBack;
    }

    /**
     * 绘制带箭头的折线
     */
    protected void drawTria(float fromX, float fromY, float toX, float toY, int heigth, int bottom, Canvas canvas) {
        // heigth和bottom分别为三角形的高与底的一半,调节三角形大小
        float maxHeight = Math.abs( toX - fromX ) - 5;
        if ( heigth > maxHeight ) { //适配箭头长度超出线段长度的情况
            bottom = (int) ( bottom * maxHeight / heigth );
            heigth = (int) maxHeight;
        }
        //自动折中取折线 如下方式：
        //   o-----|
        //         |
        //         |----->
        canvas.drawLine(fromX, fromY, toX, toY, linePaint);
        float juli = (float) Math.sqrt((toX - fromX) * (toX - fromX)
                + (toY - fromY) * (toY - fromY));// 获取线段距离
        float juliX = toX - fromX;// 有正负，不要取绝对值
        float juliY = toY - fromY;// 有正负，不要取绝对值
        float dianX = toX - (heigth / juli * juliX);
        float dianY = toY - (heigth / juli * juliY);
        //终点的箭头
        Path path = new Path();
        path.moveTo(toX, toY);// 此点为三边形的起点
        path.lineTo(dianX + (bottom / juli * juliY), dianY
                - (bottom / juli * juliX));
        path.moveTo(toX, toY);// 此点为三边形的起点
        path.lineTo(dianX - (bottom / juli * juliY), dianY
                + (bottom / juli * juliX));
//        path.lineTo(dianX - (bottom / juli * juliY), dianY
//                + (bottom / juli * juliX));
//        path.close(); // 使这些点构成封闭的三边形
        canvas.drawPath(path, linePaint);

    }

    public void changeView(float x1, float y1, float x2, float y2) {
        pointX = x1;
        pointY = y1;
        arrowX = x2;
        arrowY = y2;
        invalidate();
    }

    /**
     *  判断是否手指在线上
     * @return
     */
    private boolean isOnLine(int x , int y){
        if(srcRect1.contains(x, y) || srcRect2.contains(x, y) || srcRect3.contains(x, y)){
            return true;
        }else
            return false;
    }
    /**
     *  判断是否手指在标题上
     * @return
     */
    private boolean isOnTitle(int x , int y){
        if(titleRect.contains(x, y)){
            return true;
        }else
            return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evX = (int)event.getX();
        int evY = (int)event.getY();
        if (!isOnTitle(evX, evY)) {//操作不在标题上
            return false;
        }
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isOnTitle(evX, evY)) {
                    //操作在标题上 修改文字
                    lineCallBack.onClickLine(evX,evY);
                    return false;
                }
                drawStyle = LINE_TRY;
                matrixPoint.postTranslate( evX - pointX, evY - pointY );
                matrixArrow.postTranslate( evX - arrowX, evY - arrowY );
                pointX = evX;
                pointY = evY;
                arrowX = evX;
                arrowY = evY;
                break;
            case MotionEvent.ACTION_MOVE:
                matrixArrow.postTranslate(evX-arrowX, evY-arrowY);
                arrowX = evX;
                arrowY = evY;
                break;
            case MotionEvent.ACTION_UP:
                drawStyle = LINE_REAL;
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        switch (drawStyle) {
            case LINE_TRY:
                canvas.drawBitmap(point,matrixPoint,paint);
                canvas.drawBitmap(arrow,matrixArrow,paint);
                break;
            case LINE_REAL:
                //自动折中取折线 如下方式：
                //   o-----|
                //         |
                //         |-----o
                generateEntity(pointX, pointY,arrowX, arrowY);
                float lineWidth = arrowX - pointX;
                canvas.drawLine(pointX, pointY, pointX+lineWidth/2, pointY, linePaint);
                canvas.drawLine(pointX+lineWidth/2, pointY, pointX+lineWidth/2, arrowY, linePaint);
                canvas.drawText(title,pointX+lineWidth/2, pointY/2+arrowY/2,textPaint);
                drawTria(pointX+lineWidth/2, arrowY, arrowX, arrowY, 50, 30,canvas);
                canvas.drawRect(titleRect, rectPaint);
//                canvas.drawRect(srcRect3, linePaint);
//                canvas.drawRect(srcRect2, linePaint);
//                canvas.drawRect(srcRect1, linePaint);
                break;
            default:
                break;
        }

    }

    private void generateEntity(float fromX, float fromY, float toX, float toY) {
        float v = textPaint.measureText(title);
        float centerX = fromX/2+toX/2,centerY = fromY/2+toY/2;
        titleRect.set(centerX-v/2,centerY-35,centerX+v/2,centerY+10);
        if (toX - fromX >= 0 && toY - fromY >= 0) {
            //第一象限
            srcRect1.set(fromX, fromY - LINE_HALF_WIDTH, fromX / 2 + toX / 2, fromY + LINE_HALF_WIDTH);
            srcRect2.set(fromX / 2 + toX / 2 - LINE_HALF_WIDTH, fromY, fromX / 2 + toX / 2 + LINE_HALF_WIDTH, toY);
            srcRect3.set(fromX / 2 + toX / 2, toY - LINE_HALF_WIDTH, toX, toY + LINE_HALF_WIDTH);
        }else if (toX - fromX >= 0 && toY - fromY < 0) {
            //第二象限
            srcRect1.set(fromX, fromY - LINE_HALF_WIDTH, fromX / 2 + toX / 2, fromY + LINE_HALF_WIDTH);
            srcRect2.set(fromX / 2 + toX / 2 - LINE_HALF_WIDTH, toY, fromX / 2 + toX / 2 + LINE_HALF_WIDTH, fromY);
            srcRect3.set(fromX / 2 + toX / 2, toY - LINE_HALF_WIDTH, toX, toY + LINE_HALF_WIDTH);
        } else if (toX - fromX < 0 && toY - fromY < 0) {
            //第三象限
            srcRect1.set(fromX / 2 + toX / 2, fromY - LINE_HALF_WIDTH,fromX, fromY + LINE_HALF_WIDTH);
            srcRect2.set(fromX / 2 + toX / 2 - LINE_HALF_WIDTH, toY, fromX / 2 + toX / 2 + LINE_HALF_WIDTH, fromY);
            srcRect3.set(toX,toY - LINE_HALF_WIDTH, fromX / 2 + toX / 2,  toY + LINE_HALF_WIDTH);
        } else {
            //第四象限
            srcRect1.set(fromX / 2 + toX / 2, fromY - LINE_HALF_WIDTH,fromX, fromY + LINE_HALF_WIDTH);
            srcRect2.set(fromX / 2 + toX / 2 - LINE_HALF_WIDTH, fromY, fromX / 2 + toX / 2 + LINE_HALF_WIDTH, toY);
            srcRect3.set(toX,toY - LINE_HALF_WIDTH, fromX / 2 + toX / 2,  toY + LINE_HALF_WIDTH);

        }


    }
}
