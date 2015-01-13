/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.domdocument.dataflownodes;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import org.w3c.dom.Document;

@Singleton
public class DOMDocumentAcceptorDispatcher
{
    private static final Logger logger = Logger.getLogger(DOMDocumentAcceptorDispatcher.class.getName());

    public DOMDocumentAcceptorDispatcher()
    {
        logger.log(Level.FINE, "DOMDocumentAcceptorDispatcher");

        _syncObject                      = new Object();
        _domDocumentAcceptorDataSourceMap = new HashMap<String, DOMDocumentAcceptorDataSource>();
    }

    public void dispatch(String id, Document document)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "DOMDocumentAcceptorDispatcher.dispatch: " + id);

            DOMDocumentAcceptorDataSource domDocumentAcceptorDataSource = _domDocumentAcceptorDataSourceMap.get(id);

            if (domDocumentAcceptorDataSource != null)
                domDocumentAcceptorDataSource.dispatch(document);
            else
                logger.log(Level.FINE, "DOMDocumentAcceptorDispatcher.dispatch: unable to find 'dom document acceptor data source': " + id);
        }
    }

    public boolean register(String id, DOMDocumentAcceptorDataSource domDocumentAcceptorDataSource)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "DOMDocumentAcceptorDispatcher.register: " + id);

            if (! _domDocumentAcceptorDataSourceMap.containsKey(id))
            {
                _domDocumentAcceptorDataSourceMap.put(id, domDocumentAcceptorDataSource);
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
            logger.log(Level.FINE, "DOMDocumentAcceptorDispatcher.unregister: " + id);

            return _domDocumentAcceptorDataSourceMap.remove(id) != null;
        }
    }

    private Object                                     _syncObject;
    private Map<String, DOMDocumentAcceptorDataSource> _domDocumentAcceptorDataSourceMap;
}
