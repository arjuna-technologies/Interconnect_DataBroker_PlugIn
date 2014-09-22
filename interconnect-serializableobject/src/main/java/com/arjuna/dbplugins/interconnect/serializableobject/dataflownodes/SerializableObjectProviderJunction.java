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
public class SerializableObjectProviderJunction
{
    private static final Logger logger = Logger.getLogger(SerializableObjectProviderJunction.class.getName());

    public SerializableObjectProviderJunction()
    {
        logger.log(Level.FINE, "SerializableObjectProviderJunction");

        _syncObject            = new Object();
        _serializableObjectMap = new HashMap<String, Serializable>();
    }

    public void deposit(String id, Serializable serializableObject)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "SerializableObjectProviderJunction.deposit: " + id);

            _serializableObjectMap.put(id, serializableObject);
        }
    }

    public Serializable withdraw(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "SerializableObjectProviderJunction.withdraw: " + id);

            return _serializableObjectMap.remove(id);
        }
    }

    private Object                    _syncObject;
    private Map<String, Serializable> _serializableObjectMap;
}
