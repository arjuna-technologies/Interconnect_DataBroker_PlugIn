/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.binary.dataflownodes;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;

@Singleton
public class BinaryAcceptorDispatcher
{
    private static final Logger logger = Logger.getLogger(BinaryAcceptorDispatcher.class.getName());

    public BinaryAcceptorDispatcher()
    {
        logger.log(Level.FINE, "BinaryAcceptorDispatcher");

        _syncObject                  = new Object();
        _binaryAcceptorDataSourceMap = new HashMap<String, BinaryAcceptorDataSource>();
    }

    public void dispatch(String id, String binary)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "BinaryAcceptorDispatcher.dispatch: " + id);

            BinaryAcceptorDataSource binaryAcceptorDataSource = _binaryAcceptorDataSourceMap.get(id);

            if (binaryAcceptorDataSource != null)
                binaryAcceptorDataSource.dispatch(binary);
            else
                logger.log(Level.FINE, "BinaryAcceptorDispatcher.dispatch: unable to find ' binary acceptor data source': " + id);
        }
    }

    public boolean register(String id, BinaryAcceptorDataSource binaryAcceptorDataSource)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "BinaryAcceptorDispatcher.register: " + id);

            if (! _binaryAcceptorDataSourceMap.containsKey(id))
            {
                _binaryAcceptorDataSourceMap.put(id, binaryAcceptorDataSource);
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
            logger.log(Level.FINE, "BinaryAcceptorDispatcher.unregister: " + id);

            return _binaryAcceptorDataSourceMap.remove(id) != null;
        }
    }

    private Object                                _syncObject;
    private Map<String, BinaryAcceptorDataSource> _binaryAcceptorDataSourceMap;
}
