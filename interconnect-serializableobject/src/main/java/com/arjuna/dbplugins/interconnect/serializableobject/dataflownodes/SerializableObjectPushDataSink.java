/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.apache.commons.codec.binary.Hex;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataSink;
import com.arjuna.databroker.data.jee.annotation.DataConsumerInjection;

public class SerializableObjectPushDataSink implements DataSink
{
    private static final Logger logger = Logger.getLogger(SerializableObjectPushDataSink.class.getName());

    public static final String SERVICEROOTURL_PROPERTYNAME = "Service Root URL";
    public static final String ENDPOINTPATH_PROPERTYNAME   = "Endpoint Path";

    public SerializableObjectPushDataSink(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "SerializableObjectPushDataSink: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _serviceRootURL = properties.get(SERVICEROOTURL_PROPERTYNAME);
        _endpointPath   = properties.get(ENDPOINTPATH_PROPERTYNAME);
    }

    @Override
    public DataFlow getDataFlow()
    {
        return _dataFlow;
    }

    @Override
    public void setDataFlow(DataFlow dataFlow)
    {
        _dataFlow = dataFlow;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public void setName(String name)
    {
        _name = name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(_properties);
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        _properties = properties;
    }

    public void consume(Serializable data)
    {
        logger.log(Level.FINE, "SerializableObjectPushDataSink.consume");

        try
        {
            MessageFactory  messageFactory        = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage     request               = messageFactory.createMessage();
            SOAPPart        requestPart           = request.getSOAPPart();
            SOAPEnvelope    requestEnvelope       = requestPart.getEnvelope();
            SOAPBody        requestBody           = requestEnvelope.getBody();
            requestEnvelope.addNamespaceDeclaration("ic", CommonDefs.INTERCONNECT_NAMESPACE);

            QName           requestBodyQName   = requestBody.createQName(CommonDefs.INTERCONNECT_PORTNAME_ACCEPTOR, "ic");
            SOAPBodyElement requestBodyElement = requestBody.addBodyElement(requestBodyQName);
            SOAPElement     requestIdElement   = requestBodyElement.addChildElement(CommonDefs.INTERCONNECT_RECEIVEDATA_PARAMETERNAME_ID, "ic");
            SOAPElement     requestObjElement  = requestBodyElement.addChildElement(CommonDefs.INTERCONNECT_RECEIVEDATA_PARAMETERNAME_SERIALIALIZEDOBJECT, "ic");

            requestIdElement.setTextContent(_endpointPath);

            ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream    objectObjectOutputStream    = new ObjectOutputStream(objectByteArrayOutputStream);
            objectObjectOutputStream.writeObject(data);
            objectObjectOutputStream.flush();
            requestObjElement.setTextContent(Hex.encodeHexString(objectByteArrayOutputStream.toByteArray()));
            objectObjectOutputStream.close();

            if (logger.isLoggable(Level.FINE))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINE, "SerializableObjectPushDataSink.consume: request = " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection        connection        = connectionFactory.createConnection();
            URL                   serviceURL        = new URL(_serviceRootURL + "/" + CommonDefs.INTERCONNECT_SERVICE_PATH + "/" + CommonDefs.INTERCONNECT_SERVICENAME_ACCEPTOR);

            SOAPMessage responce = connection.call(request, serviceURL);

            if (logger.isLoggable(Level.FINE))
            {
                ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
                responce.writeTo(responceOutputStream);
                logger.log(Level.FINE, "SerializableObjectPushDataSink.consume: responce = " + responceOutputStream.toString());
                responceOutputStream.close();
            }
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "Problems with web service invoke", throwable);
        }
    }

    @Override
    public Collection<Class<?>> getDataConsumerDataClasses()
    {
        Set<Class<?>> dataConsumerDataClasses = new HashSet<Class<?>>();

        dataConsumerDataClasses.add(Serializable.class);

        return dataConsumerDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataConsumer<T> getDataConsumer(Class<T> dataClass)
    {
        if (dataClass == Serializable.class)
            return (DataConsumer<T>) _dataConsumer;
        else
            return null;
    }

    private String _serviceRootURL;
    private String _endpointPath;

    private DataFlow                  _dataFlow;
    private String                    _name;
    private Map<String, String>       _properties;
    @DataConsumerInjection(methodName="consume")
    private DataConsumer<Serializable> _dataConsumer;
}
