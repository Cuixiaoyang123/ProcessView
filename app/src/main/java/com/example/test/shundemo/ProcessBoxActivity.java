package com.example.test.shundemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.test.shundemo.bean.TreeNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by DreamLost on 2020/7/9 at 21:44
 * Description:
 */
public class ProcessBoxActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ProcessLayout";

    private Context context;
    private ProcessLayout rl ;
    private TextView outText;
    private SeekBar seekBar;
    private Button btn_add, btn_scale1, btn_export, btn_import, btn_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.process_main);
        rl = findViewById(R.id.relative_layout);
        outText = findViewById(R.id.out_text);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        btn_scale1 = findViewById(R.id.btn_scale1);
        btn_scale1.setOnClickListener(this);
        btn_export = findViewById(R.id.btn_export);
        btn_export.setOnClickListener(this);
        btn_import = findViewById(R.id.btn_import);
        btn_import.setOnClickListener(this);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: " + "progress:" + progress + progress / 50f);
                if (progress >= 15) {
                    rl.scaleGroup(progress / 50f);
                } else {
                    rl.scaleGroup(15 / 50f);
                    seekBar.setProgress(15);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.seekBar:
                rl.scaleGroup(1.2f);
                break;
            case R.id.btn_add:
                rl.addBox(context);
                break;
            case R.id.btn_scale1:
                rl.scaleGroup(1f);
                seekBar.setProgress(50);
                break;
            case R.id.btn_save:
                createJson();
                break;
            case R.id.btn_import:
                ArrayList<LinkedHashMap> maps = parseJson("tree.json");
                rl.autoGenerateTree(maps);
//                TreeNode root = parseJson2Tree();
//                root.preOrder(root);
//                String[] box1 = new String[]{"box1", "name1", null, "line1", "line2"};
//                String[] box2 = new String[]{"box2", "name2", null, "line3", "line7"};
//                String[] box3 = new String[]{"box3", "name3", null, "line4", null};
//                String[] box4 = new String[]{"box4", "name4", null, "line5", "line6"};
//                String[] box5 = new String[]{"box5", "name5", null, null, null};
//                String[] box6 = new String[]{"box6", "name6", null, null, null};
//
//                String[] line1 = new String[]{"line1", "name1", "box1", "box2"};
//                String[] line2 = new String[]{"line2", "name2", "box1", "box3"};
//                String[] line3 = new String[]{"line3", "name3", "box2", "box4"};
//                String[] line4 = new String[]{"line4", "name4", "box3", "box6"};
//                String[] line5 = new String[]{"line5", "name5", "box4", "box5"};
//                String[] line6 = new String[]{"line6", "name6", "box4", "box6"};
//                String[] line7 = new String[]{"line7", "name7", "box2", "box3"};
//
//
//                LinkedHashMap<String, String[]> boxHashMap = new LinkedHashMap<>();
//                boxHashMap.put(box1[0], box1);
//                boxHashMap.put(box2[0], box2);
//                boxHashMap.put(box3[0], box3);
//                boxHashMap.put(box4[0], box4);
//                boxHashMap.put(box5[0], box5);
//                boxHashMap.put(box6[0], box6);
//
//                LinkedHashMap<String, String[]> lineHashMap = new LinkedHashMap<>();
//                lineHashMap.put(line1[0], line1);
//                lineHashMap.put(line2[0], line2);
//                lineHashMap.put(line3[0], line3);
//                lineHashMap.put(line4[0], line4);
//                lineHashMap.put(line5[0], line5);
//                lineHashMap.put(line6[0], line6);
//                lineHashMap.put(line7[0], line7);
//
//                String[] box11 = new String[]{"box11", "name11", null, "line11", "line12"};
//                String[] box12 = new String[]{"box12", "name12", null, "line13", "line20"};
//                String[] box13 = new String[]{"box13", "name13", null, "line14", "line15"};
//                String[] box14 = new String[]{"box14", "name14", null, "line16", null};
//                String[] box15 = new String[]{"box15", "name15", null, "line10", null};
//                String[] box16 = new String[]{"box16", "name16", null, "line19", null};
//                String[] box17 = new String[]{"box17", "name17", null, null, null};
//                String[] box18 = new String[]{"box18", "name18", null, "line18", null};
//                String[] box19 = new String[]{"box19", "name19", null, null, null};
//
//                String[] line10 = new String[]{"line10", "name10", "box15", "box19"};
//                String[] line11 = new String[]{"line11", "name11", "box11", "box12"};
//                String[] line12 = new String[]{"line12", "name12", "box11", "box13"};
//                String[] line13 = new String[]{"line13", "name13", "box12", "box14"};
//                String[] line14 = new String[]{"line14", "name14", "box13", "box15"};
//                String[] line15 = new String[]{"line15", "name15", "box13", "box16"};
//                String[] line16 = new String[]{"line16", "name16", "box14", "box17"};
////                String[] line17 = new String[]{"line17", "name17", "box15", "box17"};
//                String[] line18 = new String[]{"line18", "name18", "box18", "box17"};
//                String[] line19 = new String[]{"line19", "name19", "box16", "box19"};
//                String[] line20 = new String[]{"line20", "name20", "box12", "box18"};
//
//
//                LinkedHashMap<String, String[]> boxHashMap1 = new LinkedHashMap<>();
//                boxHashMap1.put(box11[0], box11);
//                boxHashMap1.put(box12[0], box12);
//                boxHashMap1.put(box13[0], box13);
//                boxHashMap1.put(box18[0], box18);
//                boxHashMap1.put(box14[0], box14);
//                boxHashMap1.put(box16[0], box16);
//                boxHashMap1.put(box15[0], box15);
//                boxHashMap1.put(box17[0], box17);
//                boxHashMap1.put(box19[0], box19);
//
//                LinkedHashMap<String, String[]> lineHashMap1 = new LinkedHashMap<>();
//                lineHashMap1.put(line10[0], line10);
//                lineHashMap1.put(line11[0], line11);
//                lineHashMap1.put(line12[0], line12);
//                lineHashMap1.put(line13[0], line13);
//                lineHashMap1.put(line14[0], line14);
//                lineHashMap1.put(line15[0], line15);
//                lineHashMap1.put(line16[0], line16);
////                lineHashMap1.put(line17[0], line17);
//                lineHashMap1.put(line18[0], line18);
//                lineHashMap1.put(line19[0], line19);
//                lineHashMap1.put(line20[0], line20);
//
//                rl.boxMap.clear();
//                rl.lineMap.clear();
//                rl.removeAllViews();
//
//                if (((int)(Math.random() * 10)) % 2 == 1) {
//                    rl.autoGenerateTree(boxHashMap1,lineHashMap1);
//                } else {
//                    rl.autoGenerateTree(boxHashMap,lineHashMap);
//                }

                break;
            case R.id.btn_export:
                //通过父viewGroup 输出子view属性和数据
                String outTextBox = "";
                Iterator<Map.Entry<String, ProcessBox>> iterator = rl.boxMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    ProcessBox box = iterator.next().getValue();
                    int size = box.nextLine.size();
                    String s = "";
                    for (int j = 0; j<size;j++) {
                        s += " next" + (j+1) + "=" + box.nextLine.get(j).getTitle();
                    }
                    outTextBox += "Box: key=" + box.getKey()
                            + " title=" + box.getTitle()
                            + " img=" + box.getPic()
                            + s + "\n";
                    Log.d(TAG, s);
                }

                String outTextLine = "";
                Iterator<Map.Entry<String, ProcessDirectionLine>> iterator1 = rl.lineMap.entrySet().iterator();
                while (iterator1.hasNext()) {
                    ProcessDirectionLine line = iterator1.next().getValue();
                    outTextLine += "Line: key=" + line.getKey()
                            + " title=" + line.getTitle()
                            + " nextNode=" + line.getNextBox().getTitle()
                            + "\n";
                    Log.d(TAG, outTextLine);
                }

                outText.setText(outTextBox + outTextLine);
                break;

