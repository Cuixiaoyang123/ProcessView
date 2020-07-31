package com.example.test.shundemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by DreamLost on 2020/7/9 at 21:35
 * Description: 使用矩阵控制图片移动、缩放、旋转
 */

public class ProcessBox extends View{
    public interface BoxCallBack {
        public void onClickBoxTL(ProcessBox box);
    }
    BoxCallBack boxCallBack = null;

    private static final String TAG = "ProcessBox";

    private Context context ;
    private int pic; //背景图片
    private static ArrayList<Integer> picList  = new ArrayList<>(
            Arrays.asList(R.drawable.green, R.drawable.board, R.drawable.paper, R.drawable.sky, R.drawable.background)
    );
    private Bitmap mainBmp , controlBmp;
    private int mainBmpWidth , mainBmpHeight , controlBmpWidth , controlBmpHeight ;
    private Matrix matrix ;
    private float[] srcPs;
    public float[] dstPs;
    private String key;
    private String title;
    private RectF srcRect , dstRect, titleRect ;
    private Paint paint, paintRect, paintFrame, paintTitle;
    private float deltaX = 0, deltaY = 0; //位移值
    private float scaleValue = 1; //缩放值
    private Point lastPoint ;
    private Point prePivot , lastPivot;
    private float preDegree , lastDegree ;
    private short currentSelectedPointindex;        //当前操作点击点
    private Point symmetricPoint  = new Point();    //当前操作点对称点

    /**
     * 图片操作类型
     */
    public static final int OPER_DEFAULT = -1;      //默认
    public static final int OPER_TRANSLATE = 0;     //移动
    public static final int OPER_SCALE = 1;         //缩放
    public static final int OPER_ROTATE = 2;        //旋转
    public static final int OPER_SELECTED = 3;      //选择
    public static final int OPER_LINE = 4;          //引线
    public int lastOper = OPER_DEFAULT;

    /* 图片控制点
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     */
    public static final int CTR_NONE = -1;
    public static final int CTR_LEFT_TOP = 0;
    public static final int CTR_MID_TOP = 1;
    public static final int CTR_RIGHT_TOP = 2;
    public static final int CTR_RIGHT_MID = 3;
    public static final int CTR_RIGHT_BOTTOM = 4;
    public static final int CTR_MID_BOTTOM = 5;
    public static final int CTR_LEFT_BOTTOM = 6;
    public static final int CTR_LEFT_MID = 7;
    public static final int CTR_MID_MID = 8;
    public static final int CTR_TITLE = 9;


    public int current_ctr = CTR_NONE;
    public ArrayList<ProcessDirectionLine> nextLine;

    public ProcessBox(Context context){
        this(context, 0, 0);
    }

