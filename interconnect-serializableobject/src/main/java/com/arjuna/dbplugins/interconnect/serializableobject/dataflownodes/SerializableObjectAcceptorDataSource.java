/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
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
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.jee.annotation.DataProviderInjection;

public class SerializableObjectAcceptorDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(SerializableObjectAcceptorDataSource.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public SerializableObjectAcceptorDataSource()
    {
        logger.log(Level.FINE, "SerializableObjectAcceptorDataSource");
    }

    public SerializableObjectAcceptorDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "SerializableObjectAcceptorDataSource: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointPath = properties.get(ENDPOINTPATH_PROPERTYNAME);

        try
        {
            _serializableObjectAcceptorDispatcher = (SerializableObjectAcceptorDispatcher) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-serializableobject-1.0.0p1m1/SerializableObjectAcceptorDispatcher");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "SerializableObjectAcceptorDataSource: no serializableObjectAcceptorDispatcher found", throwable);
        }

        if (_serializableObjectAcceptorDispatcher != null)
            _serializableObjectAcceptorDispatcher.register(_endpointPath, this);
        else
            logger.log(Level.WARNING, "SerializableObjectAcceptorDataSource.doRegister: no serializableObjectAcceptorDispatcher");
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

    public void dispatch(Serializable serializableObject)
    {
        logger.log(Level.FINE, "SerializableObjectAcceptorDataSource.dispatch");

        _dataProvider.produce(serializableObject);
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

    private DataFlow                   _dataFlow;
    private String                     _name;
    private Map<String, String>        _properties;
    @DataProviderInjection
    private DataProvider<Serializable> _dataProvider;

    private String _endpointPath;

    private SerializableObjectAcceptorDispatcher _serializableObjectAcceptorDispatcher;
}
