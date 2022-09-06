package net.svishch.asoap.tosoap;

import net.svishch.asoap.annotations.*;
import net.svishch.asoap.util.AnnotationsUtil;
import net.svishch.asoap.util.ObjectUtil;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public class ToSoap {

    private final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    private final ObjectUtil objectUtil;
    private Logger logger;

    public ToSoap() {
        this.objectUtil = new ObjectUtil();
        initLog();
        initEnvelope();
    }

    private void initLog() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    private void initEnvelope() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
    }

    public String getString(Object obj) {

        SoapObject soapObject = getSoapObject(obj);
        envelope.setOutputSoapObject(soapObject);
        envelope.toString();

        return envelope.toString();
    }


    public SoapObject getSoapObject(Object obj) {

        Field[] fields = obj.getClass().getDeclaredFields();
        SoapObject soapObject = new SoapObject();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (AnnotationsUtil.isAnnotation(field, SoapAction.class)
                        || AnnotationsUtil.isAnnotation(field, SoapVersion.class)
                ) {
                } else if (AnnotationsUtil.isAnnotation(field, NameSpace.class) && field.getType().equals(String.class)) {
                    soapObject.setNamespace((String) field.get(obj));
                } else if (AnnotationsUtil.isAnnotation(field, NameMethod.class) && field.getType().equals(String.class)) {
                    soapObject.setName((String) field.get(obj));
                } else if (AnnotationsUtil.isAnnotation(field, AttributeName.class) || AnnotationsUtil.isAnnotation(field, Attributes.class)) {
                    addAttribute(obj, field, soapObject);
                } else {
                    addProperty(obj, field, soapObject);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return soapObject;
    }

    private Object getPrimitiveValue(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        SoapPrimitive result = new SoapPrimitive();

        for (Field field : fields) {
            field.setAccessible(true);
            if (AnnotationsUtil.isAnnotation(field, AttributeName.class) || AnnotationsUtil.isAnnotation(field, Attributes.class)) {
                addAttribute(obj, field, result);
            }

            if (AnnotationsUtil.isAnnotation(field, PrimitiveValue.class)) {
                try {
                    result.setValue(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private void addAttribute(Object obj, Field field, AttributeContainer soapObject) {
        try {

            if (AnnotationsUtil.isAnnotation(field, AttributeName.class)) {
                String name = AnnotationsSOAP.getAttributeNameValue(field);
                Object value = field.get(obj);
                soapObject.addAttribute(name, value);
            }

            if (AnnotationsUtil.isAnnotation(field, Attributes.class)) {
                // TODO Attributes MAP
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addProperty(Object obj, Field field, SoapObject soapObject) {

        String valueName = firstUpperCase(field.getName());
        if (AnnotationsUtil.isAnnotation(field, SerializedName.class)) {
            valueName = AnnotationsSOAP.getSerializedNameValue(field);
        }

        if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
            Object value = new ObjectUtil().getValueString(obj, field);
            soapObject.addProperty(valueName, value);
        } else if (field.getType().equals(List.class)) {
            addList(valueName, obj, field, soapObject);
        } else {
            Object value = new ObjectUtil().getValueObj(obj, field);
            soapObject.addProperty(valueName, getSoapObject(value));
        }

    }

    private void addList(String valueName, Object objIn, Field field, SoapObject soapObject) {

        try {
            List list = (List) field.get(objIn);
            if (list == null) {
                return;
            }

            for (Object obj : list) {
                if (AnnotationsSOAP.isPrimitive(obj)) {
                    soapObject.addProperty(valueName, getPrimitiveValue(obj));
                } else {
                    soapObject.addProperty(valueName, getSoapObject(obj));
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // First letter to uppercase
    public String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private void sendLogger(String mess) {
        this.logger.info(mess);
    }
}
