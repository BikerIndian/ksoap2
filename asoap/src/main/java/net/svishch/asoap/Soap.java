package net.svishch.asoap;

import net.svishch.asoap.formsoap.FormSoap;
import net.svishch.asoap.tosoap.ToSoap;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Soap {

    public String toSoap(Object src) {
        ToSoap toSoap = new ToSoap();
        return toSoap.getString(src);
    }

    public SoapObject toSoapObject(Object obj) {
        return new ToSoap().getSoapObject(obj);
    }


    public <T> T formSoap(SoapObject soap, Class<T> classOfT) {
        return new FormSoap().formSoap(soap,classOfT);
    }

    public <T> T formSoap(String soap, Class<T> classOfT) throws XmlPullParserException, IOException {
        return new FormSoap().formSoap(soap,classOfT);
    }

}
