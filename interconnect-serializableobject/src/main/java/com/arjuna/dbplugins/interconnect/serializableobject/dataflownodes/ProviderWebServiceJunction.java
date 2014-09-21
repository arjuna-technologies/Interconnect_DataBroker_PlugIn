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
public class ProviderWebServiceJunction
{
    private static final Logger logger = Logger.getLogger(ProviderWebServiceJunction.class.getName());

    public ProviderWebServiceJunction()
    {
        logger.log(Level.FINE, "ProviderWebServiceJunction");

        _syncObject            = new Object();
        _serializableObjectMap = new HashMap<String, Serializable>();
    }

    public void deposit(String id, Serializable serializableObject)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "ProviderWebServiceJunction.deposit: " + id);

            _serializableObjectMap.put(id, serializableObject);
        }
    }

    public Serializable withdraw(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "ProviderWebServiceJunction.withdraw: " + id);

            return _serializableObjectMap.remove(id);
        }
    }

    private Object                    _syncObject;
    private Map<String, Serializable> _serializableObjectMap;
}
