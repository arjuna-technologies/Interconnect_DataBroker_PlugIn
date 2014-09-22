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
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataSink;
import com.arjuna.databroker.data.jee.annotation.DataConsumerInjection;

public class DOMDocumentProviderDataSink implements DataSink
{
    private static final Logger logger = Logger.getLogger(DOMDocumentProviderDataSink.class.getName());

    public static final String ENDPOINTPATH_PROPERTYNAME = "Endpoint Path";

    public DOMDocumentProviderDataSink(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "DOMDocumentProviderDataSink: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _endpointId = properties.get(ENDPOINTPATH_PROPERTYNAME);
        
        try
        {
            _domDocumentProviderJunction = (DOMDocumentProviderJunction) new InitialContext().lookup("java:global/interconnect-plugin-ear-1.0.0p1m1/interconnect-domdocument-1.0.0p1m1/DOMDocumentProviderJunction");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "DOMDocumentProviderDataSink: no domDocumentProviderJunction found", throwable);
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

    public void consume(Document data)
    {
        logger.log(Level.FINE, "DOMDocumentProviderDataSink.consume");

        if (_domDocumentProviderJunction != null)
            _domDocumentProviderJunction.deposit(_endpointId, data);
        else
            logger.log(Level.WARNING, "DOMDocumentProviderDataSink.consume: no domDocumentProviderJunction");
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

    private DataFlow               _dataFlow;
    private String                 _name;
    private Map<String, String>    _properties;
    @DataConsumerInjection(methodName="consume")
    private DataConsumer<Document> _dataConsumer;

    private DOMDocumentProviderJunction _domDocumentProviderJunction;
}
