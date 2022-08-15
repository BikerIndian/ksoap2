package net.svishch.asoap.client;


import net.svishch.asoap.CallbackSOAP;
import net.svishch.asoap.RecuestSOAP;
import net.svishch.asoap.Soap;
import net.svishch.asoap.annotations.SoapActionUtil;
import net.svishch.asoap.client.okhttp.HttpClient;
import okhttp3.Credentials;
import okhttp3.Headers;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Logger;


public class SoapClient {
    private String url = "";
    private String username = "";
    private String password = "";
    private boolean debug;
    private UrlSettings urlSettings;
    private SoapSerializationEnvelope envelope   = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    private OkHttp3Transport clientTransport;
    HttpClient httpClient;
    private Logger logger;

    public SoapClient(UrlSettings urlSettings) {
        this.logger = Logger.getLogger(this.getClass().getName());

        if (urlSettings == null) {
            throw new NullPointerException("Error: SoapClient(null)");
        }

        this.url = urlSettings.getUrl();
        this.username = urlSettings.getUser();
        this.password = urlSettings.getPassword();
        this.debug = urlSettings.isDebug();
        this.urlSettings = urlSettings;
        this.httpClient = new HttpClient(urlSettings);
        init();
    }

    private void init() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);

        Headers headers = Headers.of("Authorization", getPass());

        //httpClient.getSoap(url,soapAction,callbackString);
        clientTransport = new OkHttp3Transport.Builder(this.url)
                .headers(headers)
                //.client(httpClient.getClient(this.url))
                .debug(this.debug)
                .build();
    }

    public void get(CallbackSOAP callback, RecuestSOAP recuestSOAP ) throws IOException, XmlPullParserException {
        callback.result(get(recuestSOAP));
    }

    public SoapObject get( RecuestSOAP recuestSOAP ) throws IOException, XmlPullParserException {

        envelope.setOutputSoapObject(recuestSOAP.getSoapObject());
        clientTransport.call(recuestSOAP.getSoapAction(), envelope);
        SoapObject result = (SoapObject) envelope.getResponse();

        return result;
    }

    public <T> T get(Object recuest , Class<T> response ) throws IOException, XmlPullParserException {

        Soap soap = new Soap();
        String soapAction = new SoapActionUtil().getAnnotationValue(recuest);
        envelope.setOutputSoapObject(soap.toSoapObject(recuest));
        clientTransport.call(soapAction, envelope);
        SoapObject result = (SoapObject) envelope.getResponse();
        return soap.formSoap(result,response);

    }

    private String getPass()  {
        String credentials = Credentials.basic(this.username, password);
        return credentials;
    }

    private void sendLogger(String mess) {
        this.logger.info(mess);
    }
}
