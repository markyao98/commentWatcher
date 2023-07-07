package MicroRpc.framework.tools.IO;



import MicroRpc.framework.commons.ServiceProvider;
import MicroRpc.framework.commons.ServiceRefrence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationUtils {

    public static boolean isAnnotationOfXX(Class clazz,Class xx){
        if (clazz.getAnnotations().length==0)
            return false;
        if (clazz.isAnnotationPresent(xx))
            return true;
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(xx)){
                return true;
            }
        }
        return false;
    }
/*

    public static boolean isPrototype(Class clazz){
        if (clazz.getAnnotations().length==0)
            return false;
        if (clazz.isAnnotationPresent(Scope.class)){
            Scope scope = (Scope) clazz.getAnnotation(Scope.class);
            if (scope!=null && scope.value().equals(ScopeEnum.PROTOTYPE.value())){
                return true;
            }
        }
        return false;
    }

    public static boolean isAnnotationOfAspect(Class clazz) {
        if (clazz.isAnnotationPresent(Aspect.class) ){
            return true;
        }
        return false;
    }
*/

    public static boolean hasFieldAnnotationByXx(Class clazz,Class xx){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotations().length==0) {
                continue;
            }
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(xx)){
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Class> getFieldAnnotationByXx(Class clazz, Class xx){
        Field[] fields = clazz.getDeclaredFields();
        List<Class>fieldlist=new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotations().length==0) {
                continue;
            }
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().equals(xx)){
                    fieldlist.add(field.getType());
                }
            }
        }
        return fieldlist;
    }
    public static List<Field> getFieldAnnotationByXx0(Class clazz, Class xx){
        Field[] fields = clazz.getDeclaredFields();
        List<Field>fieldlist=new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotations().length==0) {
                continue;
            }
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(xx)){
                    fieldlist.add(field);
                }
            }
        }
        return fieldlist;
    }


//    public static List<Class> getFieldAnnotationByXx(Class clazz, Class xx){
//        Field[] fields = clazz.getDeclaredFields();
//        List<Class>fieldlist=new ArrayList<>();
//        for (Field field : fields) {
//            if (field.getAnnotations().length==0) {
//                continue;
//            }
//            for (Annotation annotation : field.getAnnotations()) {
//                if (annotation.annotationType().equals(xx)){
//                    fieldlist.add(field.getType());
//                }
//            }
//        }
//        return null;
//    }

    public static Annotation[] getAnnotationByType(Class clazz) {
        return clazz.getAnnotations();
    }

    public static Map<Annotation,Method> getMethodAnnotationByType(Class clazz) {
        Map<Annotation,Method> mp=new HashMap<>();
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            Annotation[] as = declaredMethod.getDeclaredAnnotations();
            if (as.length>0){
                for (Annotation a : as) {
                    mp.put(a,declaredMethod);
                }
            }
        }
        return mp;
    }
}