    public ProcessBox(Context context,int x, int y){
        super(context);
        this.context = context;
        initData(x,y);
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int index) {
        if (index>=0 && index <picList.size()) {
            this.pic = picList.get(index);
            mainBmp = BitmapFactory.decodeResource(this.context.getResources(),pic);
            invalidate();
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    /* 设置标题 */
    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
            invalidate();
        } else {
            Toast.makeText(context, "标题不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化数据
     */
    private void initData(int x, int y){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int id = generateViewId();
            setId(id);
            title = "流程框标题"+id;
            key = "boxM" + id;
        }
        nextLine = new ArrayList<>(2);
        pic = R.drawable.background;
        mainBmp = BitmapFactory.decodeResource(this.context.getResources(),pic);
        controlBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.control16);
        mainBmpWidth = mainBmp.getWidth();
        mainBmpHeight = mainBmp.getHeight();
        controlBmpWidth = controlBmp.getWidth();
        controlBmpHeight = controlBmp.getHeight();

        srcPs = new float[]{
                0,0,
                mainBmpWidth/2,0,
                mainBmpWidth,0,
                mainBmpWidth,mainBmpHeight/2,
                mainBmpWidth,mainBmpHeight,
                mainBmpWidth/2,mainBmpHeight,
                0,mainBmpHeight,
                0,mainBmpHeight/2,
                mainBmpWidth/2,mainBmpHeight/2
        };
        dstPs = srcPs.clone();
        srcRect = new RectF(0, 0, mainBmpWidth, mainBmpHeight);
        dstRect = new RectF();
        titleRect = new RectF();

        matrix = new Matrix();
        matrix.postTranslate(x,y);

        prePivot = new Point(mainBmpWidth/2, mainBmpHeight/2);
        lastPivot = new Point(mainBmpWidth/2, mainBmpHeight/2);

        lastPoint = new Point(0,0);

        paint = new Paint();

        paintRect = new Paint();
        paintRect.setColor(Color.LTGRAY);
        paintRect.setAlpha(100);
        paintRect.setAntiAlias(true);

        paintFrame = new Paint();
        paintFrame.setColor(Color.GREEN);
        paintFrame.setAntiAlias(true);


        paintTitle = new Paint();
        paintTitle.setColor(Color.BLACK);
        paintTitle.setTextAlign(Paint.Align.CENTER);
        paintTitle.setTextSize(40);
        paintTitle.setAntiAlias(true);

        setMatrix(OPER_DEFAULT);
    }

    public void setOnBoxTLClick(BoxCallBack callBack) {
        this.boxCallBack = callBack;
    }


    /**
     * 矩阵变换，达到图形平移的目的
     *
     */
    private void setMatrix(int operationType){
        switch (operationType) {
            case OPER_TRANSLATE:
                matrix.postTranslate(deltaX , deltaY);
                break;
            case OPER_SCALE:
                matrix.postScale(scaleValue, scaleValue, symmetricPoint.x, symmetricPoint.y);
                break;
            case OPER_ROTATE:
                matrix.postRotate(preDegree - lastDegree, dstPs[CTR_MID_MID * 2], dstPs[CTR_MID_MID * 2 + 1]);
                break;
            default:
                break;
        }

        matrix.mapPoints(dstPs, srcPs);
        matrix.mapRect(dstRect, srcRect);
    }

    private boolean isOnPic(int x , int y){
        if(dstRect.contains(x, y)){
            return true;
        }else
            return false;
    }


    private int getSimpleOperationType(MotionEvent event){

        int evX = (int)event.getX();
        int evY = (int)event.getY();
        int curOper = lastOper;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                current_ctr = isOnSimpleCP(evX, evY);
                Log.i("img", "current_ctr is "+current_ctr);
                if(current_ctr == CTR_RIGHT_MID ){
                    curOper = OPER_LINE;
                }else if(current_ctr == CTR_TITLE){
                    boxCallBack.onClickBoxTL(this);
                }else if(current_ctr != CTR_NONE || isOnPic(evX, evY)){
                    curOper = OPER_SELECTED;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(lastOper == OPER_SELECTED){
                    curOper = OPER_TRANSLATE;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (curOper == OPER_LINE) {
                    Log.d(TAG, "onTouchEvent: 引线终点("+evX+","+evY+")");

                }
                curOper = OPER_DEFAULT;
                break;
            default:
                break;
        }
        Log.d("img", "curOper is "+curOper);
        return curOper;

    }

    private int getOperationType(MotionEvent event){

        int evX = (int)event.getX();
        int evY = (int)event.getY();
        int curOper = lastOper;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                current_ctr = isOnCP(evX, evY);
                Log.i("img", "current_ctr is "+current_ctr);
                if(current_ctr != CTR_NONE || isOnPic(evX, evY)){
                    curOper = OPER_SELECTED;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(current_ctr > CTR_NONE && current_ctr < CTR_MID_MID ){
                    curOper = OPER_SCALE;
                }else if(current_ctr == CTR_MID_MID ){
                    curOper = OPER_ROTATE;
                }else if(lastOper == OPER_SELECTED){
                    curOper = OPER_TRANSLATE;
                }
                break;
            case MotionEvent.ACTION_UP:
                curOper = OPER_DEFAULT;
//                curOper = OPER_SELECTED;
                break;
            default:
                break;
        }
        Log.d("img", "curOper is "+curOper);
        return curOper;

    }

    /**
     * 判断点所在的简单控制点 最左边 还是 最右边
     * @param evx
     * @param evy
     * @return
     */
    private int isOnSimpleCP(int evx, int evy) {
        Rect rect = new Rect(evx - controlBmpWidth / 2, evy - controlBmpHeight / 2, evx + controlBmpWidth / 2, evy + controlBmpHeight / 2);
//        Rect rectOut = new Rect(evx - mainBmpWidth / 2, evy - mainBmpHeight / 2, evx + mainBmpWidth / 2, evy + mainBmpHeight / 2);
        int res = 0 ;

        if (rect.contains((int) dstPs[6], (int) dstPs[7])) { //控制点位于最右边
            return CTR_RIGHT_MID;
        } else if (rect.contains((int) dstPs[14], (int) dstPs[15])) { //控制点位于最左边
            return CTR_LEFT_MID;
        } else if (titleRect.contains(evx, evy)) { //控制点位于标题上
            return CTR_TITLE;
        }else {
            return CTR_NONE;
        }
    }

    /**
     * 判断点所在的控制点
     * @param evx
     * @param evy
     * @return
     */
    private int isOnCP(int evx, int evy) {
        Rect rect = new Rect(evx - controlBmpWidth / 2, evy - controlBmpHeight / 2, evx + controlBmpWidth / 2, evy + controlBmpHeight / 2);
//        Rect rectOut = new Rect(evx - mainBmpWidth / 2, evy - mainBmpHeight / 2, evx + mainBmpWidth / 2, evy + mainBmpHeight / 2);
        int res = 0 ;
        for (int i = 0; i < dstPs.length; i+=2) {
            if(rect.contains((int)dstPs[i], (int)dstPs[i+1])){
                return res ;
            }
            ++res ;
        }
        return CTR_NONE;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evX = (int)event.getX();
        int evY = (int)event.getY();

        int operType = OPER_DEFAULT;
        operType = getSimpleOperationType(event);

        switch (operType) {
            case OPER_DEFAULT:
                lastOper = OPER_DEFAULT;
                return false;
            case OPER_TRANSLATE:
                translate(evX, evY);
                break;
            case OPER_SCALE:
                scale(event);
                break;
            case OPER_ROTATE:
                rotate(event);
                break;
            case OPER_LINE:
                Log.d(TAG, "onTouchEvent: 引线起点("+evX+","+evY+")");
                return false;
            default:
                break;
        }

        lastPoint.x = evX;
        lastPoint.y = evY;

        lastOper = operType;
        invalidate();//重绘
        return true;
    }

    public void translateX(int deltaX) {
        this.deltaX = deltaX;
        this.deltaY = 0;
        setMatrix(OPER_TRANSLATE);
    }

    /**
     * 移动
     * @param evx
     * @param evy
     * @author zhang_jin1
     */
    private void translate(int evx , int evy){

        prePivot.x += evx - lastPoint.x;
        prePivot.y += evy -lastPoint.y;

        deltaX = prePivot.x - lastPivot.x;
        deltaY = prePivot.y - lastPivot.y;

        lastPivot.x = prePivot.x;
        lastPivot.y = prePivot.y;

        setMatrix(OPER_TRANSLATE); //设置矩阵

    }

    /**
     * 缩放
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     */
    private void scale(MotionEvent event) {

        int pointIndex = current_ctr*2 ;

        float px = dstPs[pointIndex];
        float py = dstPs[pointIndex+1];

        float evx = event.getX();
        float evy = event.getY();

        float oppositeX = 0 ;
        float oppositeY = 0 ;
        if(current_ctr<4 && current_ctr >= 0){
            oppositeX = dstPs[pointIndex+8];
            oppositeY = dstPs[pointIndex+9];
        }else if(current_ctr >= 4){
            oppositeX = dstPs[pointIndex-8];
            oppositeY = dstPs[pointIndex-7];
        }
        float temp1 = getDistanceOfTwoPoints(px,py,oppositeX,oppositeY);
        float temp2 = getDistanceOfTwoPoints(evx,evy,oppositeX,oppositeY);

        this.scaleValue = temp2 / temp1 ;
        symmetricPoint.x = (int) oppositeX;
        symmetricPoint.y = (int)oppositeY;

        Log.i("img", "scaleValue is "+scaleValue);
        setMatrix(OPER_SCALE);
    }

    /**
     * 旋转图片
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     */
    private void rotate(MotionEvent event) {

        if(event.getPointerCount() == 2){
            preDegree = computeDegree(new Point((int)event.getX(0), (int)event.getY(0)), new Point((int)event.getX(1), (int)event.getY(1)));
        }else{
            preDegree = computeDegree(new Point((int)event.getX(), (int)event.getY()), new Point((int)dstPs[16], (int)dstPs[17]));
        }
        setMatrix(OPER_ROTATE);
        lastDegree = preDegree;
    }


    /**
     * 计算两点与垂直方向夹角
     * @param p1
     * @param p2
     * @return
     */
    public float computeDegree(Point p1, Point p2){
        float tran_x = p1.x - p2.x;
        float tran_y = p1.y - p2.y;
        float degree = 0.0f;
        float angle = (float)(Math.asin(tran_x/Math.sqrt(tran_x*tran_x + tran_y* tran_y))*180/Math.PI);
        if(!Float.isNaN(angle)){
            if(tran_x >= 0 && tran_y <= 0){//第一象限
                degree = angle;
            }else if(tran_x <= 0 && tran_y <= 0){//第二象限
                degree = angle;
            }else if(tran_x <= 0 && tran_y >= 0){//第三象限
                degree = -180 - angle;
            }else if(tran_x >= 0 && tran_y >= 0){//第四象限
                degree = 180 - angle;
            }
        }
        return degree;
    }

    /**
     * 计算两个点之间的距离
     * @param p1
     * @param p2
     * @return
     */
    private float getDistanceOfTwoPoints(Point p1, Point p2){
        return (float)(Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }

    private float getDistanceOfTwoPoints(float x1,float y1,float x2,float y2){
        return (float)(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void onDraw(Canvas canvas){
//        if (getPaddingLeft() != 0 || getPaddingTop() != 0) {
//            matrix.postTranslate(getPaddingLeft(), getPaddingTop());
//        }
        drawBackground(canvas);//绘制背景和标题,以便测试矩形的映射
        canvas.drawBitmap(mainBmp, matrix, paint);//绘制主图片
//        drawFrame(canvas);//绘制边框,以便测试点的映射
        drawSimpleControlPoints(canvas);//绘制控制点图片
    }

    private void drawBackground(Canvas canvas){
        float titleY = dstPs[1] ,titleX = dstPs[16];
        for (int i = 1; i <= dstPs.length; i += 4) {
            titleY = Math.min(titleY, dstPs[i]);
        }
        float v = paintTitle.measureText(title);//title宽度
        titleRect.set(titleX-v/2,titleY-controlBmpHeight/2-40,titleX+v/2,titleY-controlBmpHeight/2+10);
        canvas.drawText(title, titleX, titleY - controlBmpHeight / 2, paintTitle);
        canvas.drawRect(titleRect,paintRect);
        canvas.drawRect(dstRect, paintRect);


    }

    private void drawFrame(Canvas canvas){
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[4], dstPs[5], paintFrame);
        canvas.drawLine(dstPs[4], dstPs[5], dstPs[8], dstPs[9], paintFrame);
        canvas.drawLine(dstPs[8], dstPs[9], dstPs[12], dstPs[13], paintFrame);
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[12], dstPs[13], paintFrame);
    }

    private void drawControlPoints(Canvas canvas){

        for (int i = 0; i < dstPs.length; i += 2) {
            canvas.drawBitmap(controlBmp, dstPs[i]-controlBmpWidth/2, dstPs[i+1]-controlBmpHeight/2, paint);
        }

    }

    private void drawSimpleControlPoints(Canvas canvas){

        canvas.drawBitmap(controlBmp, dstPs[6]-controlBmpWidth/2, dstPs[7]-controlBmpHeight/2, paint);
        canvas.drawBitmap(controlBmp, dstPs[14]-controlBmpWidth/2, dstPs[15]-controlBmpHeight/2, paint);

    }

}
