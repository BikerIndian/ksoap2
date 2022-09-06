package net.svishch.asoap.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

public class ObjectUtil {
    private Logger logger;

    public ObjectUtil() {
        initLog();
    }

    private void initLog() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public <T> T create(Class<T> classOfT) {
        T object = null;
        try {

            Constructor<T> constructor = classOfT.getDeclaredConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return object;
    }


    private void printFields(Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.printf("%s %s %s%n",
                    Modifier.toString(field.getModifiers()),
                    field.getType().getSimpleName(),
                    field.getName()
            );
        }
    }

    public Object getValueString(Object obj, Field field) {

        Class<?> fieldType = field.getType();

        try {

            if (fieldType.isPrimitive() || fieldType.equals(String.class)
            ) {
                return field.get(obj);
            }

            sendLogger(LogUtil.getLogMess("This is not a string type",obj,field));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
        /*
        Class<?> fieldType = field.getType();
            else if (fieldType.equals(long.class)) {
                field.setLong(object, ParseSoapUtil.checkLong(value.toString()));
            } else if (fieldType.equals(int.class)) {
                field.setInt(object, ParseSoapUtil.checkInt(value.toString()));
            } else if (fieldType.equals(boolean.class)) {
                field.setBoolean(object, ParseSoapUtil.checkBoolean(value));
            } else if (fieldType.equals(List.class)) {
                this.setList(object, field, value);
            } else if (value.getClass().getTypeName().equals(SoapObject.class.getTypeName())) {
                field.set(object, formSoap((SoapObject) value, field.getType()));
            } else {
                System.err.println(field.getType() + " != " + value.getClass().getName());
            }
        */
    }

    public Object getValueObj(Object obj, Field field) {

        try {
            return field.get(obj);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void sendLogger(String mess) {
        this.logger.info(mess);
    }
}
