/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.tests;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import com.arjuna.databroker.data.core.DataFlowNodeLifeCycleControl;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.SerializableObjectPullDataSource;
import com.arjuna.dbutilities.testsupport.dataflownodes.lifecycle.TestJEEDataFlowNodeLifeCycleControl;

@RunWith(Arquillian.class)
public class PullTransferTest
{
    @Test
    public void simplestPullTransfer()
    {
        DataFlowNodeLifeCycleControl dataFlowNodeLifeCycleControl = new TestJEEDataFlowNodeLifeCycleControl();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(SerializableObjectPullDataSource.SERVICEROOTURL_PROPERTYNAME, "http://ouseburn:8080");
        properties.put(SerializableObjectPullDataSource.ENDPOINTPATH_PROPERTYNAME, "foo");
        properties.put(SerializableObjectPullDataSource.SCHEDULEDELAY_PROPERTYNAME, "1000");
        properties.put(SerializableObjectPullDataSource.SCHEDULEPERIOD_PROPERTYNAME, "2000");

        SerializableObjectPullDataSource serializableObjectPullDataSource = new SerializableObjectPullDataSource("Serializable Object Provider Data Sink", properties);

        dataFlowNodeLifeCycleControl.completeCreationAndActivateDataFlowNode(UUID.randomUUID().toString(), serializableObjectPullDataSource, null);

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException interruptedException)
        {
        }

        dataFlowNodeLifeCycleControl.removeDataFlowNode(serializableObjectPullDataSource);
    }
}
