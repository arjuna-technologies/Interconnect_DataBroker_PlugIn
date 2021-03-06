/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.binary.dataflownodes;

import java.io.ByteArrayOutputStream;
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
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataSink;
import com.arjuna.databroker.data.jee.annotation.DataConsumerInjection;

public class BinaryPushDataSink implements DataSink
{
    private static final Logger logger = Logger.getLogger(BinaryPushDataSink.class.getName());

    public static final String SERVICEROOTURL_PROPERTYNAME = "Service Root URL";
    public static final String ENDPOINTPATH_PROPERTYNAME   = "Endpoint Path";

    public BinaryPushDataSink()
    {
        logger.log(Level.FINE, "BinaryPushDataSink");
    }

    public BinaryPushDataSink(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "BinaryPushDataSink: " + name + ", " + properties);

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

    public void consume(String data)
    {
        logger.log(Level.FINE, "BinaryPushDataSink.consume");

        try
        {
            MessageFactory  messageFactory        = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage     request               = messageFactory.createMessage();
            SOAPPart        requestPart           = request.getSOAPPart();
            SOAPEnvelope    requestEnvelope       = requestPart.getEnvelope();
            SOAPBody        requestBody           = requestEnvelope.getBody();
            requestEnvelope.addNamespaceDeclaration("ic", CommonDefs.INTERCONNECT_NAMESPACE);

            QName           requestBodyQName   = requestBody.createQName(CommonDefs.INTERCONNECT_ACCEPTOR_ACCEPTDATA_REQUEST, "ic");
            SOAPBodyElement requestBodyElement = requestBody.addBodyElement(requestBodyQName);
            SOAPElement     requestIdElement   = requestBodyElement.addChildElement(CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_ID, "ic");
            SOAPElement     requestObjElement  = requestBodyElement.addChildElement(CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_BINARY, "ic");

            requestIdElement.setTextContent(_endpointPath);

            requestObjElement.setTextContent(data);

            if (logger.isLoggable(Level.FINE))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINE, "BinaryPushDataSink.consume: request = " + requestOutputStream.toString());
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
                logger.log(Level.FINE, "BinaryPushDataSink.consume: responce = " + responceOutputStream.toString());
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

        dataConsumerDataClasses.add(String.class);

        return dataConsumerDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataConsumer<T> getDataConsumer(Class<T> dataClass)
    {
        if (dataClass == String.class)
            return (DataConsumer<T>) _dataConsumer;
        else
            return null;
    }

    private String _serviceRootURL;
    private String _endpointPath;

    private DataFlow             _dataFlow;
    private String               _name;
    private Map<String, String>  _properties;
    @DataConsumerInjection(methodName="consume")
    private DataConsumer<String> _dataConsumer;
}
