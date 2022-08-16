package net.svishch.asoap.client;


import net.svishch.asoap.Soap;
import net.svishch.asoap.annotations.AnnotationsUtil;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Logger;


public class SoapClient {


    private ClientSettings urlSettings;
    private SoapSerializationEnvelope envelope   = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    private OkHttpClient clientTransport;
    private Logger logger;

    public SoapClient(ClientSettings urlSettings) {
        this.logger = Logger.getLogger(this.getClass().getName());

        if (urlSettings == null) {
            throw new NullPointerException("Error: SoapClient(null)");
        }

        this.urlSettings = urlSettings;
        init();
    }

    private void init() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);

        //Headers headers = Headers.of("Authorization", getPass());
        //httpClient.getSoap(url,soapAction,callbackString);
        clientTransport = new OkHttpClient(urlSettings);

    }

    /*
    public void get(CallbackSOAP callback, RecuestSOAP recuestSOAP ) throws IOException, XmlPullParserException {
        callback.result(get(recuestSOAP));
    }

    public SoapObject get( RecuestSOAP recuestSOAP ) throws IOException, XmlPullParserException {

        envelope.setOutputSoapObject(recuestSOAP.getSoapObject());
        clientTransport.call(recuestSOAP.getSoapAction(), envelope);
        SoapObject result = (SoapObject) envelope.getResponse();

        return result;
    }
    */

    public <T> T get(Object recuest , Class<T> response ) throws IOException, XmlPullParserException {

        Soap soap = new Soap();
        String soapAction = new AnnotationsUtil().getSoapActionValue(recuest);
        int soapVersion = new AnnotationsUtil().getSoapVersion(recuest);
        if (soapVersion > 0) {
            envelope.version = soapVersion;
        }
        envelope.setOutputSoapObject(soap.toSoapObject(recuest));
        clientTransport.call(soapAction, envelope);
        SoapObject result = (SoapObject) envelope.getResponse();
        return soap.formSoap(result,response);

    }



    private void sendLogger(String mess) {
        this.logger.info(mess);
    }
}
