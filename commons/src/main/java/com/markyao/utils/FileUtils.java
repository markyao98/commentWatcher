package com.markyao.utils;

import java.io.*;

public class FileUtils {
//    public final static String staticPath=FileUtils.class.getClassLoader().getResource("static").getPath()+"/"+"imgs/";
    public final static String staticPath="/E:/develop/workspace/comments-watcher/app-show/target/classes/static/imgs/";


    public static void main(String[] args) throws IOException {
        System.out.println(staticPath);
//        String staticPath=FileUtils.class.getClassLoader().getResource("static").getPath()+"/"+"imgs/"+"xx.png";
//        File file=new File(staticPath.substring(1));
//        System.out.println(file.isDirectory());
//        System.out.println(staticPath);
//        String sPath="E:\\study_daydayup\\project\\douyin-harvest\\analyze\\results\\wordCloud\\xx.png";
//        String dPath=staticPath;
//        copyImageFile(sPath,dPath);
    }
    public static boolean copyImageFile(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);

        // 检查源文件是否存在
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return false;
        }

        // 检查目标路径是否存在，如果不存在则创建目录
        File targetDirectory = targetFile.getParentFile();
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        // 创建输入流和输出流
        FileInputStream fis = new FileInputStream(sourceFile);
        FileOutputStream fos = new FileOutputStream(targetFile);

        // 缓冲区大小，可以根据需要进行调整
        byte[] buffer = new byte[4096];
        int bytesRead;

        // 逐个读取字节并写入目标文件
        while ((bytesRead = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }

        // 关闭流
        fis.close();
        fos.close();
        return true;
    }
    public static void copyFile(String sourcePath,String destPath) {
        File file=new File(sourcePath);
        File file1=new File(destPath);
        try {
            file1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileInputStream fis=new FileInputStream(file);
            FileOutputStream fos=new FileOutputStream(destPath);) {
            if (file.exists()){
//                FileInputStream fis=new FileInputStream(file);
//                FileOutputStream fos=new FileOutputStream(destPath);
                byte []bys=new byte[1024];
                if (fis.read(bys)!=-1) {
                    fos.write(bys);
                    fos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String writeToDest(String destDictory,String fileName,InputStream is){
        File dic=new File(destDictory);
        if (!dic.exists()){
            dic.mkdirs();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(destDictory+fileName)){
            byte[] bys = new byte[1024];
            int byteRead;
            while ((byteRead=is.read(bys))!=-1){
                fileOutputStream.write(bys,0,byteRead);
            }
            return destDictory+fileName;
        }catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }
}
