/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.domdocument.dataflownodes;

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

public class DOMDocumentAcceptorDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(DOMDocumentAcceptorDataSource.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public DOMDocumentAcceptorDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "DOMDocumentAcceptorDataSource: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointPath = properties.get(ENDPOINTPATH_PROPERTYNAME);

        try
        {
            _domDocumentAcceptorDispatcher = (DOMDocumentAcceptorDispatcher) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-domdocument-1.0.0p1m1/DOMDocumentAcceptorDispatcher");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "DOMDocumentAcceptorDataSource: no domDocumentAcceptorDispatcher found", throwable);
        }

        if (_domDocumentAcceptorDispatcher != null)
            _domDocumentAcceptorDispatcher.register(_endpointPath, this);
        else
            logger.log(Level.WARNING, "DOMDocumentAcceptorDataSource.doRegister: no domDocumentAcceptorDispatcher");
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
        logger.log(Level.FINE, "DOMDocumentAcceptorDataSource.dispatch");

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

    private DOMDocumentAcceptorDispatcher _domDocumentAcceptorDispatcher;
}
