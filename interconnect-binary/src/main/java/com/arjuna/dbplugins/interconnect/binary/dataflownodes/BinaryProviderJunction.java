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
public class BinaryProviderJunction
{
    private static final Logger logger = Logger.getLogger(BinaryProviderJunction.class.getName());

    public BinaryProviderJunction()
    {
        logger.log(Level.FINE, "BinaryProviderJunction");

        _syncObject            = new Object();
        _binaryMap = new HashMap<String, String>();
    }

    public void deposit(String id, String binary)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "BinaryProviderJunction.deposit: " + id);

            _binaryMap.put(id, binary);
        }
    }

    public String withdraw(String id)
    {
        synchronized (_syncObject)
        {
            logger.log(Level.FINE, "BinaryProviderJunction.withdraw: " + id);

            return _binaryMap.remove(id);
        }
    }

    private Object              _syncObject;
    private Map<String, String> _binaryMap;
}
