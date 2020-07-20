package com.example.test.shundemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by DreamLost on 2020/7/13 at 18:08
 * Description:
 */
public class ProcessLayout extends RelativeLayout {
    public static final String TAG = "ProcessLayout";

    private Context context;
    public ArrayList<ProcessBox> boxList;
    public ArrayList<ProcessDirectionLine> lineList;
    private ProcessBox downBox, upBox;
    private float fromPointX, fromPointY;
    private static final int ACCURACY_RECOGNIZE = 60;  //手指的识别精度
    private boolean showPopup = false;

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
        boxList = new ArrayList<>();
        lineList = new ArrayList<>();
    }


        @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        lineList.clear();
        boxList.clear();
        int childCount = getChildCount();

        //遍历所有子view 选择Line对象 和 Box对象 分别存储在 lineList 和 boxList中
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof ProcessDirectionLine) {
                lineList.add((ProcessDirectionLine) child);
            } else if (child instanceof ProcessBox) {
                boxList.add((ProcessBox) child);
            }
        }

        //实时刷新line的位置
        for (ProcessDirectionLine line : lineList) {
            float[] preBoxPosis = line.getPreBox().dstPs;
            float[] nextBoxPosis = line.getNextBox().dstPs;
            line.changeView(preBoxPosis[6],preBoxPosis[7],nextBoxPosis[14],nextBoxPosis[15]);

            if (!boxList.contains(line.getNextBox())) {
                //说明line的后置节点没有了 需要将这个line删除掉
                ProcessBox preBox = line.getPreBox();
                preBox.nextLine.remove(line);//从前至节点中把此连线view记录删掉
                removeView(line);//将自己从父View中删掉
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evX = (int)event.getX();
        int evY = (int)event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isOnCP(evX, evY)[0] == CTR_RIGHT_MID) {
                    Log.d(TAG, "onTouchEvent: 引线起点("+evX+","+evY+")");
                    downBox = boxList.get(isOnCP(evX, evY)[1]);
                    fromPointX = isOnCP(evX, evY)[2];
                    fromPointY = isOnCP(evX, evY)[3];
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "onTouchEvent: 引线中间点("+evX+","+evY+")");
                break;
            case MotionEvent.ACTION_UP:
                if (isOnCP(evX, evY)[0] == CTR_LEFT_MID) {
                    Log.d(TAG, "onTouchEvent: 引线终点(" + evX + "," + evY + ")");
                    ArrayList lineList = downBox.nextLine;  //方框的连线数量
                    if (lineList.size() < 2) { //一个方框的连线至多有两个

                        final ProcessDirectionLine line = new ProcessDirectionLine(context, fromPointX, fromPointY, isOnCP(evX, evY)[2], isOnCP(evX, evY)[3]);
                        line.setOnLineClick(new ProcessDirectionLine.LineCallBack() {
                            @Override
                            public void onClickLine(float x, float y) {

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
//                                                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//                                                layoutParams.addRule( CENTER_HORIZONTAL);
//                                                editText.setLayoutParams(layoutParams);
                                                //    设置我们自己定义的布局文件作为弹出框的Content
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
//                                Dialog dialog = new Dialog(context);
//                                dialog.show();
//                                if (showPopup) {
//                                    return;
//                                }
//                                showPopup = true;
//                                final LinearLayout linearLayout = new LinearLayout(context);
//                                linearLayout.setBackgroundColor(getResources().getColor(R.color.gray_color_70));
//                                linearLayout.setAlpha(0.5f);
//                                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                                LayoutParams fuLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//                                fuLayoutParams.addRule(CENTER_IN_PARENT);
//                                linearLayout.setLayoutParams(fuLayoutParams);
//
//                                final EditText editText = new EditText(context);
//                                editText.setId(R.id.line_edit_text);
//                                editText.setText("更改线段标题");
//                                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//                                layoutParams.addRule( CENTER_HORIZONTAL);
//                                editText.setLayoutParams(layoutParams);
//
//                                Button button1 = new Button(context);
//                                button1.setText("确认");
//                                button1.setOnClickListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        showPopup = false;
//                                        EditText editText1 = findViewById(R.id.line_edit_text);
//                                        String text = String.valueOf(editText1.getText());
//                                        line.changeTitle(text);
//                                        removeView(linearLayout);
//                                    }
//                                });
//
//                                Button button2 = new Button(context);
//                                button2.setText("取消");
//                                button2.setOnClickListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        showPopup = false;
//                                        removeView(linearLayout);
//                                    }
//                                });
//                                linearLayout.addView(editText);
//                                linearLayout.addView(button1);
//                                linearLayout.addView(button2);
//                                addView(linearLayout);
                            }
                        });
                        line.setNextBox(boxList.get(isOnCP(evX, evY)[1]));
                        line.setPreBox(downBox);
                        addView(line);
                        lineList.add(line);
                    } else {
                        Toast.makeText(context, "超出连线数量", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "超出连线数量");
                    }
                }
                downBox = null;
                break;

            default:
                break;
        }
        return true;
    }

    public void addBox(final Context context) {
        final ProcessBox box = new ProcessBox(context);

        box.setOnBoxTLClick(new ProcessBox.BoxCallBack() {
            @Override
            public void onClickBoxTL() {
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

                                //主动从父View中BoxList中删掉
                                parent.boxList.remove(box);

                                //删除box的前置连线
                                for (ProcessDirectionLine line : parent.lineList) {
                                    if (!parent.boxList.contains(line.getNextBox())) {
                                        //说明line的后置节点没有了 需要将这个line删除掉
                                        ProcessBox preBox = line.getPreBox();
                                        preBox.nextLine.remove(line);//从前至节点中把此连线view记录删掉
                                        parent.removeView(line);//将自己从父View中删掉
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
//                                                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//                                                layoutParams.addRule( CENTER_HORIZONTAL);
//                                                editText.setLayoutParams(layoutParams);
                                //    设置我们自己定义的布局文件作为弹出框的Content
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
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        box.setLayoutParams(layoutParams);
        addView(box);
    }

    /**
     * 判断点所在的 最左 或者 最右 或者 无 控制点
     * @param evx
     * @param evy
     * @return int[]  最左->7 或者 最右->3  或者 无->-1，方框索引i， 控制点X坐标，  控制点Y坐标
     */
    private int[] isOnCP(int evx, int evy) {
        Rect rect = new Rect(evx - ACCURACY_RECOGNIZE, evy - ACCURACY_RECOGNIZE, evx + ACCURACY_RECOGNIZE, evy + ACCURACY_RECOGNIZE);
        for (int i = 0; i < boxList.size(); i++) {
            if(rect.contains((int)boxList.get(i).dstPs[6], (int)boxList.get(i).dstPs[7])){
                return new int[]{CTR_RIGHT_MID,i,(int)boxList.get(i).dstPs[6],(int)boxList.get(i).dstPs[7]} ;
            } else if (rect.contains((int) boxList.get(i).dstPs[14], (int) boxList.get(i).dstPs[15])) {
                return new int[]{CTR_LEFT_MID,i,(int)boxList.get(i).dstPs[14],(int)boxList.get(i).dstPs[15]} ;
            }
        }
        return new int[]{CTR_NONE};
    }
}
