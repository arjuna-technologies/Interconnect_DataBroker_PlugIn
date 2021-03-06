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
public class DOMDocumentProviderJunction
{
    private static final Logger logger = Logger.getLogger(DOMDocumentProviderJunction.class.getName());

    public DOMDocumentProviderJunction()
    {
        logger.log(Level.FINE, "DOMDocumentProviderJunction");

        _syncObject  = new Object();
        _documentMap = new HashMap<String, Document>();
    }

    public void deposit(String id, Document document)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "DOMDocumentProviderJunction.deposit: " + id);

            _documentMap.put(id, document);
        }
    }

    public Document withdraw(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "DOMDocumentProviderJunction.withdraw: " + id);

            return _documentMap.remove(id);
        }
    }

    private Object                _syncObject;
    private Map<String, Document> _documentMap;
}
