package net.svishch.asoap.annotations;

import net.svishch.asoap.util.AnnotationsUtil;
import net.svishch.asoap.util.NewInstanceObject;

import java.lang.reflect.Field;

public class AnnotationsSOAP {
    private final NewInstanceObject newInstanceObject;

    public AnnotationsSOAP() {
        this.newInstanceObject = new NewInstanceObject();
    }

    public String getSoapActionValue(Object objIn) {

        Field[] fields = objIn.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                if (AnnotationsUtil.isAnnotation(field, SoapAction.class) && field.getType().equals(String.class)) {
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

                if (AnnotationsUtil.isAnnotation(field, SoapVersion.class) && field.getType().equals(int.class)) {
                    return (int) field.get(objIn);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /* SerializedName */
    public String getSerializedNameValue(Field field) {
        SerializedName annotation = field.getAnnotation(SerializedName.class);
        String name = "";

        if (annotation == null || annotation.value() == null) {
            return name;
        }

        return annotation.value();
    }

    public boolean isSerializedName(Field field, String value) {
        String annotationValue = new AnnotationsSOAP().getSerializedNameValue(field);

        return value.equals(annotationValue)
                || value.equalsIgnoreCase(field.getName());

    }

    /* AttributeName */
    public boolean isAttributeName(Field field, String value) {

        String annotationValue = new AnnotationsSOAP().getAttributeNameValue(field);

        return value.equals(annotationValue)
                || value.equalsIgnoreCase(field.getName());

    }

    public String getAttributeNameValue(Field field) {
        AttributeName annotation = field.getAnnotation(AttributeName.class);
        String name = "";

        if (annotation == null || annotation.value() == null) {
            return name;
        }

        return annotation.value();
    }

    /* PrimitiveValue */
    public boolean isPrimitiveValue(Field field) {
        return AnnotationsUtil.isAnnotation(field, PrimitiveValue.class) ;
    }

    /* Attributes */
    public static boolean isAnnotationsAttributes(Field field) {
        return AnnotationsUtil.isAnnotation(field, Attributes.class) ;
    }

  /*
    public String getPrimitiveValue(Field field) {
        PrimitiveValue annotation = field.getAnnotation(PrimitiveValue.class);
        String name = "";

        if (annotation == null || annotation.value() == null) {
            return name;
        }

        return annotation.value();
    }

   */
}
