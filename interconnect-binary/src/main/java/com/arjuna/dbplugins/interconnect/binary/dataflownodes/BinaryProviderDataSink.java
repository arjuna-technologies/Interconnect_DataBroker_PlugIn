/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.binary.dataflownodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataSink;
import com.arjuna.databroker.data.jee.annotation.DataConsumerInjection;

public class BinaryProviderDataSink implements DataSink
{
    private static final Logger logger = Logger.getLogger(BinaryProviderDataSink.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public BinaryProviderDataSink()
    {
        logger.log(Level.FINE, "BinaryProviderDataSink");
    }

    public BinaryProviderDataSink(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "BinaryProviderDataSink: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointId = properties.get(ENDPOINTPATH_PROPERTYNAME);

        try
        {
            _binaryProviderJunction = (BinaryProviderJunction) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-binary-1.0.0p1m1/BinaryProviderJunction");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "BinaryProviderDataSink: no binaryProviderJunction found", throwable);
        }
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
        logger.log(Level.FINE, "BinaryProviderDataSink.consume");

        if (_binaryProviderJunction != null)
        	_binaryProviderJunction.deposit(_endpointId, data);
        else
            logger.log(Level.WARNING, "BinaryProviderDataSink.consume: no binaryProviderJunction");
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

    private String _endpointId;

    private DataFlow                   _dataFlow;
    private String                     _name;
    private Map<String, String>        _properties;
    @DataConsumerInjection(methodName="consume")
    private DataConsumer<String>       _dataConsumer;

    private BinaryProviderJunction _binaryProviderJunction;
}
