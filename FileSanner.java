package com.xyy.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 文件扫描工具（功能是将一个文件夹下满足条件的文件都遍历出来）
 * Created by admin on 2016/9/7.
 */
public class FileSanner {
    //遍历的结果
    private List<File> resultList;
    //将要遍历的文件夹的记录
    private Stack<File> tempFolder;
    private OnScanListener onScanListener;

    private void init() {
        if (null == resultList) {
            resultList = new LinkedList<File>();
        } else {
            resultList.clear();
        }
        if (null == tempFolder) {
            tempFolder = new Stack<File>();
        } else {
            tempFolder.clear();
        }
    }

    /**
     * 遍历文件夹的入口
     *
     * @param path
     * @param filter 需要遍历的文件规则
     */
    public void startScan(String path,int type, final FileFilter filter) {
        init();
        //将要遍历的文件夹入栈
        tempFolder.push(new File(path));
        //只要要遍历的文件夹栈非空，那么就需要一直遍历
        while (!tempFolder.isEmpty()) {
            //取出要遍历的文件夹
            File file = tempFolder.pop();
            //遍历文件夹
            File[] fs = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    //返回的是子文件夹或者是满足条件的文件规则（文档、apk）
                    return pathname.isDirectory() || filter.accept(pathname);
                }
            });
            if (fs != null && fs.length > 0) {
                int size = fs.length;
                for (int i = 0; i < size; i++) {
                    File f = fs[i];
                    if (f.isDirectory()) {
                        //是文件夹则待会儿要遍历，这时候需要记录到栈中
                        tempFolder.push(f);
                    } else {
                        resultList.add(f);
                    }
                }
            }
        }
        //加载完毕
        if(null != onScanListener){
            onScanListener.onScanComplete(type,resultList);
        }
    }

    public OnScanListener getOnScanListener() {
        return onScanListener;
    }

    public void setOnScanListener(OnScanListener onScanListener) {
        this.onScanListener = onScanListener;
    }

    public interface OnScanListener {
        void onScanComplete(int type,List<File> resultList);
    }


}
