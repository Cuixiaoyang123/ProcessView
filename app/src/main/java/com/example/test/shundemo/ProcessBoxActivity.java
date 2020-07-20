package com.example.test.shundemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by DreamLost on 2020/7/9 at 21:44
 * Description:
 */
public class ProcessBoxActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ProcessLayout";

    private Context context;
    private ProcessLayout rl ;
    private TextView outText;
    private Button btn_add, btn_add_line, btn_export;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.process_main);
        rl = findViewById(R.id.relative_layout);
        outText = findViewById(R.id.out_text);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        btn_add_line = findViewById(R.id.btn_add_line);
//        btn_add_line.setOnClickListener(this);
        btn_export = findViewById(R.id.btn_export);
        btn_export.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                rl.addBox(context);
                break;
            case R.id.btn_add_line:
                ProcessDirectionLine line = new ProcessDirectionLine(context);
                RelativeLayout.LayoutParams layoutParamsLine = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                line.setLayoutParams(layoutParamsLine);
                rl.addView(line);
                break;
            case R.id.btn_export:
                //通过父viewGroup 输出子view属性和数据
                int boxSize = rl.boxList.size();
                String outTextBox = "";
                for (int i = 0; i < boxSize; i++) {
                    int size = rl.boxList.get(i).nextLine.size();
                    String s = "";
                    for (int j = 0; j<size;j++) {
                        s += " nextNode" + (j+1) + " =  " + rl.boxList.get(i).nextLine.get(j).getTitle();
                    }
                    outTextBox += "ProcessBox: id = " + rl.boxList.get(i).getId()
                            + " title =  " + rl.boxList.get(i).getTitle()
                            + " img =  " + rl.boxList.get(i).getPic()
//                            + " nextNodeSize = " + size
                            + s + "\n";
                    Log.d(TAG, s);


                }
                int lineSize = rl.lineList.size();
                String outTextLine = "";
                for (int j = 0; j < lineSize; j++) {
                    outTextLine += "ProcessLine: id = " + rl.lineList.get(j).getId()
                            + " title =  " + rl.lineList.get(j).getTitle()
                            + " nextNode =  " + rl.lineList.get(j).getNextBox().getTitle()
                            + "\n";
                    Log.d(TAG, outTextLine);
                }
                outText.setText(outTextBox+outTextLine);
                break;
        }
    }
}
