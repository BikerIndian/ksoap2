package net.svishch.asoap.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class LogUtil {
    public static String getLogMess(String messErrIn, Object obj, Field field){

        String annotations = "";
        for (Annotation annotation :  field.getAnnotations()) {
            annotations = annotations + " @" + annotation.annotationType().getSimpleName();
        }

        String mess = String.format("%n  Error: \t\t %s %n  Class: \t\t %s%n  Annotations:\t%s%n  Name: \t\t %s (%s)%n",
                messErrIn,
                obj.getClass().getName(),
                annotations,
                field.getName(),
                field.getGenericType());

        return mess;
    }
}
