package MicroRpc.framework.tools.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
    专门处理IO操作
 */
public class IOUtils {
    private final static ClassLoader CLASS_LOADER= IOUtils.class.getClassLoader();
    private final static String LOADER_CLASS_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    /*
    检查路径格式
 */
    private static boolean checkClassPathForFile(String finalScanPath) {
        return true;
    }

    public static void main(String[] args) {
        List<Class> classes = scanClassForTotalFilePath(LOADER_CLASS_PATH);
        for (Class aClass : classes) {
            System.out.println(aClass);
        }
    }
    public static List<Class> scanClassForTotalFilePath(){
        return scanClassForTotalFilePath(LOADER_CLASS_PATH);
    }
    /*
     获取总路径下的所有类文件:通过总文件路径
  */
    public static List<Class> scanClassForTotalFilePath(String finalScanPath) {

        File file=null;
        if (checkClassPathForFile(finalScanPath))
            file=new File(finalScanPath);
        if (file==null){
            System.out.println("【error】 文件路径异常..");
            throw new RuntimeException();
        }
        List<Class>clazzList=new ArrayList<>();
        try {
            final String TAG="classes";
            String absolutePath = file.getAbsolutePath();
            if (absolutePath.endsWith(TAG))
                scanClassForFile(file,clazzList, absolutePath);
            else{
                scanClassForFile(file,clazzList, absolutePath.substring(0,absolutePath.lastIndexOf(TAG)+TAG.length()));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazzList;
    }

    /*
        通过componentScan的路径查找class列表

    public static List<Class> scanClassForScanPath(String queryScanPath,String CLASS_LODAR_PATH) {
        String finalScanPath=buildFinalScanPath(queryScanPath,CLASS_LODAR_PATH);
        return scanClassForTotalFilePath(finalScanPath);
    }



    public static String buildFinalScanPath(String queryScanPath, String class_lodar_path) {
        queryScanPath=queryScanPath.replace(".",File.separator);
        return class_lodar_path+queryScanPath;
    }


     */

    /*
        通过传进来的根file查找List
     */
    public static void scanClassForFile(File file, List<Class> clazzList, String rootPath) throws ClassNotFoundException {
        recursionFindClass(file, clazzList, rootPath);
    }

    /*
        递归寻找类文件
    */
    private static void recursionFindClass(File file, List<Class> clazzList, String rootPath) throws ClassNotFoundException {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                recursionFindClass(f, clazzList, rootPath);
            }
        }else {
            String classLoaderPathName = getClassLoaderPathNameForFilePath(file.getAbsolutePath(), rootPath);
            if (classLoaderPathName==null)
                return;
            Class<?> clazz = CLASS_LOADER.loadClass(classLoaderPathName);
            if (clazz!=null)
                clazzList.add(clazz);
        }
    }

    private static String getClassLoaderPathNameForFilePath(String absolutePath,String rootPath) {
        //通过文件路径获取可被类加载器加载的路径:
        if (-1 == absolutePath.lastIndexOf(".class"))
            return null;
        String p1 = absolutePath
                .replace(rootPath + File.separator, "")
                .replace(File.separator, ".");

        return p1.substring(0,p1.lastIndexOf(".class"));
    }

    private static String transferToFilePath(String classPackgePath){
        return classPackgePath.replace(".",File.separator);
    }
    private static String transferToClassPackgePath(String filePath){
        return null;
    }


    public static List<Class> scanClassForPackgeName(String packgeName, boolean isContainChildPackge) {

        String transferToFilePath = transferToFilePath(packgeName);
        String filePath=(LOADER_CLASS_PATH+transferToFilePath);
        File file=null;
        if (checkClassPathForFile(filePath))
            file=new File(filePath);
        if (file==null){
            System.out.println("【error】 文件路径异常..");
            throw new RuntimeException();
        }
        List<Class>clazzList=new ArrayList<>();
        try {
            String rootPath=getRootPathFromPackgeFilePath(file.getAbsolutePath(),transferToFilePath);
            if (isContainChildPackge)
                scanClassForFile(file,clazzList,rootPath);
            else {
                //不包含子包
                scanClassForFileNotRecursion(file,clazzList,rootPath);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazzList;


    }
    /*
        不递归查找
     */
    private static void scanClassForFileNotRecursion(File file, List<Class> clazzList, String rootPath) throws ClassNotFoundException {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()){
                    String classLoaderPathName = getClassLoaderPathNameForFilePath(f.getAbsolutePath(), rootPath);
                    if (classLoaderPathName==null)
                        continue;
                    Class<?> clazz = CLASS_LOADER.loadClass(classLoaderPathName);
                    clazzList.add(clazz);
                }
            }
        }else {
            String classLoaderPathName = getClassLoaderPathNameForFilePath(file.getAbsolutePath(), rootPath);
            if (classLoaderPathName==null)
                return;
            Class<?> clazz = CLASS_LOADER.loadClass(classLoaderPathName);
            clazzList.add(clazz);
        }
    }


    /*
        e:xxx/com/xx/xx  --> e:xxx
     */
    private static String getRootPathFromPackgeFilePath(String absolutePath, String transferToFilePath) {
        return absolutePath.replace(File.separator+transferToFilePath,"");
    }
}
