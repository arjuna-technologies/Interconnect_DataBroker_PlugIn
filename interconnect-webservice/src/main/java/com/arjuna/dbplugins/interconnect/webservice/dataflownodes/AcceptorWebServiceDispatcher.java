/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.webservice.dataflownodes;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import org.w3c.dom.Document;

@Singleton
public class AcceptorWebServiceDispatcher
{
    private static final Logger logger = Logger.getLogger(AcceptorWebServiceDispatcher.class.getName());

    public AcceptorWebServiceDispatcher()
    {
        logger.log(Level.FINE, "AcceptorWebServiceDispatcher");

        _syncObject                      = new Object();
        _acceptorWebServiceDataSourceMap = new HashMap<String, AcceptorWebServiceDataSource>();
    }

    public void dispatch(String id, Document document)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "AcceptorWebServiceDispatcher.dispatch: " + id);

            AcceptorWebServiceDataSource acceptorWebServiceDataSource = _acceptorWebServiceDataSourceMap.get(id);

            if (acceptorWebServiceDataSource != null)
                acceptorWebServiceDataSource.dispatch(document);
            else
                logger.log(Level.FINE, "AcceptorWebServiceDispatcher.dispatch: unable to find 'acceptor webservice data source': " + id);
        }
    }

    public boolean register(String id, AcceptorWebServiceDataSource acceptorWebServiceDataSource)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "AcceptorWebServiceDispatcher.register: " + id);

            if (! _acceptorWebServiceDataSourceMap.containsKey(id))
            {
                _acceptorWebServiceDataSourceMap.put(id, acceptorWebServiceDataSource);
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
            logger.log(Level.FINE, "AcceptorWebServiceDispatcher.unregister: " + id);

            return _acceptorWebServiceDataSourceMap.remove(id) != null;
        }
    }

    private Object                                    _syncObject;
    private Map<String, AcceptorWebServiceDataSource> _acceptorWebServiceDataSourceMap;
}
