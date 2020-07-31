package com.example.test.shundemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by DreamLost on 2020/7/13 at 18:08
 * Description:
 */
public class ProcessLayout extends RelativeLayout implements ProcessDirectionLine.LineCallBack, ProcessBox.BoxCallBack {
    public static final String TAG = "ProcessLayout";

    private Context context;
    public LinkedHashMap<String,ProcessBox> boxMap;
    public LinkedHashMap<String,ProcessDirectionLine> lineMap;
    private Paint linePaint;
    private ProcessBox downBox, upBox;
    private float fromPointX, fromPointY, toPointX, toPointY, tempPointX, tempPointY;
    private static final int ACCURACY_RECOGNIZE = 60;  //手指的识别精度
    private int type;

    /* 图片控制点
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     */
    public static final int CTR_NONE = -1;
    public static final int CTR_RIGHT_MID = 3;
    public static final int CTR_LEFT_MID = 7;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_DRAW_LINE = 1;
    public static final int TYPE_TRANSLATE = 2;
    private Matrix matrix;


    public ProcessLayout(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ProcessLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ProcessLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProcessLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        boxMap = new LinkedHashMap<>();
        lineMap = new LinkedHashMap<>();

        type = TYPE_NONE;

        linePaint = new Paint();//绘制实线的Paint
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.SQUARE);
        linePaint.setColor(Color.GRAY);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(10);
        linePaint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
    }


        @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        checkLines();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (type) {
            case TYPE_NONE:
                break;
            case TYPE_DRAW_LINE:
                drawTria(fromPointX, fromPointY, toPointX, toPointY, 50, 30, canvas);
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evX = (int)event.getX();
        int evY = (int)event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                String whichR = onWhichBoxR(evX, evY);
                if (whichR != "NONE") {
                    type = TYPE_DRAW_LINE;
                    Log.d(TAG, "onTouchEvent: 引线起点(" + evX + "," + evY + ")");
                    downBox = boxMap.get(whichR);
                    fromPointX = downBox.dstPs[6];
                    fromPointY = downBox.dstPs[7];
                    toPointX = fromPointX;
                    toPointY = fromPointY;
                }else {
                    Log.d(TAG, "onTouchEvent: 准备拖动ViewGroup");
                    type = TYPE_TRANSLATE;
                    fromPointX = evX;
                    fromPointY = evY;
                    tempPointX = event.getRawX();
                    tempPointY = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (type == TYPE_DRAW_LINE) {
                    toPointX = evX;
                    toPointY = evY;
                    invalidate();
                Log.d(TAG, "onTouchEvent: 引线中间点("+evX+","+evY+")");

                }else if (type == TYPE_TRANSLATE) {
                    Log.d(TAG, "onTouchEvent: 拖动ViewGroup("+evX+","+evY+")"+"getTranslationX="+getTranslationX()+"getTranslationY="+getTranslationY());
                    if (event.getRawX() - tempPointX > 5f && getTranslationX() < dip2px(context,10f)) {
                        setTranslationX(getTranslationX() + 10f);
                    } else if (event.getRawX() - tempPointX < -5f && getTranslationX() > dip2px(context,-650f)) {
                        setTranslationX(getTranslationX() - 10f);

                    }
                    if (event.getRawY() - tempPointY > 5f && getTranslationY() < dip2px(context,440f)) {
                        setTranslationY(getTranslationY() + 10f);
                    } else if (event.getRawY() - tempPointY < -5f && getTranslationY() > dip2px(context,-610f)) {
                        setTranslationY(getTranslationY() - 10f);
                    }

                    tempPointX = event.getRawX();
                    tempPointY = event.getRawY();
                    if (getTranslationX() < 30f && getTranslationY() < 30f) {
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (type == TYPE_DRAW_LINE) {
                    Log.d(TAG, "onTouchEvent: 引线终点(" + evX + "," + evY + ")");
                    type = TYPE_NONE;
                    invalidate();
                    String whichL = onWhichBoxL(evX, evY);
                    if (whichL != "NONE") {
                        upBox = boxMap.get(whichL);
                        toPointX = upBox.dstPs[14];
                        toPointY = upBox.dstPs[15];
                        generateLine(downBox, upBox, fromPointX, fromPointY, toPointX, toPointY);
                    }
                }else if (type == TYPE_TRANSLATE) {
                    type = TYPE_NONE;
                }
                downBox = null;
                break;

            default:
                type = TYPE_NONE;
                break;
        }
        return true;
    }

    public boolean generateLine(ProcessBox downBox, ProcessBox upBox, float x1, float y1, float x2, float y2) {
        Log.d(TAG, "generateLine: 引线起点(" + x1 + "," + y1 + ")"+"引线终点(" + x2 + "," + y2 + ")");
        ArrayList lineList = downBox.nextLine;  //方框的连线数量
        if (lineList.size() < 2) { //一个方框的连线至多有两个

            ProcessDirectionLine line = new ProcessDirectionLine(context, x1, y1, x2, y2);
            line.setOnLineClick(this);
            line.setNextBox(upBox);
            line.setPreBox(downBox);
            addView(line);
            lineList.add(line);
            lineMap.put(line.getKey(), line);
            return true;
        } else {
            Toast.makeText(context, "超出连线数量", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "超出连线数量");
            return false;
        }
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

    public void autoGenerateTree(LinkedHashMap<String,String[]> boxs, LinkedHashMap<String,String[]> lines) {
        autoGenerateTree(boxs, lines, 0);

    }

    public void autoGenerateTree(LinkedHashMap<String, String[]> boxs, LinkedHashMap<String, String[]> lines, int first) {
        int disHeiUp = dip2px(context,150), disHeiDown = dip2px(context,150), disWid = dip2px(context,150);
        int startX = dip2px(context,20), startY = dip2px(context,700);
        Iterator<Map.Entry<String, String[]>> iterator = boxs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String[] boxArray = entry.getValue();
            ProcessBox newBox;
            if (boxMap.get(entry.getKey()) == null) {
                //boxMap中没有新建的这个Box 所以要New出此Box
                newBox = new ProcessBox(context, startX, startY);
                newBox.setKey(boxArray[0]);
                newBox.setTitle(boxArray[1]);
                newBox.setOnBoxTLClick(this);
                addView(newBox);
                boxMap.put(newBox.getKey(), newBox);
            } else {
                //boxMap中已经存在这个Box 所以要取出此Box
                newBox = boxMap.get(entry.getKey());
            }
            startX = (int) newBox.dstPs[0];
            startY = (int) newBox.dstPs[1];


            if (boxArray[3] != null) {
                String[] next1LineArray = lines.get(boxArray[3]);
                String[] next1BoxArray = boxs.get(next1LineArray[3]);
                ProcessBox next1Box;

                if (boxMap.get(next1BoxArray[0]) == null) {
                    //boxMap中没有新建的这个Box 所以要New出此Box
                    next1Box = new ProcessBox(context, startX + disWid,  startY - disHeiUp); //  getLeft() + getTop() +
                    next1Box.setKey(next1BoxArray[0]);
                    next1Box.setTitle(next1BoxArray[1]);
                    next1Box.setOnBoxTLClick(this);
                    addView(next1Box);
                    boxMap.put(next1Box.getKey(), next1Box);
                } else {
                    //boxMap中已经存在这个Box 所以要取出此Box
                    next1Box = boxMap.get(next1BoxArray[0]);
                    //检查此box的层级是否需要变动
                    if (startX >= next1Box.dstPs[0]) {
                        next1Box.translateX(disWid);
                        checkLines();//更新线的位置
                    }
                }
                ProcessDirectionLine next1Line = new ProcessDirectionLine(context, newBox.dstPs[6], newBox.dstPs[7], next1Box.dstPs[14], next1Box.dstPs[15]);
                next1Line.setPreBox(newBox);
                next1Line.setNextBox(next1Box);
                next1Line.setKey(next1LineArray[0]);
                next1Line.setTitle(next1LineArray[1]);
                next1Line.setOnLineClick(this);
                newBox.nextLine.add(next1Line);
                addView(next1Line);
                lineMap.put(next1Line.getKey(), next1Line);

            }
            if (boxArray[4] != null) {
                String[] next2LineArray = lines.get(boxArray[4]);
                String[] next2BoxArray = boxs.get(next2LineArray[3]);
                ProcessBox next2Box;

                if (boxMap.get(next2BoxArray[0]) == null) {
                    //boxMap中没有新建的这个Box 所以要New出此Box
                    next2Box = new ProcessBox(context,  startX + disWid, startY + disHeiDown); // getLeft() +getTop() +
                    next2Box.setKey(next2BoxArray[0]);
                    next2Box.setTitle(next2BoxArray[1]);
                    next2Box.setOnBoxTLClick(this);
                    addView(next2Box);
                    boxMap.put(next2Box.getKey(), next2Box);
                } else {
                    //boxMap中已经存在这个Box 所以要取出此Box
                    next2Box = boxMap.get(next2BoxArray[0]);
                    //检查此box的层级是否需要变动
                    if (startX >= next2Box.dstPs[0]) {
                        next2Box.translateX(disWid);
                        checkLines();//更新线的位置
                    }
                }

                ProcessDirectionLine next2Line = new ProcessDirectionLine(context, newBox.dstPs[6], newBox.dstPs[7], next2Box.dstPs[14], next2Box.dstPs[15]);
                next2Line.setPreBox(newBox);
                next2Line.setNextBox(next2Box);
                next2Line.setKey(next2LineArray[0]);
                next2Line.setTitle(next2LineArray[1]);
                next2Line.setOnLineClick(this);
                newBox.nextLine.add(next2Line);
                addView(next2Line);
                lineMap.put(next2Line.getKey(), next2Line);

            }
            if (disHeiDown >dip2px(context,60)) {
                disHeiDown -= dip2px(context,30);
                disHeiUp -= dip2px(context,30);
            }

        }


    }
    /* 实时刷新line的位置 */
    private void checkLines() {
        //实时刷新line的位置
        Iterator<Map.Entry<String, ProcessDirectionLine>> iterator = lineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            ProcessDirectionLine line = iterator.next().getValue();
            float[] preBoxPosis = line.getPreBox().dstPs;
            float[] nextBoxPosis = line.getNextBox().dstPs;
            line.changeView(preBoxPosis[6],preBoxPosis[7],nextBoxPosis[14],nextBoxPosis[15]);
            if (!boxMap.containsKey(line.getNextBox().getKey())) {
                //说明line的后置节点没有了 需要将这个line删除掉
                ProcessBox preBox = line.getPreBox();
                preBox.nextLine.remove(line);//从前至节点中把此连线view记录删掉
                removeView(line);//将自己从父View中删掉
            }
        }
    }

    public void scaleGroup(float num) {
        setScaleX(num);
        setScaleY(num);
    }

    public void addBox(final Context context) {

        final ProcessBox box = new ProcessBox(context, dip2px(context,30) - (int) getTranslationX(), dip2px(context,600) - (int) getTranslationY());
        box.setOnBoxTLClick(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        box.setLayoutParams(layoutParams);
        addView(box);
        boxMap.put(box.getKey(), box);
    }

    /**
     *  判断点哪个box的右控制点上
     * @return box.getKey()
     */
    private String onWhichBoxR(int evx, int evy) {
        Rect rect = new Rect(evx - ACCURACY_RECOGNIZE, evy - ACCURACY_RECOGNIZE, evx + ACCURACY_RECOGNIZE, evy + ACCURACY_RECOGNIZE);
        Iterator<Map.Entry<String, ProcessBox>> iterator = boxMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ProcessBox> next = iterator.next();
            ProcessBox box = next.getValue();
            if (rect.contains((int) box.dstPs[6], (int) box.dstPs[7])) {
                return next.getKey();
            }
        }
        return "NONE";
    }

    /**
     *  判断点哪个box的左控制点上
     * @return box.getKey()
     */
    private String onWhichBoxL(int evx, int evy) {
        Rect rect = new Rect(evx - ACCURACY_RECOGNIZE, evy - ACCURACY_RECOGNIZE, evx + ACCURACY_RECOGNIZE, evy + ACCURACY_RECOGNIZE);
        Iterator<Map.Entry<String, ProcessBox>> iterator = boxMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ProcessBox> next = iterator.next();
            ProcessBox box = next.getValue();
            if (rect.contains((int) box.dstPs[14], (int) box.dstPs[15])) {
                return next.getKey();
            }
        }
        return "NONE";
    }

    @Override
    public void onClickLine(final ProcessDirectionLine line, float x, float y) {

        //弹出弹窗选择删除或者重命名
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(null);
        builder.setTitle("选择下列操作");
        //    指定下拉列表的显示数据
        final String[] types = {"删除", "重命名"};
        //    设置一个下拉的列表选择项
        builder.setItems(types, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(context, "选择的操作为：" + types[which], Toast.LENGTH_SHORT).show();
                switch (which) {
                    case 0://删除
                        ProcessBox preBox = line.getPreBox();
                        preBox.nextLine.remove(line);//从前至节点中把此连线view记录删掉
                        lineMap.remove(line.getKey());//将自己从父View中lineMap中删掉
                        removeView(line);//将自己从父View中删掉
                        break;
                    case 1://重命名
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(null);
                        builder.setTitle("请重命名该线段");
                        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                        final EditText editText = new EditText(context);
                        editText.setId(R.id.line_edit_text);
                        editText.setHint("标题");
                        builder.setView(editText);

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                EditText view = editText.findViewById(R.id.line_edit_text);
                                String text = view.getText().toString().trim();
                                line.setTitle(text);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
                        builder.show();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();

    }

    @Override
    public void onClickBoxTL(final ProcessBox box) {
        //弹出弹窗选择删除或者重命名
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(null);
        builder.setTitle("选择下列操作");
        //    指定下拉列表的显示数据
        final String[] types = {"删除", "重命名", "换背景"};
        //    设置一个下拉的列表选择项
        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "选择的操作为：" + types[which], Toast.LENGTH_SHORT).show();
                switch (which) {
                    case 0://删除
                        ProcessLayout parent = (ProcessLayout) box.getParent();
                        for (ProcessDirectionLine nextLine : box.nextLine) {
                            parent.removeView(nextLine);
                        }
                        parent.removeView(box);

                        //主动从父View中BoxMap中删掉
                        parent.boxMap.remove(box.getKey());

                        //删除box的前置连线
                        Iterator<Map.Entry<String, ProcessDirectionLine>> iterator = lineMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            ProcessDirectionLine line = iterator.next().getValue();
                            if (parent.boxMap.get(line.getNextBox().getKey()) == null) {
                                //说明line的后置节点没有了 需要将这个line删除掉
                                ProcessBox preBox = line.getPreBox();
                                preBox.nextLine.remove(line);//从前至节点中把此连线view记录删掉
                                parent.removeView(line);//将自己从父View中删掉
                                iterator.remove();//将自己从父View中lineMap中删掉
                                //上述方式 等同于parent.lineMap.remove(line.getKey());

                            }
                        }



                        break;
                    case 1://重命名
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(null);
                        builder.setTitle("请重命名该线段");
                        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                        final EditText editText = new EditText(context);
                        editText.setId(R.id.line_edit_text);
                        editText.setHint("标题");
                        builder.setView(editText);

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText view = editText.findViewById(R.id.line_edit_text);
                                String text = view.getText().toString().trim();
                                box.setTitle(text);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                        break;
                    case 2:
                        //弹出弹窗选择删除或者重命名
                        final AlertDialog.Builder builderPic = new AlertDialog.Builder(context);
                        builderPic.setIcon(null);
                        builderPic.setTitle("选择下列背景资源");
                        //指定下拉列表的显示数据
                        final String[] types = {"绿景", "木板", "纸张","天空","夕阳"};
                        //设置一个下拉的列表选择项
                        builderPic.setItems(types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "选择的背景为：" + types[which], Toast.LENGTH_SHORT).show();
                                box.setPic(which);
                            }
                        });
                        builderPic.show();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
