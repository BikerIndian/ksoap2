package net.svishch.asoap.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationsUtil {
    static public <T extends Annotation> boolean isAnnotation(Field field, Class<T> classAnnotation) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getTypeName().equals(classAnnotation.getTypeName())) {
                return true;
            }
        }
        return false;
    }
}
