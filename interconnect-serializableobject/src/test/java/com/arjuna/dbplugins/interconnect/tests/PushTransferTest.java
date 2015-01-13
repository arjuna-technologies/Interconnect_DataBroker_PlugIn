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
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.SerializableObjectPushDataSink;
import com.arjuna.dbutilities.testsupport.dataflownodes.lifecycle.TestJEEDataFlowNodeLifeCycleControl;

@RunWith(Arquillian.class)
public class PushTransferTest
{
    @Test
    public void simplestPushTransfer()
    {
        DataFlowNodeLifeCycleControl dataFlowNodeLifeCycleControl = new TestJEEDataFlowNodeLifeCycleControl();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(SerializableObjectPushDataSink.SERVICEROOTURL_PROPERTYNAME, "http://ouseburn:8080");
        properties.put(SerializableObjectPushDataSink.ENDPOINTPATH_PROPERTYNAME, "foo");

        SerializableObjectPushDataSink serializableObjectPushDataSink = new SerializableObjectPushDataSink("Serializable Object Provider Data Sink", properties);

        dataFlowNodeLifeCycleControl.completeCreationAndActivateDataFlowNode(UUID.randomUUID().toString(), serializableObjectPushDataSink, null);

        serializableObjectPushDataSink.consume("Data String");

        dataFlowNodeLifeCycleControl.removeDataFlowNode(serializableObjectPushDataSink);
    }
}
