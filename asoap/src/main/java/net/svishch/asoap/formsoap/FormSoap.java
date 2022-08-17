package net.svishch.asoap.formsoap;


import net.svishch.asoap.ParseSoapUtil;
import net.svishch.asoap.annotations.AnnotationsUtil;
import net.svishch.asoap.debug.SoapObjectDebug;
import net.svishch.asoap.util.NewInstanceObject;
import org.ksoap2.serialization.AttributeInfo;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormSoap {

    private Logger logger;
    private NewInstanceObject newInstanceObject;

    public FormSoap() {
        this.logger = Logger.getLogger(FormSoap.class.getName());
        this.logger.setLevel(Level.INFO);
        this.newInstanceObject = new NewInstanceObject();
    }

    public <T> T formSoap(SoapObject soap, Class<T> classOfT) {

        T object = this.newInstanceObject.create(classOfT);

        if (soap == null) {
            return object;
        }

        for (int i = 0; i < soap.getAttributeCount(); i++) {
            AttributeInfo attribute = new AttributeInfo();
            soap.getAttributeInfo(i, attribute);
            setFieldAttribute(object, attribute, classOfT);
        }

        for (int i = 0; i < soap.getPropertyCount(); i++) {
            PropertyInfo propertyInfo = soap.getPropertyInfo(i);
            Object value = propertyInfo.getValue();
            try {
                setFieldValue(object, propertyInfo.name, value, classOfT);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return object;
    }

    public <T> T formSoap(SoapPrimitive soapPrimitive, Class<T> classOfT) {
        T object = this.newInstanceObject.create(classOfT);

        if (soapPrimitive == null) {
            return object;
        }

        for (int i = 0; i < soapPrimitive.getAttributeCount(); i++) {
            AttributeInfo attribute = new AttributeInfo();
            soapPrimitive.getAttributeInfo(i, attribute);
            setFieldAttribute(object, attribute, classOfT);
        }

        setPrimitiveValue(object, soapPrimitive);

        return object;
    }

    /* в object вставить значение из PrimitiveValue */
    private <T> void setPrimitiveValue(T object, SoapPrimitive soapPrimitive) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (new AnnotationsUtil().isPrimitiveValue(field)) {

                try {
                    addFieldValueType(object, field, soapPrimitive.getValue());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return;
            }

        }

        String mess = String.format("%n  Error: \t\tPrimitiveValue not found%n  Class: \t\t%s%n  Name: \t\t%s%n",
                object.getClass().getName(),
                soapPrimitive.getValue());

        sendLog(mess);

    }

    public <T> T formSoap(String soap, Class<T> classOfT) {

        SoapObject soapObject = new FormStringSoap().getSoapObject(soap);
        System.out.println(soap);
        new SoapObjectDebug().printSoapObject(soapObject);

        return formSoap(soapObject, classOfT);

    }

    private <T> void setFieldAttribute(T object, AttributeInfo attribute, Class<T> classOfT) {

        Field[] fields = classOfT.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (new AnnotationsUtil().isAttributeName(field, attribute.getName())) {
                try {
                    addFieldValueType(object, field, attribute.getValue());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        String mess = String.format("%n  Error: \t\tPrimitive attribute not found%n  Class: \t\t%s%n  Name: \t\t%s%n",
                object.getClass().getName(),
                attribute.getName());

        sendLog(mess);
    }

    private <T> void setFieldValue(Object object, String nameSoap, Object value, Class<T> classOfT) throws IllegalAccessException {

        Field[] fields = classOfT.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (new AnnotationsUtil().isSerializedName(field, nameSoap)) {
                addFieldValueType(object, field, value);
                return;
            }

        }
        String mess = String.format("%n  Error: \t\tObject not found%n  Class: \t\t%s%n  Name: \t\t%s%n",
                object.getClass().getName(),
                nameSoap);

        sendLog(mess);
    }

    private void addFieldValueType(Object object, Field field, Object value) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(String.class)) {
            field.set(object, ParseSoapUtil.checkString(value.toString()));
        } else if (fieldType.equals(long.class)) {
            field.setLong(object, ParseSoapUtil.checkLong(value.toString()));
        } else if (fieldType.equals(int.class)) {
            field.setInt(object, ParseSoapUtil.checkInt(value.toString()));
        } else if (fieldType.equals(boolean.class)) {
            field.setBoolean(object, ParseSoapUtil.checkBoolean(value));
        } else if (fieldType.equals(List.class)) {
            this.setList(object, field, value);
        } else if (value.getClass().getTypeName().equals(SoapObject.class.getTypeName())) {
            field.set(object, formSoap((SoapObject) value, field.getType()));
        } else if (value.getClass().getTypeName().equals(SoapPrimitive.class.getTypeName())) {
            field.set(object, formSoap((SoapPrimitive) value, field.getType()));
        } else {

            String mess = String.format("%n  Error: \t\tType mismatch%n  Class: \t\t%s%n  Property: \t%s%n  Type: \t\t%s != %s %n",
                    object.getClass().getName(),
                    field.getName(),
                    field.getType().getName(),
                    value.getClass().getName());

            sendLog(mess);

        }
    }

    private void setList(Object object, Field field, Object value) {

        Type type = field.getGenericType();
        String genericType = type.getTypeName().substring(type.getTypeName().indexOf('<') + 1, type.getTypeName().indexOf('>'));


        List objFiled = null;
        try {
            objFiled = (List) field.get(object);
            if (objFiled == null) {
                objFiled = new ArrayList();
                field.set(object, objFiled);
            }


            if (value instanceof SoapPrimitive) {
                SoapPrimitive soapPrimitive = (SoapPrimitive) value;
                objFiled.add(this.formSoap(soapPrimitive, Class.forName(genericType)));
            }

            if (value instanceof SoapObject) {
                SoapObject soapObject = (SoapObject) value;
                objFiled.add(this.formSoap(soapObject, Class.forName(genericType)));
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printSoap(SoapObject soapObject) {
        for (int i = 0; i < soapObject.getPropertyCount(); i++) {
            PropertyInfo propertyInfo = soapObject.getPropertyInfo(i);
            Object value1 = propertyInfo.getValue();
            System.out.println(propertyInfo.name + " = " + value1);
        }
    }

    private void sendLog(String mess) {
        this.logger.info(mess);
    }
}
