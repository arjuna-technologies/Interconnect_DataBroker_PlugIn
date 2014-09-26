/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.tests;

import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import com.arjuna.databroker.data.jee.DataFlowNodeLifeCycleControl;
// import static org.junit.Assert.*;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.SerializableObjectPullDataSource;

public class PullTransferTest
{
    @Test
    public void simplestPullTransfer()
    {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(SerializableObjectPullDataSource.SERVICEROOTURL_PROPERTYNAME, "http://192.168.1.65");
        properties.put(SerializableObjectPullDataSource.ENDPOINTPATH_PROPERTYNAME, "foo");
        properties.put(SerializableObjectPullDataSource.SCHEDULEDELAY_PROPERTYNAME, "1000");
        properties.put(SerializableObjectPullDataSource.SCHEDULEPERIOD_PROPERTYNAME, "2000");

        SerializableObjectPullDataSource serializableObjectPullDataSource = new SerializableObjectPullDataSource("Serializable Object Provider Data Sink", properties);

        DataFlowNodeLifeCycleControl.processCreatedDataFlowNode(serializableObjectPullDataSource, null);

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException interruptedException)
        {
        }

        DataFlowNodeLifeCycleControl.removeDataFlowNode(serializableObjectPullDataSource);
    }
}