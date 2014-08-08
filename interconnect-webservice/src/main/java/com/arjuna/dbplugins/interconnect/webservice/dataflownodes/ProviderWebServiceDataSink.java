/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.webservice.dataflownodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import org.risbic.intraconnect.basic.BasicDataConsumer;
import org.w3c.dom.Document;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataSink;

public class ProviderWebServiceDataSink implements DataSink
{
    private static final Logger logger = Logger.getLogger(ProviderWebServiceDataSink.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public ProviderWebServiceDataSink(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "ProviderWebServiceDataSink: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _dataConsumer = new BasicDataConsumer<Document>(this, "consume", Document.class);

        _endpointId = properties.get(ENDPOINTPATH_PROPERTYNAME);
        
        try
        {
            _providerWebServiceJunction = (ProviderWebServiceJunction) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-webservice-1.0.0p1m1/ProviderWebServiceJunction");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "ProviderWebServiceDataSink: no providerWebServiceJunction found", throwable);
        }
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(_properties);
    }

    public void consume(Document data)
    {
        logger.log(Level.FINE, "ProviderWebServiceDataSink.consume");

        if (_providerWebServiceJunction != null)
            _providerWebServiceJunction.deposit(_endpointId, data);
        else
            logger.log(Level.WARNING, "ProviderWebServiceDataSink.consume: no providerWebServiceJunction");
    }

    @Override
    public Collection<Class<?>> getDataConsumerDataClasses()
    {
        Set<Class<?>> dataConsumerDataClasses = new HashSet<Class<?>>();

        dataConsumerDataClasses.add(Document.class);

        return dataConsumerDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataConsumer<T> getDataConsumer(Class<T> dataClass)
    {
        if (dataClass == Document.class)
            return (DataConsumer<T>) _dataConsumer;
        else
            return null;
    }

    private String _endpointId;

    private String                 _name;
    private Map<String, String>    _properties;
    private DataConsumer<Document> _dataConsumer;

    private ProviderWebServiceJunction _providerWebServiceJunction;
}
