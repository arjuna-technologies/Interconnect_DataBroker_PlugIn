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
import org.w3c.dom.Document;
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.jee.annotation.DataProviderInjection;

public class AcceptorWebServiceDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(AcceptorWebServiceDataSource.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public AcceptorWebServiceDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "AcceptorWebServiceDataSource: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointPath = properties.get(ENDPOINTPATH_PROPERTYNAME);

        try
        {
            _acceptorWebServiceDispatcher = (AcceptorWebServiceDispatcher) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-webservice-1.0.0p1m1/AcceptorWebServiceDispatcher");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "AcceptorWebServiceDataSource: no acceptorWebServiceDispatcher found", throwable);
        }

        if (_acceptorWebServiceDispatcher != null)
            _acceptorWebServiceDispatcher.register(_endpointPath, this);
        else
            logger.log(Level.WARNING, "AcceptorWebServiceDataSource.doRegister: no acceptorWebServiceDispatcher");
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

    public void dispatch(Document document)
    {
        logger.log(Level.FINE, "AcceptorWebServiceDataSource.onMessage");

        _dataProvider.produce(document);
    }

    @Override
    public Collection<Class<?>> getDataProviderDataClasses()
    {
        Set<Class<?>> dataProviderDataClasses = new HashSet<Class<?>>();

        dataProviderDataClasses.add(Document.class);
        
        return dataProviderDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataProvider<T> getDataProvider(Class<T> dataClass)
    {
        if (dataClass == Document.class)
            return (DataProvider<T>) _dataProvider;
        else
            return null;
    }

    private DataFlow               _dataFlow;
    private String                 _name;
    private Map<String, String>    _properties;
    @DataProviderInjection
    private DataProvider<Document> _dataProvider;

    private String _endpointPath;

    private AcceptorWebServiceDispatcher _acceptorWebServiceDispatcher;
}