//                //通过父viewGroup 输出子view属性和数据
//                int boxSize = rl.boxList.size();
//                String outTextBox = "";
//                for (int i = 0; i < boxSize; i++) {
//                    int size = rl.boxList.get(i).nextLine.size();
//                    String s = "";
//                    for (int j = 0; j<size;j++) {
//                        s += " nextNode" + (j+1) + " =  " + rl.boxList.get(i).nextLine.get(j).getTitle();
//                    }
//                    outTextBox += "ProcessBox: id = " + rl.boxList.get(i).getId()
//                            + " title =  " + rl.boxList.get(i).getTitle()
//                            + " img =  " + rl.boxList.get(i).getPic()
////                            + " nextNodeSize = " + size
//                            + s + "\n";
//                    Log.d(TAG, s);
//
//
//                }
//                int lineSize = rl.lineList.size();
//                String outTextLine = "";
//                for (int j = 0; j < lineSize; j++) {
//                    outTextLine += "ProcessLine: id = " + rl.lineList.get(j).getId()
//                            + " title =  " + rl.lineList.get(j).getTitle()
//                            + " nextNode =  " + rl.lineList.get(j).getNextBox().getTitle()
//                            + "\n";
//                    Log.d(TAG, outTextLine);
//                }
//                outText.setText(outTextBox+outTextLine);
//                break;
        }
    }

    private void createJson() {
        File file = new File(getFilesDir(), "tree.json");
        JSONObject json = new JSONObject();

        try {

            Iterator<Map.Entry<String, ProcessBox>> iterator = rl.boxMap.entrySet().iterator();
            JSONArray boxes = new JSONArray();
            int boxIndex = 0;
            while (iterator.hasNext()) {
                ProcessBox processBox = iterator.next().getValue();
                JSONObject box = new JSONObject();

                box.put("key", processBox.getKey());
                box.put("title", processBox.getTitle());
                box.put("pic", processBox.getPic());
                box.put("url", "");
                box.put("x", (int) processBox.dstPs[0]);
                box.put("y", (int) processBox.dstPs[1]);

                JSONArray nexts = new JSONArray();
                int nextLineNum = processBox.nextLine.size();
                for (int i = 0; i < nextLineNum; i++) {
                    nexts.put(processBox.nextLine.get(i).getKey());
                }
                box.put("nextLine", nexts);

                boxes.put(box);
            }

            Iterator<Map.Entry<String, ProcessDirectionLine>> iterator1 = rl.lineMap.entrySet().iterator();
            JSONArray lines = new JSONArray();

            while (iterator1.hasNext()) {
                ProcessDirectionLine processLine = iterator1.next().getValue();
                JSONObject line = new JSONObject();
                line.put("key", processLine.getKey());
                line.put("title", processLine.getTitle());
                line.put("preBox", processLine.getPreBox().getKey());
                line.put("nextBox", processLine.getNextBox().getKey());

                lines.put(line);
            }

            json.put("head", rl.getRootKey());
            json.put("boxes", boxes);
            json.put("lines", lines);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.toString().getBytes());
            fos.close();
            Log.d(TAG, "createJson: " + json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }

    private TreeNode parseJson2Tree() {
        ArrayList<LinkedHashMap> maps = parseJson("");
        LinkedHashMap<String,String[]> boxMap =maps.get(0);
        LinkedHashMap<String,String[]> lineMap = maps.get(1);
        String headKey = (String) maps.get(2).get("head");

        LinkedHashMap<String, TreeNode> nodeMap = new LinkedHashMap<>();
        Iterator<Map.Entry<String, String[]>> iterator = boxMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String[] array =  iterator.next().getValue();
            nodeMap.put(array[0],
            new TreeNode(array[0], array[1], array[2], array[3],
                    Integer.valueOf(array[4]), Integer.valueOf(array[5]))
            );
        }
        Iterator<Map.Entry<String, String[]>> iterator1 = lineMap.entrySet().iterator();
        while (iterator1.hasNext()) {
            String[] array =  iterator1.next().getValue();
            nodeMap.get(array[2]).addNext(nodeMap.get(array[3]));
        }

        return nodeMap.get(headKey);
    }

    private ArrayList<LinkedHashMap> parseJson(String path){
        LinkedHashMap<String, String[]> boxMap = new LinkedHashMap<>();
        LinkedHashMap<String, String[]> lineMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> headMap = new LinkedHashMap<>();




        try {
            InputStream is = null;
            if (path != "") {
                File file = new File(getFilesDir(), path);
                is = new FileInputStream(file);
            } else {
                is = getResources().openRawResource(R.raw.tree1);
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String lineNum;
            StringBuffer sb = new StringBuffer();
            while (null != (lineNum = br.readLine())) {
                sb.append(lineNum);
            }

            is.close();
            isr.close();
            br.close();
            JSONObject jsonObject = new JSONObject(sb.toString());
            String head = jsonObject.optString("head", "");
            headMap.put("head", head);

            JSONArray boxes = jsonObject.optJSONArray("boxes");
            for (int i = 0; i < boxes.length(); i++) {
                JSONObject obj = boxes.getJSONObject(i);
                String key = obj.optString("key", "");
                String title = obj.optString("title", "");
                String pic = obj.optString("pic", "");
                String url = obj.optString("url", "");
                String x = obj.optString("x", "");
                String y = obj.optString("y", "");
                String[] box = new String[10];
                box[0] = key;box[1] = title;box[2] = pic;box[3] = url;
                box[4] = x;box[5] = y;
                JSONArray nextLines = obj.getJSONArray("nextLine");
                ArrayList<String> nexts = new ArrayList<>();
                for (int j = 0; j < nextLines.length(); j++) {
                    String nextLine = (String) nextLines.get(j);
                    box[7 + j] = nextLine;
                }
                boxMap.put(key, box);
            }

            JSONArray lines = jsonObject.optJSONArray("lines");
            for (int i = 0; i < lines.length(); i++) {
                JSONObject obj = lines.getJSONObject(i);
                String key = obj.optString("key", "");
                String title = obj.optString("title", "");
                String preBox = obj.optString("preBox", "");
                String nextBox = obj.optString("nextBox", "");
                String[] line = new String[]{key, title, preBox, nextBox};
                lineMap.put(key, line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "parseJson: 解析成功");
        return new ArrayList<LinkedHashMap>(
                Arrays.asList(boxMap, lineMap, headMap)
        );

    }
}
