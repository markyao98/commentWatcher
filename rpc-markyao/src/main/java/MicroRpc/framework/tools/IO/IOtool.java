package MicroRpc.framework.tools.IO;


import MicroRpc.framework.commons.ServiceProvider;
import MicroRpc.framework.commons.ServiceRefrence;
import MicroRpc.framework.tools.StringUtils;

import java.io.*;
import java.util.List;

public class IOtool {

    @ServiceRefrence
    private StringUtils stringUtils;

    public static void main(String[] args) {
        List<Class> classes = IOUtils.scanClassForTotalFilePath();
//        Class fieldAnnotationByXx = AnnotationUtils.getFieldAnnotationByXx(IOtool.class, ServiceRefrence.class);
//        System.out.println(fieldAnnotationByXx.getName());
    }
    public static void write(Object obj, OutputStream ops){
        writeByjdk(obj,ops);
    }

    private static void writeByjdk(Object obj, OutputStream ops) {
        ObjectOutputStream oos= null;
        try {
            oos = new ObjectOutputStream(ops);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object read(InputStream is){
        return readByJdk(is);
    }

    private static Object readByJdk(InputStream is) {
        ObjectInputStream ois= null;
        try {
            ois = new ObjectInputStream(is);
            Object result = ois.readObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
