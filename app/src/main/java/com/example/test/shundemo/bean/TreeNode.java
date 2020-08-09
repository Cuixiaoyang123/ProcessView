package com.example.test.shundemo.bean;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DreamLost on 2020/8/9 at 9:18
 * Description:
 */
public class TreeNode {
    private static final String TAG = "TreeNode";

    private String key;
    private String title;
    private String pic;
    private String url;
    private Point position = new Point();
    private int sonNum;
    private List<TreeNode> nexts = new ArrayList<>();

    public TreeNode(String key, String title, String pic, String url,int x, int y) {
        this.key = key;
        this.title = title;
        this.pic = pic;
        this.url = url;
        this.position.x = x;
        this.position.y = y;
    }

    public int getSonNum() {
        return sonNum;
    }

    public List<TreeNode> getNexts() {
        return nexts;
    }

//    public void setNexts(List<TreeNode> nexts) {
//        this.nexts = nexts;
//        this.sonNum = nexts.size();
//    }

    public void preOrder(TreeNode head) {
        if (head == null) return;
        Log.d(TAG, "preOrder: " + head.key);
        int num = sonNum;
        for (TreeNode son : head.nexts) {
            preOrder(son);
        }
    }

    public void addNext(TreeNode node) {
        nexts.add(node);
        sonNum++;
    }
}
