package net.svishch.asoap.annotations;

import net.svishch.asoap.util.NewInstanceObject;

import java.lang.reflect.Field;

public class AnnotationsUtil {
    private final NewInstanceObject newInstanceObject;

    public AnnotationsUtil() {
        this.newInstanceObject = new NewInstanceObject();
    }

    public String getSoapActionValue(Object objIn) {

        Field[] fields = objIn.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                if (this.newInstanceObject.isAnnotation(field, SoapAction.class) && field.getType().equals(String.class)) {
                    return (String) field.get(objIn);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return "";
    }


    public Integer getSoapVersion(Object objIn) {

        Field[] fields = objIn.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                if (this.newInstanceObject.isAnnotation(field, SoapVersion.class) && field.getType().equals(int.class)) {
                    return (int) field.get(objIn);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String getAnnotationValue(Field field) {
        SerializedName annotation = field.getAnnotation(SerializedName.class);
        String name = "";

        if (annotation == null || annotation.value() == null) {
            return name;
        }

        return annotation.value();
    }
}
