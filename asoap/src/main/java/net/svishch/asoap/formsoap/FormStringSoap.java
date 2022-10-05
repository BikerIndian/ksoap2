package net.svishch.asoap.formsoap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FormStringSoap {

    private SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);

    public FormStringSoap() {
        initEnvelope();
    }

    private void initEnvelope() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
    }

    SoapObject getSoapObject(String soap) throws XmlPullParserException, IOException {
        InputStream stream = new ByteArrayInputStream(soap.getBytes(StandardCharsets.UTF_8));
            envelope.parse(stream);
            return (SoapObject) envelope.getResponse();
    }

    SoapObject getSoapObject(String soap, SoapSerializationEnvelope envelope) throws XmlPullParserException, IOException {
        this.envelope = envelope;
        return getSoapObject(soap);
    }


}
