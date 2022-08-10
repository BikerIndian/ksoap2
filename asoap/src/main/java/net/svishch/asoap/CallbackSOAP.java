package net.svishch.asoap;

import org.ksoap2.serialization.SoapObject;

public interface CallbackSOAP {
    void result(SoapObject result);
}
