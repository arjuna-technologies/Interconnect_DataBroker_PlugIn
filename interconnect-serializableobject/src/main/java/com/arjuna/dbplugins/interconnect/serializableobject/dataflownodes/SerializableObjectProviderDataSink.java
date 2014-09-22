/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes;

import java.io.Serializable;
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

public class SerializableObjectProviderDataSink implements DataSink
{
    private static final Logger logger = Logger.getLogger(SerializableObjectProviderDataSink.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public SerializableObjectProviderDataSink(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "SerializableObjectProviderDataSink: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointId = properties.get(ENDPOINTPATH_PROPERTYNAME);

        try
        {
            _serializableObjectProviderJunction = (SerializableObjectProviderJunction) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-serializableobject-1.0.0p1m1/SerializableObjectProviderJunction");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "SerializableObjectProviderDataSink: no serializableObjectProviderJunction found", throwable);
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

    public void consume(Serializable data)
    {
        logger.log(Level.FINE, "SerializableObjectProviderDataSink.consume");

        if (_serializableObjectProviderJunction != null)
            _serializableObjectProviderJunction.deposit(_endpointId, data);
        else
            logger.log(Level.WARNING, "SerializableObjectProviderDataSink.consume: no serializableObjectProviderJunction");
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

    private String _endpointId;

    private DataFlow                   _dataFlow;
    private String                     _name;
    private Map<String, String>        _properties;
    @DataConsumerInjection(methodName="consume")
    private DataConsumer<Serializable> _dataConsumer;

    private SerializableObjectProviderJunction _serializableObjectProviderJunction;
}
