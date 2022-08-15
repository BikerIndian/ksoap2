package net.svishch.asoap.annotations;

import net.svishch.asoap.util.NewInstanceObject;
import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Field;

public class SoapActionUtil {
    private final NewInstanceObject newInstanceObject;

    public SoapActionUtil() {
        this.newInstanceObject = new NewInstanceObject();
    }

    public String getAnnotationValue(Object objIn) {

        Field[] fields = objIn.getClass().getDeclaredFields();
        SoapObject soapObject = new SoapObject();


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

}
