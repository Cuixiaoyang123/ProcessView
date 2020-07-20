package com.example.test.shundemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by DreamLost on 2020/7/10 at 15:45
 * Description:
 */
public class ProcessDirectionLineActivity extends Activity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl ;
    private Button btn_add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.process_main);
        rl = findViewById(R.id.relative_layout);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        ProcessDirectionLine line = new ProcessDirectionLine(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        line.setLayoutParams(layoutParams);
        rl.addView(line);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                ProcessDirectionLine line = new ProcessDirectionLine(context);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                line.setLayoutParams(layoutParams);
                rl.addView(line);
                break;
        }
    }
}


