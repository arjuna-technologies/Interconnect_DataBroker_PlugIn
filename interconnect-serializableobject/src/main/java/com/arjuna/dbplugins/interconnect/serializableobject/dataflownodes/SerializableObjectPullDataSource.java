/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.PostActivate;
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
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.jee.annotation.DataProviderInjection;
import com.arjuna.databroker.data.jee.annotation.PreDeactivated;

public class SerializableObjectPullDataSource extends TimerTask implements DataSource
{
    private static final Logger logger = Logger.getLogger(SerializableObjectPullDataSource.class.getName());

    public static final String SERVICEROOTURL_PROPERTYNAME = "Service Root URL";
    public static final String ENDPOINTPATH_PROPERTYNAME   = "Endpoint Path";
    public static final String SCHEDULEDELAY_PROPERTYNAME  = "Schedule Delay";
    public static final String SCHEDULEPERIOD_PROPERTYNAME = "Schedule Period";

    public SerializableObjectPullDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "SerializableObjectPullDataSource: " + name + ", " + properties);

        _name          = name;
        _properties    = properties;

        _serviceURL     = properties.get(SERVICEROOTURL_PROPERTYNAME);
        _endpointPath   = properties.get(ENDPOINTPATH_PROPERTYNAME);
        _scheduleDelay  = Long.parseLong(properties.get(SCHEDULEDELAY_PROPERTYNAME));
        _schedulePeriod = Long.parseLong(properties.get(SCHEDULEPERIOD_PROPERTYNAME));
    }

    @PostActivate
    public void activateTimer()
    {
        _timer = new Timer(true);
        _timer.scheduleAtFixedRate(this, _scheduleDelay, _schedulePeriod);
    }

    @PreDeactivated
    public void deactivateTimer()
    {
        _timer.cancel();
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

    @Override
    public void run()
    {
        logger.log(Level.FINE, "SerializableObjectPullDataSource.run");

        Serializable result = null;
        try
        {
            MessageFactory messageFactory   = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage    request          = messageFactory.createMessage();
            SOAPPart       requestPart      = request.getSOAPPart();
            SOAPEnvelope   requestEnvelope  = requestPart.getEnvelope();
            SOAPBody       requestBody      = requestEnvelope.getBody();
            SOAPElement    requestElement   = requestBody.addChildElement(CommonDefs.INTERCONNECT_OPERATIONNAME_PROVIDER_PROVIDEDATA, "ic", CommonDefs.INTERCONNECT_NAMESPACE);
            SOAPElement    requestParameter = requestElement.addChildElement(CommonDefs.INTERCONNECT_OBTAINDATA_PARAMETERNAME_ID, "ic");
            requestParameter.addTextNode(_endpointPath);

            if (logger.isLoggable(Level.FINER))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINER, "SerializableObjectPullDataSource.run: request = " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection        connection        = connectionFactory.createConnection();

            SOAPMessage responce = connection.call(request, _serviceURL+ "/" + CommonDefs.INTERCONNECT_SERVICE_PATH + "/" + CommonDefs.INTERCONNECT_SERVICENAME_PROVIDER);

            if (logger.isLoggable(Level.FINER))
            {
                ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
                responce.writeTo(responceOutputStream);
                logger.log(Level.FINER, "SerializableObjectPullDataSource.run: responce: " + responceOutputStream.toString());
                responceOutputStream.close();
            }

            SOAPPart     responcePart         = responce.getSOAPPart();
            SOAPEnvelope responceEnvelope     = responcePart.getEnvelope();
            SOAPBody     responceBody         = responceEnvelope.getBody();
            Iterator<?>  responceBodyChildren = responceBody.getChildElements();

            if (responceBodyChildren.hasNext())
            {
                SOAPBodyElement dataElement = (SOAPBodyElement) responceBodyChildren.next();

                if ((dataElement.getNodeType() == SOAPBodyElement.ELEMENT_NODE) &&  dataElement.getNodeName().equals(CommonDefs.INTERCONNECT_RECEIVEDATA_PARAMETERNAME_ID))
                {
                    result = dataElement.getValue();
                }
                else
                    logger.log(Level.WARNING, "More than one child in responce body");

                if (responceBodyChildren.hasNext())
                    logger.log(Level.WARNING, "More than one child in responce body");
            }
            else
                logger.log(Level.WARNING, "No child in responce body");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "Problems with web service invoke", throwable);
        }

        if (result != null)
            _dataProvider.produce(result);
    }

    @Override
    public Collection<Class<?>> getDataProviderDataClasses()
    {
        Set<Class<?>> dataProviderDataClasses = new HashSet<Class<?>>();

        dataProviderDataClasses.add(Serializable.class);
        
        return dataProviderDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataProvider<T> getDataProvider(Class<T> dataClass)
    {
        if (dataClass == Serializable.class)
            return (DataProvider<T>) _dataProvider;
        else
            return null;
    }

    private String _serviceURL;
    private String _endpointPath;
    private Long   _scheduleDelay;
    private Long   _schedulePeriod;

    private Timer _timer;
    
    private DataFlow                   _dataFlow;
    private String                     _name;
    private Map<String, String>        _properties;
    @DataProviderInjection
    private DataProvider<Serializable> _dataProvider;
}
