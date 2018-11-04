package com.xyy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.xyy.filesystemmanager.R;
import com.xyy.model.FileBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class FileUtils {

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase();
                return fileType;
            }
        }
        return "";
    }

    /**
     * check is Image
     *
     * @param type
     * @return
     */
    public static boolean isImage(String type) {
        if (type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"))) {
            return true;
        }
        return false;
    }

    /**
     * check is apk file
     *
     * @param type
     * @return
     */
    public static boolean isApkApplication(String type) {
        if (type != null && type.equals("apk")) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否是文档
     * @param type
     * @return
     */
    public static boolean isDocument(String type) {
        if (type != null && type.equals("doc") || type.equals("txt") || type.equals("pdf") || type.equals("xml")) {
            return true;
        }
        return false;
    }

    /**
     * check audio file
     *
     * @param type
     * @return
     */
    public static boolean isAudio(String type) {
        if (type.equals("m4a") || type.equals("mp3") || type.equals("mid")
                || type.equals("xmf") || type.equals("ogg")
                || type.equals("wav") || type.equals("arm")
                || type.equals("aac") || type.equals("wma")
                || type.equals("ape") || type.equals("midi")
                || type.equals("ra") || type.equals("rmx")
                || type.equals("amr") || type.equals("flac")
                || type.equals("dat") || type.equals("au")
                || type.equals("mpga") || type.equals("mp2")
                || type.equals("aiff") || type.equals("af")
                || type.equals("m3u") || type.equals("rmm")
                || type.equals("ram")) {
            return true;
        }
        return false;
    }

    /**
     * check video
     *
     * @param type
     * @return
     */
    public static boolean isVideo(String type) {
        if (type.equals("3gp") || type.equals("mp4") || type.equals("mov")
                || type.equals("rmvb") || type.equals("avi")
                || type.equals("wav") || type.equals("mpeg")
                || type.equals("rm") || type.equals("m4v")
                || type.equals("wmv") || type.equals("mpg")
                || type.equals("flv") || type.equals("mkv")
                || type.equals("vob") || type.equals("ts")
                || type.equals("swf") || type.equals("mpe")
                || type.equals("qt") || type.equals("mxu") || type.equals("rv")
                || type.equals("movie") || type.equals("mpv")) {
            return true;
        }
        return false;
    }

    /**
     * get file icon
     *
     * @param file
     * @return
     */
    public static int getIconRes(File file) {
        if (file.isDirectory()) {
            return R.drawable.filesystem_icon_folder;
        } else {
            String type = getFileType(file.getName());
            if (isImage(type)) {
                return R.drawable.filesystem_grid_icon_photo;
            } else if (isAudio(type)) {
                return R.drawable.filesystem_icon_music;
            } else if (isVideo(type)) {
                return R.drawable.filesystem_icon_movie;
            } else if (isApkApplication(type)) {
                return R.drawable.filesystem_icon_apk;
            } else {
                return R.drawable.filesystem_icon_txt;
            }
        }
    }

    /**
     * 获取sd卡路径  /mnt/sdcard   /sdcard
     *
     * @param context
     * @return
     */
    public static String getSDPath(Context context) {
        File sdDir = null;
        //检测sd卡是否挂载
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取sd卡的路径
            return sdDir.toString();
        } else {
            Toast.makeText(context, "sd卡未挂载", Toast.LENGTH_LONG)
                    .show();
        }
        return "/";
    }

    /**
     * 加载文件夹下的所有文件
     *
     * @param folderPath
     * @param isShowHideFiles 表示是否显示隐藏文件
     * @return
     */
    public List<FileBean> loadData(String folderPath, boolean isShowHideFiles) {
        File file = new File(folderPath);
        if (file.isDirectory()) {
            File[] files;
            if (isShowHideFiles) {
                files = file.listFiles();
            } else {
                files = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return !pathname.getName().startsWith(".");
                    }
                });
            }
            if (files != null && files.length > 0) {
                List<FileBean> list = new ArrayList<FileBean>();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    int icon = getIconRes(f);
                    long fileLenght = f.length();
                    String lenghtStr = compSize(fileLenght);
                    long lastDate = f.lastModified();
                    String dateStr = formatData(lastDate);
                    boolean isDirectory = f.isDirectory();
                    FileBean bean = new FileBean(isDirectory, icon, f.getName(), f.getAbsolutePath(), lenghtStr, dateStr);
                    bean.setLenght(fileLenght);
                    if (isDirectory) {
                        int count = getChildCount(f);
                        bean.setFolderChildCount(count);
                    }
                    list.add(bean);
                }
                //排序，让文件夹在前面，文件在后面(升序排列)
                Collections.sort(list, new Comparator<FileBean>() {
                    @Override
                    public int compare(FileBean o1, FileBean o2) {
                        if (o1.isDirectory() && !o2.isDirectory()) {
                            return -1;
                        } else if (!o1.isDirectory() && o2.isDirectory()) {
                            return 1;
                        } else {
                            return o1.getName().compareTo(o2.getName());
                        }
                    }
                });
                //分类排序，让同一个类型显示在一起
                Collections.sort(list, new Comparator<FileBean>() {
                    @Override
                    public int compare(FileBean o1, FileBean o2) {
                        //比较后缀名
                        return FileUtils.getFileType(o1.getName()).compareTo(FileUtils.getFileType(o2.getName()));
                    }
                });
                return list;
            }
        }
        return null;
    }

    /**
     * 获取文件夹孩子个数
     *
     * @param f
     * @return
     */
    private int getChildCount(File f) {
        File[] fs = f.listFiles();
        return fs == null ? 0 : fs.length;
    }

    /**
     * 时间格式化为2016-07-03格式
     *
     * @param time
     * @return
     */
    private String formatData(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sf.format(new Date(time));
        return str;
    }


    /**
     * 格式化文件大小
     *
     * @param fileSize
     * @return
     */
    public static String compSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (fileSize >= 1073741824) {
            return df.format(fileSize / 1073741824) + "GB";
        } else if (fileSize >= 1048576) {
            return df.format(fileSize / 1048576) + "MB";
        } else if (fileSize >= 1024) {
            return df.format(fileSize / 1024) + "KB";
        } else {
            return df.format(fileSize) + "B";
        }
    }

    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     * @return boolean
     */
    private static void copyFile(String oldPath, String newPath) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                in = new BufferedInputStream(new FileInputStream(oldfile));
                out = new BufferedOutputStream(new FileOutputStream(newPath));
                int byteread = 0;
                byte[] buffer = new byte[1024];
                while ((byteread = in.read(buffer)) != -1) {
                    out.write(buffer, 0, byteread);
                }
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("复制异常");
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 移动文件
     *
     * @param oldPath String
     * @param newPath String
     */
    private static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }

    /**
     * 删除文件
     *
     * @param filePathAndName
     */
    private static boolean delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            File myDelFile = new File(filePath);
            return myDelFile.delete();
        } catch (Exception e) {
            Log.e("error", "删除错误");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件夹复制
     *
     * @param from 起始文件夹
     * @param to   目标文件夹
     * @return
     */
    private static boolean copyDirectiory(String from, String to) {
        LinkedList<File> list = new LinkedList<File>();
        File f = new File(from);
        // 记录拷贝的起始文件夹的父级目录
        String fromParent = f.getParent();
        // 要遍历的内容先进入队列
        list.addLast(f);
        while (!list.isEmpty()) {
            // 获取头部元素
            File head = list.removeFirst();
            // 获取要拷贝的原文件的路径
            String path = head.getAbsolutePath();
            String toPath;
            // 判断文件夹路径后有没有分隔符
            if (to.endsWith(File.separator)) {
                // 新建目标文件夹
                toPath = to + path.substring(fromParent.length());
            } else {
                toPath = to + File.separator + path.substring(fromParent.length());
            }
            //创建目标文件夹
            new File(toPath).mkdir();
            // 遍历头部元素（文件夹）
            File[] fs = head.listFiles();
            for (int i = 0; i < fs.length; i++) {
                File child = fs[i];
                if (child.isDirectory()) {
                    list.addLast(child);
                } else {
                    String fromFile = child.getAbsolutePath();
                    //目标文件路径
                    String targetPath;
                    // 判断文件夹路径后有没有分隔符
                    if (to.endsWith(File.separator)) {
                        // 新建目标文件夹
                        targetPath = to + fromFile.substring(fromParent.length());
                    } else {
                        targetPath = to + File.separator + fromFile.substring(fromParent.length());
                    }
                    copyFile(fromFile, targetPath);
                }
            }
        }
        return true;
    }

     /**
     * 移动文件夹
     *
     * @param sourceDir
     * @param targetDir
     */
    private static void moveDirectiory(String sourceDir, String targetDir) {
        copyDirectiory(sourceDir, targetDir);
        deleteDirectiory(new File(sourceDir));
    }

    //复制文件（或者文件夹）
    public static void copy(String oldPath, String targetPath) {
        File file = new File(oldPath);
        Log.e("tag", "oldPath:" + oldPath + " targetPath:" + targetPath);
        if (file.isDirectory()) {
            copyDirectiory(oldPath, targetPath);
        } else {
            copyFile(oldPath, targetPath);
        }
    }

    //移动文件
    public static void move(String oldPath, String targetPath) {
        File file = new File(oldPath);
        if (file.isDirectory()) {
            moveDirectiory(oldPath, targetPath);
        } else {
            moveFile(oldPath, targetPath);
        }
    }

    public static void delete(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            deleteDirectiory(f);
        } else {
            f.delete();
        }
    }

    private static void deleteDirectiory(File f){
        Stack<File> stack = new Stack<File>();
        // 先从传进来的f开始遍历
        stack.push(f);
        while (!stack.isEmpty()) {
            // 弹出栈顶元素
            File top = stack.peek();
            // 遍历栈顶文件夹
            File[] fs = top.listFiles(); // 整个遍历依赖于文件夹，搜索的目标是具体的文件类型
            if (fs == null || fs.length == 0) {
                top.delete();
                stack.pop();
                continue;
            }
            for (int i = 0; i < fs.length; i++) {
                File child = fs[i];
                // 如果child是文件夹对象则进栈
                if (child.isDirectory()) {
                    stack.push(child);
                } else {
                    // 在这里找到需要的文件(记录结果)
                    child.delete();
                }
            }
        }
    }

    public static void startSystemIntent(Context context, Uri playUri,
                                         String type) {
        Intent innerIntent = new Intent(Intent.ACTION_VIEW);
        innerIntent.setDataAndType(playUri, type);
        Intent playIntent = Intent.createChooser(innerIntent, null);
        context.startActivity(playIntent);
    }

    public static String getMIMEType(File file) {
        String fileName = file.getName();
        String fileType = FileUtils.getFileType(fileName);
        return getMIMEType(fileType);
    }

    /**
     * 获取文件mime类型
     */
    public static String getMIMEType(String fileType) {

        String type = null;
        if (fileType != null) {
            if (FileUtils.isAudio(fileType)) {
                type = "audio/*";
            } else if (FileUtils.isVideo(fileType)) {
                type = "video/*";
            } else if (fileType.equals("jpg") || fileType.equals("gif")
                    || fileType.equals("png") || fileType.equals("jpeg")
                    || fileType.equals("bmp") || fileType.equals("tiff")
                    || fileType.equals("tif") || fileType.equals("wbmp")
                    || fileType.equals("ief") || fileType.equals("jpe")
                    || fileType.equals("djvu") || fileType.equals("djv")
                    || fileType.equals("rp") || fileType.equals("ras")
                    || fileType.equals("ico") || fileType.equals("pnm")
                    || fileType.equals("pbm") || fileType.equals("pgm")
                    || fileType.equals("ppm") || fileType.equals("rgb")
                    || fileType.equals("xbm") || fileType.equals("xpm")
                    || fileType.equals("xwd")) {
                type = "image/*";
            } else if (isText(fileType)) {
                type = "text/*";
            } else if (fileType.equals("apk")) {
                type = "application/vnd.android.package-archive";
            } else if (fileType.equals("doc") || fileType.equals("docx")) {
                type = "application/msword";
            } else if (fileType.equals("xls") || fileType.equals("xlsx")) {
                type = "application/vnd.ms-excel";
            } else if (fileType.equals("ppt") || fileType.equals("pptx")) {
                type = "application/vnd.ms-powerpoint";
            } else if (fileType.equals("pdf")) {
                type = "application/pdf";// ϵͳ���г����п��ܴ��ı��ļ��ĳ���ѡ����
            } else {
                type = "*/*";
            }
        }
        return type;
    }

    public static boolean isText(String type) {

        if (type.equals("txt") || type.equals("json")) {
            return true;
        }
        return false;
    }


    /**
     * 获取apk图标
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static Bitmap getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                Drawable d = appInfo.loadIcon(pm);
                if (null != d) {
                    BitmapDrawable bd = (BitmapDrawable) d;
                    return bd.getBitmap();
                }
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }

}
