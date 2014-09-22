/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;

@Singleton
public class SerializableObjectAcceptorDispatcher
{
    private static final Logger logger = Logger.getLogger(SerializableObjectAcceptorDispatcher.class.getName());

    public SerializableObjectAcceptorDispatcher()
    {
        logger.log(Level.FINE, "SerializableObjectAcceptorDispatcher");

        _syncObject                              = new Object();
        _serializableObjectAcceptorDataSourceMap = new HashMap<String, SerializableObjectAcceptorDataSource>();
    }

    public void dispatch(String id, Serializable serializableObject)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "SerializableObjectAcceptorDispatcher.dispatch: " + id);

            SerializableObjectAcceptorDataSource serializableObjectAcceptorDataSource = _serializableObjectAcceptorDataSourceMap.get(id);

            if (serializableObjectAcceptorDataSource != null)
                serializableObjectAcceptorDataSource.dispatch(serializableObject);
            else
                logger.log(Level.FINE, "SerializableObjectAcceptorDispatcher.dispatch: unable to find ' serializable object acceptor data source': " + id);
        }
    }

    public boolean register(String id, SerializableObjectAcceptorDataSource serializableObjectAcceptorDataSource)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "SerializableObjectAcceptorDispatcher.register: " + id);

            if (! _serializableObjectAcceptorDataSourceMap.containsKey(id))
            {
                _serializableObjectAcceptorDataSourceMap.put(id, serializableObjectAcceptorDataSource);
                return true;
            }
            else
                return false;
        }
    }

    public boolean unregister(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "SerializableObjectAcceptorDispatcher.unregister: " + id);

            return _serializableObjectAcceptorDataSourceMap.remove(id) != null;
        }
    }

    private Object                                            _syncObject;
    private Map<String, SerializableObjectAcceptorDataSource> _serializableObjectAcceptorDataSourceMap;
}
