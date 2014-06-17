/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.webservice.dataflownodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import org.w3c.dom.Document;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;

public class AcceptorWebServiceDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(AcceptorWebServiceDataSource.class.getName());

    public AcceptorWebServiceDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "AcceptorWebServiceDataSource: " + name + ", " + properties);

        _name       = name;
        _properties = properties;

        _id           = UUID.randomUUID().toString();
        _dataProvider = new BasicDataProvider<Document>(this);
    }

    @PostConstruct
    public void doRegister()
    {
        _acceptorWebServiceDispatcher.register(_id, this);
    }

    @PreDestroy
    public void doUnregister()
    {
        _acceptorWebServiceDispatcher.unregister(_id);
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

    private String                 _name;
    private Map<String, String>    _properties;
    private DataProvider<Document> _dataProvider;

    private String _id;
    @EJB
    private AcceptorWebServiceDispatcher _acceptorWebServiceDispatcher;
}
